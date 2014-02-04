/**
 * 
 */
package in.ac.iitd.cse.Tests;

import in.ac.iitd.cse.Classifier.LibSvmClassifier;
import in.ac.iitd.cse.Properties.Common;
import in.ac.iitd.cse.Properties.Hollywood1Dataset;
import in.ac.iitd.cse.YouTubeClip.YTClip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author mcs122810
 * 
 */
public class Hollywood1 extends Utilities
{

	/**
	 * Main function.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main( String[] args ) throws Exception
	{
		// current dataset is hollywood2
		Common.DataSet.HOLLYWOOD1.currentDS( true );

		allTrainingClips.clear();

		int choice = 0;
		boolean breakLoop = false;
		Scanner scan = new Scanner( System.in );

		populateAllClips( Hollywood1Dataset.ClipsDirPath );

		do
		{
			System.out.println( "Enter your choice : [0-6]" );
			System.out.println( "Prepare KMeans input file : 0" );
			System.out.println( "Run KMeans : 1" );
			System.out.println( "Convert clips into histograms : 2" );
			System.out.println( "Train weka SMO classifier : 3" );
			System.out.println( "Train meka SMO classifier : 4" );
			System.out.println( "Prepare libsvm training file : 5" );
			System.out.println( "Exit : 6" );
			System.out.print( "Your Choice : " );

			choice = scan.nextInt();

			switch ( choice )
			{
				case 0:
					try
					{
						prepareKMeansInputFile_2( true );
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
					breakLoop = true;
					break;

				case 3:
					try
					{
						Common.Classifier.WEKA.setCurrentClassifier( true );
						//WekaSMOClassifier smo = new WekaSMOClassifier();
						//smo.Train();
						//smo.TestAndPrintResult( true );
						// smo.TrainOneVsAll( 50, 0.01 );

						Common.Classifier.WEKA.setCurrentClassifier( false );
					}
					catch ( Exception e )
					{
						e.printStackTrace();
						breakLoop = true;
					}
					break;

				case 4:
					try
					{
						Common.Classifier.MEKA.setCurrentClassifier( true );

						//MekaSMOClassifier smo = new MekaSMOClassifier();
						//smo.Train();
						//smo.TestAndPrintResult( true );

						Common.Classifier.MEKA.setCurrentClassifier( false );
					}
					catch ( Exception e )
					{
						e.printStackTrace();
						breakLoop = true;
					}
					break;

				case 5:
					Common.Classifier.LIBSVM.setCurrentClassifier( true );
					new LibSvmClassifier( allTrainingClips, allTestingClips );
					Common.Classifier.LIBSVM.setCurrentClassifier( false );
					break;

				case 6:
					breakLoop = true;
					break;

				default:
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
	 * @param ClipsDir
	 * @throws Exception
	 */
	private static void populateAllTrainingClips( String ClipsDir ) throws Exception
	{
		List < String > uniqueLabels = new ArrayList < String >();

		// read classes - file is supposed to have unique entries of classes.
		// if the file is not present in original data set, just create it.
		BufferedReader reader = new BufferedReader( new FileReader( Hollywood1Dataset.labelDirPath + File.separator
				+ "classes.txt" ) );
		String line;

		while ( ( line = reader.readLine() ) != null )
			uniqueLabels.add( line );

		reader.close();

		String fileName = Hollywood1Dataset.labelsFile_trainClean;
		CSVReader csvReader = new CSVReader( new FileReader( fileName ), '\t' );
		String[] nextLine;

		// read all labels of all clips
		while ( ( nextLine = csvReader.readNext() ) != null )
		{
			// get the name of the clip - with extension.
			String clipName = nextLine[ 0 ];

			// extract only the name - without extension
			if ( clipName.endsWith( ".avi" ) )
				clipName = clipName.substring( 0, clipName.length() - 4 );

			int i = 1;

			// if the clip is already added, append it with next higher integer
			// example clip, clip_1, clip_2, etc.
			while ( isPresent( clipName, allTrainingClips ) == true )
			{
				int pos = clipName.indexOf( '_' );
				if ( pos != -1 )
				{
					clipName = clipName.substring( 0, pos );
				}

				clipName += ( "_" + i );
				i++;
			}

			YTClip clip = new YTClip( clipName ); // this name is without extension .avi
			allTrainingClips.add( clip );

			//read label - all labels in case of multi label
			List < Integer > labelAsIntList = new ArrayList < Integer >();

			for ( i = 1; i < nextLine.length; i++ )
				labelAsIntList.add( uniqueLabels.indexOf( nextLine[ i ] ) + 1 );//  indexing starts at 1 not 0

			clip.setLabelAsIntList( labelAsIntList );
		}
		
		csvReader.close();

		//		for ( YTClip clip : allTrainingClips )
		//		{
		//			System.out.print( clip.getName() + "\t" );
		//			for ( int i : clip.getLabelAsIntList() )
		//			{
		//				System.out.print( i + "\t" );
		//			}
		//			System.out.println( "" );
		//		}

		Common.state.ALL_TRAINING_CLIPS_INITIALISED.isDone( true );
	}

