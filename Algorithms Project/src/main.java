import java.util.ArrayList;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        BPlusTree bpt = new BPlusTree(3);

        // Create path to file
        String filePath = "Algorithms Project/src/partfile.txt";
        bpt.buildTreeFromFile(filePath);

        Scanner scanner = new Scanner(System.in);

        boolean exit = false;
        boolean saveChanges = false;

        while (!exit) {
            System.out.println("1. Search for a part");
            System.out.println("2. Display next 10 parts");
            System.out.println("3. Modify part data");
            System.out.println("4. Add a new part");
            System.out.println("5. Delete a part");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");

            int choice = -1;
            while (true) {
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                    if (choice >= 1 && choice <= 6) {
                        break;
                    } else {
                        System.out.println("Please enter a number between 1 and 6.");
                        System.out.print("Enter your choice: ");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    System.out.print("Enter your choice: ");
                }
            }

            switch (choice) {

                case 1:
                    // Search for a part
                    System.out.print("Enter the part number to search: ");
                    String searchKey = scanner.nextLine();
                    String result = bpt.search(searchKey);
                    if (result != null) {
                        System.out.println("Found: " + result);
                    } else {
                        System.out.println("Not Found");
                    }
                    break;

                case 2:
                    // Display next 10 parts
                    System.out.print("Enter the starting part number: ");
                    String startPart = scanner.nextLine();
                    int count = 10;
                    ArrayList<RecordPair> nextPartsWithData = bpt.getNextPartsWithData(startPart, count);
                    if (!nextPartsWithData.isEmpty()) {
                        System.out.println("Next " + count + " parts with data:");
                        for (RecordPair dp : nextPartsWithData) {
                            System.out.println("Part: " + dp.key + ", Data: " + dp.value);
                        }
                    } else {
                        System.out.println("No next parts found.");
                    }
                    break;

                case 3:
                    // Modify part data
                    System.out.print("Enter the part number to modify: ");
                    String partNumber = scanner.nextLine();
                    System.out.print("Enter the new data: ");
                    String newData = scanner.nextLine();

                    // Modify part data in-memory
                    bpt.modifyPartData(partNumber, newData);
                    System.out.println("Part data modified in-memory. Save changes before exit.");
                    break;
                case 4:
                    // Add a new part
                    BPlusTree.addNewPart(scanner, filePath);
                    saveChanges = true; // Mark changes for saving
                    break;

                case 5:
                    // Delete a part
                    System.out.print("Enter the part number to delete: ");
                    String partToDelete = scanner.nextLine();
                    bpt.deletePart(partToDelete);
                    saveChanges = true; // Mark changes for saving
                    break;

                case 6:
                    // Exit prompt
                    System.out.print("Do you want to save changes before exiting? (yes/no): ");
                    String saveChangesInput = scanner.nextLine().toLowerCase();
                    if (saveChangesInput.equals("yes")) {
                        // Save changes to the file
                        System.out.println("Changes saved to the file.");
                    } else {
                        System.out.println("Changes not saved.");
                    }
                    exit = true;
                    bpt.displayStatistics();
                    break;

            }
        }

        scanner.close();
    }
}