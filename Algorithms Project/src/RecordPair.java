
public class RecordPair implements Comparable<RecordPair> {
    String key;
    String value;

    public RecordPair(String key, String value) {
      this.key = key;
      this.value = value;
    }

    public int compareTo(RecordPair o) {
      return this.key.compareTo(o.key);
    }
  }

