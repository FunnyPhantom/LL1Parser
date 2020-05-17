package main;

import core.InputParser;
import core.Parser;
import utils.PrintUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Application {
  public static void main(String[] args) {
    var idx = List.of(args).indexOf("-f") + 1;
    String filePath;
    InputParser ip = null;
    try {
      filePath = args[idx];
      ip = new InputParser(filePath);
    } catch (Exception e) {
      System.out.println("bad input param");
    }
    if (ip == null) {
      String userInput = "";
      Scanner sc = new Scanner(System.in);
      do {
        System.out.println("enter file path or enter EXIT for quit");
        userInput = sc.nextLine().strip();
        if (userInput.equals("EXIT")) {
          System.out.println("exiting program..");
          System.exit(0);
        }
        try {
          ip = new InputParser(userInput);
        } catch (Exception e) {
          System.out.println(
              String.format(
                  "Bad input, the file %s does not exist. try again",
                  Path.of(userInput).toAbsolutePath()));
        }
      } while (ip == null);
    }

    var parser = Parser.createParser(ip.parseInput());
    var printUtils = PrintUtils.getInstance();

    printUtils.printNullSet(parser);
    printUtils.printFirsSet(parser);
    printUtils.printFollowSet(parser);
    printUtils.printPredictSet(parser);
    printUtils.printParseTable(parser.getParseTable());

    System.out.println("exiting program...");
  }
}
