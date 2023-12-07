
public class InnerNode extends Node {
    int maxDegree;
    int minDegree;
    int degree;
    InnerNode leftSibling;
    InnerNode rightSibling;
    String[] keys;
    Node[] childPointers;

    //Appends a child pointer to the end of the node's list of child pointers

    public void appendChildPointer(Node pointer) {
      this.childPointers[degree] = pointer;
      this.degree++;
    }
//Finds the index of a given child pointer within the node's list of child pointers.
    public int findIndexOfPointer(Node pointer) {
      for (int i = 0; i < childPointers.length; i++) {
        if (childPointers[i] == pointer) {
          return i;
        }
      }
      return -1;
    }
//Inserts a child pointer at the specified index, shifting existing pointers

    public void insertChildPointer(Node pointer, int index) {
      for (int i = degree - 1; i >= index; i--) {
        childPointers[i + 1] = childPointers[i];
      }
      this.childPointers[index] = pointer;
      this.degree++;
    }

//Checks whether the node is overfull
    public boolean isOverfull() {
      return this.degree == maxDegree + 1;
    }


//Removes a pointer
    public void removePointer(int index) {
      this.childPointers[index] = null;
      this.degree--;
    }

//Creates an inner node
    public InnerNode(int numberOfPointers, String[] parent_keys) {
      this.maxDegree = numberOfPointers;
      this.minDegree = (int) Math.ceil(numberOfPointers / 2.0);
      this.degree = 0;
      this.keys = parent_keys;
      this.childPointers = new Node[this.maxDegree + 1];
    }
//Creates a inner node
    public InnerNode(int numberOfPointers, String[] keys, Node[] pointers) {
      this.maxDegree = numberOfPointers;
      this.minDegree = (int) Math.ceil(numberOfPointers / 2.0);
      this.degree = BPlusTree.treeSearch(pointers);
      this.keys = keys;
      this.childPointers = pointers;
    }
  }
