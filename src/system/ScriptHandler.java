package system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Parses and handles each of the commands given in the script file, passing them
 * along to the supplied database.
 * 
 * @author Aidan Sprague
 * @version 2020.12.23
 */
public class ScriptHandler {
    
    private File script;
    private Database database;
    private File log;
    
    /**
     * Constructor for the class. Simply passes the parameters as fields.
     * @param script        The script file to read from
     * @param database      The database to send commands to
     * @param log           The log file to write to
     */
    public ScriptHandler(File script, Database database, File log) {
        this.script = script;
        this.database = database;
        this.log = log;
    }
    
    /**
     * Parses the entire script file and attempts to send all of its commands to
     * the database. Utilizes a BufferedReader to read the script file, and a 
     * FileWriter to write to the log file.
     */
    public void parse() {
        BufferedReader reader;
        FileWriter writer;
        try {
            // Set up the file I/O tools.
            reader = new BufferedReader(new FileReader(script));
            writer = new FileWriter(log);
            
            String command = reader.readLine();
            int count = 1;
            boolean wasExited = false;
            
            // Read through all of the commands until EOF is reached.
            while (command != null) {
                writer.write("cmd " + count + ": " + command + "\n\n");
                String[] tokens = command.split("\\s+"); 
                
                // If "ERROR" is printed, something went wrong somewhere...
                String output = "ERROR"; 
                switch (tokens[0]) {
                    case "lookup_fund":
                        output = database.lookupFund(tokens[1]);
                        break;
                        
                    case "lookup_person":
                        output = database.lookupPerson(tokens[1]);
                        break;
                        
                    case "overview":
                        output = database.overview();
                        break;
                        
                    case "all_purchases":
                        output = database.allPurchases();
                        break;
                        
                    case "clear_data":
                        // Since this actually wipes the database, make sure the user
                        // is certain.
                        System.out.print("\nWARNING: You are about to clear the database (unreversible)."
                                         + "\nEnter 'CLEAR' to confirm:\n> ");
                        Scanner scanner = new Scanner(System.in);
                        String choice = scanner.nextLine();
                        if (choice.equals("CLEAR")) 
                            output = database.clearData();
                        else 
                            System.out.println("Unknown input '" + choice + "'; command <clear_data> terminated.");
                        
                        scanner.close();
                        break;
                        
                    case "add_fund":
                        output = database.addFund(tokens[1], tokens[2], tokens[3]);
                        break;
                        
                    case "add_purchase":
                        output = database.addPurchase(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]);
                        break;
                        
                    case "exit":
                        output = database.exit();
                        wasExited = true;
                        break;
                        
                    default:
                        output = "Invalid Command: " + tokens[0];
                }
                
                writer.write(output + "\n------------------------------------\n");
                command = reader.readLine();
                count++;
                
            }
            writer.close();
            if (!wasExited)
                database.exit();
            
        }
        catch (IOException e) {e.printStackTrace();}
        
    }
    
}
