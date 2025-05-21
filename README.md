# CodingTaskAlephys

**JAVA Expense Tracker**
A simple command-line expense tracker application written in Java that allows users to:
1. Add income and expenses
2. Categorize transactions
3. View monthly summaries
4. Save/load data from files

#Features:-
1. Transaction Management: Add income and expenses with categories, dates, and descriptions
2. Categorization: Predefined categories for both income and expenses
3. Monthly Summaries: View detailed monthly financial reports
4. Data Persistence: Save and load transaction data automatically
5. Import Functionality: Import transactions from CSV files

#How to Run
1. Compile the Java file:
javac ExpenseTracker.java
2. Run the application:
java ExpenseTracker
3. Data File Format : The application uses CSV files to store and import transactions.
4. The format is: TYPE,CATEGORY,AMOUNT,DATE,DESCRIPTION Where:
TYPE: Either "INCOME" or "EXPENSE"
CATEGORY: One of the predefined categories
AMOUNT: Numeric amount (e.g., 100.50)
DATE: Date in format YYYY-MM-DD
DESCRIPTION: Optional description of the transaction
5. Sample Import File
A sample import file sample import.csv file using option 5 in the main menu.
is included to demonstrate the format. You can import this
