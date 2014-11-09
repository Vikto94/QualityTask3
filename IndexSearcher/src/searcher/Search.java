/**
 * 
 */
package searcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import vocabulary.Vocabulary;
import indexer.*;

/**
 * @author Viktor
 *
 */
public class Search {
	private final String path;
	private final File folder;
	static private final int maxNumberOfPrintedFilesNames = 2;
	private final String encoding = "Cp866";
	private final QueryHandler queryHandler;
	
	public Search (long index){
		path = Index.indexToPath(index);
		folder = new File (path);
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.scanVocabulary(path);
		queryHandler = new QueryHandler (folder.list(), path, vocabulary.getAllWords());
	}
	
	public void run (){
		String query = "";
		Scanner sc= new Scanner(System.in, encoding);
		while (true){
			query = sc.nextLine();
			if (query.equalsIgnoreCase("q") || query.equalsIgnoreCase("")) break;
			System.out.print(queryHandler.RunQuery(query) + "\n");
		}
		sc.close();
	}
	
	private String runQuery (String query){
		String[] queryArr = query.split("\\s");
		if (queryArr.length % 2 == 0){
			return "incorrect query";
		}
		else if (queryArr.length == 1){
			return runQueryAND(queryArr);
		}
		else if (queryArr[1].equals("AND")){
			for (int i = 1; i < queryArr.length; i += 2){
				if (!queryArr[1].equals("AND")){
					return "incorrect query";
				}
			}
			return runQueryAND(queryArr);
		}
		else if (queryArr[1].equals("OR")){
			for (int i = 1; i < queryArr.length; i += 2){
				if (!queryArr[1].equals("OR")){
					return "incorrect query";
				}
			}
			return runQueryOR(queryArr);
		}
		else return "incorrect query";
	}
	
	
	private String runQueryAND (String[] query){
		String res = "";
		String[] fileList = folder.list();
		int numberOfFindFiles = 0;
		for (String fileName : fileList){
			try {
				TokenSearcher tokenSearcher = new TokenSearcher (path + "\\" + fileName);
				boolean isFind = true;
				for (int i = 0; i < query.length; i += 2){
					if (!tokenSearcher.isFind(query[i])){
						isFind = false;
						break;
					}					
				}
				if (isFind){
					if (numberOfFindFiles < maxNumberOfPrintedFilesNames){
						res = res.concat(fileName).concat(" ");
					}
					numberOfFindFiles++;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (res.equals("")) res = "no documents found";
		else if (numberOfFindFiles > maxNumberOfPrintedFilesNames){
			numberOfFindFiles -= 2;
			res = res + "and " + numberOfFindFiles + " more";
		}
		return res;		
	}
	
	private String runQueryOR (String[] query){
		String res = "";
		String[] fileList = folder.list();
		int numberOfFindFiles = 0;
		for (String fileName : fileList){
			try {
				TokenSearcher tokenSearcher = new TokenSearcher (path + "\\" + fileName);
				boolean isFind = false;
				for (int i = 0; i < query.length; i += 2){
					if (tokenSearcher.isFind(query[i])){
						isFind = true;
						break;
					}					
				}
				if (isFind){
					if (numberOfFindFiles < maxNumberOfPrintedFilesNames){
						res = res.concat(fileName).concat(" ");
					}
					numberOfFindFiles++;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (res.equals("")) res = "no documents found";
		else if (numberOfFindFiles > maxNumberOfPrintedFilesNames){
			numberOfFindFiles -= 2;
			res = res + "and " + numberOfFindFiles + " more";
		}
		return res;		
	}
}
