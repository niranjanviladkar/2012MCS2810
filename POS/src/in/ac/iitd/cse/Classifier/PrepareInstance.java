/**
 * 
 */
package in.ac.iitd.cse.Classifier;

import in.ac.iitd.cse.Properties.Common;
import in.ac.iitd.cse.Properties.Hollywood2Dataset;
import in.ac.iitd.cse.Properties.YouTubeDataset;
import in.ac.iitd.cse.YouTubeClip.YTClip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Reads the data and prepares instance.<br/>
 * This instance can now be used in various types of classifier models.
 * 
 * @author mcs122810
 * 
 */
class PrepareInstance
{
	List < YTClip >	trainingClips	= new ArrayList < YTClip >();

	List < YTClip >	testingClips	= new ArrayList < YTClip >();

	List < String >	uniqueLabels	= new ArrayList < String >();

	Instances		TrainingSet;

	Instances		TestingSet;

	double[]		maxHistogram;

	double[]		avgHistogram;

	/**
	 * Constructor.<br/>
	 * It reads the data and prepares training instance.<br/>
	 * This instance can now be used in various types of classifier models.
	 * 
	 * @throws IOException
	 */
	PrepareInstance() throws Exception
	{
		int numClusters = 0;

		if ( Common.DataSet.YOUTUBE.currentDS() == true )
		{
			readYT_TrainingData();
			numClusters = YouTubeDataset.KMeansNumClusters;
		}
		else
			if ( Common.DataSet.HOLLYWOOD2.currentDS() == true )
			{
				// read training data
				readHW2_Data( true );

				// read testing data
				readHW2_Data( false );

				numClusters = Hollywood2Dataset.KMeansNumClusters;
			}

		if ( Common.Classifier.WEKA.getCurrentClassifier() == true )
			processForWekaClassifier( numClusters );
		else
			if ( Common.Classifier.LIBSVM.getCurrentClassifier() == true )
				processForLibsvmClassifier();
	}

	private void processForWekaClassifier( int numClusters ) throws Exception
	{
		maxHistogram = new double[numClusters];
		avgHistogram = new double[numClusters];

		for ( int i = 0; i < numClusters; i++ )
		{
			// we divide by max so keep default to 1
			maxHistogram[ i ] = 1;

			// we subtract mean so keep default to 0
			avgHistogram[ i ] = 0;
		}

		// declare attributes corresponding to histogram
		Attribute[] histogramAttrs = new Attribute[numClusters];

		// for each element in histogram, add an attribute
		// ATTR1, ATTR2 ... ATTR200
		for ( int i = 0; i < histogramAttrs.length; i++ )
			histogramAttrs[ i ] = new Attribute( "ATTR" + ( i + 1 ) );

		// Declare the class attribute along with its values
		FastVector classes = new FastVector( uniqueLabels.size() );

		for ( String lbl : uniqueLabels )
			classes.addElement( lbl );

		Attribute classAttribute = new Attribute( "theClass", classes );

		// Declare the feature vector
		FastVector WekaAttributes = new FastVector( histogramAttrs.length + 1 );

		// histogram attributes
		for ( Attribute attr : histogramAttrs )
			WekaAttributes.addElement( attr );

		// class attribute
		WekaAttributes.addElement( classAttribute );

		// Create an empty training set
		TrainingSet = new Instances( "Rel", WekaAttributes, trainingClips.size() );

		if ( testingClips.size() > 0 )
		{
			// Create empty testing set
			TestingSet = new Instances( "Testing", WekaAttributes, testingClips.size() );

			// last element will be class
			TestingSet.setClassIndex( TestingSet.numAttributes() - 1 );
		}

		// last element will be class
		TrainingSet.setClassIndex( TrainingSet.numAttributes() - 1 );

		// calculate max and average for each cluster.
		statisticalProcessing( numClusters, trainingClips );

		System.err.println( "Adding training instances" );

		// Create training instances
		for ( YTClip clip : trainingClips )
		{
			// declare
			Instance instance = new Instance( histogramAttrs.length + 1 );

			// get histogram
			int[] currentHistogram = clip.getHistogram();

			// add histogram
			for ( int i = 0; i < histogramAttrs.length; i++ )
				instance.setValue( (Attribute) WekaAttributes.elementAt( i ), ( currentHistogram[ i ] )
						/ maxHistogram[ i ] );

			// add label
			instance.setValue( (Attribute) WekaAttributes.elementAt( histogramAttrs.length ), clip.getLabelAsString() );

			// add the instance to training set
			TrainingSet.add( instance );
		}

		// write training instances as arff file. - mainly for debugging
		String trainARFF = TrainingSet.toString();
		BufferedWriter writer = new BufferedWriter( new FileWriter( "train.arff" ) );
		writer.write( trainARFF );
		writer.close();
		trainARFF = null;

		// Create testing instances
		if ( testingClips.size() > 0 )
		{
			System.err.println( "Adding testing instances" );

			statisticalProcessing( numClusters, testingClips );

			for ( YTClip clip : testingClips )
			{
				// declare
				Instance instance = new Instance( histogramAttrs.length + 1 );

				// get histogram
				int[] currentHistogram = clip.getHistogram();

				// add histogram
				for ( int i = 0; i < histogramAttrs.length; i++ )
					instance.setValue( (Attribute) WekaAttributes.elementAt( i ), ( currentHistogram[ i ] )
							/ maxHistogram[ i ] );

				// add label
				instance.setValue( (Attribute) WekaAttributes.elementAt( histogramAttrs.length ),
						clip.getLabelAsString() );

				// add the instance to training set
				TestingSet.add( instance );
			}

			// write testing instances as arff file. - mainly for debugging
			String testARFF = TestingSet.toString();
			writer = new BufferedWriter( new FileWriter( "test.arff" ) );
			writer.write( testARFF );
			writer.close();
			testARFF = null;

		}
	}

