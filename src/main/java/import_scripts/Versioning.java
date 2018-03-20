package import_scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.lineSeparator;

public class Versioning {
    private List<String> configFile;
    private Connection con;

    public void loadConfigFile(String directory) throws IOException {
        StringBuilder configFile = new StringBuilder();

        configFile.append(directory).append("/files.conf");
        BufferedReader br = new BufferedReader(new FileReader(configFile.toString()));

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
            this.configFile = new ArrayList<String>();

            for (int i=0; i<arrayLines.length; i++) {
                System.out.println(arrayLines[i]);
            }
        } finally {
            br.close();
        }
    }

    public void setConnection(Connection con) {
        this.con = con;
    }

    public void createVersionTable() {

        String query = "SELECT COUNT(1) AS isVersion FROM pg_catalog.pg_tables WHERE tablename = 'tb_version';";
        try {
            Statement st = this.con.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                System.out.println(rs.getInt("isVersion"));
                if (rs.getInt("isVersion") == 0) {
                    query =
                            "CREATE TABLE public.tb_version ( " +
                            "id          SERIAL NOT NULL, " +
                            "script_file VARCHAR(40), " +
                            "exec_date   TIMESTAMP NOT NULL DEFAULT NOW(), " +
                            "CONSTRAINT  pk_tb_version PRIMARY KEY (id) );";
                    st.execute(query);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
