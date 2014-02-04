/**
 * 
 */
package in.ac.iitd.cse.Classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.meta.MultiClassClassifier;

/**
 * @author mcs122810
 * 
 */
public class WekaSMOClassifier extends PrepareInstance
{
	private SMO						cModel;
	private Evaluation				eTest;
	private MultiClassClassifier	multiClassClassifier;

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

	public void TrainOneVsAll( double cost, double gamma ) throws Exception
	{
		multiClassClassifier = new MultiClassClassifier();

		List < String > options = new ArrayList < String >();

		// one vs all
		options.add( "-M" );
		options.add( "0" );

		// set base classifier to be SMO from weka
		options.add( "-W" );
		options.add( "weka.classifiers.functions.SMO" );

		// debugging
		options.add( "-D" );

		// rest of the options for SMO classifier
		options.add( "--" );

		// debugging
		options.add( "-D" );

		// set SMO cost = 50.0
		options.add( "-C" );
		options.add( String.valueOf( cost ) );

		// do not normalize
		options.add( "-N" );
		options.add( "2" );

		// select RBF kernel
		options.add( "-K" );
		options.add( "weka.classifiers.functions.supportVector.RBFKernel " // kernel class
				+ "-G " + String.valueOf( gamma ) ); // gamma

		multiClassClassifier.setOptions( options.toArray( new String[0] ) );

		//	System.err.println( "Value of C : " + ((SMO)multiClassClassifier.getClassifier()).getC() );
		//	System.err.println( "Value of gamma : " + ((RBFKernel)((SMO)multiClassClassifier.getClassifier()).getKernel()).getGamma() );

		System.err.println( "Started building classifier." );

		multiClassClassifier.buildClassifier( TrainingSet );

		System.err.println( "Done building classifier." );

		eTest = new Evaluation( TrainingSet );
		eTest.evaluateModel( multiClassClassifier, TestingSet );
		System.out.println( eTest.toSummaryString() );

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
