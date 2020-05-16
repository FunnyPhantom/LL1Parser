package utils;

import Models.Word;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MapSetCache extends HashMap<Word, Set<Word>> {
  public Set<Word> put(Word key, Word value) {
    computeIfAbsent(key, w -> new HashSet<>());
    super.get(key).add(value);
    return super.get(key);
  }
}
