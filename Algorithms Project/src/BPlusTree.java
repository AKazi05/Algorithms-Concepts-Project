import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BPlusTree {

  public int parentSplitCount = 0;
  public int fusionCount = 0;
  public int splitCount = 0;
  public int parentFusionCount = 0;
  public int numberOfPointers;
  public InnerNode root;
  public LeafNode LeftLeafNode;

  // Returns the mid point of the tree
  public int returnMidpoint() {
    return (int) Math.ceil((this.numberOfPointers + 1) / 2.0) - 1;
  }

  // Checks if the leafnode is empty
  public boolean isEmpty() {
    return LeftLeafNode == null;
  }

  // Search the tree for a index of a null pointer given a RecordPair object
  public static int treeSearch(RecordPair[] recordPairs) {
    for (int i = 0; i < recordPairs.length; i++) {
      if (recordPairs[i] == null) {
        return i;
      }
    }
    return -1;
  }

  // Search the tree for a index of a null pointer given a Node
  public static int treeSearch(Node[] pointers) {
    for (int i = 0; i < pointers.length; i++) {
      if (pointers[i] == null) {
        return i;
      }
    }
    return -1;
  }

  // Sorts a Record
  public void sortRecord(RecordPair[] record) {
    Arrays.sort(record, new Comparator<RecordPair>() {
      @Override
      public int compare(RecordPair o1, RecordPair o2) {
        if (o1 == null && o2 == null) {
          return 0;
        }
        if (o1 == null) {
          return 1;
        }
        if (o2 == null) {
          return -1;
        }
        return o1.compareTo(o2);
      }
    });
  }

  // Splits the pointers for a child node
  public Node[] splitChildPointers(InnerNode in, int split) {

    Node[] pointers = in.childPointers;
    Node[] halfPointers = new Node[this.numberOfPointers + 1];

    for (int i = split + 1; i < pointers.length; i++) {
      halfPointers[i - split - 1] = pointers[i];
      in.removePointer(i);
    }

    return halfPointers;
  }

  // Splits the record
  public RecordPair[] splitRecord(LeafNode ln, int split) {

    RecordPair[] record = ln.record;

    RecordPair[] halfPair = new RecordPair[this.numberOfPointers];

    for (int i = split; i < record.length; i++) {
      halfPair[i - split] = record[i];
      ln.delete(i);
    }

    return halfPair;
  }

  // Splits an inner node
  public void splitInnerNode(InnerNode in) {
    splitCount++;

    InnerNode parent = in.parent;

    int midpoint = returnMidpoint();
    String newParentKey = in.keys[midpoint];
    String[] halfKeys = splitKeys(in.keys, midpoint);
    Node[] halfPointers = splitChildPointers(in, midpoint);

    in.degree = treeSearch(in.childPointers);

    InnerNode sibling = new InnerNode(this.numberOfPointers, halfKeys, halfPointers);
    for (Node pointer : halfPointers) {
      if (pointer != null) {
        pointer.parent = sibling;
      }
    }

    sibling.rightSibling = in.rightSibling;
    if (sibling.rightSibling != null) {
      sibling.rightSibling.leftSibling = sibling;
    }
    in.rightSibling = sibling;
    sibling.leftSibling = in;

    if (parent == null) {
      parentSplitCount++;

      String[] keys = new String[this.numberOfPointers];
      keys[0] = newParentKey;
      InnerNode newRoot = new InnerNode(this.numberOfPointers, keys);
      newRoot.appendChildPointer(in);
      newRoot.appendChildPointer(sibling);
      this.root = newRoot;

      in.parent = newRoot;
      sibling.parent = newRoot;
    } else {
      parentFusionCount++;

      parent.keys[parent.degree - 1] = newParentKey;
      Arrays.sort(parent.keys, 0, parent.degree);

      int pointerIndex = parent.findIndexOfPointer(in) + 1;
      parent.insertChildPointer(sibling, pointerIndex);
      sibling.parent = parent;
    }
  }

  // Finds a key from a RecordPair object
  public int binarySearch(RecordPair[] recordPairs, int numPairs, String key) {
    Comparator<RecordPair> comparer = new Comparator<RecordPair>() {
      @Override
      public int compare(RecordPair o1, RecordPair o2) {
        return o1.key.compareTo(key);
      }
    };
    return Arrays.binarySearch(recordPairs, 0, numPairs, new RecordPair(key, null), comparer);
  }

  // Splits a key a number of times
  public String[] splitKeys(String[] keys, int split) {

    String[] halfKeys = new String[this.numberOfPointers];

    keys[split] = null;

    for (int i = split + 1; i < keys.length; i++) {
      halfKeys[i - split - 1] = keys[i];
      keys[i] = null;
    }

    return halfKeys;
  }

  // Searches for a value given a key
  public String search(String key) {
    if (isEmpty()) {
      return null;
    }

    LeafNode ln = (this.root == null) ? this.LeftLeafNode : SearchLeafNode(key);

    RecordPair[] recordPairs = ln.record;
    int index = binarySearch(recordPairs, ln.numPairs, key);

    if (index < 0) {
      return null;
    } else {
      return recordPairs[index].value;
    }
  }

  public ArrayList<String> search(String lowerBound, String upperBound) {
    ArrayList<String> values = new ArrayList<>();

    LeafNode currNode = this.LeftLeafNode;
    while (currNode != null) {
      RecordPair[] recordPairs = currNode.record;
      for (RecordPair dp : recordPairs) {
        if (dp == null) {
          break;
        }

        if (lowerBound.compareTo(dp.key) <= 0 && dp.key.compareTo(upperBound) <= 0) {
          values.add(dp.value);
        }
      }
      currNode = currNode.rightSibling;
    }

    return values;
  }

  public BPlusTree(int numberOfPointers) {
    this.numberOfPointers = numberOfPointers;
    this.root = null;
  }

  // Puts the txt file into the B+ tree
  public void buildTreeFromFile(String filePath) {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        String key = line.substring(0, 7).trim();
        String value = line.substring(8).trim();
        // Assuming values are strings, you might need to modify this based on your
        // actual use case
        insert(key, value);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Searches the leaf node for a string
  public LeafNode SearchLeafNode(String key) {
    if (isEmpty()) {
      return null;
    }

    Node child = this.root;
    while (child instanceof InnerNode) {
      InnerNode innerNode = (InnerNode) child;
      int i;
      for (i = 0; i < innerNode.degree - 1; i++) {
        if (key.compareTo(innerNode.keys[i]) < 0) {
          break;
        }
      }
      child = innerNode.childPointers[i];
    }

    return (LeafNode) child;
  }

  // Returns the next part data
  public ArrayList<RecordPair> getNextPartsWithData(String startPart, int count) {
    ArrayList<RecordPair> nextParts = new ArrayList<>();
    LeafNode startLeaf = SearchLeafNode(startPart);

    if (startLeaf == null) {
      return nextParts; // Start part not found
    }

    boolean foundStartPart = false;

    // Traverse the leaf nodes to find the starting part
    LeafNode currNode = startLeaf;
    while (currNode != null) {
      RecordPair[] recordPairs = currNode.record;

      for (RecordPair dp : recordPairs) {
        if (dp == null) {
          break;
        }

        if (foundStartPart) {
          nextParts.add(dp);

          if (nextParts.size() == count) {
            return nextParts; // Found the required number of parts
          }
        } else if (dp.key.equals(startPart)) {
          foundStartPart = true;
        }
      }

      currNode = currNode.rightSibling;
    }

    return nextParts; // Not enough parts found
  }

  public void modifyPartData(String partNumber, String newData) {
    if (isEmpty()) {
      System.out.println("The tree is empty.");
      return;
    }

    LeafNode leafNode = SearchLeafNode(partNumber);
    if (leafNode == null) {
      System.out.println("Part not found in the tree.");
      return;
    }

    RecordPair[] record = leafNode.record;
    for (int i = 0; i < record.length; i++) {
      if (record[i] != null && record[i].key.equals(partNumber)) {
        // Modify the data for the specified part number
        record[i].value = newData;

        // Update the data in the text file
        updateFile(partNumber, newData);

        System.out.println("Part data modified successfully.");
        return;
      }
    }

    System.out.println("Part not found in the tree.");
  }

  // Updates the file with the B+tree data
  public void updateFile(String partNumber, String newData) {
    // Modify the text file content
    String filePath = "Algorithms Project/src/partfile.txt";
    File file = new File(filePath);

    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      StringBuilder content = new StringBuilder();
      String line;

      while ((line = reader.readLine()) != null) {
        String key = line.substring(0, 7).trim();
        if (key.equals(partNumber)) {
          // Modify the line with the new data
          line = String.format("%-7s        %-72s", partNumber, newData);
        }
        content.append(line).append("\n");
      }

      reader.close();

      // Write the modified content back to the file
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write(content.toString());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error updating the file.");
    }
  }

  // Add a new part to the B+tree
  public static void addNewPart(Scanner scanner, String filePath) {
    System.out.print("Enter the new part ID: ");
    String newPartID = scanner.nextLine();

    System.out.print("Enter the new part data: ");
    String newPartData = scanner.nextLine();

    String newPartEntry = String.format("%-7s        %-72s", newPartID, newPartData);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
      writer.write(newPartEntry);
      writer.newLine();
      System.out.println("New part added successfully.");
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error adding the new part to the file.");
    }
  }

  // Delete a part from the B+tree
  public void deletePart(String partNumber) {
    if (isEmpty()) {
      System.out.println("The tree is empty.");
      return;
    }

    LeafNode leafNode = SearchLeafNode(partNumber);
    if (leafNode == null) {
      System.out.println("Part not found in the tree.");
      return;
    }

    RecordPair[] record = leafNode.record;
    for (int i = 0; i < record.length; i++) {
      if (record[i] != null && record[i].key.equals(partNumber)) {
        // Delete the part from the leaf node
        leafNode.delete(i);

        // Remove the corresponding line from the file
        removeLineFromFile(partNumber);

        System.out.println("Part deleted successfully.");
        return;
      }
    }

    System.out.println("Part not found in the tree.");
  }

  // Inserts a key and a value into the b+tree
  public void insert(String key, String value) {
    if (isEmpty()) {
      LeafNode ln = new LeafNode(this.numberOfPointers, new RecordPair(key, value));
      this.LeftLeafNode = ln;
    } else {
      LeafNode ln = (this.root == null) ? this.LeftLeafNode : SearchLeafNode(key);

      if (!ln.insert(new RecordPair(key, value))) {
        ln.record[ln.numPairs] = new RecordPair(key, value);
        ln.numPairs++;
        sortRecord(ln.record);

        int midpoint = returnMidpoint();
        RecordPair[] halfPair = splitRecord(ln, midpoint);

        if (ln.parent == null) {
          String[] parent_keys = new String[this.numberOfPointers];
          parent_keys[0] = halfPair[0].key;
          InnerNode parent = new InnerNode(this.numberOfPointers, parent_keys);
          ln.parent = parent;
          parent.appendChildPointer(ln);
        } else {
          String newParentKey = halfPair[0].key;
          ln.parent.keys[ln.parent.degree - 1] = newParentKey;
          Arrays.sort(ln.parent.keys, 0, ln.parent.degree, Comparator.nullsLast(Comparator.naturalOrder()));
        }

        LeafNode newLeafNode = new LeafNode(this.numberOfPointers, halfPair, ln.parent);

        int pointerIndex = ln.parent.findIndexOfPointer(ln) + 1;
        ln.parent.insertChildPointer(newLeafNode, pointerIndex);

        newLeafNode.rightSibling = ln.rightSibling;
        if (newLeafNode.rightSibling != null) {
          newLeafNode.rightSibling.leftSibling = newLeafNode;
        }
        ln.rightSibling = newLeafNode;
        newLeafNode.leftSibling = ln;

        if (this.root == null) {
          this.root = ln.parent;
        } else {
          InnerNode in = ln.parent;
          while (in != null) {
            if (in.isOverfull()) {
              splitInnerNode(in);
            } else {
              break;
            }
            in = in.parent;
          }
        }
      }
    }
  }

  // Removes a part from the file
  public void removeLineFromFile(String partNumber) {
    String filePath = "Algorithms Project/src/partfile.txt";
    File file = new File(filePath);

    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      StringBuilder content = new StringBuilder();
      String line;

      while ((line = reader.readLine()) != null) {
        String key = line.substring(0, 7).trim();
        if (!key.equals(partNumber)) {
          content.append(line).append("\n");
        }
      }

      reader.close();

      // Write the modified content back to the file
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write(content.toString());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error updating the file.");
    }
  }

  // Shows the statistics of the b+tree(number of splits, parent splits, fusions,
  // parent fusions and tree depth)
  public void displayStatistics() {
    System.out.println("Total number of splits: " + splitCount);
    System.out.println("Total number of parent splits: " + parentSplitCount);
    System.out.println("Total number of fusions: " + fusionCount);
    System.out.println("Total number of parent fusions: " + parentFusionCount);

    int treeDepth = calculateTreeDepth(root);
    System.out.println("Tree depth: " + treeDepth);
  }

  public int calculateTreeDepth(InnerNode node) {
    if (node == null) {
      return 0;
    }

    int maxChildDepth = 0;
    for (Node pointer : node.childPointers) {
      if (pointer instanceof InnerNode) {
        int childDepth = calculateTreeDepth((InnerNode) pointer);
        maxChildDepth = Math.max(maxChildDepth, childDepth);
      }
    }

    return 1 + maxChildDepth;
  }

}