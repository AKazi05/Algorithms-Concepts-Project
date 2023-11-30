import java.util.Arrays;

public class LeafNode extends Node {
        int maxNumPairs;
    int minNumPairs;
    int numPairs;
    LeafNode leftSibling;
    LeafNode rightSibling;
    RecordPair[] record;

    public void delete(int index) {
      this.record[index] = null;
      numPairs--;
    }

    public boolean insert(RecordPair dp) {
      if (this.isFull()) {
        return false;
      } else {
        this.record[numPairs] = dp;
        numPairs++;
        Arrays.sort(this.record, 0, numPairs);

        return true;
      }
    }

    public boolean isDeficient() {
      return numPairs < minNumPairs;
    }

    public boolean isFull() {
      return numPairs == maxNumPairs;
    }

    public boolean isLendable() {
      return numPairs > minNumPairs;
    }

    public boolean isMergeable() {
      return numPairs == minNumPairs;
    }

    public LeafNode(int numberOfPointers, RecordPair dp) {
      this.maxNumPairs = numberOfPointers - 1;
      this.minNumPairs = (int) (Math.ceil(numberOfPointers / 2) - 1);
      this.record = new RecordPair[numberOfPointers];
      this.numPairs = 0;
      this.insert(dp);
    }

    public LeafNode(int numberOfPointers, RecordPair[] recordPairs, InnerNode parent) {
      this.maxNumPairs = numberOfPointers - 1;
      this.minNumPairs = (int) (Math.ceil(numberOfPointers / 2) - 1);
      this.record = recordPairs;
      this.numPairs = BPlusTree.linearNullSearch(recordPairs);
      this.parent = parent;
    }
  }


