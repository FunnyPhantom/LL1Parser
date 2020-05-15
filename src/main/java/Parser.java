import Models.Rule;
import Models.RuleTable;
import Models.Sentence;
import Models.Word;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * most of logic was implemented by the help of this Reference:
 * https://www.usna.edu/Users/cs/roche/courses/f11si413/c10/ff.pdf
 */
public class Parser {
  private final RuleTable ruleTable;
  private Set<Word> nullableWordsCache;

  private Parser(RuleTable rt) {
    this.ruleTable = rt;
    nullableWordsCache = new HashSet<>();
  }

  public static Parser createParser(RuleTable rt) {
    return new Parser(rt);
  }

  private Set<Word> firstOrderNullableRule() {
    return ruleTable.getLHSWords().stream()
        .filter(
            w ->
                ruleTable.getWordRules(w).stream()
                    .map(Rule::getRHS)
                    .anyMatch(Sentence::isNullSentence))
        .collect(Collectors.toSet());
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
    return ruleTable.getLHSWords().stream()
        .filter(this::isWordNullable)
        .collect(Collectors.toUnmodifiableSet());
  }
}
