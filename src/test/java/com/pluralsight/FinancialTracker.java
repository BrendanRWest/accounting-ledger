package com.pluralsight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class FinancialTracker {
    public static void main(String[] args) {
        System.out.println("Welcome to your personal Accounting Ledger!\n");
        Scanner scan = new Scanner(System.in);
        boolean mainCondition = true;

        while(mainCondition){

        }
    }

    public static String getTimestamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(formatter);
        return timestamp;
    }
}
