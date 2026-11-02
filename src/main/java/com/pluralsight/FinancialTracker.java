package com.pluralsight;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
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
                System.out.print("Please enter the letter of your selection: ");

                String homeChoice = scan.nextLine();
                switch (homeChoice.toUpperCase()) {
                    case "D":
                        addDeposit(scan, writer, myLedger);
                        break;
                    case "P":
                        makePayment(scan, writer, myLedger);
                        break;
                    case "L":
                        displayLedger(scan, myLedger);
                        break;
                    case "X":
                        System.out.println("\nThank you, have a nice day!");
                        mainCondition = false;
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.\n");
                        break;
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
    public static void makePayment(Scanner scan, BufferedWriter writer, ArrayList<Transaction> myLedger){
        String timestamp = getTimestamp();

        System.out.print("\nPlease enter a description for your payment: ");
        String description = scan.nextLine();

        System.out.print("\nPlease enter the vendor: ");
        String vendor = scan.nextLine();

        System.out.print("\nPlease enter the payment amount: ");
        double amount = (scan.nextDouble()) * -1;
        scan.nextLine();

        Transaction payment = new Transaction(timestamp, description, vendor, amount);
        myLedger.add(payment);

        String csvLine = timestamp + "|" + description + "|" + vendor + "|$" + String.format("%.2f", amount);
        try {
            writer.write(csvLine);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void displayLedger(Scanner scan, ArrayList<Transaction> myLedger){
        System.out.println("\nLedger Menu: \n");
        boolean ledgerCondition = true;

        while(ledgerCondition){
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("\nPlease enter the letter of your selection: ");

            String ledgerChoice = scan.nextLine();

            switch (ledgerChoice.toUpperCase()) {
                case "A":
                    System.out.println("\nAll entries:\n");
                    for (int i = myLedger.size() - 1; i >= 0; i--) {
                        System.out.println(myLedger.get(i));
                    }
                    break;
                case "D":
                    System.out.println("\nAll Deposits:\n");
                    for (int i = myLedger.size() - 1; i >= 0; i--) {
                        if(myLedger.get(i).getAmount()>0){
                            System.out.println(myLedger.get(i));
                        }
                    }
                    break;
                case "P":
                    System.out.println("\nAll Payments:\n");
                    for (int i = myLedger.size() - 1; i >= 0; i--) {
                        if(myLedger.get(i).getAmount()<0){
                            System.out.println(myLedger.get(i));
                        }
                    }
                    break;
                case "R":
                    displayReports(myLedger, scan);
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
    public static void displayReports(ArrayList<Transaction> myLedger, Scanner scan){

    }
}
