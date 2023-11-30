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




  public int binarySearch(RecordPair[] recordPairs, int numPairs, String key) {
    Comparator<RecordPair> comparer = new Comparator<RecordPair>() {
        @Override
        public int compare(RecordPair o1, RecordPair o2) {
            return o1.key.compareTo(key);
        }
    };
    return Arrays.binarySearch(recordPairs, 0, numPairs, new RecordPair(key, null), comparer);
}

  public LeafNode findLeafNode(String key) {
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









  // Get the mid point
  public int getMidpoint() {
    return (int) Math.ceil((this.numberOfPointers + 1) / 2.0) - 1;
  }


  public boolean isEmpty() {
    return LeftLeafNode == null;
  }

  public static int linearNullSearch(RecordPair[] recordPairs) {
    for (int i = 0; i < recordPairs.length; i++) {
      if (recordPairs[i] == null) {
        return i;
      }
    }
    return -1;
  }

  public static int linearNullSearch(Node[] pointers) {
    for (int i = 0; i < pointers.length; i++) {
      if (pointers[i] == null) {
        return i;
      }
    }
    return -1;
  }


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

  public Node[] splitChildPointers(InnerNode in, int split) {

    Node[] pointers = in.childPointers;
    Node[] halfPointers = new Node[this.numberOfPointers + 1];

    for (int i = split + 1; i < pointers.length; i++) {
      halfPointers[i - split - 1] = pointers[i];
      in.removePointer(i);
    }

    return halfPointers;
  }

  public RecordPair[] splitRecord(LeafNode ln, int split) {

    RecordPair[] record = ln.record;

    RecordPair[] halfDict = new RecordPair[this.numberOfPointers];

    for (int i = split; i < record.length; i++) {
      halfDict[i - split] = record[i];
      ln.delete(i);
    }

    return halfDict;
  }

public void splitInnerNode(InnerNode in) {
        splitCount++;

        InnerNode parent = in.parent;

        int midpoint = getMidpoint();
        String newParentKey = in.keys[midpoint];
        String[] halfKeys = splitKeys(in.keys, midpoint);
        Node[] halfPointers = splitChildPointers(in, midpoint);

        in.degree = linearNullSearch(in.childPointers);

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


  public String[] splitKeys(String[] keys, int split) {

    String[] halfKeys = new String[this.numberOfPointers];

    keys[split] = null;

    for (int i = split + 1; i < keys.length; i++) {
      halfKeys[i - split - 1] = keys[i];
      keys[i] = null;
    }

    return halfKeys;
  }

  public void insert(String key, String value) {
    if (isEmpty()) {
        LeafNode ln = new LeafNode(this.numberOfPointers, new RecordPair(key, value));
        this.LeftLeafNode = ln;
    } else {
        LeafNode ln = (this.root == null) ? this.LeftLeafNode : findLeafNode(key);

        if (!ln.insert(new RecordPair(key, value))) {
            ln.record[ln.numPairs] = new RecordPair(key, value);
            ln.numPairs++;
            sortRecord(ln.record);

            int midpoint = getMidpoint();
            RecordPair[] halfDict = splitRecord(ln, midpoint);

            if (ln.parent == null) {
                String[] parent_keys = new String[this.numberOfPointers];
                parent_keys[0] = halfDict[0].key;
                InnerNode parent = new InnerNode(this.numberOfPointers, parent_keys);
                ln.parent = parent;
                parent.appendChildPointer(ln);
            } else {
                String newParentKey = halfDict[0].key;
                ln.parent.keys[ln.parent.degree - 1] = newParentKey;
                Arrays.sort(ln.parent.keys, 0, ln.parent.degree, Comparator.nullsLast(Comparator.naturalOrder()));
            }

            LeafNode newLeafNode = new LeafNode(this.numberOfPointers, halfDict, ln.parent);

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
  public String search(String key) {
    if (isEmpty()) {
      return null;
    }

    LeafNode ln = (this.root == null) ? this.LeftLeafNode : findLeafNode(key);

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



  public void buildTreeFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String key = line.substring(0, 7).trim();
                String value = line.substring(8).trim();
                // Assuming values are strings, you might need to modify this based on your actual use case
                insert(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RecordPair> getNextPartsWithData(String startPart, int count) {
      ArrayList<RecordPair> nextParts = new ArrayList<>();
      LeafNode startLeaf = findLeafNode(startPart);

      if (startLeaf == null) {
          return nextParts;  // Start part not found
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
                      return nextParts;  // Found the required number of parts
                  }
              } else if (dp.key.equals(startPart)) {
                  foundStartPart = true;
              }
          }

          currNode = currNode.rightSibling;
      }

      return nextParts;  // Not enough parts found
  }

  public void modifyPartData(String partNumber, String newData) {
    if (isEmpty()) {
        System.out.println("The tree is empty.");
        return;
    }

    LeafNode leafNode = findLeafNode(partNumber);
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



public void deletePart(String partNumber) {
  if (isEmpty()) {
      System.out.println("The tree is empty.");
      return;
  }

  LeafNode leafNode = findLeafNode(partNumber);
  if (leafNode == null) {
      System.out.println("Part not found in the tree.");
      return;
  }

  RecordPair[] record = leafNode.record;
  for (int i = 0; i < record.length; i++) {
      if (record[i] != null && record[i].key.equals(partNumber)) {
          // Delete the part from the leaf node
          leafNode.delete(i);

          // If the leaf node becomes deficient, handle deficiency
          if (leafNode.isDeficient()) {
              handleLeafDeficiency(leafNode);
          }

          // Remove the corresponding line from the file
          removeLineFromFile(partNumber);

          System.out.println("Part deleted successfully.");
          return;
      }
  }

  System.out.println("Part not found in the tree.");
}

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
public void handleLeafDeficiency(LeafNode ln) {
  LeafNode sibling;

  if (ln.leftSibling != null && ln.leftSibling.isLendable()) {
      sibling = ln.leftSibling;
      // Perform necessary actions for lending
  } else if (ln.rightSibling != null && ln.rightSibling.isLendable()) {
      sibling = ln.rightSibling;
      // Perform necessary actions for lending
  } else if (ln.leftSibling != null && ln.leftSibling.isMergeable()) {
      sibling = ln.leftSibling;
      // Perform necessary actions for merging
  } else if (ln.rightSibling != null && ln.rightSibling.isMergeable()) {
      sibling = ln.rightSibling;
      // Perform necessary actions for merging
  }

  // Perform any additional actions based on the chosen strategy
}














































}