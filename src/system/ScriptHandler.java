package system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * 
 * @author Aidan Sprague
 * @version 2020.12.21
 */
public class ScriptHandler {
    
    private File script;
    private Database database;
    private File log;
    
    public ScriptHandler(File script, Database database, File log) {
        this.script = script;
        this.database = database;
        this.log = log;
    }
    
    public void parse() {
        BufferedReader reader;
        FileWriter writer;
        try {
            reader = new BufferedReader(new FileReader(script));
            writer = new FileWriter(log);
            
            String command = reader.readLine();
            int count = 1;
            while (command != null) {
                writer.write("cmd " + count + ": " + command + "\n\n");
                String[] tokens = command.split("\\s+"); 
                
                // If "ERROR" is printed, something went wrong...
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
                        
                    case "recent":
                        output = database.recent();
                        break;
                        
                    case "clear_data":
                        System.out.print("\nWARNING: You are about to clear the database (unreversible).\nEnter 'CLEAR' to confirm:\n> ");
                        Scanner scanner = new Scanner(System.in);
                        String choice = scanner.nextLine();
                        if (choice.equals("CLEAR")) {
                            output = database.clearData();
                        }
                        else {
                            System.out.println("Unknown input '" + choice + "'; command <clear_data> terminated.");
                        }
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
                        break;
                        
                    default:
                        output = "Invalid Command: " + tokens[0];
                        database.exit();
                }
                writer.write(output + "\n------------------------------------\n");
                command = reader.readLine();
                count++;
            }
            writer.close();
        }
        catch (IOException e) {e.printStackTrace();}
    }
    
}
