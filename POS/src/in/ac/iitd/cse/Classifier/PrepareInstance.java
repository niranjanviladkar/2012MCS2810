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

	List < String >	uniqueLabels	= new ArrayList < String >();

	Instances		TrainingSet;

	/**
	 * Constructor.<br/>
	 * It reads the data and prepares instance.<br/>
	 * This instance can now be used in various types of classifier models.
	 * 
	 * @throws IOException
	 */
	PrepareInstance() throws Exception
	{
		if ( Common.DataSet.YOUTUBE.currentDS() == true )
			PrepareYTInstance();
		else
			if ( Common.DataSet.HOLLYWOOD2.currentDS() == true )
				PrepareHW2Instance();
	}

	private void PrepareYTInstance() throws Exception
	{
		readYTData();

		// declare attributes corresponding to histogram
		Attribute[] histogramAttrs = new Attribute[YouTubeDataset.KMeansNumClusters];

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

		// last element will be class
		TrainingSet.setClassIndex( TrainingSet.numAttributes() - 1 );

		// Create instances
		for ( YTClip clip : discoveredClips )
		{
			// declare
			Instance instance = new Instance( histogramAttrs.length + 1 );

			// get histogram
			int[] currentHistogram = clip.getHistogram();

			// add histogram
			for ( int i = 0; i < histogramAttrs.length; i++ )
				instance.setValue( (Attribute) WekaAttributes.elementAt( i ), currentHistogram[ i ] );

			// add label
			instance.setValue( (Attribute) WekaAttributes.elementAt( histogramAttrs.length ), clip.getLabelAsString() );

			// add the instance to training set
			TrainingSet.add( instance );
		}
	}

	private void PrepareHW2Instance() throws Exception
	{
		readHW2Data();

		// declare attributes corresponding to histogram
		Attribute[] histogramAttrs = new Attribute[Hollywood2Dataset.KMeansNumClusters];

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

	}

	private void readYTData() throws Exception
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

	private void readHW2Data() throws Exception
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

	}
}
