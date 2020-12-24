package system;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database object that stores the methods used to communicate with the sql database.
 * NOTE: this system is currently setup to be used with funds corresponding to 
 *       the Student Engineer's Council; slight tweaks in string formatting are
 *       required for other applications.
 *       
 * @author Aidan Sprague
 * @version 2020.12.23
 */
public class Database {
    
    private Connection connection;
    private Statement statement;

    /**
     * Constructor for the class. Checks to see if the necessary classes can be
     * found before continuing; attempts to connect to the sql database given in
     * the parameter (an exception is thrown otherwise).
     * 
     * The query timeout is set to 30 seconds by default.
     * @param file  The database file
     */
    public Database(File file) {
        try {
            Class.forName("org.sqlite.JDBC");
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance(); 
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath().replace("\\", "/"));
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
        }
        catch (SQLException e) {e.printStackTrace();}
        catch (ClassNotFoundException e) {e.printStackTrace();}
        catch (InstantiationException e) {e.printStackTrace();}
        catch (IllegalAccessException e) {e.printStackTrace();}
    }
    
    /**
     * Lookup a particular fund and retrieve all of its information and purchases. Each fund
     * contains the following information:
     *      - Expiration date
     *      - Starting balance
     *      - Current balance
     *      
     * Additionally, each purchase contains the following information:
     *      - Date of purchase
     *      - Person who made purchase
     *      - Amount of purchase
     *      - Description of purchase
     *      
     * If the given fund has no associated purchases, this information is returned.
     * @param fund      The fund to check
     * @return a string with the above information.
     */
    public String lookupFund(String fund) {
        StringBuilder builder = new StringBuilder();
        try {
            // Access the specific fund.
            ResultSet rs = statement.executeQuery("select * from funds where fund_name='" + fund + "'");
            
            // Format and append the fund name.
            builder.append("Details of Fund " + formatFund(fund) + ":\n\n");
            
            // Get the fund expiration, format, and append.
            int expiration = rs.getInt("expiration");
            builder.append("\t- Expiration Date: " + formatDate(expiration) + "\n");
            
            // Get the starting balance, format, and append.
            float start = rs.getFloat("starting_amount");
            builder.append("\t- Starting Balance: " + formatMoney(start) + "\n");
            
            // Get current balance (format and append come later).
            float curr = rs.getFloat("current_amount");
            
            // Find all of the purchases made from this fund.
            builder.append("\t- Purchases:\n\n");
            builder.append("\tDATE\t\tPERSON\t\tAMOUNT\t\tDESCRIPTION\n");
            ResultSet purchases = statement.executeQuery("select * from purchases where fund_name='" + fund + "'");
            boolean hasPurchases = false;
            while (purchases.next()) {
                
                // If there is at least one purchase, set the flag to true.
                hasPurchases = true;
                
                // Get the purchase date.
                int date = rs.getInt("date");
                
                // Get the purchaser.
                String person = rs.getString("person");
                
                // Get the amount.
                float am = rs.getFloat("amount");
                
                // Get the description.
                String description = rs.getString("description");
                
                // Append each of the above, and format if necessary.
                builder.append("\t" + formatDate(date) + "\t" + person + "\t\t" + formatMoney(am) + "\t\t"
                                + description + "\n");
            }
            
            // If there are no recorded purchases...
            if (!hasPurchases) 
                builder.append("\t\t**No purchases to date**\n");
            
            // Format and append the current balance.
            builder.append("\n\t- Current Balance: " + formatMoney(curr) + "\n");
        }
        catch (SQLException e) {e.printStackTrace();}
        
        return builder.toString();
    }
    
    /**
     * Lookup a particular person and retrieve all of their logged purchases. Each purchase
     * includes the following information:
     *      - Date of purchase
     *      - Fund pulled from
     *      - Amount of purchase
     *      - Description of purchase
     *      
     * If the given person has not made any purchases, this information is returned.
     * @param person    The person to check
     * @return a string with the above information.
     */
    public String lookupPerson(String person) {
        StringBuilder builder = new StringBuilder();
        builder.append("Purchases for " + person + ":\n\n");
        builder.append("\t\tDATE\t\tFUND\t\tAMOUNT\t\tDESCRIPTION\n");
        try {
            ResultSet rs = statement.executeQuery("select * from purchases where person='" + person + "'");
            float total = 0;
            boolean hasPurchases = false;
            while (rs.next()) {
                
                // If there is at least one purchase, set the flag to true.
                hasPurchases = true;
                
                // Get the purchase date.
                int date = rs.getInt("date");
                
                // Get the fund name.
                String fund = rs.getString("fund_name");
                
                // Get the amount spent and increment the total.
                float am = rs.getFloat("amount");
                total += am;
                
                // Get the description.
                String description = rs.getString("description");
                
                // Append each of the above, and format if necessary.
                builder.append("\t\t" + formatDate(date) + "\t" + formatFund(fund) + "\t"
                                + formatMoney(am) + "\t\t" + description + "\n");
            }
            
            // If there are no recorded purchases...
            if (!hasPurchases) 
                builder.append("\t\t**No purchases to date**\n");
            
            // Format and Append the total amount spent.
            builder.append("\nTotal Spent: " + formatMoney(total) + "\n");
            
        }
        catch (SQLException e) {e.printStackTrace();}
        
        return builder.toString();
    }
    
    /**
     * Returns an overview of all of the funds in the database, including the following:
     *      - Their name
     *      - Their expiration date
     *      - Their initial amount
     *      - Their current amount
     * @return a string containing the above information.
     */
    public String overview() {
        StringBuilder builder = new StringBuilder();
        builder.append("Overview of All Funds\n\n");
        try {
            ResultSet rs = statement.executeQuery("select * from funds");
            while (rs.next()) {
                
                // Get the fund name, format, and append.
                String fund = rs.getString("fund_name");
                builder.append(formatFund(fund) + ":\n");
                
                // Get the fund expiration, format, and append.
                int expiration = rs.getInt("expiration");
                builder.append("\t- Expiration Date: " + formatDate(expiration) + "\n");
                
                // Get the starting balance, format, and append.
                float start = rs.getFloat("starting_amount");
                builder.append("\t- Starting Balance: " + formatMoney(start) + "\n");
                
                // Get the current balance, format, and append.
                float curr = rs.getFloat("current_amount");
                builder.append("\t- Current Balance: " + formatMoney(curr) + "\n\n");
            }
        }
        catch (SQLException e) {e.printStackTrace();}
        
        return builder.toString();
    }
    
    /**
     * Returns all of the logged purchases in the database, in order of descending
     * price. Each purchase includes the following information:
     *      - Amount of purchase
     *      - Date of purchase
     *      - Fund pulled from
     *      - Person who made the purchase
     *      - Description of purchase
     * @return a string containing the above information.
     */
    public String allPurchases() {
        StringBuilder builder = new StringBuilder();
        builder.append("List of Purchases (By Amount Descending):\n\n");
        builder.append("\t\tAMOUNT\t\tDATE\t\tFUND\t\tPERSON\t\tDESCRIPTION\n");
        try {
            ResultSet rs = statement.executeQuery("select * from purchases order by amount desc");
            float total = 0;
            boolean hasPurchases = false;
            while (rs.next()) {
                
                // If there is at least one purchase, set the flag to true.
                hasPurchases = true;
                
                // Get the purchase date.
                int date = rs.getInt("date");
                
                // Get the fund name.
                String fund = rs.getString("fund_name");
                
                // Get the purchaser.
                String person = rs.getString("person");
                
                // Get the amount spent and increment the total.
                float am = rs.getFloat("amount");
                total += am;
                
                // Get the description.
                String description = rs.getString("description");
                
                // Append each of the above, and format if necessary.
                builder.append("\t\t" + formatMoney(am) + "\t\t" + formatDate(date) + "\t"
                                + formatFund(fund) + "\t" + person + "\t\t"  + description
                                + "\n");
            }
            
            // If there are no recorded purchases...
            if (!hasPurchases) 
                builder.append("\t\t**No purchases to date**\n");
            
            // Format and Append the total amount spent.
            builder.append("\nTotal Spent: " + formatMoney(total) + "\n");
        }
        catch (SQLException e) {e.printStackTrace();}
        
        return builder.toString();
    }
    
    /**
     * Clears all of the data in the database by dropping its two tables.
     * @return a string indicating the database was successfully cleared.
     */
    public String clearData() {
        try {
            statement.executeUpdate("drop table if exists funds");
            statement.executeUpdate("drop table if exists purchases");
        }
        catch (SQLException e) {e.printStackTrace();}
        
        return "Database Cleared.";
    }
    
    /**
     * Add a fund to the database; if the 'funds' table is not present in the database
     * file, one will be created. In total, this function executes two updates on the
     * database.
     * @param fund          The fund to be added
     * @param amount        The initial amount of the fund
     * @param expiration    The expiration date of the fund
     * @return a string indicating the addition was successful.
     */
    public String addFund(String fund, String amount, String expiration) {
        try {
            // Format the expiration date and amount
            int exp = Integer.parseInt(expiration.replaceAll("[^\\d.]", ""));
            float am = Float.parseFloat(amount.replaceAll("[$,]", ""));
            
            // Create the fund table if necessary
            statement.executeUpdate("create table if not exists funds ("
                + "fund_name text, "
                + "expiration integer, "
                + "starting_amount real, "
                + "current_amount real, "
                + "primary key (fund_name))");
            
            // Add the fund
            statement.executeUpdate("insert into funds values('" + fund + "', " + exp + 
                ", " + am + ", " + am + ")");
        }
        catch (SQLException e) {e.printStackTrace();}
        
        // Format the fund
        fund = formatFund(fund);
        
        return fund + " added to databse.";
    }
    
    /**
     * Add a purchase to the database; if the 'purchases' tables is not present in the
     * database file, one will be created. In total, the function executes three updates
     * on the database.
     * @param fund      The fund to be pulled from
     * @param amount    The amount of the purchase
     * @param person    The person who made the purchase
     * @param date      The date of the purchase   
     * @param info      Any additional info about the purchase (i.e. what it was)
     * @return a string indicating the addition was successful.
     */
    public String addPurchase(String fund, String amount, String person, String date, String info) {
        try {
            // Format the date and amount
            int dt = Integer.parseInt(date.replaceAll("[^\\d.]", ""));
            float am = Float.parseFloat(amount.replaceAll("[$,]", ""));
            
            // Create the purchase table if necessary
            statement.executeUpdate("create table if not exists purchases (" 
                + "person text, "
                + "amount real, "
                + "date integer, "
                + "description text, "
                + "fund_name text, "
                + "foreign key (fund_name) references funds (fund_name))");
            
            // Add the purchase
            statement.executeUpdate("insert into purchases values('" + person + "', " + am + ", " + dt 
                + ", '" + info + "', '" + fund + "')");
            
            // Update the amount in the selected fund
            statement.executeUpdate("update funds set current_amount = current_amount - " + am
                + " where fund_name = '" + fund + "'");
        }
        catch (SQLException e) {e.printStackTrace();}
        
        // Format the fund
        fund = formatFund(fund);
        
        return "Purchase added to " + fund;
    }
    
    /**
     * Closes the connection to the SQL database.
     * @return a string confirming the connection was closed.
     */
    public String exit() {
        try {
            if (connection != null)
                connection.close();
        }
        catch (SQLException e) {e.printStackTrace();}
        return "Exiting the Program.";
    }
    
    /**
     * Formats a fund to a more human-readable version; for example:
     * 
     *      EOFS20  ->  EOF Spring '20
     *      
     * NOTE: This is only for Student Engineer's Council type funds; this function
     *       can be changed to allow for formatting of other fund types.
     * @param fund      The fund to format
     * @return a string containing the above information.
     */
    private String formatFund(String fund) {
        String type = fund.substring(0, 3);
        String term = fund.substring(3, 4);
        if (term.equals("S")) 
            term = "Spring";
        else 
            term = "Fall";
        String year = fund.substring(4);
        year = "'" + year;
        
        return type + " " + term + " " + year;
    }
    
    /**
     * Formats a date to a more human-readable version; for example:
     * 
     *      1252019  ->  01/25/2019
     *      
     * @param date      The date to be formatted    
     * @return a string containing the above information.
     */
    private String formatDate(int dt) {
        String date = "" + dt;
        if (date.length() == 7)
            date = "0" + date;
        date = date.substring(0,2) + "/" + date.substring(2,4) + "/" + date.substring(4);
        return date;
    }
    
    /**
     * Formats a money float to a more human-readable version; for example:
     * 
     *      2573.8  ->  $2,573.50
     *      
     * @param amount    The money float to be formatted
     * @return a string containing the above information.
     */
    private String formatMoney(float amount) {
        return String.format("$%,.02f", amount);
    }
}
