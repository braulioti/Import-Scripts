package import_scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
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
}