	private void processForLibsvmClassifier() throws IOException
	{
		String filename = Hollywood2Dataset.libsvmDir + "Hollywood2.train";

		try
		{
			FileReader f = new FileReader( filename );

			// if exception does not occure, file exists. do not proceed.
			f.close();

			System.err.println( "Using old file : " + filename );

			return;
		}
		catch ( Exception e )
		{
			// file does not exists, so create it.
		}

		BufferedWriter writer = new BufferedWriter( new FileWriter( filename ) );

		System.err.println( "Preparing Input file for libsvm training." );

		for ( YTClip clip : trainingClips )
		{
			int label = clip.getLabelAsInt();
			int[] histogram = clip.getHistogram();

			String training_instance;

			// add label
			training_instance = String.valueOf( label ) + " ";

			// add attributes
			for ( int i = 0; i < histogram.length; i++ )
			{
				training_instance += String.valueOf( i + 1 ) + ":" + String.valueOf( histogram[ i ] ) + " ";
			}

			// write to training file
			writer.write( training_instance );

			writer.newLine();
		}

		writer.close();

		System.err.println( "Done Preparing Input file for libsvm training." );
	}

	private void statisticalProcessing( int numClusters, List < YTClip > clipList )
	{
		int sum;
		int max;
		int current;

		for ( int i = 0; i < numClusters; i++ )
		{
			sum = 0;
			max = 1;

			for ( YTClip clip : clipList )
			{
				current = clip.getHistogram()[ i ];

				sum += current;

				if ( current > max )
					max = current;
			}

			maxHistogram[ i ] = max;
			avgHistogram[ i ] = sum / clipList.size();
		}
	}

