import Models.RuleTable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class InputParser {
    private File ruleFile;
//    private List<String> parsedRuleStrings;

    public InputParser(String path) throws IOException {
        this(Path.of(URI.create(path)).toFile());
    }

    public InputParser(File f) throws IOException {
        checkIfFileIsValid(f);
        ruleFile = f;
    }

    public void parseInput() {
        try {
            var parsedRuleStrings = getListOfRuleStringsFromFile(ruleFile);
            var ruleTable = createRuleTable(parsedRuleStrings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RuleTable createRuleTable(List<String> parsedRuleStrings) {
        return null;
    }

    private List<String> getListOfRuleStringsFromFile(File f) throws IOException {
        return Files.readAllLines(f.toPath())
                .stream()
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    private void checkIfFileIsValid(File f) throws RuntimeException {
        if (!f.exists() || !f.isFile())
            throw new RuntimeException("file does not exist of the path is not a file");
    }
}
