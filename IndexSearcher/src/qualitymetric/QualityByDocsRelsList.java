package qualitymetric;

import java.util.Arrays;

public class QualityByDocsRelsList {
	private final int numberOfDocs;
	private final double[] docsRels;
	
	public QualityByDocsRelsList (int numberOfDocs, double[] docsRels){
		this.numberOfDocs = numberOfDocs;
		this.docsRels = docsRels;
	}
	
	private double calcDCG (int numberOfDocs, double[] docsRels){
		if (numberOfDocs > this.numberOfDocs || numberOfDocs < 1){
			numberOfDocs = this.numberOfDocs;
		}
		double dcg = 0;
		double toLog2 = 1 / Math.log(2);
		for (int i = 0; i < numberOfDocs; i++){
			dcg += (Math.pow(2, docsRels[i]) - 1) / (Math.log(i + 2) * toLog2);
		}
		return dcg;
	}

	public double calcDCG (int numberOfDocs){
		return calcDCG(numberOfDocs, docsRels);
	}	
	
	public double calcIdealDCG (int numberOfDocs){
		double[] docsRels = Arrays.copyOf(this.docsRels, numberOfDocs);
		Arrays.sort(docsRels);
		for (int i = 0, numDiv2 = numberOfDocs / 2; i < numDiv2; i++){
			double buf = docsRels[i];
			docsRels[i] = docsRels[numberOfDocs - 1 - i];
			docsRels[numberOfDocs - 1 - i] = buf;
		}
		return calcDCG(numberOfDocs, docsRels);
	}	
	
	public double calcDCG (){
		return calcDCG(numberOfDocs, docsRels);
	}	
	
	public double calcNDCG (int numberOfDocs){
		return calcDCG(numberOfDocs) / calcIdealDCG(numberOfDocs);
	}
	
	public double calcNDCG (){
		return calcNDCG(numberOfDocs);
	}
	
	public double calcPFound (int numberOfDocs){
		class PFounder {
			private static final double pBreak = 0.15;
			private final double divDenumCalcedByMaxGrade;
			private final int numberOfDocs;
			private double[] pLooks;
			private double[] pRels;
			
			public PFounder (int numberOfDocs){
				this.numberOfDocs = numberOfDocs;
				double maxGrade = 0;
				for (int i = 0; i < numberOfDocs; i++){
					if (docsRels[i] > maxGrade) maxGrade = docsRels[i];
				}
				divDenumCalcedByMaxGrade = 1 / Math.pow(2, maxGrade);
				pLooks = new double [numberOfDocs];
				pRels = new double [numberOfDocs];
				calcPRels ();
				calcPLooks ();
			}
			
			private double calcPRel (int numberOfDoc){
				return (Math.pow(2, docsRels[numberOfDoc]) - 1) * divDenumCalcedByMaxGrade;
			}

			private void calcPRels (){
				for (int i = 0; i < numberOfDocs; i++){
					pRels[i] = calcPRel(i);
				}
			}			
			
			private void calcPLooks (){
				pLooks[0] = 1 - pBreak;
				for (int i = 1; i < numberOfDocs; i++){
					pLooks[i] = pLooks[i - 1] * (1 - pRels[i - 1]) * (1 - pBreak);
				}				
			}
			
			public double calcPFound (){
				double pFound = 0;
				for (int i = 0; i < numberOfDocs; i++){
					pFound += pLooks[i] * pRels[i];
				}
				return pFound;			
			}
		}
		if (numberOfDocs > this.numberOfDocs || numberOfDocs < 1){
			numberOfDocs = this.numberOfDocs;
		}		
		PFounder pFounder = new PFounder(numberOfDocs);
		return pFounder.calcPFound();
	}
	
	public double calcPFound (){
		return calcPFound(numberOfDocs);
	}
}
