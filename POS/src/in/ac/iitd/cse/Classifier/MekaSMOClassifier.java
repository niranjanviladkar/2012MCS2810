/**
 * Copyright IIT Delhi 2012 - 2013, All rights Reserved.
 */
package in.ac.iitd.cse.Classifier;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.multilabel.BR;
import weka.core.Result;

/**
 * @author mcs122810
 * 
 */
public class MekaSMOClassifier extends PrepareInstance
{
	private SMO			cModel;
	private Evaluation	eTest;

	BR					binaryRelevance	= new BR();

	public void Train() throws Exception
	{
		Train( 50.0, 0.01 );
	}

	public void Train( double cost, double gamma ) throws Exception
	{
		List < String > options = new ArrayList < String >();

		// set base classifier to be SMO from weka
		options.add( "-W" );
		options.add( "weka.classifiers.functions.SMO" );

		// debugging
		options.add( "-D" );

		// rest of the options for SMO classifier
		options.add( "--" );

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

		binaryRelevance.setOptions( options.toArray( new String[0] ) );

		TrainingSet.setClassIndex( uniqueLabels.size() );
		//		binaryRelevance.buildClassifier( TrainingSet );

		TestingSet.setClassIndex( uniqueLabels.size() );

		Result result = weka.classifiers.multilabel.Evaluation.evaluateModel( binaryRelevance, TrainingSet, TestingSet );

		System.out.println( result.toString() );

		//		System.out.println( weka.classifiers.multilabel.Evaluation.testClassifier( binaryRelevance, TestingSet ).toString());

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
	 * @throws Exception
	 */
	public MekaSMOClassifier() throws Exception
	{
		super();
	}

}
