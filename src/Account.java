// Student Name: Lindsay Torres
// Student ID: 110130322
/**
 * Account.java
 * -> This class is supposed to hold all current information, such as the user's current balance, as well as the balance over the current month. This gets updated manually by Service.java whenever a transaction is added.
 * 
 */
import java.util.ArrayList;
import java.time.LocalDate;

public class Account {
    private double currentBalance = 0.0;
    private double balanceOverThisMonth = 0.0;


    //Updating the balance with the previous transactions from the database
    public void updatePrevTransactions(ArrayList<Transaction> transactions){
        for(Transaction transaction : transactions){
            if(transaction.getType().equals("income")){
                currentBalance += transaction.getAmount();
                if(transaction.getDate().split("-")[1].equals(LocalDate.now().toString().split("-")[1])){ // If the transaction is from the current month, update the balance over this month as well
                    balanceOverThisMonth += transaction.getAmount();
                }
            }
            else{
                if(transaction.getDate().split("-")[1].equals(LocalDate.now().toString().split("-")[1])){ // If the transaction is from the current month, update the balance over this month as well
                    balanceOverThisMonth -= transaction.getAmount();
                }
                currentBalance -= transaction.getAmount();
            }
        }

    }

    //Updating any new transactions to the balance
    public void update(double newAmount, String type){
        if(type.equals("income")){
            currentBalance += newAmount;
        }
        else{
            currentBalance -= newAmount;
        }
    }
    public void updateBalanceOverThisMonth(double newAmount, String type){
        if(type.equals("income")){
            balanceOverThisMonth += newAmount;
        }
        else{
            balanceOverThisMonth -= newAmount;
        }
    }

    //Getters 
    public double getCurrentBalance() {
        return currentBalance;
    }
    public double getBalanceOverThisMonth() {
        return balanceOverThisMonth;
    }
    
}
