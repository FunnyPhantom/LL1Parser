import Models.RuleTable;
import Models.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParserTest {
  private static List<String> rs;

  @BeforeAll
  private static void initRs() {
    rs =
        List.of(
            "S: EXP",
            "EXP: TERM EXP1",
            "EXP1: + TERM EXP1",
            "EXP1: - TERM EXP1",
            "EXP1: #",
            "TERM: FACTOR TERM1",
            "TERM1: * FACTOR TERM1",
            "TERM1: / FACTOR TERM1",
            "TERM1: #",
            "FACTOR: ID",
            "ID: id ID1",
            "ID1: ++",
            "ID1: --",
            "ID1: #",
            "ID: -- id",
            "ID: ++ id",
            "FACTOR: num",
            "FACTOR: ( EXP )");
  }

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
            .map(Word::of)
            .collect(Collectors.toUnmodifiableSet()));
  }

  @Test
  public void wordsFirstSetIsCorrect() {
    var parser = Parser.createParser(new RuleTable(rs));
    Assertions.assertEquals(
        parser.first(Word.of("ID1")),
        Set.of("++", "--").stream().map(Word::of).collect(Collectors.toUnmodifiableSet()));
    Assertions.assertEquals(
        parser.first(Word.of("ID")),
        Set.of("++", "--", "id").stream().map(Word::of).collect(Collectors.toUnmodifiableSet()));
    Assertions.assertEquals(
        parser.first(Word.of("FACTOR")),
        Set.of("++", "--", "id", "num", "(").stream()
            .map(Word::of)
            .collect(Collectors.toUnmodifiableSet()));
    Assertions.assertEquals(
        parser.first(Word.of("TERM1")),
        Set.of("*", "/").stream().map(Word::of).collect(Collectors.toUnmodifiableSet()));
    Assertions.assertEquals(
        parser.first(Word.of("TERM")),
        Set.of("++", "--", "id", "num", "(").stream()
            .map(Word::of)
            .collect(Collectors.toUnmodifiableSet()));
    Assertions.assertEquals(
        parser.first(Word.of("EXP1")),
        Set.of("+", "-").stream().map(Word::of).collect(Collectors.toUnmodifiableSet()));
    Assertions.assertEquals(
        parser.first(Word.of("EXP")),
        Set.of("++", "--", "id", "num", "(").stream()
            .map(Word::of)
            .collect(Collectors.toUnmodifiableSet()));
    Assertions.assertEquals(
        parser.first(Word.of("S")),
        Set.of("++", "--", "id", "num", "(").stream()
            .map(Word::of)
            .collect(Collectors.toUnmodifiableSet()));
  }

  @Test
  public void followOfSIsCorrect() {
    var parser = Parser.createParser(new RuleTable(rs));
    Assertions.assertEquals(parser.follow(Word.of("S")), Set.of(Word.of("$")));
    Assertions.assertEquals(parser.follow(Word.of("EXP")), Set.of(Word.of("$"), Word.of(")")));
    Assertions.assertEquals(parser.follow(Word.of("EXP1")), Set.of(Word.of("$"), Word.of(")")));
    Assertions.assertEquals(
        parser.follow(Word.of("TERM")),
        Set.of(Word.of("$"), Word.of(")"), Word.of("+"), Word.of("-")));
    Assertions.assertEquals(
        parser.follow(Word.of("TERM1")),
        Set.of(Word.of("$"), Word.of(")"), Word.of("+"), Word.of("-")));
    Assertions.assertEquals(
        parser.follow(Word.of("FACTOR")),
        Set.of(Word.of("$"), Word.of(")"), Word.of("+"), Word.of("-"), Word.of("*"), Word.of("/")));
    Assertions.assertEquals(
        parser.follow(Word.of("ID")),
        Set.of(Word.of("$"), Word.of(")"), Word.of("+"), Word.of("-"), Word.of("*"), Word.of("/")));
    Assertions.assertEquals(
        parser.follow(Word.of("ID1")),
        Set.of(Word.of("$"), Word.of(")"), Word.of("+"), Word.of("-"), Word.of("*"), Word.of("/")));
  }
}
