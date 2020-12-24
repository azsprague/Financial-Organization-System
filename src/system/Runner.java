package system;

import java.io.File;

/**
 * Runner class for the system. Contains the main() method. Invocation for the
 * system (as a jar file) is:
 * 
 *      java -jar FOS.jar <Script File> <DB File>
 * 
 * @author Aidan Sprague
 * @version 2020.12.23
 */
public class Runner {
    
    /**
     * Runs the system. Checks the parameters and files before sending them off to
     * a ScriptHandler.
     * @param args      System arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invocation: java -jar FOS.jar <Script File> <DB File>");
            System.exit(1);
        }
        
        File scriptFile = new File(args[0]);
        if (!scriptFile.exists()) {
            System.err.println("Error Opening Script File <" + scriptFile.getName() + ">.");
            System.exit(1);
        }
        
        File databaseFile = new File(args[1]);
        if (!databaseFile.exists()) {
            System.err.println("Error Opening Database File <" + databaseFile.getName() + ">.");
            System.exit(1);
        }
        
        File logFile = new File("log.txt");
        if (!logFile.exists()) {
            System.err.println("Error Creating Log File.");
            System.exit(1);
        }
        
        Database database = new Database(databaseFile);
        ScriptHandler handler = new ScriptHandler(scriptFile, database, logFile);
        handler.parse();
        
        System.out.println("\nResults copied to log.txt.");
    }

}
