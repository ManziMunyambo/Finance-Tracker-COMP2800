// Student Name: Lindsay Torres 
// Student ID: 110130322
/**
 * Main.java
 * -> This is the main class to run the finance tracker application.
 */

import com.formdev.flatlaf.FlatLightLaf;


public class Main {
        public static void main(String[] args) {
        //new MainInterface();  // Constructor handles everything now

        //try launching the cooler UI
        try{
                FlatLightLaf.setup();
        } catch (Exception e){
                System.out.println("Error! " + e.getMessage());
                e.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
                UserInterface gui = new UserInterface();
                gui.setVisible(true);
        });
    }
}
