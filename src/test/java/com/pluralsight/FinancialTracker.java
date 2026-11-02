package com.pluralsight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

public class FinancialTracker {
    public static void main(String[] args) {
        System.out.println("Welcome to your personal Accounting Ledger!\n");
        Scanner scan = new Scanner(System.in);
        boolean mainCondition = true;

        while(mainCondition){
            System.out.println("Home Menu: \n");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit\n");
            System.out.print("Please type the letter of your selection: ");

            String homeChoice = scan.nextLine();
            switch (homeChoice.toUpperCase()) {
                case "D":
                    addDeposit(scan);
                    break;
                case "P":
                    makePayment(scan);
                    break;
                case "L":
                    displayLedger();
                    break;
                case "X":
                    System.out.println("\nThank you, have a nice day!");
                    mainCondition = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.\n");
            }
        }
    }

    public static String getTimestamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(formatter);
        return timestamp;
    }
}
