package Models;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleTable {
  private Set<Rule> rules;

  public RuleTable(List<String> parsedRuleStrings) {
    rules = parsedRuleStrings.stream().map(Rule::new).collect(Collectors.toUnmodifiableSet());
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

  public Set<Rule> getStartingRules() {
    return getWordRules("S");
  }

  public Set<Rule> getRules() {
    return rules;
  }
}
