package searcher;

import java.io.FileNotFoundException;

import documentqueryview.DocumentQuery;

public class QueryHandler {
	public class DocumentIndex{
		DocumentQuery documentQuery;
		double coef;
		String name;
		
		public DocumentIndex (){
			coef = 2;
			name = null;
			documentQuery = null;
		}
	}
	
	private DocumentIndex[] di;
	String[] allWords;
	
	QueryHandler (String[] fileList, String path, String[] allWords){
		di = new DocumentIndex[fileList.length - 1];
		int number = 0;
		this.allWords = allWords;
		for (String fileName : fileList){
			if (fileName.contains(".voc")) continue;
			try {
				di[number] = new DocumentIndex();
				di[number].documentQuery = new DocumentQuery (allWords.length, path + "\\" + fileName, allWords);
				di[number].name = fileName;
				di[number].coef = 0;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			number++;
		}
	}
	
	private void SetStartSituation (){
		for (int j = 0; j < di.length; j++){
			di[j].coef = 2;
		}		
	}
	
	String RunQuery (String query){
		SetStartSituation();
		String queryToDocument = "";
		String[] words = query.split("\\s");
		queryToDocument = queryToDocument.concat(words[0]).concat(" ").concat(words[words.length - 1]);
		for (int i = 1; i < words.length - 1; i++){
			if (words[i].charAt(0) == '/'){
				char type = ' ';
				if (words[i].length() < 2) return "incorrect query";
				else if (words[i].charAt(1) == '+') type = '+';
				else if (words[i].charAt(1) == '-') type = '-';
				for (int j = 0; j < di.length; j++){
					if (di[j].coef < 0.1) continue;
					if (!di[j].documentQuery.contains(words[i-1], words[i+1], 
							Integer.parseInt(words[i].substring(1)), type))
						di[j].coef = 0;
				}
			}
			else 
				queryToDocument = queryToDocument.concat(" ").concat(words[0]);
		}
		DocumentQuery curQuery = new DocumentQuery (allWords, queryToDocument);
		int indMax1 = 0, indMax2 = 0;
		double max1 = 0, max2 = 0;
		int numberOfResults = 0;
		for (int j = 0; j < di.length; j++){
			if (di[j].coef < 0.1) continue;
			di[j].coef = di[j].documentQuery.compareWithDQ(curQuery);
			if (di[j].coef > 0.1) numberOfResults++;
			if (di[j].coef > max1) {
				max2 = max1;
				max1 = di[j].coef;
				indMax2 = indMax1;
				indMax1 = j;
			} else if (di[j].coef > max2) {
				max2 = di[j].coef;
				indMax2 = j;
			}
		}
		if (max1 <= 0.1) return "no documents found";
		else if (max2 <= 0.1) return di[indMax1].name;
		else if (numberOfResults < 3) return (di[indMax1].name + " " + di[indMax2].name);
		else return (di[indMax1].name + " " + di[indMax2].name + " and " + numberOfResults + " more");
	}
}
