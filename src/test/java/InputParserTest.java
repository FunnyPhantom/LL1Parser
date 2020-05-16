import Models.Rule;
import Models.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class InputParserTest {
  private static InputParser ip;
  String validFilePath = "/test/resource/sampleRuleFile.ll1";
  String invalidFilePath = "/test/resource/mamad";

  @BeforeAll
  private static void init() {
    ip = new InputParser("src/test/resources/sampleRuleFile.ll1");
  }

  @Test
  public void canCreateInstance() {}

  @Test
  public void invalidFilePathThrowsError() {
    Assertions.assertThrows(RuntimeException.class, () -> new InputParser(invalidFilePath));
  }

  @Test
  public void canMakeRuleStringListFromFile() {
    var ruleList = ip.getListOfRuleStringsFromFile();
  }

  @Test
  public void ruleListIsParsedCorrectly() {
    // The content of sampleFile:
    /*
    S : A B
    A : mamad
    B : #
    */
    var ruleTable = ip.parseInput();
    Assertions.assertEquals(
        ruleTable.getLHSWords(),
        Set.of("S", "A", "B").stream().map(Word::of).collect(Collectors.toSet()));
    Assertions.assertEquals(
        ruleTable.getStartingRules(),
        Set.of("S: A B").stream().map(Rule::new).collect(Collectors.toSet()));
    Assertions.assertEquals(
        ruleTable.getWordRules("A"),
        Set.of("A  :  mamad").stream().map(Rule::new).collect(Collectors.toSet()));
  }
}
