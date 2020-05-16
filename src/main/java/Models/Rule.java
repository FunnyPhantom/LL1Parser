package Models;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Rule {
  private Word leftHandSide;
  private Sentence rightHandSide;

  public Rule(String ruleString) {
    var sideStrings =
        Arrays.stream(ruleString.strip().split(":"))
            .map(String::strip)
            .collect(Collectors.toUnmodifiableList());
    if (sideStrings.size() != 2)
      throw new RuntimeException(
          String.format("rule string \"%s\" is not a valid ruleString", ruleString));

    leftHandSide = Word.of(sideStrings.get(0));
    if (leftHandSide.getType() == WordType.TERMINAL)
      throw new RuntimeException("LHS cannot be Terminal Word");
    rightHandSide = Sentence.getSentenceFromString(sideStrings.get(1));
  }

  public Word getLHS() {
    return leftHandSide;
  }

  public Sentence getRHS() {
    return rightHandSide;
  }

  public boolean isRuleDirectlyContainLHSWordInRHS() {
    return getRHS().getWords().stream().anyMatch(getLHS()::equals);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Rule)) return false;
    Rule rule = (Rule) o;
    return Objects.equals(leftHandSide, rule.leftHandSide)
        && Objects.equals(rightHandSide, rule.rightHandSide);
  }

  @Override
  public int hashCode() {
    return Objects.hash(leftHandSide, rightHandSide);
  }

  @Override
  public String toString() {
    return leftHandSide + " : " + rightHandSide;
  }
}
