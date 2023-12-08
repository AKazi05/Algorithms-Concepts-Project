import java.util.Arrays;

public class LeafNode extends Node {
  int maxNumPairs;
  int minNumPairs;
  int numPairs;
  LeafNode leftSibling;
  LeafNode rightSibling;
  RecordPair[] record;

  // Deletes a leaf node given an index
  public void delete(int index) {
    this.record[index] = null;
    numPairs--;
  }

  // insert a record pair
  public boolean insert(RecordPair zed) {
    if (this.isFull()) {
      return false;
    } else {
      this.record[numPairs] = zed;
      numPairs++;
      Arrays.sort(this.record, 0, numPairs);

      return true;
    }
  }

  // Checks if the node is deficient
  public boolean isDeficient() {
    return numPairs < minNumPairs;
  }

  // Checks if the node is full
  public boolean isFull() {
    return numPairs == maxNumPairs;
  }

  // Checks if the node is lendable
  public boolean isLendable() {
    return numPairs > minNumPairs;
  }


  // Creates a leaf node
  public LeafNode(int numberOfPointers, RecordPair zed) {
    this.maxNumPairs = numberOfPointers - 1;
    this.minNumPairs = (int) (Math.ceil(numberOfPointers / 2) - 1);
    this.record = new RecordPair[numberOfPointers];
    this.numPairs = 0;
    this.insert(zed);
  }

  // Creates a loaded leaf node
  public LeafNode(int numberOfPointers, RecordPair[] recordPairs, InnerNode parent) {
    this.maxNumPairs = numberOfPointers - 1;
    this.minNumPairs = (int) (Math.ceil(numberOfPointers / 2) - 1);
    this.record = recordPairs;
    this.numPairs = BPlusTree.treeSearch(recordPairs);
    this.parent = parent;
  }
}
