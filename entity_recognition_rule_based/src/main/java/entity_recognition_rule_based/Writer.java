package entity_recognition_rule_based;

import java.io.*;
import java.util.List;

public class Writer {

    public void writeToFile(File outputFile, List<Token> tokens) {

        FileWriter writer;
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (Token tok : tokens) {
                bw.write(tok.getToken() + "\t" + tok.getTag());
                bw.newLine();
            }

//			writer = new FileWriter(outputFile);
//
//			StringBuilder builder = new StringBuilder();
//			for(Token tok: tokens) {
//			  builder.append(tok.getToken()).append("\t").append(tok.getTag()).append("\n");
//			}
//			writer.write(builder.toString());
//			writer.close();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
