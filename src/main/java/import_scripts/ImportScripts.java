package import_scripts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ImportScripts { 
	
    public static void main(String[] args) {
    	ScriptFileImport fileImport = new ScriptFileImport();

        if (args.length > 0) {
        	try {
        		if (args[0].equalsIgnoreCase("folder")) {
        			Versioning versioning = new Versioning();
        			versioning.loadConfigFile(args[4]);

					Connection con = loadConnection(args[1], args[2], args[3]);
					versioning.setConnection(con);
					versioning.createVersionTable();
					versioning.executeScripts();
				} else {
					Connection con = loadConnection(args[0], args[1], args[2]);
					fileImport.setConnection(con);
					fileImport.loadLines(args[3]);
					fileImport.executeScript();
        		}
        	} catch(Exception e) {
    			e.printStackTrace();        		
        	}
        }
    }
    
    private static Connection loadConnection(String connection, String user, String password) {
        String driver = "org.postgresql.Driver";

        try {
			Class.forName(driver);
	        StringBuilder url = new StringBuilder();
	        url.append("jdbc:postgresql://").append(connection);

            return DriverManager.getConnection(url.toString(), user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
	      	return null;
		} catch (SQLException e) {
			e.printStackTrace();
	      	return null;
		}
    }


}
