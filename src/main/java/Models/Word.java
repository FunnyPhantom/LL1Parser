package Models;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

public class Word {
  @Getter private String name;
  @Getter private WordType type;

  private Word(String name) {
    if (isNameIllegal(name)) throw new RuntimeException(String.format("Name cant be \"%s\"", name));
    this.name = name;
    this.type = isAllCapitalLetter(name) ? WordType.NON_TERMINAL : WordType.TERMINAL;
  }

  public static Word of(String name) {
    return new Word(name);
  }

  private static boolean isNameIllegal(String s) {
    var illegalNames = new String[] {"#", ":"};
    return Arrays.asList(illegalNames).contains(s);
  }

  private boolean isAllCapitalLetter(String s) {
    return s.matches("[A-Z][A-Z0-9]*");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Word)) return false;
    Word word = (Word) o;
    return name.equals(word.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return name;
  }
}
