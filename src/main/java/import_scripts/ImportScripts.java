package import_scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ImportScripts { 
	
    public static void main(String[] args) {
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
					List<String> lines = loadLines(args[3]);

					for (int i=0; i<lines.size(); i++) {
						Statement st = con.createStatement();
						st.execute(lines.get(i));
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

    private static List<String> loadLines(String filename) throws IOException {
    	BufferedReader br = new BufferedReader(new FileReader(filename));
    	try {
    	    StringBuilder sb = new StringBuilder();
    	    String line = br.readLine();

    	    while (line != null) {
    	        sb.append(line);
    	        sb.append(System.lineSeparator());
    	        line = br.readLine();
    	    }
    	    String everything = sb.toString();
    	    
    	    String[] arrayLines = everything.split("\r\n");
    	    List<String> arrayAux = new ArrayList<String>();
	    	StringBuilder lineAux = new StringBuilder();
    	    
    	    for (int i=0; i<arrayLines.length; i++) {
    	    	lineAux.append(" ").append(arrayLines[i].trim());
    	    	if ((arrayLines[i].trim().length() > 0) && (arrayLines[i].trim().substring(arrayLines[i].trim().length()-1, arrayLines[i].trim().length()).equals(";"))) {
    	    			arrayAux.add(lineAux.toString());
    	    			lineAux = new StringBuilder();
    	    	}
    	    }
    	    
    	    for (int i=0; i<arrayAux.size(); i++) {
    	    	lineAux = new StringBuilder();
    	    	lineAux.append(i).append(" - ").append(arrayAux.get(i));
    	    }    	    
    	    
    	    return arrayAux;
    	} finally {
    	    br.close();
    	}
    }
}
