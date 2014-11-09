import searcher.Search;

/**
 * 
 */

/**
 * @author Viktor
 *
 */
public class StartPointSearcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length > 0){
			Search search = new Search (Long.parseLong(args[0]));
			search.run();
		}
		else System.out.print("ERROR: no argument");
	}

}
