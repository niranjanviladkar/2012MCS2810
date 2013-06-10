/**
 * 
 */
package in.ac.iitd.cse.Tests;

import in.ac.iitd.cse.Classifier.SMOClassifier;
import in.ac.iitd.cse.Properties.YouTubeDataset;
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
	 * @param args
	 */
	public static void main( String[] args )
	{
		allClips.clear();


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
						populateAllClips( YouTubeDataset.stipFeaturesDirPath );
						
						PrepareKMeansInputFile();
					}
					catch ( Exception e1 )
					{
						// TODO Auto-generated catch block
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
						// TODO Auto-generated catch block
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

	static void populateAllClips( String stipFeaturesDir ) throws Exception
	{
		File featuresDirectory = new File( stipFeaturesDir );

		File[] allFiles = featuresDirectory.listFiles();

		if ( allFiles == null || allFiles.length == 0 )
			throw new Exception( "Empty or non existent features directory : " + stipFeaturesDir );

		for ( File file : allFiles )
		{
			if ( file.getName().endsWith( ".features" ) )
			{
				int length = file.getName().length();

				String onlyName = (String) file.getName().substring( 0, length - 9 );

				YTClip clip = new YTClip( onlyName );

				allClips.add( clip );
			}
		}
		
		YouTubeDataset.state.ALLCLIPS_INITIALISED.isDone( true );
	}
}