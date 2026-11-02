This repo is a small Java-based personal accounting ledger app, built as a single Maven project.

High-level overview:
- It is a console application, not a web app or service.
- The project is packaged under com.pluralsight and uses Java 17.
- The main entry point is src/main/java/com/pluralsight/FinancialTracker.java.
- The app presents a simple menu for:
  - adding a deposit
  - making a payment
  - viewing the ledger
  - exiting
- The data model is a Transaction object in src/main/java/com/pluralsight/Transaction.java.
- Transactions are written to a CSV file named transactions.csv using a pipe-delimited format like:
  - timestamp|description|vendor|amount

What it appears to do:
- It records financial activity as a personal ledger.
- When a deposit is added, it grabs the current timestamp, asks for a description, vendor, and amount, then appends it to the CSV.
- The repository looks like an early/in-progress project: some menu options and ledger/payment features are stubbed out or commented, while the deposit flow is implemented.

Project structure:
- pom.xml: Maven configuration
- src/main/java/com/pluralsight/FinancialTracker.java: CLI app logic
- src/main/java/com/pluralsight/Transaction.java: transaction object
- transactions.csv: local ledger data file

Overall, this is a compact learning/project-style app for tracking personal account activity rather than a production accounting system.
