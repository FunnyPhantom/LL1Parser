import Models.RuleTable;
import Models.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParserTest {
  @Test
  public void nullableWordsSetIsCorrect() {
    var parser =
        Parser.createParser(
            new RuleTable(
                List.of(
                    // = skip
                    "A: A",
                    "A: #",
                    "B: #",
                    "C: #",
                    "D: E F",
                    "E: #",
                    "F: a",
                    "G: H",
                    "H: I",
                    "I: J K",
                    "J: B",
                    "K: C",
                    "L: #",
                    "L : A")));
    Assertions.assertEquals(
        parser.nullableWords(),
        Set.of("A", "B", "C", "E", "G", "H", "I", "J", "K", "L").stream()
            .map(Word::new)
            .collect(Collectors.toUnmodifiableSet()));
  }
}
