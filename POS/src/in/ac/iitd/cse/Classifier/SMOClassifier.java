/**
 * 
 */
package in.ac.iitd.cse.Classifier;

import java.io.IOException;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;

/**
 * @author mcs122810
 * 
 */
public class SMOClassifier extends PrepareInstance
{
	private SMO			cModel;
	private Evaluation	eTest;

	public void Train() throws Exception
	{
		cModel = new SMO();

		System.out.println( "Value of C : " + cModel.getC() );

		cModel.buildClassifier( TrainingSet );
	}

	private void Test() throws Exception
	{
		if ( testingClips.size() == 0 )
		{
			eTest = new Evaluation( TrainingSet );
			eTest.useNoPriors();
			eTest.evaluateModel( cModel, TrainingSet );
		}
		else
		{
			eTest = new Evaluation( TestingSet );
			eTest.useNoPriors();
			eTest.evaluateModel( cModel, TestingSet );
		}

	}

	public void TestAndPrintResult( boolean shouldPrint ) throws Exception
	{
		Test();

		if ( shouldPrint == true )
		{
			String strSummary = eTest.toSummaryString();
			System.out.println( strSummary );

			System.out.println( "Value of C : " + cModel.getC() );
		}
	}

	/**
	 * Creates an instance of SMO classifier.
	 * 
	 * @throws IOException
	 * 
	 */
	public SMOClassifier() throws Exception
	{
		super();
	}
}
