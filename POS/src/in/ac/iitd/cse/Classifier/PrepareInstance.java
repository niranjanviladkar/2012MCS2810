/**
 * 
 */
package in.ac.iitd.cse.Classifier;

import in.ac.iitd.cse.Properties.Common;
import in.ac.iitd.cse.Properties.Hollywood2Dataset;
import in.ac.iitd.cse.Properties.YouTubeDataset;
import in.ac.iitd.cse.YouTubeClip.YTClip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	List < YTClip >	discoveredClips	= new ArrayList < YTClip >();

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
		TrainingSet = new Instances( "Rel", WekaAttributes, discoveredClips.size() );
		
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
		statisticalProcessing( numClusters );

		// Create training instances
		for ( YTClip clip : discoveredClips )
		{
			// declare
			Instance instance = new Instance( histogramAttrs.length + 1 );

			// get histogram
			int[] currentHistogram = clip.getHistogram();

			// add histogram
			for ( int i = 0; i < histogramAttrs.length; i++ )
				instance.setValue( (Attribute) WekaAttributes.elementAt( i ),
						( currentHistogram[ i ] - avgHistogram[ i ] ) / maxHistogram[ i ] );

			// add label
			instance.setValue( (Attribute) WekaAttributes.elementAt( histogramAttrs.length ), clip.getLabelAsString() );

			// add the instance to training set
			TrainingSet.add( instance );
		}
		
		// Create testing instances
		if( testingClips.size() > 0 )
		{
			for( YTClip clip : testingClips )
			{
				// declare
				Instance instance = new Instance( histogramAttrs.length + 1 );

				// get histogram
				int[] currentHistogram = clip.getHistogram();

				// add histogram
				for ( int i = 0; i < histogramAttrs.length; i++ )
					instance.setValue( (Attribute) WekaAttributes.elementAt( i ),
							( currentHistogram[ i ] - avgHistogram[ i ] ) / maxHistogram[ i ] );

				// add label
				instance.setValue( (Attribute) WekaAttributes.elementAt( histogramAttrs.length ), clip.getLabelAsString() );

				// add the instance to training set
				TestingSet.add( instance );
			}
		}
	}

	private void statisticalProcessing( int numClusters )
	{
		int sum;
		int max;
		int current;

		for ( int i = 0; i < numClusters; i++ )
		{
			sum = 0;
			max = 1;

			for ( YTClip clip : discoveredClips )
			{
				current = clip.getHistogram()[ i ];

				sum += current;

				if ( current > max )
					max = current;
			}

			maxHistogram[ i ] = max;
			avgHistogram[ i ] = sum / discoveredClips.size();
		}
	}

	private void readYT_TrainingData() throws Exception
	{
		// if current dataset is not YouTube, return.
		if ( Common.DataSet.YOUTUBE.currentDS() == false )
			return;

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

				discoveredClips.add( clip );

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
					for ( int i = 0; i < discoveredClips.size(); i++ )
						if ( discoveredClips.get( i ).getName().equalsIgnoreCase( clip.getName() ) )
						{
							discoveredClips.remove( i );
							break;
						}

					continue;
				}
			}
		}
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
		
		if( isTrainingData == true )
			line = "all_labels_train.txt";
		else
			line = "all_labels_test.txt";

		CSVReader csvReader = new CSVReader( new FileReader( Hollywood2Dataset.labelDirPath + File.separator
				+ line ), ' ' );

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

			if( isTrainingData == true )
				discoveredClips.add( clip );
			else
				testingClips.add( clip );
		}

		csvReader.close();
	}
}
