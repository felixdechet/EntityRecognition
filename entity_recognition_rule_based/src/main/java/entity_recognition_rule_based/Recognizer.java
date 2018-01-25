package entity_recognition_rule_based;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Recognizer {
	
	private List<Token> trainTokenList;
	private List<TaggedWord> posTrainingTokens;
	
	private Set<String> stopWords;
	private Map<String,String> dictionary;
	
	// RULES
	private Map<List<String>,Map<String,Double>> posTwoBeforeOneAfter;
	private Map<List<String>,Map<String,Double>> posOneBeforeOneAfter;
	private Map<String,Map<String,Double>> wordBefore;
	
	
	public Recognizer(List<Token> trainDataList, List<String> untaggedTrainDataList, Set<String> stopWords, Map<String,String> dictionary) throws FileNotFoundException {
		
		this.stopWords = stopWords;
		this.dictionary = dictionary;
		this.trainTokenList = trainDataList;
		
		// needed for POS tagging
		this.posTrainingTokens = new ArrayList<TaggedWord>();
		
		// POS Tagging
		MaxentTagger postagger = new MaxentTagger("postagger/english-left3words-distsim.tagger");
		List<List<HasWord>> sentences = postagger.tokenizeText(new StringReader(String.join("\n", untaggedTrainDataList)));
		
		System.out.println("POS-tagging on training data...");
	    for (List<HasWord> sentence : sentences) {
	      List<TaggedWord> tSentence =  postagger.tagSentence(sentence);
	      	for (TaggedWord taggedWord : tSentence) {
				this.posTrainingTokens.add(taggedWord);
			}
	    }
		System.out.println("POS-tagging on training data done.");

		Set<String> tags = new HashSet<String>();
		this.posTwoBeforeOneAfter = new HashMap<List<String>,Map<String,Double>>();
		this.posOneBeforeOneAfter = new HashMap<List<String>,Map<String,Double>>();
		this.wordBefore = new HashMap<String,Map<String,Double>>();
		
		for (int i = 0; i<this.trainTokenList.size(); i++) {
			Token tok = this.trainTokenList.get(i);
			String pos_tag = this.posTrainingTokens.get(i).tag();
			if (!tok.getTag().equals("O")) {
//				System.out.printf("%-20s%-20s%-20s%-20s%n", i+":", tok.getToken(), tok.getTag(), this.posTrainingTokens.get(i).tag());
				
				countRule(this.posTrainingTokens.get(i-2).tag(),this.posTrainingTokens.get(i-1).tag(),this.posTrainingTokens.get(i+1).tag());
				countRule(this.posTrainingTokens.get(i-1).word());
				tags.add(pos_tag);
			}
		}

		
		 // COUNT TOTAL POS RULES
		 for(int i = 2; i<this.posTrainingTokens.size()-1; i++) {
			 List<String> occTwoBefore = Arrays.asList(new String[] {this.posTrainingTokens.get(i-2).tag(),this.posTrainingTokens.get(i-1).tag(),this.posTrainingTokens.get(i+1).tag()});
			 List<String> occOneBefore = Arrays.asList(new String[] {this.posTrainingTokens.get(i-1).tag(),this.posTrainingTokens.get(i+1).tag()});

			 if(this.posTwoBeforeOneAfter.containsKey(occTwoBefore)) {
				 
				 Map<String,Double> ruleCountAndTotal = this.posTwoBeforeOneAfter.get(occTwoBefore);
				 
				 if (!ruleCountAndTotal.containsKey("total")) {
					 ruleCountAndTotal.put("total", 1.0);
				 }
				 else {
					 ruleCountAndTotal.replace("total", ruleCountAndTotal.get("total")+1);
				 }	

			 }
			 if(this.posOneBeforeOneAfter.containsKey(occOneBefore)) {
				 
				 Map<String,Double> ruleCountAndTotal = this.posOneBeforeOneAfter.get(occOneBefore);
				 
				 if (!ruleCountAndTotal.containsKey("total")) {
					 ruleCountAndTotal.put("total", 1.0);
				 }
				 else {
					 ruleCountAndTotal.replace("total", ruleCountAndTotal.get("total")+1);
				 }	

			 }
		 }
		 
		 
		 
		 // COUNT TOTAL WORD RULES
		 for(int i = 1; i<this.trainTokenList.size(); i++) {
			 
			 String occ = this.posTrainingTokens.get(i-1).word();
			 if(this.wordBefore.containsKey(occ)) {
				 
				 Map<String,Double> ruleCountAndTotal = this.wordBefore.get(occ);
				 
				 if (!ruleCountAndTotal.containsKey("total")) {
					 ruleCountAndTotal.put("total", 1.0);
				 }
				 else {
					 ruleCountAndTotal.replace("total", ruleCountAndTotal.get("total")+1.0);
				 }	

			 }
		 }
		 
	}
	



	private void countRule(String... content) {

		
		// if rule is defined by several arguments => POS rule
		if(content.length>1) {

			Map<String,Double> ruleCountAndTotalTwoBeforeOneAfter = new HashMap<String,Double>();
			List<String> ruleTwoBeforeOneAfter = Arrays.asList(content);
			if (!this.posTwoBeforeOneAfter.containsKey(ruleTwoBeforeOneAfter)) {
				
				ruleCountAndTotalTwoBeforeOneAfter.put("count", 1.0);
				this.posTwoBeforeOneAfter.put(ruleTwoBeforeOneAfter, ruleCountAndTotalTwoBeforeOneAfter);
				
			}
			else {
				ruleCountAndTotalTwoBeforeOneAfter = this.posTwoBeforeOneAfter.get(ruleTwoBeforeOneAfter);
				ruleCountAndTotalTwoBeforeOneAfter.replace("count", ruleCountAndTotalTwoBeforeOneAfter.get("count") + 1.0);
//				this.posBeforeAndAfter.replace(rule, ruleCountAndTotal);
			}
			
			Map<String,Double> ruleCountAndTotalOneBeforeOneAfter = new HashMap<String,Double>();
			List<String> ruleOneBeforeOneAfter = Arrays.asList(new String[] {content[1],content[2]});
			if(!this.posOneBeforeOneAfter.containsKey(ruleOneBeforeOneAfter)) {
				ruleCountAndTotalOneBeforeOneAfter.put("count", 1.0);
				this.posOneBeforeOneAfter.put(ruleOneBeforeOneAfter, ruleCountAndTotalOneBeforeOneAfter);
			}
			else {
				ruleCountAndTotalOneBeforeOneAfter = this.posOneBeforeOneAfter.get(ruleOneBeforeOneAfter);
				ruleCountAndTotalOneBeforeOneAfter.replace("count", ruleCountAndTotalOneBeforeOneAfter.get("count") + 1.0);
			}
			
		}
		// else rule is a word rule
		else {
			Map<String,Double> ruleCountAndTotal = new HashMap<String,Double>();
			String rule = content[0];

			if (!this.wordBefore.containsKey(rule)) {
				ruleCountAndTotal.put("count", 1.0);
				this.wordBefore.put(rule, ruleCountAndTotal);
			}
			else {
				ruleCountAndTotal = this.wordBefore.get(rule);
				ruleCountAndTotal.replace("count", ruleCountAndTotal.get("count") + 1.0);
//				this.wordBefore.replace(rule, ruleCountAndTotal);
			}
			
		}
		
	}
	

	public List<Token> recognize(List<Token> testTokenList, double threshold) {

		// POS Tagging Test Data
		List<String> untaggedTestTokenList = new ArrayList<String>();
		List<TaggedWord> posTestTokens = new ArrayList<TaggedWord>();
		
		for (Token tok :testTokenList) {
			untaggedTestTokenList.add(tok.getToken());
		}
		
		
		MaxentTagger postagger = new MaxentTagger("postagger/english-left3words-distsim.tagger");
		List<List<HasWord>> sentences = postagger.tokenizeText(new StringReader(String.join("\n", untaggedTestTokenList)));
		
		System.out.println("POS-tagging on test data...");
	    for (List<HasWord> sentence : sentences) {
	      List<TaggedWord> tSentence =  postagger.tagSentence(sentence);
	      	for (TaggedWord taggedWord : tSentence) {
	      		posTestTokens.add(taggedWord);
			}
	    }
		System.out.println("POS-tagging on test data done.");
		
		for (int i = 2; i < posTestTokens.size()-1; i ++) {
			
			Token token = testTokenList.get(i);
			
			String word = posTestTokens.get(i).word();
			String tag = posTestTokens.get(i).tag();
			
			String predecessorWord = posTestTokens.get(i-1).word();
			
			String[] tokenFeatures = new String[]{	
					
					word,							// WORD OF TOKEN
					tag,								// POS-TAG OF TOKEN
					predecessorWord.toLowerCase(),	// PREDECESSOR WORD 
					posTestTokens.get(i-2).tag(), 	// PRE PREDECESSOR TAG
					posTestTokens.get(i-1).tag(), 	// PREDECESSOR TAG
					posTestTokens.get(i+1).tag(), 	// SUCCESSOR TAG
					
			};
			if(!this.stopWords.contains(tokenFeatures[0].toLowerCase())) {
				if(isGene(tokenFeatures,i,threshold)) {
					token.setTag("B-protein");
					continue;
				}
			}

			token.setTag("O");
				
		}
		
		return testTokenList;
	}


	private boolean isGene(String[] tokenFeatures, int i, double threshold) {
		
		if (Arrays.asList("NN","NNP","NNS").contains(tokenFeatures[1])) {
			
			double probInDict = 0.0;
			double wordProb = 0.0;
			double posProb = 0.0;
			double wordStruct = 0.0;
			
			String[]posruleTwoBeforeStructure = new String[] {tokenFeatures[3],tokenFeatures[4],tokenFeatures[5]};
			String[]posruleOneBeforeStructure = new String[] {tokenFeatures[4],tokenFeatures[5]};
			
			// Rule #1: Dictionary look-up
			if(this.dictionary.keySet().contains(tokenFeatures[0].toLowerCase())) probInDict = threshold-0.1;
			
			// Rule #2: Word Before
			if(this.wordBefore.containsKey(tokenFeatures[2])) {
				wordProb = this.wordBefore.get(tokenFeatures[2]).get("count") / this.wordBefore.get(tokenFeatures[2]).get("total");
			}
			
			//Rule #3: POS Structure
			if(this.posTwoBeforeOneAfter.containsKey(Arrays.asList(posruleTwoBeforeStructure))) {
				Map<String,Double> countAndTotal = this.posTwoBeforeOneAfter.get(Arrays.asList(posruleTwoBeforeStructure));
				posProb = 2 * countAndTotal.get("count") / countAndTotal.get("total");
//				System.out.println(posProb);
			}
			else if(this.posOneBeforeOneAfter.containsKey(Arrays.asList(posruleOneBeforeStructure))) {
				Map<String,Double> countAndTotal = this.posOneBeforeOneAfter.get(Arrays.asList(posruleOneBeforeStructure));
				posProb += countAndTotal.get("count") / countAndTotal.get("total");
//				System.out.println(posProb);
			}
			
			//Rule #4: Structure of Word:
			if(tokenFeatures[0].matches(".*\\d+.*")  || (!StringUtils.isAllUpperCase(tokenFeatures[0])
					&& !StringUtils.isAllLowerCase(tokenFeatures[0])
					&& !Character.isUpperCase(tokenFeatures[0].charAt(0)))) {
				wordStruct += 0.8;
			}
			if(
					probInDict + 
					2*wordProb + 
					posProb + 
					wordStruct
					> threshold) {
				return true; 
			}
			
		}
		return false;
	}
		
		
		
	
		
	}
