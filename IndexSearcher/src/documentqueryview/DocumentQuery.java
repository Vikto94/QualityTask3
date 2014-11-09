package documentqueryview;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import vocabulary.AnalizeWord;
import vocabulary.Vocabulary;

public class DocumentQuery {
	public class Word{
		int number;
		HashSet<Integer> position;
		
		public Word (){
			number = 0;
			position = new HashSet<Integer>();		
		} 	
	}
	private static AnalizeWord analizeWord = new AnalizeWord();
	private Word[] dq;
	private String[] allWords;
	
	public DocumentQuery (String[] allWords, String dqString){
		this.allWords = allWords;
		this.dq = new Word[allWords.length];
		String[] dqArr = dqString.split("\\s*(;|,|\\s)\\s*");
		for (int i = 0; i < allWords.length; i++){
			this.dq[i] = new Word();
			for (int j = 0; j < dqArr.length; j++){
				if (analizeWord.ParseWords(dqArr[j]).contains(allWords[i])){
					dq[i].number++;
					dq[i].position.add(new Integer (j));
				}
			}
		}
	}
	
	public DocumentQuery (int size, String fileName, String[] allWords) throws FileNotFoundException{
		this.allWords = allWords;
		this.dq = new Word[size];
		Scanner in = new Scanner(new File(fileName));
		for (int i = 0; i < size; i++) {
			dq[i] = new Word ();
			dq[i].number = in.nextInt();
			dq[i].position = new HashSet<Integer>();
			for (int j = 0; j < dq[i].number; j++){
				int pos = in.nextInt();
				dq[i].position.add(new Integer (pos));
			}
		}
		in.close();
	}
	
	public double compareWithDQ (DocumentQuery documentQuery){
		if (dq.length != documentQuery.dq.length) return -1;
		double scProduct = 0;
		double thisLen = 0;
		double dqtcLen = 0;
		for (int i = 0; i < dq.length; i++){
			scProduct += (dq[i].number * documentQuery.dq[i].number);
			thisLen += (dq[i].number * dq[i].number);
			dqtcLen += (documentQuery.dq[i].number * documentQuery.dq[i].number);
		}
		thisLen = Math.sqrt (thisLen);
		dqtcLen = Math.sqrt (dqtcLen);
		return (scProduct / (thisLen * dqtcLen));
	}
	
	public boolean contains (String first, String second, int maxDist, char type){
		HashSet<String> firstValues = analizeWord.ParseWords(first);
		HashSet<String> secondValues = analizeWord.ParseWords(second);
		// get positions in document
		LinkedList<Integer> firstPos = new LinkedList<Integer>();
		LinkedList<Integer> secondPos = new LinkedList<Integer>();
		for (int i = 0; i < allWords.length; i++){
			if (firstValues.contains(allWords[i])){
				firstPos.addAll(dq[i].position);
			}
			if (secondValues.contains(allWords[i])){
				secondPos.addAll(dq[i].position);
			}
		}
		//Collections.sort(firstPos);
		//Collections.sort(secondPos);
		for (Integer firstForm : firstPos){
			for (Integer secondForm : secondPos){
				switch (type){
				case ' ':
					if (Math.abs(firstForm - secondForm) < maxDist) return true;
					break;
				case '+':
					if (Math.abs(firstForm - secondForm) < maxDist && firstForm <= secondForm) return true;
					break;
				case '-':
					if (Math.abs(firstForm - secondForm) < maxDist && firstForm >= secondForm) return true;
					break;
				}
			}	
		}
		return false;
	}
	
	public void print (BufferedWriter bw) throws IOException{
		for (Word word : dq){
			bw.write(word.number + " ");
			for (Integer pos : word.position){
				bw.write(pos + " ");
			}
			bw.write("\n");
		}
	}
}
