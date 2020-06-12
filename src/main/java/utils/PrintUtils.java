package utils;

import Models.Word;
import core.Parser;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PrintUtils {
  private PrintUtils() {}

  private static PrintUtils instance = new PrintUtils();

  public static PrintUtils getInstance() {
    return instance;
  }

  public void printParseTable(ParseTable parseTable) {
    System.out.println("Parse Table:");
    var keySet = parseTable.keySet();
    var nonTerminalWords =
        keySet.stream().map(Map.Entry::getKey).distinct().collect(Collectors.toUnmodifiableList());
    var terminalWords =
        keySet.stream()
            .map(Map.Entry::getValue)
            .distinct()
            .collect(Collectors.toUnmodifiableList());

    printHeader(terminalWords);
    nonTerminalWords.forEach(printRow(terminalWords, parseTable));
    System.out.println();
    System.out.println("====");
  }

  public void saveParseTable(ParseTable parseTable) {
    try {
      var originalOut = System.out;
      System.setOut(new PrintStream("./table.pt"));
      printParseTable(parseTable);
      System.setOut(originalOut);
    } catch (Exception ignored) {
    }
  }

  private Consumer<Word> printRow(List<Word> terminalWords, ParseTable parseTable) {
    return W -> {
      System.out.print(String.format("%12s", W));
      terminalWords.forEach(
          w ->
              System.out.print(
                  String.format(
                      "%12s",
                      parseTable.containsKey(Map.entry(W, w))
                          ? Set.of(parseTable.get(Map.entry(W, w)))
                          : Set.of())));
      System.out.println();
    };
  }

  private void printHeader(List<Word> terminalWords) {
    System.out.print(String.format("%12s", " "));
    terminalWords.forEach(w -> System.out.print(String.format("%12s", w)));
    System.out.println();
  }

  public void printNullSet(Parser parser) {
    System.out.println(String.format("Nullable Words: \n%s", parser.nullableWords()));
    System.out.println();
    System.out.println("====");
  }

  public void printFirsSet(Parser parser) {
    System.out.println("First: ");
    parser
        .getLHSWords()
        .forEach(w -> System.out.println(String.format("%10s: %30s", w, parser.first(w))));
    System.out.println();
    System.out.println("====");
  }

  public void printFollowSet(Parser parser) {
    System.out.println("Follow: ");
    parser
        .getLHSWords()
        .forEach(w -> System.out.println(String.format("%10s: %30s", w, parser.follow(w))));
    System.out.println();
    System.out.println("====");
  }

  public void printPredictSet(Parser parser) {
    System.out.println("Predict: ");
    parser
        .getLHSWords()
        .forEach(w -> System.out.println(String.format("%10s: %30s", w, parser.predict(w))));
    System.out.println();
    System.out.println("====");
  }
}
