
public class RecordPair implements Comparable<RecordPair> {
    String key;
    String value;

    //Create a record pair of an index and a value
    public RecordPair(String key, String value) {
      this.key = key;
      this.value = value;
    }

    //Compares two keys
    public int compareTo(RecordPair x) {
      return this.key.compareTo(x.key);
    }
  }

