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
import weka.core.DenseInstance;
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
			{
				processForLibsvmClassifier( true );
				//processForLibsvmClassifierTesting( true );
			}
			else
				if ( Common.Classifier.MEKA.getCurrentClassifier() == true )
					processForMekaClassifier( numClusters );
	}

	private void processForMekaClassifier( int numClusters ) throws IOException
	{
		// declare attributes corresponding to histogram
		ArrayList < Attribute > allAttrs = new ArrayList < Attribute >( uniqueLabels.size() + numClusters );

		ArrayList < String > attrValues = new ArrayList < String >();
		attrValues.add( "0" );
		attrValues.add( "1" );

		// for each element in histogram, add an attribute
		// ATTR1, ATTR2 ... ATTR200...
		for ( int i = 0; i < uniqueLabels.size(); i++ )
		{
			allAttrs.add( new Attribute( uniqueLabels.get( i ), attrValues ) );
		}

		for ( int i = 0; i < numClusters; i++ )
			allAttrs.add( new Attribute( "ATTR" + ( i + 1 ) ) );

		// Create an empty training set
		String nameOfRelation = "Train" + ": -C " + uniqueLabels.size();
		TrainingSet = new Instances( nameOfRelation, allAttrs, trainingClips.size() );

		if ( testingClips.size() > 0 )
		{
			nameOfRelation = "Test" + ": -C " + uniqueLabels.size();

			// Create empty testing set
			TestingSet = new Instances( nameOfRelation, allAttrs, testingClips.size() );
		}

		System.err.println( "Adding training instances" );

		fill_data( trainingClips, allAttrs, numClusters, true );

		// Create testing instances
		if ( testingClips.size() > 0 )
		{
			System.err.println( "Adding testing instances" );

			fill_data( testingClips, allAttrs, numClusters, false );
		}
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
		ArrayList < Attribute > histogramAttrs = new ArrayList < Attribute >( numClusters + 1 );

		// for each element in histogram, add an attribute
		// ATTR1, ATTR2 ... ATTR200...
		for ( int i = 0; i < numClusters; i++ )
			histogramAttrs.add( new Attribute( "ATTR" + ( i + 1 ) ) );

		// Declare the class attribute along with its values
		histogramAttrs.add( new Attribute( "theClass", uniqueLabels ) );

		// Create an empty training set
		TrainingSet = new Instances( "Rel", histogramAttrs, trainingClips.size() );

		if ( testingClips.size() > 0 )
		{
			// Create empty testing set
			TestingSet = new Instances( "Testing", histogramAttrs, testingClips.size() );

			// last element will be class
			TestingSet.setClassIndex( TestingSet.numAttributes() - 1 );
		}

		// last element will be class
		TrainingSet.setClassIndex( TrainingSet.numAttributes() - 1 );

		// calculate max and average for each cluster.
		//statisticalProcessing( numClusters, trainingClips );

		System.err.println( "Adding training instances" );

		// Create training instances
		for ( YTClip clip : trainingClips )
		{
			// declare
			Instance instance = new DenseInstance( numClusters + 1 );

			// get histogram
			int[] currentHistogram = clip.getHistogram();

			// add histogram
			for ( int i = 0; i < numClusters; i++ )
				instance.setValue( histogramAttrs.get( i ), currentHistogram[ i ] );// / maxHistogram[ i ] );

			// add label
			instance.setValue( histogramAttrs.get( TrainingSet.numAttributes() - 1 ), clip.getLabelAsString() );

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

			//statisticalProcessing( numClusters, testingClips );

			for ( YTClip clip : testingClips )
			{
				// declare
				Instance instance = new DenseInstance( numClusters + 1 );

				// get histogram
				int[] currentHistogram = clip.getHistogram();

				// add histogram
				for ( int i = 0; i < numClusters; i++ )
					instance.setValue( histogramAttrs.get( i ), currentHistogram[ i ] );// / maxHistogram[ i ] );

				// add label
				instance.setValue( histogramAttrs.get( TrainingSet.numAttributes() - 1 ), clip.getLabelAsString() );

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

	/**
	 * Create a training and testing file in libsvm format.
	 * 
	 * @param forceOverwrite
	 *            - Should overwrite previous files?
	 * @throws IOException
	 */
	private void processForLibsvmClassifier( boolean forceOverwrite ) throws IOException
	{
		String filename = Hollywood2Dataset.libsvmDir + "/Hollywood2.train";
		boolean processTraining = true;
		boolean processTesting = true;
		int numClusters = Hollywood2Dataset.KMeansNumClusters;
		maxHistogram = new double[numClusters];
		avgHistogram = new double[numClusters];

		// prepare a training file
		try
		{
			FileReader f = new FileReader( filename );

			// if exception does not occur, file exists. do not proceed.
			f.close();

			if ( forceOverwrite == false )
				System.err.println( "Using old file : " + filename );

			processTraining = false;
		}
		catch ( Exception e )
		{
			// file does not exists, so create it.
			processTraining = true;
		}

		if ( forceOverwrite == true || processTraining == true )
		{
			BufferedWriter writer = new BufferedWriter( new FileWriter( filename ) );

			statisticalProcessing( numClusters, trainingClips );

			System.err.println( "Preparing Input file for libsvm training : " + filename );

			for ( YTClip clip : trainingClips )
			{
				// int label = clip.getLabelAsInt();
				List < Integer > labelList = clip.getLabelAsIntList();
				int[] histogram = clip.getHistogram();

				String training_instance = "";

				// add label - this does not support multi label dataset... so commented
				// training_instance = String.valueOf( label ) + " ";

				// add label - supports multi label datasets also. - but this is not a default libsvm format.
				{/*
					for ( int i = 0; i < labelList.size(); i++ )

					{
						training_instance += labelList.get( i );

						if ( i < labelList.size() - 1 )
							training_instance += ",";
						else
							training_instance += " ";
					}

					// add attributes
					for ( int i = 0; i < histogram.length; i++ )
					{
						training_instance += String.valueOf( i + 1 ) + ":" + String.valueOf( histogram[ i ] ) + " ";
					}

					// write to training file
					writer.write( training_instance );
					
					writer.newLine();
					*/
				}

				// add label - supports multi label datasets also.
				// create a duplicate training instance for each multi label instance.
				{
					for ( int i = 0; i < labelList.size(); i++ )
					{
						training_instance += labelList.get( i );

						training_instance += " ";

						// add attributes
						for ( int j = 0; j < histogram.length; j++ )
						{
							// for ex. 1:0.6 2:0.0 3:1.0 ... etc
							training_instance += String.valueOf( j + 1 )
									+ ":"
									+ String.valueOf( histogram[ j ]
											/ ( maxHistogram[ j ] == 0 ? 1 : maxHistogram[ j ] ) ) + " ";
						}

						// write to training file
						writer.write( training_instance );

						writer.newLine();

						training_instance = "";
					}
				}
			}
			writer.close();

			System.err.println( "Done Preparing Input file for libsvm training." + filename );
		}

		// Now prepare for testing file

		filename = Hollywood2Dataset.libsvmDir + "/Hollywood2.test";

		try
		{
			FileReader f = new FileReader( filename );

			// if exception does not occur, file exists. do not proceed.
			f.close();

			if ( forceOverwrite == false )
				System.err.println( "Using old file : " + filename );

			processTesting = false;
		}
		catch ( Exception e )
		{
			// file does not exists, so create it.
			processTesting = true;
		}

		if ( forceOverwrite == true || processTesting == true )
		{
			BufferedWriter writer = new BufferedWriter( new FileWriter( filename ) );

			statisticalProcessing( numClusters, testingClips );

			System.err.println( "Preparing Input file for libsvm testing : " + filename );

			for ( YTClip clip : testingClips )
			{
				// int label = clip.getLabelAsInt();
				List < Integer > labelList = clip.getLabelAsIntList();
				int[] histogram = clip.getHistogram();

				String testing_instance = "";

				// add label - this does not support multi label dataset... so commented
				// testing_instance = String.valueOf( label ) + " ";

				//add label - supports multi label datasets also.
				{/*
					for ( int i = 0; i < labelList.size(); i++ )
					{
						testing_instance += labelList.get( i );

						if ( i < labelList.size() - 1 )
							testing_instance += ",";
						else
							testing_instance += " ";
					}

					// add attributes
					for ( int i = 0; i < histogram.length; i++ )
					{
						testing_instance += String.valueOf( i + 1 ) + ":" + String.valueOf( histogram[ i ] ) + " ";
					}

					// write to training file
					writer.write( testing_instance );

					writer.newLine();
					*/
				}

				// add label - supports multi label datasets also.
				// create a duplicate training instance for each multi label instance.
				{
					for ( int i = 0; i < labelList.size(); i++ )
					{
						testing_instance += labelList.get( i );

						testing_instance += " ";

						// add attributes
						for ( int j = 0; j < histogram.length; j++ )
						{
							testing_instance += String.valueOf( j + 1 )
									+ ":"
									+ String.valueOf( histogram[ j ]
											/ ( maxHistogram[ j ] == 0 ? 1 : maxHistogram[ j ] ) ) + " ";
						}

						// write to training file
						writer.write( testing_instance );

						writer.newLine();

						testing_instance = "";
					}
				}
			}
			writer.close();

			System.err.println( "Done Preparing Input file for libsvm testing." + filename );
		}
	}

	/**
	 * Create a training and testing file in libsvm format. Testing function -
	 * only prepares a training and testing file for 2 labels.
	 * 
	 * @param forceOverwrite
	 *            - Should overwrite previous files?
	 * @throws IOException
	 */
	private void processForLibsvmClassifierTesting( boolean forceOverwrite ) throws IOException
	{
		String filename = Hollywood2Dataset.libsvmDir + "/Hollywood2.train";
		boolean processTraining = true;
		boolean processTesting = true;
		int numClusters = Hollywood2Dataset.KMeansNumClusters;
		maxHistogram = new double[numClusters];
		avgHistogram = new double[numClusters];

		int label1 = 5; // first label
		int label2 = 7; // second label

		// prepare a training file
		try
		{
			FileReader f = new FileReader( filename );

			// if exception does not occur, file exists. do not proceed.
			f.close();

			if ( forceOverwrite == false )
				System.err.println( "Using old file : " + filename );

			processTraining = false;
		}
		catch ( Exception e )
		{
			// file does not exists, so create it.
			processTraining = true;
		}

		if ( forceOverwrite == true || processTraining == true )
		{
			BufferedWriter writer = new BufferedWriter( new FileWriter( filename ) );

			System.err.println( "Preparing Input file for libsvm training : " + filename );
			System.err.println( "Fisrt Label : " + label1 + " Second Label : " + label2 );

			statisticalProcessingTesting( numClusters, trainingClips, label1, label2 );

			for ( YTClip clip : trainingClips )
			{
				// int label = clip.getLabelAsInt();
				List < Integer > labelList = clip.getLabelAsIntList();
				int[] histogram = clip.getHistogram();

				String training_instance = "";

				// add label - supports multi label datasets also.
				// create a duplicate training instance for each multi label instance.
				{
					for ( int i = 0; i < labelList.size(); i++ )
					{
						if ( labelList.get( i ) != label1 && labelList.get( i ) != label2 )
							continue;

						training_instance += labelList.get( i );

						training_instance += " ";

						// add attributes
						for ( int j = 0; j < histogram.length; j++ )
						{
							training_instance += String.valueOf( j + 1 )
									+ ":"
									+ String.valueOf( histogram[ j ]
											/ ( maxHistogram[ j ] == 0 ? 1 : maxHistogram[ j ] ) ) + " ";
						}

						// write to training file
						writer.write( training_instance );

						writer.newLine();

						training_instance = "";

					}
				}

			}
			writer.close();

			System.err.println( "Done Preparing Input file for libsvm training." + filename );
			System.err.println( "Fisrt Label : " + label1 + " Second Label : " + label2 );
		}

		// Now prepare for testing file

		filename = Hollywood2Dataset.libsvmDir + "/Hollywood2.test";

		try
		{
			FileReader f = new FileReader( filename );

			// if exception does not occur, file exists. do not proceed.
			f.close();

			if ( forceOverwrite == false )
				System.err.println( "Using old file : " + filename );

			processTesting = false;
		}
		catch ( Exception e )
		{
			// file does not exists, so create it.
			processTesting = true;
		}

		if ( forceOverwrite == true || processTesting == true )
		{
			BufferedWriter writer = new BufferedWriter( new FileWriter( filename ) );

			System.err.println( "Preparing Input file for libsvm testing : " + filename );
			System.err.println( "Fisrt Label : " + label1 + " Second Label : " + label2 );

			statisticalProcessingTesting( numClusters, testingClips, label1, label2 );

			for ( YTClip clip : testingClips )
			{
				// int label = clip.getLabelAsInt();
				List < Integer > labelList = clip.getLabelAsIntList();
				int[] histogram = clip.getHistogram();

				String testing_instance = "";

				// add label - supports multi label datasets also.
				// create a duplicate training instance for each multi label instance.
				{
					for ( int i = 0; i < labelList.size(); i++ )
					{
						if ( labelList.get( i ) != label1 && labelList.get( i ) != label2 )
							continue;

						testing_instance += labelList.get( i );

						testing_instance += " ";

						// add attributes
						for ( int j = 0; j < histogram.length; j++ )
						{
							testing_instance += String.valueOf( j + 1 )
									+ ":"
									+ String.valueOf( histogram[ j ]
											/ ( maxHistogram[ j ] == 0 ? 1 : maxHistogram[ j ] ) ) + " ";
						}

						// write to training file
						writer.write( testing_instance );

						writer.newLine();

						testing_instance = "";
					}
				}
			}
			writer.close();

			System.err.println( "Done Preparing Input file for libsvm testing." + filename );
			System.err.println( "Fisrt Label : " + label1 + " Second Label : " + label2 );
		}
	}

	private void fill_data( List < YTClip > list, ArrayList < Attribute > allAttrs, int numClusetrs, boolean isTraining )
			throws IOException
	{
		// Create instances
		for ( YTClip clip : list )
		{
			// declare
			Instance instance = new DenseInstance( allAttrs.size() );

			// get histogram
			int[] currentHistogram = clip.getHistogram();

			// add label
			{
				// get label as int list
				List < Integer > labels = clip.getLabelAsIntList();

				for ( int i = 0; i < uniqueLabels.size(); i++ )
				{
					if ( labels.contains( Integer.valueOf( i ) ) )
						instance.setValue( allAttrs.get( i ), "1" );
					else
						instance.setValue( allAttrs.get( i ), "0" );
				}
			}

			// add histogram
			for ( int i = 0; i < numClusetrs; i++ )
				instance.setValue( allAttrs.get( uniqueLabels.size() + i ), ( currentHistogram[ i ] ) );// / maxHistogram[ i ] );

			// add the instance to training set
			if ( isTraining == true )
				TrainingSet.add( instance );
			else
				TestingSet.add( instance );
		}

		{
			// write instances as arff file. - mainly for debugging
			String ARFF_output, output_file;

			if ( isTraining == true )
			{
				ARFF_output = TrainingSet.toString();
				output_file = "train.arff";
			}
			else
			{
				ARFF_output = TestingSet.toString();
				output_file = "test.arff";
			}

			BufferedWriter writer = new BufferedWriter( new FileWriter( output_file ) );
			writer.write( ARFF_output );
			writer.close();
			ARFF_output = null;
		}
	}

	private void statisticalProcessing( int numClusters, List < YTClip > clipList )
	{
		int sum;
		int max = -1;
		int current;

		for ( int i = 0; i < numClusters; i++ )
		{
			sum = 0;
			max = -1;

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

	private void statisticalProcessingTesting( int numClusters, List < YTClip > clipList, int label1, int label2 )
	{
		int sum;
		int max = -1;
		int current;
		int count = 0;

		for ( int i = 0; i < numClusters; i++ )
		{
			sum = 0;
			max = -1;

			for ( YTClip clip : clipList )
			{
				List < Integer > labelList = clip.getLabelAsIntList();

				for ( int j = 0; j < labelList.size(); j++ )
				{
					if ( labelList.get( j ) != label1 && labelList.get( j ) != label2 )
						continue;

					current = clip.getHistogram()[ i ];

					sum += current;

					if ( current > max )
						max = current;

					count++;
				}
			}

			maxHistogram[ i ] = max;
			avgHistogram[ i ] = sum / count;
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

			// read label - only first label
			for ( int i = 2; i < nextLine.length; i++ )
			{
				if ( nextLine[ i ].equalsIgnoreCase( "1" ) == true )
				{
					String lbl = uniqueLabels.get( i - 2 );

					clip.setLabelAsString( lbl );
					clip.setLabelAsInt( i - 1 ); //  indexing starts at 1 not 0

					// read only first positive instance of label
					break;
				}
			}

			//read label - all labels in case of multi label
			List < Integer > labelAsIntList = new ArrayList < Integer >();

			for ( int i = 2; i < nextLine.length; i++ )
				if ( nextLine[ i ].equalsIgnoreCase( "1" ) == true )
					labelAsIntList.add( i - 1 );//  indexing starts at 1 not 0

			clip.setLabelAsIntList( labelAsIntList );

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
