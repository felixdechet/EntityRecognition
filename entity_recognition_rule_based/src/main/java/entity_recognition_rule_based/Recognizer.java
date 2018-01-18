package entity_recognition_rule_based;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Recognizer {
	
	private List<String> trainTokenList;
	private List<List<TaggedWord>> taggedTrainingTokens;
	
	private Set<String> stopWords;
	private Map<String,String> dictionary;
	
	public Recognizer(List<String> trainDataList, Set<String> stopWords, Map<String,String> dictionary) throws FileNotFoundException {
		
		this.stopWords = stopWords;
		this.dictionary = dictionary;
		this.trainTokenList = trainDataList;
		
		// needed for POS tagging
		this.taggedTrainingTokens = new ArrayList<List<TaggedWord>>();
		
		// POS Tagging
		MaxentTagger postagger = new MaxentTagger("postagger/english-left3words-distsim.tagger");
		List<List<HasWord>> sentences = postagger.tokenizeText(new StringReader(String.join("\n", this.trainTokenList)));
		
		System.out.println("POS-tagging on training data...");
	    for (List<HasWord> sentence : sentences) {
	      List<TaggedWord> tSentence =  postagger.tagSentence(sentence);
	      this.taggedTrainingTokens.add(tSentence);
	    }
		System.out.println("POS-tagging on training data done.");
		
	}
	


	public List<Token> recognize(List<Token> testTokenList) {
//		System.out.println(this.tokenList.size());
//		System.out.println(this.genes.size());
//		System.out.println(this.stopWords.size());
		
		for (Token token : testTokenList) {
			String lowerCaseToken = token.getToken().toLowerCase();
			if(!this.stopWords.contains(lowerCaseToken)) {
				if(this.dictionary.keySet().contains(lowerCaseToken)) token.setTag(this.dictionary.get(lowerCaseToken));
			}
		}
		
		return testTokenList;
	}

}
