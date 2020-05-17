import Models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class ModelsTest {
  // Word
  @Test
  public void canCreateWord() {
    Word.of("mamad");
  }

  @Test
  public void wordsTypeGetsAssignedSuccessfully() {
    Assertions.assertEquals(Word.of("A").getType(), WordType.NON_TERMINAL);
    Assertions.assertEquals(Word.of("a").getType(), WordType.TERMINAL);
    Assertions.assertEquals(Word.of(";").getType(), WordType.TERMINAL);
    Assertions.assertEquals(Word.of("$").getType(), WordType.TERMINAL);
    Assertions.assertEquals(Word.of("EXP").getType(), WordType.NON_TERMINAL);
    Assertions.assertEquals(Word.of("EXP2").getType(), WordType.NON_TERMINAL);
  }

  @Test
  public void wordsWithSameNameAreEqual() {
    Assertions.assertEquals(Word.of("A"), Word.of("A"));
    Assertions.assertEquals(Word.of("b"), Word.of("b"));
    Assertions.assertNotEquals(Word.of("A"), Word.of("a"));
    Assertions.assertNotEquals(Word.of("B"), Word.of("b"));
  }

  @Test
  public void toStringWorksIsAsIntended() {
    Assertions.assertEquals(Word.of("A").toString(), "A");
    System.out.println(Word.of("A"));
    Assertions.assertEquals(Word.of("EXP0").toString(), "EXP0");
    System.out.println(Word.of("EXP0"));
  }

  @Test
  public void shouldThrowExceptionIfNameIsIllegal() {
    Assertions.assertThrows(RuntimeException.class, () -> Word.of("#"));
    Assertions.assertThrows(RuntimeException.class, () -> Word.of(":"));
  }

  // Sentence
  @Test
  public void canCreateSentence() {
    Sentence.getSentenceFromString("A B C");
  }

  @Test
  public void correctSequenceOfWordsWillBeCreated() {
    var sentence = Sentence.getSentenceFromString("A B C");
    Assertions.assertEquals(sentence.getWordAt(0), Word.of("A"));
    Assertions.assertEquals(sentence.getWordAt(1), Word.of("B"));
    Assertions.assertEquals(sentence.getWordAt(2), Word.of("C"));

    sentence = Sentence.getSentenceFromString("A if B else ST");
    Assertions.assertEquals(sentence.getWordAt(0), Word.of("A"));
    Assertions.assertEquals(sentence.getWordAt(1), Word.of("if"));
    Assertions.assertEquals(sentence.getWordAt(2), Word.of("B"));
    Assertions.assertEquals(sentence.getWordAt(3), Word.of("else"));
    Assertions.assertEquals(sentence.getWordAt(4), Word.of("ST"));

    sentence = Sentence.getSentenceFromString("    A     B     C    ");
    Assertions.assertEquals(sentence.getWordAt(0), Word.of("A"));
    Assertions.assertEquals(sentence.getWordAt(1), Word.of("B"));
    Assertions.assertEquals(sentence.getWordAt(2), Word.of("C"));

    sentence = Sentence.getSentenceFromString("#");
    Assertions.assertTrue(sentence.isNullSentence());
  }

  @Test
  public void illegalSentenceWillThrowError() {
    Assertions.assertThrows(RuntimeException.class, () -> Sentence.getSentenceFromString("# a"));
    Assertions.assertThrows(RuntimeException.class, () -> Sentence.getSentenceFromString("# #"));
    Assertions.assertThrows(RuntimeException.class, () -> Sentence.getSentenceFromString("# A"));
    Assertions.assertThrows(RuntimeException.class, () -> Sentence.getSentenceFromString(""));
    Assertions.assertThrows(RuntimeException.class, () -> Sentence.getSentenceFromString("a # b"));
  }

  @Test
  public void sameWordSentencesAreEqual() {
    Assertions.assertEquals(
        Sentence.getSentenceFromString("A B C"), Sentence.getSentenceFromString("A B C"));
    Assertions.assertEquals(
        Sentence.getSentenceFromString("a b"), Sentence.getSentenceFromString("a b"));
    Assertions.assertEquals(
        Sentence.getSentenceFromString("#"), Sentence.getSentenceFromString("#"));
    Assertions.assertEquals(
        Sentence.getSentenceFromString("if BE STB else STB"),
        Sentence.getSentenceFromString(
            " if BE      STB else                                   STB"));
  }

  @Test
  private void canCorrectlyStateRuleContainAWord() {
    Assertions.assertTrue(Sentence.getSentenceFromString("A B C").contain(Word.of("B")));
    Assertions.assertFalse(Sentence.getSentenceFromString("#").contain(Word.of("A")));
  }

  // Rule
  @Test
  public void canCreateRule() {
    Rule.of("S : A B");
    Rule.of("S : #");
  }

  @Test
  public void ruleLHSGetsParsedCorrectly() {
    Assertions.assertEquals(Rule.of("S : A B").getLHS(), Word.of("S"));
    Assertions.assertEquals(Rule.of("A : A B").getLHS(), Word.of("A"));
    Assertions.assertEquals(Rule.of("              B          : A B").getLHS(), Word.of("B"));
  }

  @Test
  public void ruleLHSMustBeNotTerminalOrEmpty() {
    Assertions.assertThrows(RuntimeException.class, () -> Rule.of("a : A B"));
    Assertions.assertThrows(RuntimeException.class, () -> Rule.of("a : A #"));
    Assertions.assertThrows(RuntimeException.class, () -> Rule.of(" : A #"));
    Assertions.assertThrows(RuntimeException.class, () -> Rule.of(" "));
  }

  @Test
  public void ruleRHSGetsParsedCorrectly() {
    var rule = Rule.of("A : b c");
    Assertions.assertEquals(rule.getRHS(), Sentence.getSentenceFromString("b c"));
    rule = Rule.of("A : #");
    Assertions.assertEquals(rule.getRHS(), Sentence.getSentenceFromString("#"));
    rule = Rule.of("ST: ++ A");
    Assertions.assertEquals(rule.getRHS(), Sentence.getSentenceFromString("++ A"));
  }

  @Test
  public void canDetectLHSinRHS() {
    Assertions.assertTrue(Rule.of("A : A B").isRuleDirectlyContainLHSWordInRHS());
    Assertions.assertFalse(Rule.of("A : L C").isRuleDirectlyContainLHSWordInRHS());
  }

  @Test
  public void rulesFromSameStringAreEqual() {
    Assertions.assertEquals(Rule.of("A : mamad B"), Rule.of("A  :  mamad B"));
  }

  @Test
  public void canCorrectlyStateRuleRHSContainsWord() {
    Assertions.assertTrue(Rule.of("S: A B mamad").isRuleContainWordInRHS(Word.of("B")));
    Assertions.assertFalse(Rule.of("S: A B mamad").isRuleContainWordInRHS(Word.of("op")));
    Assertions.assertFalse(Rule.of("S: #").isRuleContainWordInRHS(Word.of("X")));
  }

  // RuleTable
  List<String> ruleStringsSample =
      List.of("S : ST STP", "STP: #", "STP: ST STP", "ST: mamad", "S: abbas");
  RuleTable table = new RuleTable(ruleStringsSample);

  @Test
  public void canCreateRuleTable() {
    new RuleTable(List.of("S : A"));
  }

  @Test
  public void rulesGetAddedCorrectly() {
    Assertions.assertEquals(
        table.getRules(), ruleStringsSample.stream().map(Rule::of).collect(Collectors.toSet()));
  }

  @Test
  public void canCorrectlyAggregateRules() {
    Assertions.assertEquals(
        table.getWordRules("S"),
        ruleStringsSample.stream()
            .map(Rule::of)
            .filter(r -> r.getLHS().equals(Word.of("S")))
            .collect(Collectors.toSet()));
    Assertions.assertEquals(
        table.getWordRules("ST"),
        ruleStringsSample.stream()
            .map(Rule::of)
            .filter(r -> r.getLHS().equals(Word.of("ST")))
            .collect(Collectors.toSet()));
    Assertions.assertEquals(
        table.getWordRules("STP"),
        ruleStringsSample.stream()
            .map(Rule::of)
            .filter(r -> r.getLHS().equals(Word.of("STP")))
            .collect(Collectors.toSet()));
  }

  @Test
  public void canCorrectlyAggregateLHS() {
    Assertions.assertEquals(
        table.getLHSWords(),
        ruleStringsSample.stream().map(Rule::of).map(Rule::getLHS).collect(Collectors.toSet()));
  }

  @Test
  public void canCorrectlyFindRulesThatContainWordInRHS() {
    Assertions.assertEquals(
        table.findRulesContainingWordInRHS(Word.of("STP")),
        ruleStringsSample.stream()
            .map(Rule::of)
            .filter(r -> r.isRuleContainWordInRHS(Word.of("STP")))
            .collect(Collectors.toUnmodifiableSet()));
  }

  @Test
  public void printSomeStuff() {
    System.out.println(table.getStartingRules());
    System.out.println(table.getWordRules("ST"));
    System.out.println(table.getWordRules("STP"));
    System.out.println(table.getLHSWords());
    System.out.println(table.getRules());
  }
}