	private void readYT_TrainingData() throws Exception
	{
		// if current dataset is not YouTube, return.
		if ( Common.DataSet.YOUTUBE.currentDS() == false )
			return;

		System.err.println( "Reading training data." );

		File labelsDirectory = new File( YouTubeDataset.labelDirPath );

		File[] allFiles = labelsDirectory.listFiles();

		if ( allFiles == null || allFiles.length == 0 )
			throw new Exception( "Empty Label directory. Please generate labels first." );

		for ( File file : allFiles )
		{
			// if the file is a label file, then only process it
			if ( file.getName().endsWith( ".label" ) )
			{
				int length = file.getName().length();

				// register name - without extension
				String onlyName = (String) file.getName().subSequence( 0, length - 6 );

				YTClip clip = new YTClip( onlyName );

				trainingClips.add( clip );

				BufferedReader reader = new BufferedReader( new FileReader( file ) );

				// read label 
				String label = reader.readLine();

				reader.close();

				// and set the label
				clip.setLabelAsString( label );

				// check and add to the list of unique labels
				boolean found = false;

				for ( String lbl : uniqueLabels )
					if ( lbl.equalsIgnoreCase( label ) )
					{
						found = true;
						break;
					}

				// if not added previously, add this label
				if ( found == false )
					uniqueLabels.add( label );

				// read attribute - the histogram
				String histogramFile = YouTubeDataset.histogramDirPath + File.separator + clip.getName() + ".histogram";

				try
				{
					reader = new BufferedReader( new FileReader( histogramFile ) );

					// read histogram as a string
					String[] histogramAsString = reader.readLine().split( " " );

					reader.close();

					int[] histogram = new int[YouTubeDataset.KMeansNumClusters];

					// convert histogram into integer values.
					for ( int i = 0; i < histogram.length; i++ )
						histogram[ i ] = Integer.parseInt( histogramAsString[ i ] );

					// set the label
					clip.setHistogram( histogram );
				}
				catch ( FileNotFoundException e )
				{
					//System.err.println( "This file should exists : " + histogramFile );

					// remove clip from list
					for ( int i = 0; i < trainingClips.size(); i++ )
						if ( trainingClips.get( i ).getName().equalsIgnoreCase( clip.getName() ) )
						{
							trainingClips.remove( i );
							break;
						}

					continue;
				}
			}
		}

		System.err.println( "Reading training data - Done" );
	}

	private void readHW2_Data( boolean isTrainingData ) throws Exception
	{
		// if current dataset is not Hollywood2, then return
		if ( Common.DataSet.HOLLYWOOD2.currentDS() == false )
			return;

		// read classes
		BufferedReader reader = new BufferedReader( new FileReader( Hollywood2Dataset.labelDirPath + File.separator
				+ "classes.txt" ) );
		uniqueLabels.clear();

		String line;

		while ( ( line = reader.readLine() ) != null )
			uniqueLabels.add( line );

		reader.close();

		if ( isTrainingData == true )
		{
			line = "all_labels_train.txt";
			System.err.println( "Reading training data." );
		}
		else
		{
			line = "all_labels_test.txt";
			System.err.println( "Reading testing data." );
		}

		CSVReader csvReader = new CSVReader( new FileReader( Hollywood2Dataset.labelDirPath + File.separator + line ),
				' ' );

		String[] nextLine = null;

		while ( ( nextLine = csvReader.readNext() ) != null )
		{
			// add clip
			YTClip clip = new YTClip( nextLine[ 0 ] );

			// read label

			for ( int i = 2; i < nextLine.length; i++ )
			{
				if ( nextLine[ i ].equalsIgnoreCase( "1" ) == true )
				{
					String lbl = uniqueLabels.get( i - 2 );

					clip.setLabelAsString( lbl );
					clip.setLabelAsInt( i - 2 );

					// read only first positive instance of label
					break;
				}
			}

			// read histograms
			String histogramFile = Hollywood2Dataset.histogramDirPath + File.separator + clip.getName() + ".histogram";

			try
			{
				reader = new BufferedReader( new FileReader( histogramFile ) );

				// read histogram as a string
				String[] histogramAsString = reader.readLine().split( " " );

				reader.close();

				int[] histogram = new int[Hollywood2Dataset.KMeansNumClusters];

				// convert histogram into integer values.
				for ( int i = 0; i < histogram.length; i++ )
					histogram[ i ] = Integer.parseInt( histogramAsString[ i ] );

				// set the label
				clip.setHistogram( histogram );
			}
			catch ( FileNotFoundException e )
			{
				System.err.println( "This file should exists : " + histogramFile );

				continue;
			}

			if ( isTrainingData == true )
				trainingClips.add( clip );
			else
				testingClips.add( clip );
		}

		csvReader.close();

		System.err.println( "Reading data - Done" );
	}
}
