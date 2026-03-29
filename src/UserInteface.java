/*
Student Name: Manzi Munyambo 
Student ID: 110179611

UserInterface.java 
* creates a professional UI for the user to interact with the Finance Tracker and its systems
*/
import com.formdev.flatlaf.FlatLightLaf; 
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList; 

public class UserInterface extends JFrame {
    //logic for other classes
    private Service service; 
    private Account account;

    //UI components
    private JPanel cardPanel; //creates a deck of buttons on the screen that the user will select
    private CardLayout cardLayout;
    private JLabel labelBalance; //the user's balance displayed on screen 
    private DefaultTableModel tableModel; //for displaying the database

    //important variables for Reports and Graphs
    private JTextArea reportTextArea;
    private Graph balanceGraph;
    private Report reportGenerator;

    public UserInterface(){
        //initialize the logic 
        this.account = new Account();
        this.service = new Service(this.account); 

        //setup the window 
        setTitle("2800 Finance Tracker!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,700);
        setLocationRelativeTo(null);

        //layout setup 
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        //add the menu and cards 
        add(createMenu(), BorderLayout.NORTH);
        cardPanel.add(createHomePanel(), "Home");
        cardPanel.add(createInputPanel(), "Enter Data");
        cardPanel.add(createDatabasePanel(), "Transactions");
        cardPanel.add(createReportsPanel(), "Reports");

        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createMenu(){
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0,0,2,0, new Color(230,230,230)));

        //make a subpanel for buttons use GridLayout
        //JPanel buttonPanel = new JPanel(new GridLayout(1,3,10,0));
        //buttonPanel.setBackground(Color.WHITE);
        //buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,20,15,20));
        
        //when all the features are implemented, use createMenuButton to make it look nice
        JButton homebutton = new JButton("Home");
        JButton inputbutton = new JButton("Enter Data");
        JButton databasebutton = new JButton("Database");
        JButton reportbutton = new JButton("Reports");

        //action listeners to swap to different parts 
        homebutton.addActionListener(e -> cardLayout.show(cardPanel, "Home"));
        inputbutton.addActionListener(e -> cardLayout.show(cardPanel, "Enter Data"));
        databasebutton.addActionListener(e -> {
            refreshTable(service.transactions); //update the table before showing it 
            cardLayout.show(cardPanel, "Transactions");
        });
        reportbutton.addActionListener(e -> {
            updateReportsPanel();
            cardLayout.show(cardPanel, "Reports");
        });

        header.add(homebutton);
        header.add(inputbutton);
        header.add(databasebutton);
        header.add(reportbutton);

        return header;
    }

