import java.util.Scanner;

import qualitymetric.QualityByDocsRelsList;


public class StartPointQuality {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc= new Scanner(System.in);
		int numberOfDocuments = sc.nextInt();
		double[] docsRels = new double[numberOfDocuments];
		for (int i = 0; i < numberOfDocuments; i++){
			docsRels[i] = sc.nextDouble();
		}
		sc.close();
		QualityByDocsRelsList qualityByDocsRelsList = new QualityByDocsRelsList(numberOfDocuments, docsRels);
		System.out.print("DCQ: " + qualityByDocsRelsList.calcDCG() + "\n");
		System.out.print("NDCQ: " + qualityByDocsRelsList.calcNDCG() + "\n");
		System.out.print("PFound: " + qualityByDocsRelsList.calcPFound() + "\n");
	}
}
