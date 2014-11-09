/**
 * 
 */
package indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

import documentqueryview.DocumentQuery;
import documentqueryview.DocumentQuery.Word;
import vocabulary.Vocabulary;

/**
 * @author Viktor
 *
 */


public class Index {
	private final long index;
	private final String path;
	private final File folder;
	private final String[] fileList;
	private static final Integer maxPathLen = new Integer(200);
	private static final Analyzer[] analyzers = new Analyzer[]{  
        new EnglishAnalyzer(Version.LUCENE_31),  
        new org.apache.lucene.analysis.ru.RussianAnalyzer(Version.LUCENE_31),  
        new StopAnalyzer(Version.LUCENE_31),  
        new StandardAnalyzer(Version.LUCENE_31)  
    };  
	private LuceneMorphology luceneMorph;
	private Vocabulary vocabulary;
	
	public Index (long index){
		try {
			luceneMorph = new RussianLuceneMorphology();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			luceneMorph = null;
		}
		this.index = index;
		path = indexToPath (index);
		folder = new File (path);
		fileList = folder.list();
	}
	
	public Index (String path){
		try {
			luceneMorph = new RussianLuceneMorphology();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			luceneMorph = null;
		}
		this.path = path.concat("\\index");
		folder = new File (path);
		folder.mkdirs();
		fileList = folder.list();
		buildVocabulary(path);
		buildIndex(path);
		index = pathToIndex (this.path);
	}	
	
	public void printVocabulary (){
		vocabulary.printVocabulary(path);
	}
	
	public Vocabulary getVocabulary (){
		return vocabulary;
	}
	
	public long getIndex (){
		return index;
	}
	
	public String getPath (){
		return path;
	}
	
	public void buildVocabulary (String path){
		HashSet<String> hvocabulary = new HashSet<String>();
		for (String fileName : fileList){
			File f = new File(path + "\\" + fileName);
			if (f.isDirectory()) continue;
			try {
				Scanner in = new Scanner(new File(path + "\\" + fileName));
				String word = "";
				while (in.hasNext()) {
					word = in.next();
					for (int i = 0; i < analyzers.length; i++){
						Analyzer analyzer = analyzers[i];
						TokenStream stream = analyzer.tokenStream("contents", new StringReader(word)); 
			            while (stream.incrementToken()) {   
			                AttributeSource token = stream.cloneAttributes();  
			                CharTermAttribute term =(CharTermAttribute) token.addAttribute(CharTermAttribute.class);  
			                String morphToAdd = term.toString();
			                hvocabulary.add(morphToAdd);
			            }  
					}
					if (luceneMorph != null){
						try {
							List<String> wordBaseForms = luceneMorph.getNormalForms(word);//.getMorphInfo(word);
							for (String morph : wordBaseForms){
						    	hvocabulary.add(morph);
							}
						} catch (WrongCharaterException e){
						}
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.print ("can not find directory");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.print ("can not find directory");
			}
		}
		vocabulary = new Vocabulary(hvocabulary);
	}
	
	public void buildIndex (String path){
		for (String fileName : fileList){
			File f = new File(path + "\\" + fileName);
			if (f.isDirectory()) continue;
			try {
				Scanner in = new Scanner(new File(path + "\\" + fileName));
				String text = "";
				while (in.hasNext()) {
					text = text.concat(in.next().concat(" "));
				}
				if (text.equals("")) continue;
				DocumentQuery documentQuery = new DocumentQuery (vocabulary.getAllWords(), text);
				new File(this.path).mkdirs();
				f = new File(this.path + "\\" + fileName);
				if (f.exists()) f.delete();
				BufferedWriter bw = new BufferedWriter(new FileWriter(this.path.concat("\\").concat(fileName),true));
				documentQuery.print(bw);
				bw.close();
				in.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.print ("can not find directory");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.print ("can not find directory");
			}
		}
	}
	
	public static long pathToIndex (String path){
		long index = 0;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		try {
			Connection bd = DriverManager.getConnection("jdbc:sqlite:sqlite.db_ip");
			Statement st = bd.createStatement();
			String query = "create table if not exists pathIndexes "
					.concat("(id INTEGER PRIMARY KEY AUTOINCREMENT, path VARCHAR(")
					.concat(Objects.toString(maxPathLen))
					.concat("));");
			st.executeUpdate (query);
			query = "select id from pathIndexes where path LIKE '"
					.concat(path)
					.concat("'");
			ResultSet rs = st.executeQuery(query);
			if (rs.next()){
				index = rs.getLong(1);
			}
			else
			{
				query = "insert into pathIndexes (path) values ( '"
						.concat(path)
						.concat("');");
				st.executeUpdate (query);
				query = "select id from pathIndexes where path LIKE '"
						.concat(path)
						.concat("';");
				ResultSet rs1 = st.executeQuery(query);
				if (rs1.next()){
					index = rs1.getLong("id");
				}
				else index = -1;
			}
			st.close();
			bd.close();
			return index;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	public static String indexToPath (long index){
		String path = "";
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			Connection bd = DriverManager.getConnection("jdbc:sqlite:sqlite.db_ip");
			Statement st = bd.createStatement();
			String query = "select path from pathIndexes where id LIKE '"
					.concat(Objects.toString(new Long (index)))
					.concat("';");
			ResultSet rs = st.executeQuery(query);
			if (rs.next()){
				path = rs.getString(1);
			}
			else path = null;
			st.close();
			bd.close();
			return path;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	
	
}
