package vocabulary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

public class Vocabulary {
	private String[] allWords;
	static private final String vocabularyFilename = "index.voc";
	
	public Vocabulary (String[] allWords){
		this.allWords = allWords;
	}
	
	public Vocabulary (Collection<String> words){
		allWords = (String[]) words.toArray(new String[words.size()]);
	}
	
	public Vocabulary() {
	}

	public String[] getAllWords (){
		return allWords;
	}
	
	public void printVocabulary (String path){
		new File(path).mkdirs();
		File f = new File(path + "\\" + vocabularyFilename);
		if (f.exists()) f.delete();
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(path.concat("\\").concat(vocabularyFilename),true));
			for (String morph : allWords){
				bw.write(morph.concat("\n"));
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.print ("can not find directory");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print ("can not find directory");
		}
	}
	
	public void scanVocabulary (String path){
		Scanner in;
		HashSet<String> words = new HashSet<String>();
		try {
			in = new Scanner(new File(path.concat("\\").concat(vocabularyFilename)));
			String word = "";
			while (in.hasNext()) {
				word = in.next();
				words.add(word);
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.print ("wrong index");
		}
		allWords = (String[]) words.toArray(new String[words.size()]);
	}
}
