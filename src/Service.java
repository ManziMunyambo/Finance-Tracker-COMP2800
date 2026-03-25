//Student Name: Lindsay Torres
//Student ID: 110130322
/**
 * Service.java
 * -> This class represents the service layer for the finance tracker application. It handles the business logic and interacts with the database and account classes.
 *  Here, you can see it sorts based on whatever is requested by the MainInterface and also adds transactions to the database and updates the account balance accordingly.
 */

import java.util.ArrayList;
import java.time.LocalDate; 

public class Service {
    private Account account; 
    private TransactionSQL database = new TransactionSQL();
    public ArrayList<Transaction> transactions = new ArrayList<>();

    public Service(Account account) {
        // Firstly adding any previous transactions from the database to the service's
        // transaction list
        transactions = database.getAllTransactions();
        this.account = account;
        account.updatePrevTransactions(transactions); // Updating the account balance with the previous transactions from the database
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction); // Adding transaction to the service's list
        database.addTransaction(transaction); // Save to database
        account.update(transaction.getAmount(), transaction.getType()); // Update the account balance with the new transaction

        if(transaction.getDate().split("-")[1].equals(LocalDate.now().toString().split("-")[1])){ // If the transaction is from the current month, update the balance over this month as well
            account.updateBalanceOverThisMonth(transaction.getAmount(), transaction.getType());
        }
    }

// ------------  All the methods that are by retrival of certain information ---------------

// Retrieving by date 
    public ArrayList<Transaction> getByDate(String date){
        ArrayList<Transaction> transactionsByDate = new ArrayList<>();
        for(Transaction transaction : transactions){
            if(transaction.getDate().equals(date)){
                transactionsByDate.add(transaction);
            }
        }
        return transactionsByDate;
    }

//Retrieving by category
    public ArrayList<Transaction> getByCategory(String category){
        ArrayList<Transaction> transactionsByCategory = new ArrayList<>();
        for(Transaction transaction : transactions){
            if(transaction.getCategory().equals(category)){
                transactionsByCategory.add(transaction);
            }
        }
        return transactionsByCategory;
    }

//Retrieving by type (income or expense)
    public ArrayList<Transaction> getByType(String type){
        ArrayList<Transaction> transactionsByType = new ArrayList<>();
        for(Transaction transaction : transactions){
            if(transaction.getType().equals(type)){
                transactionsByType.add(transaction);       
            }
        }
        return transactionsByType;
    }

// By greatest to least amount
    public ArrayList<Transaction> getByAmount(){
        ArrayList<Transaction> transactionsByAmount = new ArrayList<>(transactions);
        transactionsByAmount.sort((t1, t2) -> Double.compare(t2.getAmount(), t1.getAmount())); // Sort in descending order
        return transactionsByAmount;
    }

// By least to greatest amount
    public ArrayList<Transaction> getByAmountAsc(){
        ArrayList<Transaction> transactionsByAmountAsc = new ArrayList<>(transactions);
        transactionsByAmountAsc.sort((t1, t2) -> Double.compare(t1.getAmount(), t2.getAmount())); // Sort in ascending order
        return transactionsByAmountAsc;
    }

// By type and then amount
    public ArrayList<Transaction> getByTypeAndAmount(String type){
        ArrayList<Transaction> transactionsByTypeAndAmount = new ArrayList<>();
        for(Transaction transaction : transactions){
            if(transaction.getType().equals(type)){
                transactionsByTypeAndAmount.add(transaction);
            }
        }
        transactionsByTypeAndAmount.sort((t1, t2) -> Double.compare(t2.getAmount(), t1.getAmount())); // Sort in descending order
        return transactionsByTypeAndAmount;
    }

}
