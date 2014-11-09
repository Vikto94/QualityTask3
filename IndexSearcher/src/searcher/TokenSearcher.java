package searcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.morphology.EnglishLuceneMorphology;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;



public class TokenSearcher {
	private final HashSet<String> morphemeInFile = new HashSet<String>();	
	private static final Analyzer[] analyzers = new Analyzer[]{  
           new EnglishAnalyzer(Version.LUCENE_31),  
           new RussianAnalyzer(Version.LUCENE_31), 
           new StandardAnalyzer(Version.LUCENE_31)  
   };  
	private LuceneMorphology luceneMorph;
 	
	
	TokenSearcher (String fileName) throws FileNotFoundException{
		Scanner in = new Scanner(new File(fileName));
		String word = "";
		while (in.hasNext()) {
			word = in.next();
			morphemeInFile.add(word);
		}
		in.close();
		try {
			luceneMorph = new RussianLuceneMorphology();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isFind (String word){
		for (int i = 0; i < analyzers.length; i++){
			Analyzer analyzer = analyzers[i];
			TokenStream stream = analyzer.tokenStream("contents", new StringReader(word)); 
            try {
				while (stream.incrementToken()) {   
				    AttributeSource token = stream.cloneAttributes();  
				    CharTermAttribute term =(CharTermAttribute) token.addAttribute(CharTermAttribute.class);  
				    String morph = term.toString();
				    if (morphemeInFile.contains(morph)){
				    	return true;
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}  
		}
		try {
			List<String> wordBaseForms = luceneMorph.getNormalForms(word);//.getMorphInfo(word);
			for (String morph : wordBaseForms){
			    if (morphemeInFile.contains(morph)){
			    	return true;
			    }
			}
		} catch (WrongCharaterException e){
			return false;
		}
		return false;
	}
}
