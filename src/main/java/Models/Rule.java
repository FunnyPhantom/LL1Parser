package Models;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Rule {
    private Word leftHandSide;
    private Sentence rightHandSide;

    public Rule(String ruleString){
        var sideStrings = Arrays.stream(ruleString
                .strip()
                .split(":"))
                .map(String::strip).
                collect(Collectors.toList());
        leftHandSide = new Word(sideStrings.get(0));
        rightHandSide = new Sentence(Arrays.stream(sideStrings.get(1).split(" ")).map(String::strip).toArray(String[]::new));
    }

    public Word getLHS() {
        return leftHandSide;
    }

    public Sentence getRHS() {
        return rightHandSide;
    }
}
