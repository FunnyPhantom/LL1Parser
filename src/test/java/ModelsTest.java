import Models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class ModelsTest {
  // Word
  @Test
  public void canCreateWord() {
    new Word("mamad");
  }

  @Test
  public void wordsTypeGetsAssignedSuccessfully() {
    Assertions.assertEquals(new Word("A").getType(), WordType.NON_TERMINAL);
    Assertions.assertEquals(new Word("a").getType(), WordType.TERMINAL);
    Assertions.assertEquals(new Word(";").getType(), WordType.TERMINAL);
    Assertions.assertEquals(new Word("$").getType(), WordType.TERMINAL);
    Assertions.assertEquals(new Word("EXP").getType(), WordType.NON_TERMINAL);
    Assertions.assertEquals(new Word("EXP2").getType(), WordType.NON_TERMINAL);
  }

  @Test
  public void wordsWithSameNameAreEqual() {
    Assertions.assertEquals(new Word("A"), new Word("A"));
    Assertions.assertEquals(new Word("b"), new Word("b"));
    Assertions.assertNotEquals(new Word("A"), new Word("a"));
    Assertions.assertNotEquals(new Word("B"), new Word("b"));
  }

  @Test
  public void toStringWorksIsAsIntended() {
    Assertions.assertEquals(new Word("A").toString(), "A");
    System.out.println(new Word("A"));
    Assertions.assertEquals(new Word("EXP0").toString(), "EXP0");
    System.out.println(new Word("EXP0"));
  }

  @Test
  public void shouldThrowExceptionIfNameIsIllegal() {
    Assertions.assertThrows(RuntimeException.class, () -> new Word("#"));
    Assertions.assertThrows(RuntimeException.class, () -> new Word(":"));
  }

  // Sentence
  @Test
  public void canCreateSentence() {
    Sentence.getSentenceFromString("A B C");
  }

  @Test
  public void correctSequenceOfWordsWillBeCreated() {
    var sentence = Sentence.getSentenceFromString("A B C");
    Assertions.assertEquals(sentence.getWordAt(0), new Word("A"));
    Assertions.assertEquals(sentence.getWordAt(1), new Word("B"));
    Assertions.assertEquals(sentence.getWordAt(2), new Word("C"));

    sentence = Sentence.getSentenceFromString("A if B else ST");
    Assertions.assertEquals(sentence.getWordAt(0), new Word("A"));
    Assertions.assertEquals(sentence.getWordAt(1), new Word("if"));
    Assertions.assertEquals(sentence.getWordAt(2), new Word("B"));
    Assertions.assertEquals(sentence.getWordAt(3), new Word("else"));
    Assertions.assertEquals(sentence.getWordAt(4), new Word("ST"));

    sentence = Sentence.getSentenceFromString("    A     B     C    ");
    Assertions.assertEquals(sentence.getWordAt(0), new Word("A"));
    Assertions.assertEquals(sentence.getWordAt(1), new Word("B"));
    Assertions.assertEquals(sentence.getWordAt(2), new Word("C"));

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
            "if BE      STB else                                   STB"));
  }

  // Rule
  @Test
  public void canCreateRule() {
    new Rule("S : A B");
    new Rule("S : #");
  }

  @Test
  public void ruleLHSGetsParsedCorrectly() {
    Assertions.assertEquals(new Rule("S : A B").getLHS(), new Word("S"));
    Assertions.assertEquals(new Rule("A : A B").getLHS(), new Word("A"));
    Assertions.assertEquals(new Rule("              B          : A B").getLHS(), new Word("B"));
  }

  @Test
  public void ruleLHSMustBeNotTerminalOrEmpty() {
    Assertions.assertThrows(RuntimeException.class, () -> new Rule("a : A B"));
    Assertions.assertThrows(RuntimeException.class, () -> new Rule("a : A #"));
    Assertions.assertThrows(RuntimeException.class, () -> new Rule(" : A #"));
    Assertions.assertThrows(RuntimeException.class, () -> new Rule(" "));
  }

  @Test
  public void ruleRHSGetsParsedCorrectly() {
    var rule = new Rule("A : b c");
    Assertions.assertEquals(rule.getRHS(), Sentence.getSentenceFromString("b c"));
    rule = new Rule("A : #");
    Assertions.assertEquals(rule.getRHS(), Sentence.getSentenceFromString("#"));
    rule = new Rule("ST: ++ A");
    Assertions.assertEquals(rule.getRHS(), Sentence.getSentenceFromString("++ A"));
  }

  @Test
  public void rulesFromSameStringAreEqual() {
    Assertions.assertEquals(new Rule("A : mamad B"), new Rule("A  :  mamad B"));
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
        table.getRules(), ruleStringsSample.stream().map(Rule::new).collect(Collectors.toSet()));
  }

  @Test
  public void canCorrectlyAggregateRules() {
    Assertions.assertEquals(
        table.getWordRules("S"),
        ruleStringsSample.stream()
            .map(Rule::new)
            .filter(r -> r.getLHS().equals(new Word("S")))
            .collect(Collectors.toSet()));
    Assertions.assertEquals(
        table.getWordRules("ST"),
        ruleStringsSample.stream()
            .map(Rule::new)
            .filter(r -> r.getLHS().equals(new Word("ST")))
            .collect(Collectors.toSet()));
    Assertions.assertEquals(
        table.getWordRules("STP"),
        ruleStringsSample.stream()
            .map(Rule::new)
            .filter(r -> r.getLHS().equals(new Word("STP")))
            .collect(Collectors.toSet()));
  }

  @Test
  public void canCorrectlyAggregateLHS() {
    Assertions.assertEquals(
        table.getLHSWords(),
        ruleStringsSample.stream().map(Rule::new).map(Rule::getLHS).collect(Collectors.toSet()));
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
