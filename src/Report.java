import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;

public class Report {
    // keep all transactions here
    private final ArrayList<Transaction> transactions = new ArrayList<>();

    public Report() {
    }



    public Report(ArrayList<Transaction> transactions) {

        setTransactions(transactions);

    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions.clear();
        if (transactions != null) {

            // just copy the list
            this.transactions.addAll(transactions);
        }
    }

    public ArrayList<Transaction> getTransactionsInRange(String startDate, String endDate) {
        return getTransactionsInRange(parseDate(startDate), parseDate(endDate));
    }


    public ArrayList<Transaction> getTransactionsInRange(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);



        ArrayList<Transaction> transactionsInRange = new ArrayList<>();

        for (Transaction transaction : getSortedTransactions()) {

            LocalDate transactionDate = parseDate(transaction.getDate());
            if (!transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate)) {
                transactionsInRange.add(transaction);
            }
        }
        return transactionsInRange;
    }

    public String generateReport(String startDate, String endDate) {
        return generateReport(parseDate(startDate), parseDate(endDate));

    }





    public String generateReport(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);

        ArrayList<Transaction> transactionsInRange = getTransactionsInRange(startDate, endDate);
        // balance before the report starts
        double openingBalance = getOpeningBalance(startDate);
        double runningBalance = openingBalance;
        double totalIncome = 0.0;
        double totalExpenses = 0.0;



        // build the report as text

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Finance Tracker Statement").append(System.lineSeparator());
        reportBuilder.append("Period: ").append(startDate).append(" to ").append(endDate).append(System.lineSeparator());
        reportBuilder.append("Opening balance: ").append(formatCurrency(openingBalance)).append(System.lineSeparator());
        reportBuilder.append(System.lineSeparator());

        if (transactionsInRange.isEmpty()) {
            reportBuilder.append("No transactions found in the selected timeframe.").append(System.lineSeparator());


        } else {
            reportBuilder.append(String.format("%-12s %-10s %-18s %12s %12s%n",
                    "Date", "Type", "Category", "Amount", "Balance"));
            reportBuilder.append("--------------------------------------------------------------------")
                    .append(System.lineSeparator());

            for (Transaction transaction : transactionsInRange) {
                double signedAmount = getSignedAmount(transaction);
                runningBalance += signedAmount;

                // basic totals
                if ("income".equalsIgnoreCase(transaction.getType())) {
                    totalIncome += transaction.getAmount();
                } else {
                    totalExpenses += transaction.getAmount();
                }

                reportBuilder.append(String.format("%-12s %-10s %-18s %12s %12s%n",
                        transaction.getDate(),
                        transaction.getType(),

                        transaction.getCategory(),
                        formatSignedCurrency(signedAmount),
                        formatCurrency(runningBalance)));
            }
        }

        reportBuilder.append(System.lineSeparator());
        reportBuilder.append("Total income: ").append(formatCurrency(totalIncome)).append(System.lineSeparator());
        reportBuilder.append("Total expenses: ").append(formatCurrency(totalExpenses)).append(System.lineSeparator());
        reportBuilder.append("Net change: ").append(formatCurrency(totalIncome - totalExpenses)).append(System.lineSeparator());
        reportBuilder.append("Closing balance: ").append(formatCurrency(runningBalance)).append(System.lineSeparator());

        return reportBuilder.toString();
    }

    public Path exportReport(String startDate, String endDate) throws IOException {
        LocalDate parsedStartDate = parseDate(startDate);
        LocalDate parsedEndDate = parseDate(endDate);
        Path outputPath = Paths.get(String.format("report_%s_to_%s.txt", parsedStartDate, parsedEndDate));
        return exportReport(parsedStartDate, parsedEndDate, outputPath.toString());
    }

    public Path exportReport(String startDate, String endDate, String outputFilePath) throws IOException {
        return exportReport(parseDate(startDate), parseDate(endDate), outputFilePath);
    }

    public Path exportReport(LocalDate startDate, LocalDate endDate, String outputFilePath) throws IOException {
        validateRange(startDate, endDate);

        Path outputPath = Paths.get(outputFilePath).toAbsolutePath();
        Path parentPath = outputPath.getParent();
        if (parentPath != null) {
            Files.createDirectories(parentPath);
        }

        // write it out
        Files.writeString(outputPath, generateReport(startDate, endDate));

        return outputPath;
    }

    private double getOpeningBalance(LocalDate startDate) {
        double balance = 0.0;
        for (Transaction transaction : getSortedTransactions()) {
            if (parseDate(transaction.getDate()).isBefore(startDate)) {
                // add older stuff first
                balance += getSignedAmount(transaction);
            }
        }
        return balance;
    }

    private ArrayList<Transaction> getSortedTransactions() {
        ArrayList<Transaction> sortedTransactions = new ArrayList<>(transactions);
        // easier if the dates are in order
        sortedTransactions.sort(Comparator.comparing(transaction -> parseDate(transaction.getDate())));
        return sortedTransactions;
    }

    private double getSignedAmount(Transaction transaction) {

        
        if ("income".equalsIgnoreCase(transaction.getType())) {
            return transaction.getAmount();
        }
        return -transaction.getAmount();
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Dates must use YYYY-MM-DD format: " + date, exception);
        }
    }

    private void validateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be on or before end date.");
        }
    }

    private String formatCurrency(double amount) {
        return String.format("$%,.2f", amount);
    }

    private String formatSignedCurrency(double amount) {
        return (amount >= 0 ? "+" : "-") + formatCurrency(Math.abs(amount));
    }
}
