package import_scripts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ImportScripts { 
	
    public static void main(String[] args) {
    	ScriptFileImport fileImport = new ScriptFileImport();

        if (args.length > 0) {
        	try {
        		if (args[0].toLowerCase().equals("folder")) {
        			Versioning versioning = new Versioning();
        			versioning.loadConfigFile(args[4]);

					Connection con = loadConnection(args[1], args[2], args[3]);
					versioning.setConnection(con);
					versioning.createVersionTable();
				} else {
					Connection con = loadConnection(args[0], args[1], args[2]);
					fileImport.loadLines(args[3]);

					for (int i=0; i<fileImport.getLines().size(); i++) {
						Statement st = con.createStatement();
						st.execute(fileImport.getLines().get(i));
					}
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
	        Connection con = null;
	        StringBuilder url = new StringBuilder();
	        url.append("jdbc:postgresql://").append(connection);
	        
			con = (Connection) DriverManager.getConnection(url.toString(), user, password);

			return con;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
	      	return null;
		} catch (SQLException e) {
			e.printStackTrace();
	      	return null;
		}
    }


}
