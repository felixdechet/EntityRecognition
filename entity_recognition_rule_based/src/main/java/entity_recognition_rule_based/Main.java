package entity_recognition_rule_based;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class Main {

	public static void main(String[] args) {
		
		File inputFile;
		File outputFile;
		
		// load tokens from file
		if(args.length != 2) {
			System.out.println("Parameter count not valid.");
			return;
		}
		
		inputFile = new File(args[0]);
		outputFile = new File(args[1]);
		
		List<Token> testDataList = new ArrayList<Token>();
		List<String> trainDataList = new ArrayList<String>();
		
		Set<String> stopWords = new HashSet<String>();
		Map<String,String> dictionary = new HashMap<String,String>();
		
		// Read Testing File
		try {
			String content = FileUtils.readFileToString(inputFile, "UTF-8");
			String[] lines = content.split("[\\r\\n]+");
			
			for (String s : lines) {
				String[] splitWordTag = s.split("\\t");
				testDataList.add(new Token(splitWordTag[0],splitWordTag[1]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Read stop words
		try {
			String stopwords_content = FileUtils.readFileToString(new File("english_stop_words.txt"), "UTF-8");
			String[] stopwords_entries = stopwords_content.split("[\\r\\n]+");
			for (String s : stopwords_entries) {
				stopWords.add(s);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		// Read Gene dictionary and add it to general dictionary
		try {
			String genes_content = FileUtils.readFileToString(new File("human-genenames.txt"), "UTF-8");
			String[] genes_entries = genes_content.split("[\\r\\n]+");
			for (String s : genes_entries) {
				dictionary.put(s.toLowerCase(), "B-protein");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		// Read Training dataset and add tagged tokens to general dictionary
		try {
			String training_content = FileUtils.readFileToString(new File("uebung4-training.iob"), "UTF-8");
			String[] training_entries = training_content.split("[\\r\\n]+");
			
			
			for (String s : training_entries) {
				String[] splitWordTag = s.split("\\t");
				trainDataList.add(splitWordTag[0]);
				if(!splitWordTag[1].equals("0")) dictionary.put(splitWordTag[0], splitWordTag[1]);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		try {
			Recognizer recognizer = new Recognizer(trainDataList,stopWords,dictionary);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		List<Token> taggedList = recognizer.recognize(testDataList);
//		Writer writer = new Writer();
//		writer.writeToFile(outputFile, taggedList);
		
	}
}
