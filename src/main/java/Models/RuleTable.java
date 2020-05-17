package Models;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleTable {
  private Set<Rule> rules;
  private List<Rule> ruleOrders;

  public RuleTable(List<String> parsedRuleStrings) {
    rules = parsedRuleStrings.stream().map(Rule::of).collect(Collectors.toUnmodifiableSet());
    ruleOrders = parsedRuleStrings.stream().map(Rule::of).collect(Collectors.toUnmodifiableList());
  }

  public Set<Word> getLHSWords() {
    return rules.stream().map(Rule::getLHS).collect(Collectors.toUnmodifiableSet());
  }

  public Set<Rule> getWordRules(String wordName) {
    return getWordRules(Word.of(wordName));
  }

  public Set<Rule> getWordRules(Word word) {
    return rules.stream()
        .filter(r -> r.getLHS().equals(word))
        .collect(Collectors.toUnmodifiableSet());
  }

  public Set<Rule> findRulesContainingWordInRHS(Word w) {
    return rules.stream()
        .filter(r -> r.isRuleContainWordInRHS(w))
        .collect(Collectors.toUnmodifiableSet());
  }

  public int getRuleNumber(Rule r) {
    return ruleOrders.indexOf(r);
  }

  public Set<Rule> getStartingRules() {
    return getWordRules("S");
  }

  public Set<Rule> getRules() {
    return rules;
  }
}