	/**
	 * Populate only testing clips.<br/>
	 * From the directory containing video clips, read and add clips to
	 * allTestingClips list.<br/>
	 * Only testing clips are considered. Rest of the clips are ignored.<br/>
	 * 
	 * @param clipsDir
	 * @throws Exception
	 */
	private static void populateAllTestingClips( String clipsDir ) throws Exception
	{
		List < String > uniqueLabels = new ArrayList < String >();

		// read classes - file is supposed to have unique entries of classes.
		// if the file is not present in original data set, just create it.
		BufferedReader reader = new BufferedReader( new FileReader( Hollywood1Dataset.labelDirPath + File.separator
				+ "classes.txt" ) );
		String line;

		while ( ( line = reader.readLine() ) != null )
			uniqueLabels.add( line );

		reader.close();

		String fileName = Hollywood1Dataset.labelsFile_testClean;
		CSVReader csvReader = new CSVReader( new FileReader( fileName ), '\t' );
		String[] nextLine;

		// read all labels of all clips
		while ( ( nextLine = csvReader.readNext() ) != null )
		{
			// get the name of the clip - with extension.
			String clipName = nextLine[ 0 ];

			// extract only the name - without extension
			if ( clipName.endsWith( ".avi" ) )
				clipName = clipName.substring( 0, clipName.length() - 4 );

			int i = 1;

			// if the clip is already added, append it with next higher integer
			// example clip, clip_1, clip_2, etc.
			// there is a dependency between populating training and testing clips.
			// training clips have to be populated before testing clips.
			// a clip name is supposed to be unique across both training and testing clips. 
			while ( isPresent( clipName, allTrainingClips ) == true || isPresent( clipName, allTestingClips ) == true )
			{
				int pos = clipName.indexOf( '_' );

				// if this clip name is already appended with _, extract only the name
				// i.e. if clip is clipName_2, extract only clipName.
				if ( pos != -1 )
				{
					clipName = clipName.substring( 0, pos );
				}

				clipName += ( "_" + i );
				i++;
			}

			YTClip clip = new YTClip( clipName ); // this name is without extension .avi
			allTestingClips.add( clip );

			//read label - all labels in case of multi label
			List < Integer > labelAsIntList = new ArrayList < Integer >();

			for ( i = 1; i < nextLine.length; i++ )
				labelAsIntList.add( uniqueLabels.indexOf( nextLine[ i ] ) + 1 );//  indexing starts at 1 not 0

			clip.setLabelAsIntList( labelAsIntList );
		}

		Common.state.ALL_TESTING_CLIPS_INITIALISED.isDone( true );
	}

	/**
	 * Populate both training as well as testing clips.<br/>
	 * From the directory containing video clips, read and add training clips to
	 * allTrainingClips list <br/>
	 * and testing clips to allTestingClips list.<br/>
	 * 
	 * @param clipsDir
	 * @throws Exception
	 */
	private static void populateAllClips( String clipsDir ) throws Exception
	{
		allTrainingClips.clear();
		allTestingClips.clear();

		// the order of populating clips is important.
		// first populate training (clean) clips
		// then testing (clean)

		populateAllTrainingClips( clipsDir );

		populateAllTestingClips( clipsDir );

		Common.state.ALL_CLIPS_INITIALISED.isDone( true );
	}

	private static boolean isPresent( String clipName, List < YTClip > l )
	{
		for ( YTClip c : l )
		{
			if ( c.getName().equalsIgnoreCase( clipName ) )
				return true;
		}

		return false;
	}
}
