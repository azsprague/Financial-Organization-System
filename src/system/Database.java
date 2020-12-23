package system;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author Aidan Sprague
 * @version 2020.12.21
 */
public class Database {
    
    private Connection connection;
    private Statement statement;

    /**
     * Constructor for the class.
     * @param file  A valid RandomAccessFile with database info
     */
    public Database(File file) {
        // Create a database connection
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
     * 
     * @param fund
     * @return
     */
    public String lookupFund(String fund) {
        return "lookup_fund not yet implemented";
    }
    
    /**
     * 
     * @param person
     * @return
     */
    public String lookupPerson(String person) {
        return "lookup_person not yet implemented";
    }
    
    /**
     * 
     * @return
     */
    public String overview() {
        return "overview not yet implemented";
    }
    
    /**
     * 
     * @return
     */
    public String recent() {
        return "recent not yet implemented";
    }
    
    /**
     * 
     * @return
     */
    public String clearData() {
        try {
            statement.executeUpdate("drop table funds");
            statement.executeUpdate("drop table purchases");
        }
        catch (SQLException e) {e.printStackTrace();}
        
        return "Database Cleared.";
    }
    
    /**
     * 
     * @param fund
     * @param amount
     * @param expiration
     * @return
     */
    public String addFund(String fund, String amount, String expiration) {
        try {
            // Format the expiration date and amount
            int exp = Integer.parseInt(expiration.replaceAll("[^\\d.]", ""));
            float am = Float.parseFloat(amount.replaceAll("/[^0-9.]/g", ""));
            
            // Create the fund table if necessary
            statement.executeUpdate("create table if not exists funds ("
                + "fund_name text, "
                + "expiration integer, "
                + "amount real, "
                + "primary key (fund_name))");
            
            // Add the fund
            statement.executeUpdate("insert into funds values('" + fund + "', " + exp + 
                ", " + am + ")");
        }
        catch (SQLException e) {e.printStackTrace();}
        
        // Let the user know it was updated
        String type = fund.substring(0, 3);
        String term = fund.substring(3, 4);
        if (term.equals("S")) 
            term = "Spring";
        else 
            term = "Fall";
        String year = fund.substring(4);
        year = "20" + year;
        
        return type + " " + term + " " + year + " added to databse.";
    }
    
    /**
     * 
     * @param fund
     * @param amount
     * @param person
     * @param date
     * @param info
     */
    public String addPurchase(String fund, String amount, String person, String date, String info) {
        try {
            // Format the date and amount
            int dt = Integer.parseInt(date.replaceAll("[^\\d.]", ""));
            float am = Float.parseFloat(amount.replaceAll("/[^0-9.]/g", ""));
            
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
        }
        catch (SQLException e) {e.printStackTrace();}
        
        // Let the user know it was updated
        String type = fund.substring(0, 3);
        String term = fund.substring(3, 4);
        if (term.equals("S")) 
            term = "Spring";
        else 
            term = "Fall";
        String year = fund.substring(4);
        year = "20" + year;
        
        return "Purchase added to " + type + " " + term + " " + year;
    }
    
    /**
     * 
     * @return
     */
    public String exit() {
        // Close the connection
        try {
            if (connection != null)
                connection.close();
        }
        catch (SQLException e) {e.printStackTrace();}
        
        return "Exiting the Program.";
    }
}
