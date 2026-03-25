// Student Name: Lindsay Torres
// Student ID: 110130322
/**
 * MainInterface.java
 * -> This class represents the main interface for the finance tracker application. Handles all of the UI components and user interactions. Also has access to all of Service.java and Account.java's information.
 * 
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainInterface extends JPanel {
    //Classes
    private Account account;
    private Service service;


    //Getting the height and width of a user's screen
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public final int WIDTH = (int) screenSize.getWidth(), HEIGHT = (int) screenSize.getHeight();

    //Panel components 
    private JComboBox<String> categoryDropdown;
    private JButton addButton;
    private JTextArea displayArea;
    private JTextField amountField;
    private JTextField dateField;
    private JTextField categoryField;

    //Strings to hold the user input for the transaction
    private String amountInput;
    private String dateInput;
    private String categoryInput;

    public MainInterface() {

        // Initialize the account
        this.account = new Account();
        // Initialize the service
        this.service = new Service(this.account);

        // Set up the panel layout
        setLayout(new BorderLayout());

        //Create panel at the top of the interface
        JPanel topPanel = new JPanel();


        // Create components
        categoryDropdown = new JComboBox<>(new String[] { "Income", "Expense" });
        addButton = new JButton("Add Transaction");
        displayArea = new JTextArea(10, 30);
        displayArea.setEditable(false);

        amountField = createLabeledField(topPanel, "Amount:","0.0");
        dateField = createLabeledField(topPanel, "Date:","YYYY-MM-DD");
        categoryField = createLabeledField(topPanel, "Category:","");

        // Add components to the panel
        topPanel.add(new JLabel("Category:"));
        topPanel.add(categoryDropdown);
        topPanel.add(addButton);
        add(topPanel, BorderLayout.NORTH);

        // Add event listener for the button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String category = (String) categoryDropdown.getSelectedItem();
                System.out.println("Selected category: " + category);
                // TODO: Implement actual transaction logic
                dateInput = dateField.getText();
                amountInput = amountField.getText();
                categoryInput = categoryField.getText();

                if(dateInput.isEmpty() || amountInput.isEmpty() || categoryInput.isEmpty() || dateInput.equals("YYYY-MM-DD") || amountInput.equals("0.0") || categoryInput.equals("")){
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return; // Error message if dateInput or amountInput is empty
                }
                else{
                    try { //NOTE: Did not check for a valid input of the date, just trusting user to put in a valid date
                        double amount = Double.parseDouble(amountInput);
                        Transaction transaction;
                        if (category.equals("Income")) {
                            transaction = new Income(category.toLowerCase(), amount, dateInput, categoryInput);
                        } else {
                            transaction = new Expense(category.toLowerCase(), amount, dateInput, categoryInput);
                        }
                        service.addTransaction(transaction);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid number for the amount.");
                    }
                }
            }
        });

        // Create and show the frame
        setFieldsEditable(true);
        JFrame frame = new JFrame("Finance Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT); // Set a reasonable size
        frame.add(this);
        frame.setVisible(true);
    }

    // --------------------- Helper Methods ---------------------

    //Creating the text fields with their labels
        private JTextField createLabeledField(JPanel parent, String label, String placeholder) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));

        JTextField field = new JTextField(placeholder);
        field.setPreferredSize(new Dimension(100,50));
        parent.add(field);
        parent.add(Box.createVerticalStrut(8));
        
        return field;
    }

    //Making the text fields editable 
    private void setFieldsEditable(boolean editable) {
        amountField.setEditable(editable);
        dateField.setEditable(editable);
        categoryField.setEditable(editable);
    }
}


