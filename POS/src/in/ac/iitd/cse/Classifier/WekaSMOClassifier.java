/**
 * 
 */
package in.ac.iitd.cse.Classifier;

import java.io.IOException;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.RBFKernel;

/**
 * @author mcs122810
 * 
 */
public class WekaSMOClassifier extends PrepareInstance
{
	private SMO			cModel;
	private Evaluation	eTest;

	public void Train() throws Exception
	{
		cModel = new SMO();

		RBFKernel rbfKernel = new RBFKernel();
		rbfKernel.setGamma( 0.01 );

		cModel.setKernel( rbfKernel );
		cModel.setC( 50 );

		System.err.println( "Value of C : " + cModel.getC() );
		System.err.println( "Value of gamma : " + rbfKernel.getGamma() );
		System.err.println( "Started building classifier." );

		cModel.buildClassifier( TrainingSet );

		System.err.println( "Done building classifier." );
	}

	private void Test() throws Exception
	{
		System.err.println( "Started evaluation." );

		eTest = new Evaluation( TrainingSet );
		//eTest.useNoPriors();

		if ( testingClips.size() == 0 )
		{
			System.err.println( "Using Training set for testing." );
			eTest.evaluateModel( cModel, TrainingSet );
		}
		else
		{
			System.err.println( "Using Testing set for testing." );
			eTest.evaluateModel( cModel, TestingSet );
		}

		System.out.println( "Done Evaluation." );
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
	public WekaSMOClassifier() throws Exception
	{
		super();
	}
}
