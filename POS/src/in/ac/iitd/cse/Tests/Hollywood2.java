/**
 * 
 */
package in.ac.iitd.cse.Tests;

import in.ac.iitd.cse.Classifier.SMOClassifier;
import in.ac.iitd.cse.Properties.Common;
import in.ac.iitd.cse.Properties.Hollywood2Dataset;
import in.ac.iitd.cse.YouTubeClip.YTClip;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author mcs122810
 * 
 */
public class Hollywood2 extends Utilities
{

	/**
	 * Main function.
	 * @param args
	 * @throws Exception
	 */
	public static void main( String[] args ) throws Exception
	{
		// current dataset is hollywood2
		Common.DataSet.HOLLYWOOD2.currentDS( true );

		allTrainingClips.clear();

		int choice = 0;
		boolean breakLoop = false;
		Scanner scan = new Scanner( System.in );

		do
		{
			System.out.println( "Enter your choice : [0-4]" );
			System.out.println( "Prepare KMeans input file : 0" );
			System.out.println( "Run KMeans : 1" );
			System.out.println( "Convert clips into histograms : 2" );
			System.out.println( "Train classifier : 3" );
			System.out.println( "Exit : 4" );

			choice = scan.nextInt();

			switch ( choice )
			{
				case 0:
					try
					{
						populateAllClips( Hollywood2Dataset.stipFeaturesDirPath );

						PrepareKMeansInputFile();
					}
					catch ( Exception e1 )
					{
						e1.printStackTrace();

						breakLoop = true;
					}

					break;

				case 1:
					// Run KMeans clustering

					RunKMeans();

					break;

				case 2:
					try
					{
						ClipsToHistograms();
					}
					catch ( IOException e1 )
					{
						e1.printStackTrace();
						breakLoop = true;
					}
					break;

				case 3:
					try
					{
						SMOClassifier smo = new SMOClassifier();
						smo.Train();
						smo.TestAndPrintResult( true );
					}
					catch ( Exception e )
					{
						e.printStackTrace();
						breakLoop = true;
					}
					break;

				case 4:
					breakLoop = true;
					break;
			}
			System.out.println( "---------------------" );
			System.out.println( "---------------------\n\n" );
		} while ( breakLoop == false );

		scan.close();
	}

	/**
	 * Populate only training clips.<br/>
	 * From the directory containing video clips, read and add clips to
	 * allTrainingClips list.<br/>
	 * Only training clips are considered. Rest of the clips are ignored.<br/>
	 * 
	 * @param stipFeaturesDir
	 * @throws Exception
	 */
	private static void populateAllTrainingClips( String stipFeaturesDir ) throws Exception
	{
		File featuresDirectory = new File( stipFeaturesDir );

		File[] allFiles = featuresDirectory.listFiles();

		if ( allFiles == null || allFiles.length == 0 )
			throw new Exception( "Empty or non existent features directory : " + stipFeaturesDir );

		for ( File file : allFiles )
		{
			if ( file.getName().startsWith( "actioncliptrain" ) && file.getName().endsWith( ".features" ) )
			{
				int length = file.getName().length();

				String onlyName = (String) file.getName().substring( 0, length - 9 );

				YTClip clip = new YTClip( onlyName );

				allTrainingClips.add( clip );
			}
		}

		Common.state.ALL_TRAINING_CLIPS_INITIALISED.isDone( true );
	}

	/**
	 * Populate only testing clips.<br/>
	 * From the directory containing video clips, read and add clips to
	 * allTestingClips list.<br/>
	 * Only testing clips are considered. Rest of the clips are ignored.<br/>
	 * 
	 * @param stipFeaturesDir
	 * @throws Exception
	 */
	private static void populateAllTestingClips( String stipFeaturesDir ) throws Exception
	{
		File featuresDirectory = new File( stipFeaturesDir );

		File[] allFiles = featuresDirectory.listFiles();

		if ( allFiles == null || allFiles.length == 0 )
			throw new Exception( "Empty or non existent features directory : " + stipFeaturesDir );

		for ( File file : allFiles )
		{
			if ( file.getName().startsWith( "actioncliptest" ) && file.getName().endsWith( ".features" ) )
			{
				int length = file.getName().length();

				String onlyName = (String) file.getName().substring( 0, length - 9 );

				YTClip clip = new YTClip( onlyName );

				allTestingClips.add( clip );
			}
		}

		Common.state.ALL_TESTING_CLIPS_INITIALISED.isDone( true );
	}

	/**
	 * Populate both training as well as testing clips.<br/>
	 * From the directory containing video clips, read and add training clips to
	 * allTrainingClips list <br/>
	 * and testing clips to allTestingClips list.<br/>
	 * 
	 * @param stipFeaturesDir
	 * @throws Exception
	 */
	private static void populateAllClips( String stipFeaturesDir ) throws Exception
	{
		populateAllTrainingClips( stipFeaturesDir );

		populateAllTestingClips( stipFeaturesDir );

		Common.state.ALL_CLIPS_INITIALISED.isDone( true );
	}
}
