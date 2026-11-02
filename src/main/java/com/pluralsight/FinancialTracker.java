package com.pluralsight;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class FinancialTracker {
    public static void main(String[] args) {
        System.out.println("Welcome to your Personal Accounting Ledger!\n");
        Scanner scan = new Scanner(System.in);
        ArrayList<Transaction> myLedger = new ArrayList<>();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true));

            boolean mainCondition = true;

            while(mainCondition){
                System.out.println("\nHome Menu: \n");
                System.out.println("D) Add Deposit");
                System.out.println("P) Make Payment (Debit)");
                System.out.println("L) Ledger");
                System.out.println("X) Exit\n");
                System.out.print("Please type the letter of your selection: ");

                String homeChoice = scan.nextLine();
                switch (homeChoice.toUpperCase()) {
                    case "D":
                        addDeposit(scan, writer, myLedger);
                        break;
                    case "P":
                        //makePayment(scan, writer, myLedger);
                        break;
                    case "L":
                        //displayLedger();
                        break;
                    case "X":
                        System.out.println("\nThank you, have a nice day!");
                        mainCondition = false;
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getTimestamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(formatter);
        return timestamp;
    }

    public static void addDeposit(Scanner scan, BufferedWriter writer, ArrayList<Transaction> myLedger) {
        String timestamp = getTimestamp();

        System.out.print("\nPlease enter a description for your deposit: ");
        String description = scan.nextLine();

        System.out.print("\nPlease enter the vendor: ");
        String vendor = scan.nextLine();

        System.out.print("\nPlease enter the deposit amount: ");
        double amount = scan.nextDouble();
        scan.nextLine();

        Transaction deposit = new Transaction(timestamp, description, vendor, amount);
        myLedger.add(deposit);

        String csvLine = timestamp + "|" + description + "|" + vendor + "|$" + String.format("%.2f", amount);
        try {
            writer.write(csvLine);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
