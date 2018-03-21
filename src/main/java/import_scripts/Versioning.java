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
    private String directory;

    public void loadConfigFile(String directory) throws IOException {
        StringBuilder configFile = new StringBuilder();

        configFile.append(directory).append("/files.conf");
        BufferedReader br = new BufferedReader(new FileReader(configFile.toString()));

        this.directory = directory;

        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();

            String[] arrayLines = everything.split("\n");
            this.configFile = new ArrayList<String>();
            for (int i = 0; i < arrayLines.length; i++) {
                this.configFile.add(arrayLines[i]);
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
            if (rs.next() && rs.getInt("isVersion") == 0) {
                query =
                        "CREATE TABLE public.tb_version ( " +
                        "id          SERIAL NOT NULL, " +
                        "script_file VARCHAR(40), " +
                        "exec_date   TIMESTAMP NOT NULL DEFAULT NOW(), " +
                        "CONSTRAINT  pk_tb_version PRIMARY KEY (id) );";
                st.execute(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeScripts() {
        this.configFile.forEach((String item) -> {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT COUNT(1) AS executed FROM tb_version WHERE script_file = '");
            sb.append(item.trim()).append("';");

            try {
                Statement st = this.con.createStatement();
                ResultSet rs = st.executeQuery(sb.toString());
                if (rs.next() && (rs.getInt("executed") == 0)) {
                    sb = new StringBuilder();
                    sb.append(this.directory).append("/").append(item);

                    ScriptFileImport fileImport = new ScriptFileImport();
                    fileImport.setConnection(this.con);
                    fileImport.loadLines(sb.toString().trim());
                    fileImport.executeScript();

                    sb = new StringBuilder();
                    sb.append("INSERT INTO public.tb_version (script_file) VALUES ('");
                    sb.append(item.trim()).append("');");
                    st.execute(sb.toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
