package core;

import Models.RuleTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class InputParser {
  private File ruleFile;
  //    private List<String> parsedRuleStrings;

  public InputParser(String path) {
    this(Path.of(path).toFile());
  }

  public InputParser(File f) {
    checkIfFileIsValid(f);
    ruleFile = f;
  }

  public RuleTable parseInput() {
    var parsedRuleStrings = getListOfRuleStringsFromFile();
    return createRuleTable(parsedRuleStrings);
  }

  private RuleTable createRuleTable(List<String> parsedRuleStrings) {
    return new RuleTable(parsedRuleStrings);
  }

  public List<String> getListOfRuleStringsFromFile() {
    try {
      return Files.readAllLines(this.ruleFile.toPath()).stream()
          .filter(s -> !s.isBlank())
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private void checkIfFileIsValid(File f) throws RuntimeException {
    if (!f.exists() || !f.isFile())
      throw new RuntimeException("file does not exist of the path is not a file");
  }
}
