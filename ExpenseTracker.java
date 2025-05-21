import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ExpenseTracker {
    private static List<Transaction> transactions = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DATA_FILE = "expense_data.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Define transaction categories
    private static final List<String> INCOME_CATEGORIES = Arrays.asList("Salary", "Business", "Investment", "Gift", "Other");
    private static final List<String> EXPENSE_CATEGORIES = Arrays.asList("Food", "Rent", "Travel", "Utilities", "Entertainment", "Shopping", "Healthcare", "Education", "Other");

    public static void main(String[] args) {
        loadData();

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getUserChoice(1, 6);

            switch (choice) {
                case 1:
                    addTransaction("INCOME");
                    break;
                case 2:
                    addTransaction("EXPENSE");
                    break;
                case 3:
                    viewAllTransactions();
                    break;
                case 4:
                    viewMonthlySummary();
                    break;
                case 5:
                    importFromFile();
                    break;
                case 6:
                    saveData();
                    running = false;
                    break;
            }
        }

        System.out.println("Thank you for using the Expense Tracker!");
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n===== EXPENSE TRACKER =====");
        System.out.println("1. Add Income");
        System.out.println("2. Add Expense");
        System.out.println("3. View All Transactions");
        System.out.println("4. View Monthly Summary");
        System.out.println("5. Import Transactions from File");
        System.out.println("6. Exit");
        System.out.print("Enter your choice (1-6): ");
    }

    private static int getUserChoice(int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < min || choice > max) {
                    System.out.print("Please enter a valid choice (" + min + "-" + max + "): ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
        return choice;
    }

    private static void addTransaction(String type) {
        System.out.println("\n===== Add " + type + " =====");

        List<String> categories = type.equals("INCOME") ? INCOME_CATEGORIES : EXPENSE_CATEGORIES;
        
        // Display categories
        System.out.println("Choose a category:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        
        int categoryChoice = getUserChoice(1, categories.size());
        String category = categories.get(categoryChoice - 1);
        
        System.out.print("Enter amount: ");
        double amount = getValidAmount();
        
        System.out.print("Enter date (yyyy-MM-dd) or press Enter for today: ");
        LocalDate date = getValidDate();

        System.out.print("Enter description (optional): ");
        String description = scanner.nextLine();

        Transaction transaction = new Transaction(
                type, category, amount, date, description
        );
        
        transactions.add(transaction);
        System.out.println(type + " added successfully!");
        saveData();
    }

    private static double getValidAmount() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid amount: ");
            }
        }
    }

    private static LocalDate getValidDate() {
        String input = scanner.nextLine();
        if (input.isEmpty()) {
            return LocalDate.now();
        }
        
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Using today's date.");
            return LocalDate.now();
        }
    }

    private static void viewAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\n===== ALL TRANSACTIONS =====");
        System.out.printf("%-15s %-15s %-15s %-12s %-30s\n", 
                "DATE", "TYPE", "CATEGORY", "AMOUNT", "DESCRIPTION");
        System.out.println("-------------------------------------------------------------------------");
        
        for (Transaction transaction : transactions) {
            System.out.printf("%-15s %-15s %-15s ₹%-11.2f %-30s\n",
                    transaction.getDate().format(DATE_FORMATTER),
                    transaction.getType(),
                    transaction.getCategory(),
                    transaction.getAmount(),
                    transaction.getDescription());
        }
    }

    private static void viewMonthlySummary() {
        System.out.print("Enter year (e.g., 2025): ");
        int year = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Enter month (1-12): ");
        int month = getUserChoice(1, 12);
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        double totalIncome = 0;
        double totalExpense = 0;
        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();
        
        // Initialize categories with zero amounts
        for (String category : INCOME_CATEGORIES) {
            incomeByCategory.put(category, 0.0);
        }
        for (String category : EXPENSE_CATEGORIES) {
            expenseByCategory.put(category, 0.0);
        }
        
        // Calculate totals
        for (Transaction transaction : transactions) {
            if (transaction.getDate().isAfter(startDate.minusDays(1)) && 
                transaction.getDate().isBefore(endDate.plusDays(1))) {
                
                if (transaction.getType().equals("INCOME")) {
                    totalIncome += transaction.getAmount();
                    incomeByCategory.put(transaction.getCategory(), 
                                        incomeByCategory.get(transaction.getCategory()) + transaction.getAmount());
                } else {
                    totalExpense += transaction.getAmount();
                    expenseByCategory.put(transaction.getCategory(), 
                                        expenseByCategory.get(transaction.getCategory()) + transaction.getAmount());
                }
            }
        }
        
        // Display summary
        System.out.println("\n===== MONTHLY SUMMARY: " + Month.of(month) + " " + year + " =====");
        System.out.println("Total Income:  ₹" + String.format("%.2f", totalIncome));
        System.out.println("Total Expense: ₹" + String.format("%.2f", totalExpense));
        System.out.println("Balance:       ₹" + String.format("%.2f", totalIncome - totalExpense));
        
        System.out.println("\n----- Income Breakdown -----");
        for (Map.Entry<String, Double> entry : incomeByCategory.entrySet()) {
            if (entry.getValue() > 0) {
                System.out.printf("%-15s ₹%.2f\n", entry.getKey() + ":", entry.getValue());
            }
        }
        
        System.out.println("\n----- Expense Breakdown -----");
        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            if (entry.getValue() > 0) {
                System.out.printf("%-15s ₹%.2f\n", entry.getKey() + ":", entry.getValue());
            }
        }
    }

    private static void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            // Write header
            writer.println("TYPE,CATEGORY,AMOUNT,DATE,DESCRIPTION");
            
            // Write data
            for (Transaction transaction : transactions) {
                writer.println(
                    transaction.getType() + "," +
                    transaction.getCategory() + "," +
                    transaction.getAmount() + "," +
                    transaction.getDate().format(DATE_FORMATTER) + "," +
                    transaction.getDescription().replace(",", ";;") // Replace commas in description
                );
            }
            
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String type = parts[0];
                    String category = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    LocalDate date = LocalDate.parse(parts[3], DATE_FORMATTER);
                    
                    // Handle description (might contain commas)
                    String description = parts.length > 4 ? parts[4] : "";
                    // Reconstruct description if it contained commas
                    for (int i = 5; i < parts.length; i++) {
                        description += "," + parts[i];
                    }
                    description = description.replace(";;", ","); // Replace back our special sequence
                    
                    transactions.add(new Transaction(type, category, amount, date, description));
                }
            }
            
            System.out.println("Loaded " + transactions.size() + " transactions.");
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    private static void importFromFile() {
        System.out.print("Enter the path to the import file: ");
        String filePath = scanner.nextLine();
        
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found.");
            return;
        }
        
        int importCount = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Assume first line is header
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String type = parts[0].toUpperCase();
                        if (!type.equals("INCOME") && !type.equals("EXPENSE")) {
                            System.out.println("Skipping invalid transaction type: " + type);
                            continue;
                        }
                        
                        String category = parts[1];
                        double amount = Double.parseDouble(parts[2]);
                        LocalDate date = LocalDate.parse(parts[3], DATE_FORMATTER);
                        
                        // Handle description (might contain commas)
                        String description = parts.length > 4 ? parts[4] : "";
                        // Reconstruct description if it contained commas
                        for (int i = 5; i < parts.length; i++) {
                            description += "," + parts[i];
                        }
                        description = description.replace(";;", ","); // Replace back our special sequence
                        
                        transactions.add(new Transaction(type, category, amount, date, description));
                        importCount++;
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing line: " + line);
                }
            }
            
            System.out.println("Successfully imported " + importCount + " transactions.");
            saveData();
        } catch (IOException e) {
            System.out.println("Error importing data: " + e.getMessage());
        }
    }
}

class Transaction {
    private String type; // INCOME or EXPENSE
    private String category;
    private double amount;
    private LocalDate date;
    private String description;
    
    public Transaction(String type, String category, double amount, LocalDate date, String description) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public String getCategory() {
        return category;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public String getDescription() {
        return description;
    }
}
