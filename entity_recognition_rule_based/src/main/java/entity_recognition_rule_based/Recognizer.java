package entity_recognition_rule_based;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Recognizer {
	
	private List<Token> tokenList;
	private Set<String> stopWords;
	private Map<String,String> dictionary;
	
	public Recognizer(List<Token> tokenList, Set<String> stopWords, Map<String,String> dictionary) {
		this.tokenList = tokenList;
		this.stopWords = stopWords;
		this.dictionary = dictionary;
	}
	
	public List<Token> recognize() {
//		System.out.println(this.tokenList.size());
//		System.out.println(this.genes.size());
//		System.out.println(this.stopWords.size());
		
		for (Token token : this.tokenList) {
			String lowerCaseToken = token.getToken().toLowerCase();
			if(!this.stopWords.contains(lowerCaseToken)) {
				if(this.dictionary.keySet().contains(lowerCaseToken)) token.setTag(this.dictionary.get(lowerCaseToken));
			}
		}
		
		return tokenList;
	}

}