    private JButton createMenuButton(String text){
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 10));
        button.setFocusPainted(false); //remove that weird square around the buttons
        button.setPreferredSize(new Dimension(20, 50));

        button.putClientProperty("JButton.buttonType", "roundRect"); //round the corners 

        return button;
    }

    private JPanel createHomePanel(){
        JPanel panel = new JPanel(new GridBagLayout());

        //display Account.currentBalance 
        double currentBalance = account.getCurrentBalance();
        labelBalance = new JLabel(String.format("$%.2f", currentBalance));
        labelBalance.setFont(new Font("SansSerif", Font.BOLD, 60));
        
        if(currentBalance < 0.00) //negative balance, make it red
            labelBalance.setForeground(new Color(214,59,28)); 
        else //otherwise, make it green
            labelBalance.setForeground(new Color(102,204,51));

        panel.add(labelBalance);
        return panel;
    }

    private JPanel createInputPanel(){
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,20,15,20);
        gbc.fill = GridBagConstraints.HORIZONTAL; 

        //input fields: Amount, Date, Category 
        JTextField amountField = StylizeTextField();
        JTextField dateField = StylizeTextField();
        dateField.setText(LocalDate.now().toString()); //current date is the default
        JTextField categoryField = StylizeTextField();

        //create the dropdown menu for "income" or "expense"
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Income", "Expense"});
        typeBox.setPreferredSize(new Dimension(300,50));
        typeBox.setFont(new Font("SansSerif", Font.PLAIN, 18));

        //create the add transactions button 
        JButton addTransButton = new JButton("Add Transaction!");
        addTransButton.setPreferredSize(new Dimension(300,50));
        addTransButton.setFont(new Font("SansSerif", Font.BOLD, 20));

        addTransButton.setBackground(new Color(102, 204, 51)); //make it green, might look good
        addTransButton.setForeground(Color.WHITE);
        addTransButton.setFocusPainted(false);

        addTransButton.putClientProperty("JButton.buttonType", "roundRect"); //round the corners

        //add the buttons to the panel with GridBag 
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(StylizeLabel("Amount: "), gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(StylizeLabel("Type: "), gbc);
        gbc.gridx = 1;
        panel.add(typeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(StylizeLabel("Date: "), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(StylizeLabel("Category: "), gbc);
        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2; //make button span across both columns
        gbc.insets = new Insets(30,20,10,20); //more space above the button 
        panel.add(addTransButton, gbc);

        //button logic for adding transactions, connect UI to Service.java 
        addTransButton.addActionListener(e -> {
            try{
                double amount = Double.parseDouble(amountField.getText());
                String type = typeBox.getSelectedItem().toString().toLowerCase();

                //Capitalize the first letter of categories
                String category = categoryField.getText().trim().toLowerCase();
                if(!category.isEmpty()){
                    category = category.substring(0,1).toUpperCase() + category.substring(1).toLowerCase();
                }

                Transaction trans; 
                if(type.equalsIgnoreCase("income"))
                    trans = new Income(type, amount, dateField.getText(), categoryField.getText());
                else 
                    trans = new Expense(type, amount, dateField.getText(), categoryField.getText());

                service.addTransaction(trans);

                //UI feedback 
                double newBalance = account.getCurrentBalance();
                labelBalance.setText(String.format("$%.2f", newBalance));

                if(newBalance < 0.00) //negative balance, make it red
                    labelBalance.setForeground(new Color(214,59,28)); 
                else //otherwise, make it green
                    labelBalance.setForeground(new Color(102,204,51));

                JOptionPane.showMessageDialog(this, "Transaction Successfully Saved!");

                //clear fields for next time 
                amountField.setText("");
                categoryField.setText("");
            } catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(this, "Please enter a valid number!");
            } catch (Exception ex){
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        return panel; 
    }

    private JLabel StylizeLabel(String text){
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        return label;
    }

    private JTextField StylizeTextField(){
        JTextField txt = new JTextField(15);
        txt.setPreferredSize(new Dimension(300,50));
        txt.setFont(new Font("SansSerif", Font.PLAIN, 18));
        return txt;
    }

    private JPanel createDatabasePanel(){
        JPanel panel = new JPanel(new BorderLayout());

        //adding a container for filter-related stuff 
        JPanel filterContainer = new JPanel(new GridLayout(2,1));

        //Row1 will have sorting and getting by type
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton sortButtonLTG = new JButton("Descending");
        JButton sortButtonGTL = new JButton("Ascending");
        String[] types = {"All", "Income", "Expense"};
        JComboBox<String> filterType = new JComboBox<>(types);

        //Row2 will have service.getByTypeAndAmount(), getByDate(), and getByCategory()
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton typeAmountButton = new JButton("Sort Type by Amount (Descending)");
        JTextField dateSearch = new JTextField(10);
        JButton dateSearchButton = new JButton("Seach Date");

        JTextField categorySearch = new JTextField(10);
        JButton categorySearchButton = new JButton("Search Category");

        //search by date logic 
        dateSearchButton.addActionListener(e -> {
            String date = dateSearch.getText().trim();
            if(!date.isEmpty()){
                refreshTable(service.getByDate(date));
            }
            else{
                JOptionPane.showMessageDialog(this, "Please enter a date!");
            }
        });

        //search by category logic 
        categorySearchButton.addActionListener(e -> {
            String category = categorySearch.getText().trim();
            if(!category.isEmpty()){
                refreshTable(service.getByCategory(category));
            }
            else{
                JOptionPane.showMessageDialog(this, "Please enter a category!");
            }
        });

        //search by type and amount logic 
        typeAmountButton.addActionListener(e -> {
            String selected = filterType.getSelectedItem().toString();
            if(!selected.equalsIgnoreCase("all")){
                refreshTable(service.getByTypeAndAmount(selected));
            }
            else{
                JOptionPane.showMessageDialog(this, "Please select Income or Expense!");
            }
        });

        //sort logic 
        sortButtonLTG.addActionListener(e -> refreshTable(service.getByAmount())); //least to greatest
        sortButtonGTL.addActionListener(e -> refreshTable(service.getByAmountAsc())); //greatest to least

        //type filter logic 
        filterType.addActionListener(e -> {
            String selected = filterType.getSelectedItem().toString();
            if(selected.equals("All")){
                refreshTable(service.transactions);
            }
            else{
                refreshTable(service.getByType(selected.toLowerCase()));
            }
        });

        //"Clear Filters" button that will get rid of sort/search filters
        JButton clearButton = new JButton("Clear Filters");

        clearButton.addActionListener(e -> {
            filterType.setSelectedItem(0);
            refreshTable(service.transactions);
        });

        //putting components into row1
        row1.add(new JLabel("General Sort:"));
        row1.add(sortButtonLTG);
        row1.add(sortButtonGTL);
        row1.add(new JLabel("Filter:"));
        row1.add(filterType);
        row1.add(typeAmountButton);

        //row2 
        row2.add(new JLabel("Find Date (YYYY-MM-DD):"));
        row2.add(dateSearch);
        row2.add(dateSearchButton);
        row2.add(new JLabel("Find Category:"));
        row2.add(categorySearch);
        row2.add(categorySearchButton);
        row2.add(clearButton);

        filterContainer.add(row1);
        filterContainer.add(row2);
        //displaying the database
        String[] columns = {"Type", "Amount", "Date", "Category"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        panel.add(filterContainer, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReportsPanel(){
        JPanel panel = new JPanel(new BorderLayout());

        //initialize classes
        reportGenerator = new Report();
        balanceGraph = new Graph();

        //text area for the Report.java string output 
        reportTextArea = new JTextArea();
        reportTextArea.setEditable(false); 
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportTextArea.setBorder(BorderFactory.createTitledBorder("Finance Tracker Statement"));

        JScrollPane reportScroll = new JScrollPane(reportTextArea); //scroll wheel for the text
        reportScroll.setPreferredSize(new Dimension(700,250));

        //adding components to panel
        panel.add(balanceGraph, BorderLayout.NORTH);
        panel.add(reportScroll, BorderLayout.CENTER);

        return panel;
    }

    private void updateReportsPanel(){
        ArrayList<Transaction> currentData = service.transactions; 

        //set the transactions in Graph and Report 
        balanceGraph.setTransactions(currentData);
        reportGenerator.setTransactions(currentData);

        //let's make the default range the last 30 days
        if(!currentData.isEmpty()){
            //find the earliest and latest date in currentData
            String firstDate = currentData.stream().map(Transaction::getDate).min(String::compareTo).get();
            String lastDate = LocalDate.now().toString();

            String statement = reportGenerator.generateReport(firstDate, lastDate);
            reportTextArea.setText(statement);
        }
        else{
            reportTextArea.setText("Not enough data to generate a report");
        }
    }
    private void refreshTable(ArrayList<Transaction> list){
        tableModel.setRowCount(0); //clear the table first 
        for(Transaction t : list)
            tableModel.addRow(new Object[]{t.getType(), t.getAmount(), t.getDate(), t.getCategory()});
    }
}
