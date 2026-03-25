// Student Name: Lindsay Torres
// Student ID: 110130322
/**
 * Transaction.java
 * -> An abstract class of what a basic transaction should look like. 
 * 
 */
public abstract class Transaction {
   String type;
   double amount;
   String date;
   String category;

   public Transaction(String type, double amount, String date, String category) {
      this.type = type;
      this.amount = amount;
      this.date = date;
      this.category = category;
   }

   public String getType() {
      return type;
   }

   public double getAmount() {
      return amount;
   }

   public String getDate() {
      return date;
   }

   public String getCategory() {
      return category;
   }

}