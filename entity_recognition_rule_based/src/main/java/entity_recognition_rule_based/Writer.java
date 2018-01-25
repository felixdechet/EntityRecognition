package entity_recognition_rule_based;

import java.io.*;
import java.util.List;

public class Writer {

    public void writeToFile(File outputFile, List<Token> tokens) {

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (Token tok : tokens) {
                bw.write(tok.getToken() + "\t" + tok.getTag());
                bw.newLine();
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
