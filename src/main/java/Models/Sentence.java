package Models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Sentence {
    private List<Word> words;
    public Sentence(String[] wordStrings) {
        words = new ArrayList<>();
        for (String wordString : wordStrings) {
            words.add(new Word(wordString));
        }
        words = Collections.unmodifiableList(words);
    }

    public List<Word> getWords() {
        return words;
    }

    public Word getWordAt(int index) {
        return words.get(index);
    }
}
