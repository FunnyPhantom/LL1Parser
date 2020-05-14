package Models;

import lombok.Getter;

public class Word {
    @Getter
    private String name;
    @Getter
    private WordType type;
    public Word(String name) {
        this.name = name;
        this.type = isAllCapitalLetter(name) ? WordType.NON_TERMINAL : WordType.TERMINAL;
    }
    private boolean isAllCapitalLetter(String s) {
        return s.matches("[A-Z][A-Z0-9]*");
    }

}
