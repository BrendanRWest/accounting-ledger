package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class FinancialTracker {

    // Private variables Raymond provided

    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    public static void main(String[] args) {
        System.out.println("\nWelcome to your Personal Accounting Ledger!");
        Scanner scan = new Scanner(System.in);


        // Load + sort the csv file
        loadTransactions(FILE_NAME);
        sortTransactions();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {

            boolean mainCondition = true;


            // Home menu
            while(mainCondition){
                System.out.println("\nHome Menu: \n");
                System.out.println("D) Add Deposit");
                System.out.println("P) Make Payment (Debit)");
                System.out.println("L) Ledger");
                System.out.println("X) Exit\n");
                System.out.print("Please enter the letter of your selection: ");

                String homeChoice = scan.nextLine();
                switch (homeChoice.toUpperCase()) {
                    case "D":
                        addDeposit(scan, writer);
                        break;
                    case "P":
                        makePayment(scan, writer);
                        break;
                    case "L":
                        displayLedger(scan);
                        break;
                    case "X":
                        System.out.println("\nThank you, have a nice day!");
                        mainCondition = false;
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.\n");  // User input validation
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    // Method to read the csv file and load it into the arraylist
    public static void loadTransactions(String fileName) {

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(fileName))) {

            String csvLine;

            while ((csvLine = reader.readLine()) != null) {

                String[] tokens = csvLine.split("\\|", -1);

                // If the line doesn't match the format, skip to the next one
                if (tokens.length != 5) {
                    System.out.println("Skipping invalid transaction: " + csvLine);
                    continue;
                }

                // Creating an object for each line
                try {
                    LocalDate date = LocalDate.parse(tokens[0], DATE_FMT);
                    LocalTime time = LocalTime.parse(tokens[1], TIME_FMT);
                    String description = tokens[2];
                    String vendor = tokens[3];
                    double amount = Double.parseDouble(tokens[4]);

                    Transaction transaction = new Transaction(
                            date, time, description, vendor, amount
                    );

                    // Storing the object in the ArrayList
                    transactions.add(transaction);

                } catch (RuntimeException e) {
                    System.out.println("Skipping invalid transaction: " + csvLine);
                }
            }

            // If no file is found, have the writer create one
        } catch (FileNotFoundException e) {
            try (BufferedWriter writer =
                         new BufferedWriter(new FileWriter(fileName))) {

                System.out.println("Created a new transactions file.");

            } catch (IOException ex) {
                System.out.println("Error creating file: " + ex.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }
    }

    public static void sortTransactions() {

        // Not sure if Raymond taught this or not, but when I looked it up,
        // this seemed like the best practice for sorting.
        transactions.sort(
                Comparator.comparing(Transaction::getDate)
                        .thenComparing(Transaction::getTime)
                        .reversed()
        );
    }

    // Created my own method for taking user input for Date + Time and storing it
    public static LocalDateTime readDateTime(Scanner scan) {

        // Runs until the LocalDateTime returned is in the valid format
        // The return breaks out of the while loop so no condition variable needed
        while (true) {
            System.out.print("Enter date and time (yyyy-MM-dd HH:mm:ss): ");

            String input = scan.nextLine();

            try {
                return LocalDateTime.parse(input, DATETIME_FMT);

            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid date/time. Please try again.");
            }
        }
    }

    public static void addDeposit(Scanner scan, BufferedWriter writer) {
        LocalDateTime dateTime = readDateTime(scan);

        // Extracting LocalDate object out of the LocalDateTime object
        LocalDate date = dateTime.toLocalDate();
        // Extracting LocalTime object out of the LocalDateTime object
        LocalTime time = dateTime.toLocalTime();

        System.out.print("\nPlease enter a description for your deposit: ");
        String description = scan.nextLine();

        System.out.print("\nPlease enter the vendor: ");
        String vendor = scan.nextLine();

        double amount;

        while (true) {
            System.out.print("\nPlease enter the deposit amount: ");

            try {
                amount = Double.parseDouble(scan.nextLine());
                // If the amount is positive and a real number, it is a deposit and is stored
                if (Double.isFinite(amount) && amount > 0) {
                    break;
                }

                System.out.println("Amount must be greater than zero.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a number.");
            }
        }
        // Create the deposit object
        Transaction deposit = new Transaction(date, time, description, vendor, amount);

        String csvLine = deposit.toString();

        // Write the new deposit to the csv file
        try {
            writer.write(csvLine);
            writer.newLine();
            writer.flush();
            // store the deposit in the list
            transactions.add(deposit);
            System.out.println("Deposit successful");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Resort the array list
        sortTransactions();
    }

    public static void makePayment(Scanner scan, BufferedWriter writer){
        LocalDateTime dateTime = readDateTime(scan);

        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        System.out.print("\nPlease enter a description for your payment: ");
        String description = scan.nextLine();

        System.out.print("\nPlease enter the vendor: ");
        String vendor = scan.nextLine();

        double amount;

        while (true) {
            System.out.print("\nPlease enter the payment amount: ");

            try {
                amount = Double.parseDouble(scan.nextLine());
                // If the amount is a real number, it is stored (we'll handle the negative aspect later)
                if (Double.isFinite(amount) && amount > 0) {
                    break;
                }

                System.out.println("Amount must be greater than zero.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a number.");
            }
        }
        // Make the amount negative as it is a payment
        amount = -amount;

        // Create the payment object
        Transaction payment = new Transaction(date, time, description, vendor, amount);

        String csvLine = payment.toString();
        // Write the payment to the csv file
        try {
            writer.write(csvLine);
            writer.newLine();
            writer.flush();

            transactions.add(payment);
            System.out.println("Payment successful");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sortTransactions();
    }

    public static void displayLedger(Scanner scan){
        System.out.println("\nLedger Menu: \n");
        boolean ledgerCondition = true;

        // Ledger menu
        while(ledgerCondition){
            System.out.println("\nA) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("\nPlease enter the letter of your selection: ");

            String ledgerChoice = scan.nextLine();

            // Using user input to direct the program
            switch (ledgerChoice.toUpperCase()) {
                case "A":
                    displayAll();
                    break;
                case "D":
                    displayDeposits();
                    break;
                case "P":
                    displayPayments();
                    break;
                case "R":
                    displayReports(scan);
                    break;
                case "H":
                    ledgerCondition = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        }
    }

    // Iterate through the array list and print each transaction
    public static void displayAll() {
        System.out.println("\nAll Transactions:\n");

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    // Iterate through the array list and print each transaction that is positive (deposits)
    public static void displayDeposits() {
        System.out.println("\nAll Deposits:\n");

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                System.out.println(transaction);
            }
        }
    }

    // Iterate through the array list and print each transaction that is negative (payments)
    public static void displayPayments() {
        System.out.println("\nAll Payments:\n");

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.println(transaction);
            }
        }
    }

    // Reports menu
    public static void displayReports(Scanner scan){
        System.out.println("\nReports Menu:\n");
        boolean reportsCondition = true;

        // Using a string here to make user input validation simpler in the switch case
        // (Anything other than 0-6 --including strings of letters-- goes to the default case)
        String reportsChoice;

        // Use user input to direct the program for report type choice
        while(reportsCondition){
            System.out.println("\n1) Month to Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year to Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");
            System.out.print("Please enter the number of your selection: ");
            reportsChoice = scan.nextLine().trim();

            switch (reportsChoice) {
                case "1":
                    monthToDate();
                    break;
                case "2":
                    previousMonth();
                    break;
                case "3":
                    yearToDate();
                    break;
                case "4":
                    previousYear();
                    break;
                case "5":
                    searchByVendor(scan);
                    break;
                case "6":
                    customSearch(scan);
                    break;
                case "0":
                    reportsCondition = false;
                    break;
                default:
                    System.out.println("\nInvalid selection, please try again.\n");
                    break;
            }

        }
    }

    public static void monthToDate(){
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        System.out.println("\nMonth to Date Report:\n");
        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getDate();

            int transactionMonth = transactionDate.getMonthValue();
            int transactionYear = transactionDate.getYear();

            // Comparing current month and year to transaction month and year
            // Ensuring that it isn't just the same month, but the same month of the CURRENT YEAR
            // Also checking to make sure the transaction date isn't after the current date
            if (month == transactionMonth
                    && year == transactionYear
                    && !transactionDate.isAfter(today)) {
                System.out.println(transaction);
            }

        }
    }

    public static void previousMonth() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        int previousMonth = lastMonth.getMonthValue();
        int year = lastMonth.getYear();

        System.out.println("\nPrevious Month Report:\n");

        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getDate();

            int transactionMonth = transactionDate.getMonthValue();
            int transactionYear = transactionDate.getYear();

            // Comparing the previous month and current year to the transaction month and year
            // Print out transaction if it's in the previous month and current year
            if (previousMonth == transactionMonth && year == transactionYear) {
                System.out.println(transaction);
            }
        }
    }

    public static void yearToDate(){
        LocalDate today = LocalDate.now();
        int year = today.getYear();

        System.out.println("\nYear To Date Report:\n");
        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getDate();

            int transactionYear = transactionDate.getYear();

            // Comparing the current year and transaction year
            // print out transaction if it's from this year and isn't after the current date
            if (year == transactionYear && !transactionDate.isAfter(today)) {
                System.out.println(transaction);
            }
        }
    }

    public static void previousYear() {
        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1;

        System.out.println("\nPrevious Year Report:\n");

        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getDate();

            // Compare the previous year to the transaction year
            // Print out the transaction if it's from the previous year
            if (transactionDate.getYear() == previousYear) {
                System.out.println(transaction);
            }
        }
    }

    public static void searchByVendor(Scanner scan) {
        System.out.println("\nSearch By Vendor:\n");
        System.out.print("Please enter the vendor you'd like to filter by: ");

        String vendorInput = scan.nextLine();

        // Comparing the user vendor input to the transaction vendor and print if they match
        for (Transaction transaction : transactions) {
            if (vendorInput.equalsIgnoreCase(transaction.getVendor())) {
                System.out.println(transaction);
            }
        }
    }

    public static void customSearch(Scanner scan) {

        // Ask for, accept, and store user input for each element
        System.out.println("\nCustom Transaction Search\n");
        System.out.println("Leave any field blank to ignore that filter.");

        System.out.print("Start date (yyyy-MM-dd): ");
        String startInput = scan.nextLine().trim();

        System.out.print("End date (yyyy-MM-dd): ");
        String endInput = scan.nextLine().trim();

        System.out.print("Description: ");
        String descriptionInput = scan.nextLine().trim();

        System.out.print("Vendor: ");
        String vendorInput = scan.nextLine().trim();

        System.out.print("Exact amount: ");
        String amountInput = scan.nextLine().trim();

        // Set non string inputs to null initially since .isEmpty() won't work on them
        LocalDate startDate = null;
        LocalDate endDate = null;
        Double amount = null;

        try {
            // If start input or end input aren't left blank + aren't invalid , parse and store them
            if (!startInput.isEmpty()) {
                startDate = LocalDate.parse(startInput, DATE_FMT);
            }

            if (!endInput.isEmpty()) {
                endDate = LocalDate.parse(endInput, DATE_FMT);
            }

            // If amount input isn't left blank and isn't invalid, parse and store it
            if (!amountInput.isEmpty()) {
                amount = Double.parseDouble(amountInput);

                if (!Double.isFinite(amount)) {
                    System.out.println("Invalid amount.");
                    return;
                }
            }

        } catch (RuntimeException e) {
            System.out.println("Invalid date or amount. Please try again.");
            return;
        }
        // Check to ensure that the start date is before the end date
        if (startDate != null && endDate != null
                && startDate.isAfter(endDate)) {
            System.out.println("Start date cannot be after end date.");
            return;
        }

        System.out.println("\nSearch Results:\n");

        boolean found = false;

        for (Transaction transaction : transactions) {

            // Here we begin the If Statement Gauntlet muahahaha
            // This truly is not as complex as it may look

            // Each statement will include != null or !.isEmpty() before checking the other condition

            /* If at any point the user's input for a certain parameter was left blank,
            the if statement condition is false off the bat, it doesn't run, and we move
            to the next if statement.
             */

            /* If the transaction date is before the start date, it's outside of the user's
            search parameters and we 'continue' to the next transaction object. Otherwise,
            we move to the next if statement
             */
            if (startDate != null
                    && transaction.getDate().isBefore(startDate)) {
                continue;
            }

            /* If the transaction date is after the end date, it's outside of the user's search parameters
            and we 'continue' to the next transaction object. Otherwise, we move to the next
            if statement
             */
            if (endDate != null
                    && transaction.getDate().isAfter(endDate)) {
                continue;
            }

            /* If the description input is not contained within the transaction's description
            we 'continue' to the next transaction object. Otherwise, we keep on moving along
            to the next if statement
             */
            if (!descriptionInput.isEmpty()
                    && !transaction.getDescription().toLowerCase()
                    .contains(descriptionInput.toLowerCase())) {
                continue;
            }

            /* If the vendor input is not contained within the transaction's vendor
            we 'continue' to the next transaction object. Otherwise, we keep on moving along
            to the next if statement
             */
            if (!vendorInput.isEmpty()
                    && !transaction.getVendor().toLowerCase()
                    .contains(vendorInput.toLowerCase())) {
                continue;
            }

            /* If the amount input doesn't equal the transaction's amount,
            we 'continue' to the next transaction object. Otherwise, we keep moving along
             */
            if (amount != null
                    // .compare returns 0 if they are equal
                    && Double.compare(transaction.getAmount(), amount) != 0) {
                continue;
            }

            /* Phew, we made it through the if statement gauntlet without a single one running!
            That means that the current transaction object fit into all of the user's
            search parameters. Let's print it!!! Afterwards, we move to our next transaction until
            we've made it through the whole list
             */
            System.out.println(transaction);
            found = true;
        }

        if (!found) {
            System.out.println("No matching transactions found.");
        }
    }

}
