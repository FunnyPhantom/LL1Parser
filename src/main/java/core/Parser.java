package core;

import Models.*;
import utils.MapSetCache;
import utils.ParseTable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * most of logic was implemented by the help of this Reference:
 * https://www.usna.edu/Users/cs/roche/courses/f11si413/c10/ff.pdf
 */
public class Parser {
  private final RuleTable ruleTable;
  private Set<Word> nullableWordsCache;
  private MapSetCache firstOfAWordCache;
  private MapSetCache followOfAWordCache;
  private MapSetCache predictOfAWordCache;
  private ParseTable parseTable;

  private Parser(RuleTable rt) {
    this.ruleTable = rt;
    nullableWordsCache = new HashSet<>();
    firstOfAWordCache = new MapSetCache();
    followOfAWordCache = new MapSetCache();
    predictOfAWordCache = new MapSetCache();
    parseTable = new ParseTable();
    calculatePrimarySets();
    calculateParseTable();
  }

  public static Parser createParser(RuleTable rt) {
    return new Parser(rt);
  }

  private boolean isRuleNullable(Rule r) {
    if (r.getRHS().isNullSentence()) return true;
    return r.getRHS().getWords().stream()
        .map(ruleTable::getWordRules)
        .reduce(
            true,
            (tillNowIsNullable, nextWordSetOfRules) ->
                tillNowIsNullable
                    && nextWordSetOfRules.stream()
                        .filter(nwr -> !nwr.isRuleDirectlyContainLHSWordInRHS())
                        .anyMatch(this::isRuleNullable),
            (tillNowWasNullable, isThisNullable) -> tillNowWasNullable && isThisNullable);
  }

  private boolean isWordNullable(Word w) {
    if (nullableWordsCache.contains(w)) return true;
    if (ruleTable.getWordRules(w).stream().anyMatch(this::isRuleNullable)) {
      nullableWordsCache.add(w);
      return true;
    }
    return false;
  }

  public Set<Word> nullableWords() {
    return Collections.unmodifiableSet(nullableWordsCache);
  }

  public Set<Word> first(Word word) {
    if (word.getType() == WordType.TERMINAL) return Set.of(word);
    if (!firstOfAWordCache.containsKey(word)) {
      var firstSet =
          ruleTable.getWordRules(word).stream()
              .map(Rule::getRHS)
              .filter(s -> !s.isNullSentence())
              .map(this::trimToFirstNonNullableWord)
              .flatMap(s -> s.getWords().stream())
              .filter(w -> !w.equals(word))
              .flatMap(w -> first(w).stream())
              .collect(Collectors.toUnmodifiableSet());
      firstOfAWordCache.put(word, firstSet);
    }

    return firstOfAWordCache.get(word);
  }

  private Sentence trimToFirstNonNullableWord(Sentence s) {
    return Sentence.getSentenceFromStreamOfWords(
        Stream.concat(
            s.getWords().stream().takeWhile(this::isWordNullable),
            s.getWords().stream().dropWhile(this::isWordNullable).findFirst().stream()));
  }
  // S -> ABC
  public Set<Word> follow(Word word, Set<Word> visitedWords) {
    if (word.equals(Word.of("S"))) return Set.of(Word.of("$"));
    if (!followOfAWordCache.containsKey(word)) {
      var rules = ruleTable.findRulesContainingWordInRHS(word);
      var firstWords =
          rules.stream()
              .flatMap(r -> firstOfWordInFollowSet(r, word))
              .flatMap(w -> first(w).stream());
      System.out.println(String.format("first %s", firstWords));
      var followWords =
          rules.stream()
              .flatMap(r -> followOfWordInFollowSet(r, word))
              .filter(w -> !visitedWords.contains(w))
              .flatMap(
                  w ->
                      follow(
                          w,
                          Stream.concat(visitedWords.stream(), Stream.of(w))
                              .collect(Collectors.toUnmodifiableSet()))
                          .stream());

      var followSet =
          Stream.concat(firstWords, followWords).collect(Collectors.toUnmodifiableSet());
      followOfAWordCache.put(word, followSet);
    }

    return followOfAWordCache.get(word);
  }

  public Set<Word> follow(Word word) {
    return follow(word, Set.of(word));
  }

  private Stream<Word> firstOfWordInFollowSet(Rule rule, Word word) {
    return rule.getRHS().getWords().stream().dropWhile(w -> !w.equals(word)).skip(1);
  }

  private Stream<Word> followOfWordInFollowSet(Rule rule, Word word) {
    var maybeWord =
        rule.getRHS().getWords().stream()
            .dropWhile(w -> !w.equals(word))
            .skip(1)
            .dropWhile(this::isWordNullable)
            .findAny();
    if (maybeWord.isPresent()) return Stream.empty();
    else return Stream.of(rule.getLHS());
  }

  public Set<Word> predict(Word word) {
    if (!predictOfAWordCache.containsKey(word)) {
      var rules = ruleTable.getWordRules(word);
      var tokens =
          rules.stream()
              .map(Rule::getRHS)
              .filter(s -> !s.isNullSentence())
              .map(s -> s.getWordAt(0))
              .filter(w -> w.getType() == WordType.TERMINAL);

      var followTokens = isWordNullable(word) ? follow(word).stream() : Stream.<Word>empty();

      var predictTokens =
          rules.stream()
              .map(Rule::getRHS)
              .filter(s -> !s.isNullSentence())
              .map(s -> s.getWordAt(0))
              .filter(w -> w.getType() == WordType.NON_TERMINAL)
              .flatMap(w -> predict(w).stream());

      var predictSet =
          Stream.concat(Stream.concat(tokens, followTokens), predictTokens)
              .collect(Collectors.toUnmodifiableSet());
      predictOfAWordCache.put(word, predictSet);
    }
    return predictOfAWordCache.get(word);
  }

  public Set<Word> predict(Rule r) {
    if (r.getRHS().isNullSentence()) return follow(r.getLHS());
    if (r.getRHS().getWordAt(0).getType() == WordType.TERMINAL)
      return Set.of(r.getRHS().getWordAt(0));
    if (r.getRHS().getWordAt(0).equals(r.getLHS())) {
      System.err.println("LEFT RECURSION");
      return Set.of();
    }
    return predict(r.getRHS().getWordAt(0));
  }

  public Set<Word> getLHSWords() {
    return ruleTable.getLHSWords();
  }

  private void calculatePrimarySets() {
    calculateNullableSet();
    calculateFirstSet();
    calculateFollowSet();
    calculatePredictSet();
  }

  private void calculateParseTable() {
    ruleTable
        .getRules()
        .forEach(
            rule ->
                predict(rule)
                    .forEach(
                        token ->
                            parseTable.put(
                                Map.entry(rule.getLHS(), token), ruleTable.getRuleNumber(rule))));
  }

  public ParseTable getParseTable() {
    return parseTable;
  }

  private void calculateNullableSet() {
    ruleTable.getLHSWords().forEach(this::isWordNullable);
  }

  private void calculateFirstSet() {
    ruleTable.getLHSWords().forEach(this::first);
  }

  private void calculatePredictSet() {
    ruleTable.getLHSWords().forEach(this::follow);
  }

  private void calculateFollowSet() {
    ruleTable.getLHSWords().forEach(this::predict);
  }
}
