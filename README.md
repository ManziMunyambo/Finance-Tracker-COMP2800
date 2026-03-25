# Finance-Tracker-COMP2800

Last Update: Mar 24, 2026

This is a step-by-step instruction on how to use the Finance Tracker made for University of Windsor's COMP 2800 course. Simply put the following in your terminal to compile and run the program:

(First command is to compile, seocnd is to run)

**FOR MAC** 
javac -cp lib/sqlite-jdbc-3.51.3.0.jar src/*.java -d out/ 
java -cp out:lib/sqlite-jdbc-3.51.3.0.jar Main

**FOR WINDOWS**

javac -cp lib/sqlite-jdbc-3.51.3.0.jar src/*.java -d out/
java -cp out;lib/sqlite-jdbc-3.51.3.0.jar Main

**ALSO BE SURE TO NOT PUSH FINANCE.DB, THAT IS FOR YOUR LOCAL COMPUTER**

# --------------------------------------------------------------------------------------------

## Current Updates - Mar 23, 2026 - Lindsay Torres 110130322

I added a bunch of classes as a layout for what these classes will look like. Underneath is an explanation for all of these classes (they're included on the .java file too, but just in case anyone wants to look at all of them in one place) and their functionalities.

**TransactionSQL.java** -> The database itself. This is so the application remembers the user's past transactions.

**Transaction.java** -> An abstract class for the two types of transactions our finance tracker will handle. As of now, I don't know what methods they will include, but most likely it'll be for assistance for categorizing and different display types.

**Account.java** -> The user. Holds all current information.

**Expense.java** -> A Transaction object that holds all of the expenses a user makes.

**Income.java** -> A Transaction object that holds all of the income a user recieves. 

**Service.java** -> Manages inserting into the database and holding/creating all of the transaction objects. Also updates the account.

**Main.java** -> What the user will run.

**MainInterface.java** -> The screen interface.

## Current Updates - Mar 24, 2026 - Lindsay Torres - 110130322

I successfully connected the sqlite database with the root project. The UI needs to be worked on. 