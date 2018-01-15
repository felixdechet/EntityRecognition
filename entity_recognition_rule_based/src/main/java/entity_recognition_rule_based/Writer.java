package entity_recognition_rule_based;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Writer {

	public void writeToFile(File outputFile, List<Token> tokens) {
		
		FileWriter writer;
		try {
			
			writer = new FileWriter(outputFile);
			
			for(Token tok: tokens) {
			  writer.write(String.format("%-40s%s%n", tok.getToken(), tok.getTag()));
			}
			writer.close();

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
}
