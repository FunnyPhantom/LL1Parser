import Models.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

  private Parser(RuleTable rt) {
    this.ruleTable = rt;
    nullableWordsCache = new HashSet<>();
    firstOfAWordCache = new MapSetCache();
    followOfAWordCache = new MapSetCache();
    calculatePrimarySets();
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

  private Set<Word> follow(Word word) {
    if (word.equals(Word.of("S"))) return Set.of(Word.of("$"));
    return null;
  }

  private Set<Word> predict(Word word) {
    return null;
  }

  private void calculatePrimarySets() {
    calculateNullableSet();
    calculateFirstSet();
    calculateFollowSet();
    calculatePredictSet();
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

class MapSetCache extends HashMap<Word, Set<Word>> {
  public Set<Word> put(Word key, Word value) {
    computeIfAbsent(key, w -> new HashSet<>());
    super.get(key).add(value);
    return super.get(key);
  }
}
