package import_scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.lineSeparator;

public class ScriptFileImport {
    private List<String> lines;
    private Connection con;

    public void loadLines(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();

            String[] arrayLines = everything.split("\r\n");
            this.lines = new ArrayList<String>();
            StringBuilder lineAux = new StringBuilder();

            for (int i=0; i<arrayLines.length; i++) {
                lineAux.append(" ").append(arrayLines[i].trim());
                if ((arrayLines[i].trim().length() > 0) && (arrayLines[i].trim().substring(arrayLines[i].trim().length()-1, arrayLines[i].trim().length()).equals(";"))) {
                    this.lines.add(lineAux.toString());
                    lineAux = new StringBuilder();
                }
            }

            for (int i=0; i<this.lines.size(); i++) {
                lineAux = new StringBuilder();
                lineAux.append(i).append(" - ").append(this.lines.get(i));
            }
        } finally {
            br.close();
        }
    }

    public void setConnection(Connection con) {
        this.con = con;
    }

    public void executeScript() throws SQLException {
        for (int i = 0; i < this.lines.size(); i++) {
            Statement st = this.con.createStatement();
            st.execute(this.lines.get(i));
        }
    }
}
