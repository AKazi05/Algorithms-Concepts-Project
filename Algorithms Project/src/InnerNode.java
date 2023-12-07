
public class InnerNode extends Node {
    int maxDegree;
    int minDegree;
    int degree;
    InnerNode leftSibling;
    InnerNode rightSibling;
    String[] keys;
    Node[] childPointers;

    public void appendChildPointer(Node pointer) {
      this.childPointers[degree] = pointer;
      this.degree++;
    }

    public int findIndexOfPointer(Node pointer) {
      for (int i = 0; i < childPointers.length; i++) {
        if (childPointers[i] == pointer) {
          return i;
        }
      }
      return -1;
    }

    public void insertChildPointer(Node pointer, int index) {
      for (int i = degree - 1; i >= index; i--) {
        childPointers[i + 1] = childPointers[i];
      }
      this.childPointers[index] = pointer;
      this.degree++;
    }


    public boolean isOverfull() {
      return this.degree == maxDegree + 1;
    }



    public void removePointer(int index) {
      this.childPointers[index] = null;
      this.degree--;
    }


    public InnerNode(int numberOfPointers, String[] parent_keys) {
      this.maxDegree = numberOfPointers;
      this.minDegree = (int) Math.ceil(numberOfPointers / 2.0);
      this.degree = 0;
      this.keys = parent_keys;
      this.childPointers = new Node[this.maxDegree + 1];
    }

    public InnerNode(int numberOfPointers, String[] keys, Node[] pointers) {
      this.maxDegree = numberOfPointers;
      this.minDegree = (int) Math.ceil(numberOfPointers / 2.0);
      this.degree = BPlusTree.treeSearch(pointers);
      this.keys = keys;
      this.childPointers = pointers;
    }
  }
