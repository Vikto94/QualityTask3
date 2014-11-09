import indexer.Index;

/**
 * 
 */

/**
 * @author Viktor
 *
 */
public class StartPointIndexer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length > 0){
			Index index = new Index (args[0]);
			index.printVocabulary();
			System.out.print(index.getIndex());
		}
		else System.out.print("ERROR: no argument");
	}

}
