// Student Name: Lindsay Torres
// Student ID: 110130322
/**
 * Income.java
 * -> This class represents an expense transaction and extends the Transaction class.
 * 
 */
public class Income extends Transaction {
    private String type;
    private double amount;
    private String date;
    private String category;

    public Income(String type, double amount, String date, String category) {
        super(type, amount, date, category);
    }
}
