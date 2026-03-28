import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class Graph extends JPanel {
    private static final String SERIES_NAME = "Balance";

    // just keep the transactions here
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    private final XYChart chart;
    private final XChartPanel<XYChart> chartPanel;
    private LocalDate startDate;
    private LocalDate endDate;

    public Graph() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(720, 360));

        // make the chart
        chart = new XYChartBuilder()
                .width(720)
                .height(360)
                .title("Balance Over Time")
                .xAxisTitle("Date")
                .yAxisTitle("Balance")
                .build();
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setDatePattern("yyyy-MM-dd");
        chart.getStyler().setYAxisDecimalPattern("$#,##0.00");

        chartPanel = new XChartPanel<>(chart);
        add(chartPanel, BorderLayout.CENTER);

        updateChart();
    }

    public Graph(ArrayList<Transaction> transactions) {
        this();
        setTransactions(transactions);
    }

    public Graph(ArrayList<Transaction> transactions, String startDate, String endDate) {
        this(transactions);
        setDateRange(startDate, endDate);
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions.clear();
        if (transactions != null) {
            // copy them over
            this.transactions.addAll(transactions);
        }
        updateChart();
    }

    public void setDateRange(String startDate, String endDate) {
        LocalDate parsedStartDate = LocalDate.parse(startDate);
        LocalDate parsedEndDate = LocalDate.parse(endDate);
        if (parsedStartDate.isAfter(parsedEndDate)) {
            throw new IllegalArgumentException("Start date must be on or before end date.");
        }

        this.startDate = parsedStartDate;
        this.endDate = parsedEndDate;
        updateChart();
    }

    public void clearDateRange() {
        startDate = null;
        endDate = null;
        updateChart();
    }

    public ArrayList<BalancePoint> getBalancePoints() {
        ArrayList<BalancePoint> points = new ArrayList<>();
        if (transactions.isEmpty()) {
            return points;
        }

        // sort by date first
        ArrayList<Transaction> sortedTransactions = new ArrayList<>(transactions);
        sortedTransactions.sort(Comparator.comparing(transaction -> LocalDate.parse(transaction.getDate())));

        LocalDate graphStart = startDate != null ? startDate : LocalDate.parse(sortedTransactions.get(0).getDate());
        LocalDate graphEnd = endDate != null ? endDate : LocalDate.parse(sortedTransactions.get(sortedTransactions.size() - 1).getDate());

        if (graphStart.isAfter(graphEnd)) {
            return points;
        }




        
        // same day stuff gets added together
        TreeMap<LocalDate, Double> dailyChanges = new TreeMap<>();
        double balance = 0.0;

        for (Transaction transaction : sortedTransactions) {


            LocalDate date = LocalDate.parse(transaction.getDate());
            double amount = getSignedAmount(transaction);

            if (date.isBefore(graphStart)) {
                balance += amount;

            } else if (!date.isAfter(graphEnd)) {
                dailyChanges.put(date, dailyChanges.getOrDefault(date, 0.0) + amount);
            }
        }

        points.add(new BalancePoint(graphStart, balance));


        for (LocalDate date : dailyChanges.keySet()) {
            balance += dailyChanges.get(date);
            points.add(new BalancePoint(date, balance));
        }

        // keeps the line going to the end date

        if (!points.get(points.size() - 1).getDate().equals(graphEnd)) {
            points.add(new BalancePoint(graphEnd, balance));
        }

        return points;
    }

    private void updateChart() {
        ArrayList<BalancePoint> points = getBalancePoints();

        ArrayList<Date> dates = new ArrayList<>();
        ArrayList<Double> balances = new ArrayList<>();

        if (points.isEmpty()) {
            // dummy point  so the chart still shows up
            chart.setTitle("Balance Over Time (No Data)");
            dates.add(Date.valueOf(LocalDate.now()));
            balances.add(0.0);
        } else {
            chart.setTitle("Balance Over Time");
            for (BalancePoint point : points) {

                dates.add(Date.valueOf(point.getDate()));
                balances.add(point.getBalance());
            }
        }

        if (chart.getSeriesMap().containsKey(SERIES_NAME)) {
            chart.updateXYSeries(SERIES_NAME, dates, balances, null);
        } else {

            chart.addSeries(SERIES_NAME, dates, balances);
        }

        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private double getSignedAmount(Transaction transaction) {
        if ("income".equalsIgnoreCase(transaction.getType())) {
            return transaction.getAmount();
        }
        // expenses pull it down
        return -transaction.getAmount();
    }

    public static class BalancePoint {
        private final LocalDate date;

        private final double balance;

        public BalancePoint(LocalDate date, double balance) {
            this.date = date;
            this.balance = balance;
        }

        public LocalDate getDate() {
            return date;
        }

        public double getBalance() {
            return balance;
        }
    }
}
