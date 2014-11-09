package vocabulary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public class AnalizeWord {
	private static final Analyzer[] analyzers = new Analyzer[]{  
        new EnglishAnalyzer(Version.LUCENE_31),  
        new RussianAnalyzer(Version.LUCENE_31), 
        new StandardAnalyzer(Version.LUCENE_31)  
};  
	private LuceneMorphology luceneMorph;
	
	
	public AnalizeWord (){
		try {
			luceneMorph = new RussianLuceneMorphology();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashSet<String> ParseWords (String word){
		HashSet<String> res = new HashSet<String>();
		for (int i = 0; i < analyzers.length; i++){
			Analyzer analyzer = analyzers[i];
			TokenStream stream = analyzer.tokenStream("contents", new StringReader(word)); 
         try {
				while (stream.incrementToken()) {   
				    AttributeSource token = stream.cloneAttributes();  
				    CharTermAttribute term =(CharTermAttribute) token.addAttribute(CharTermAttribute.class);  
				    String morph = term.toString();
				    res.add(morph);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		try {
			List<String> wordBaseForms = luceneMorph.getNormalForms(word);//.getMorphInfo(word);
			for (String morph : wordBaseForms){
				res.add(morph);
			}
		} catch (WrongCharaterException e){
		}
		return res;
	}
}
