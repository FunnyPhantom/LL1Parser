import Models.RuleTable;
import Models.Word;
import core.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.PrintUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ParserTest {
  private static List<String> rs;
  private static Parser parser;

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
    parser = Parser.createParser(new RuleTable(rs));
  }

  @Test
  public void nullableWordsSetIsCorrect() {
    Assertions.assertEquals(
        parser.nullableWords(),
        Set.of("EXP1", "TERM1", "ID1").stream()
            .map(Word::of)
            .collect(Collectors.toUnmodifiableSet()));
  }

  @Test
  public void wordsFirstSetIsCorrect() {

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

  @Test
  public void predictSetIsCorrect() {

    Assertions.assertEquals(
        parser.predict(Word.of("S")),
        Set.of(Word.of("("), Word.of("num"), Word.of("++"), Word.of("--"), Word.of("id")));
    Assertions.assertEquals(
        parser.predict(Word.of("EXP")),
        Set.of(Word.of("("), Word.of("num"), Word.of("++"), Word.of("--"), Word.of("id")));
    Assertions.assertEquals(
        parser.predict(Word.of("EXP1")),
        Set.of(Word.of("$"), Word.of(")"), Word.of("+"), Word.of("-")));
    Assertions.assertEquals(
        parser.predict(Word.of("TERM")),
        Set.of(Word.of("("), Word.of("num"), Word.of("++"), Word.of("--"), Word.of("id")));
    Assertions.assertEquals(
        parser.predict(Word.of("TERM1")),
        Set.of(Word.of("$"), Word.of(")"), Word.of("+"), Word.of("-"), Word.of("/"), Word.of("*")));
    Assertions.assertEquals(
        parser.predict(Word.of("FACTOR")),
        Set.of(Word.of("("), Word.of("num"), Word.of("++"), Word.of("--"), Word.of("id")));
    Assertions.assertEquals(
        parser.predict(Word.of("ID")), Set.of(Word.of("++"), Word.of("--"), Word.of("id")));
    Assertions.assertEquals(
        parser.predict(Word.of("ID1")),
        Set.of(
            Word.of("$"),
            Word.of(")"),
            Word.of("+"),
            Word.of("-"),
            Word.of("*"),
            Word.of("/"),
            Word.of("++"),
            Word.of("--")));
  }

  @Test
  public void predictRuleNumberIsCorrect() {
    var pt = parser.getParseTable();

    Assertions.assertEquals(
        pt.getOrDefault(Map.entry(Word.of("EXP1"), Word.of("+")), -1), Integer.valueOf(2));
    Assertions.assertEquals(
        pt.getOrDefault(Map.entry(Word.of("EXP1"), Word.of("-")), -1), Integer.valueOf(3));
    Assertions.assertEquals(
        pt.getOrDefault(Map.entry(Word.of("EXP1"), Word.of(")")), -1), Integer.valueOf(4));
    Assertions.assertEquals(
        pt.getOrDefault(Map.entry(Word.of("EXP1"), Word.of("$")), -1), Integer.valueOf(4));

    Assertions.assertEquals(
        pt.getOrDefault(Map.entry(Word.of("S"), Word.of("--")), -1), Integer.valueOf(0));
    Assertions.assertEquals(
        pt.getOrDefault(Map.entry(Word.of("S"), Word.of("num")), -1), Integer.valueOf(0));

    Assertions.assertEquals(
        pt.getOrDefault(Map.entry(Word.of("EXP"), Word.of("id")), -1), Integer.valueOf(1));
    Assertions.assertEquals(
        pt.getOrDefault(Map.entry(Word.of("EXP"), Word.of("++")), -1), Integer.valueOf(1));
    Assertions.assertEquals(
        pt.getOrDefault(Map.entry(Word.of("EXP"), Word.of("(")), -1), Integer.valueOf(1));
  }

  @Test
  public void printParseTable() {
    PrintUtils.getInstance().printParseTable(parser.getParseTable());
  }
}
