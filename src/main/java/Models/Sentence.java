package Models;

import java.util.*;
import java.util.stream.Collectors;

public class Sentence {
  private List<Word> words;

  private Sentence(String[] wordStrings) {
    if (isNullableWordStrings(wordStrings))
      words = Collections.unmodifiableList(new ArrayList<>(0));
    else words = Arrays.stream(wordStrings).map(Word::new).collect(Collectors.toUnmodifiableList());
  }

  private static boolean isNullableWordStrings(String[] wordStrings) {
    if (wordStrings[0].equals("#")) {
      if (wordStrings.length == 1) return true;
      else throw new RuntimeException("Sentence with epsilon must not contain any other word");
    } else return false;
  }

  public static Sentence getSentenceFromString(String sentenceString) {
    if (sentenceString.isBlank()) throw new RuntimeException("Sentence String cannot be blank");
    return new Sentence(
        Arrays.stream(sentenceString.split(" ")).filter(s -> !s.isBlank()).toArray(String[]::new));
  }

  public List<Word> getWords() {
    return words;
  }

  public Word getWordAt(int index) {
    return words.get(index);
  }

  public boolean isNullSentence() {
    return words.size() == 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Sentence)) return false;
    Sentence sentence = (Sentence) o;
    return Objects.equals(words, sentence.words);
  }

  @Override
  public int hashCode() {
    return Objects.hash(words);
  }

  @Override
  public String toString() {
    if (isNullSentence()) return "#";
    else return words.stream().map(Word::toString).collect(Collectors.joining(" "));
  }
}