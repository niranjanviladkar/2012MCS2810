/**
 * 
 */
package in.ac.iitd.cse.Tests;

import in.ac.iitd.cse.Classifier.SMOClassifier;
import in.ac.iitd.cse.Properties.YouTubeDataset;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Reads CSV and finds classes of clips using similarity.<br/>
 * Reads STIP features of clips and prepares a KMeans Input file.<br/>
 * <br/>
 * Revision history :<br/>
 * 2013 - April - 20 : Prepared the file.<br/>
 * 
 * @author mcs122810 ( Niranjan A Viladkar )
 */
public class YouTubeDataSet extends Utilities
{

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	public static void main( String[] args ) throws IOException, ClassNotFoundException
	{
		// Initialize dictionary

		String path = YouTubeDataset.wordNet_3_0_DictFolderPath;

		URL url = new URL( "file", null, path );

		IDictionary dict = new Dictionary( url );

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

					// Initialize the tagger

					MaxentTagger tagger = new MaxentTagger( YouTubeDataset.taggerFilePath );
					YouTubeDataset.state.TAGGER_INITIALISED.isDone( true );

					dict.open();
					YouTubeDataset.state.DICT_OPENED.isDone( true );

					// Create stemmer

					WordnetStemmer wordNetStemmer = new WordnetStemmer( dict );
					YouTubeDataset.state.STEMMER_INITIALISED.isDone( true );

					// Process CSV to get set of verbs for each clip
					CSVProcessing( tagger, wordNetStemmer );

					tagger = null;
					wordNetStemmer = null;
					YouTubeDataset.state.STEMMER_INITIALISED.isDone( false );
					YouTubeDataset.state.TAGGER_INITIALISED.isDone( false );

					// make labels of each clip using WordNet Similarity
					SimilarityComparisons( dict );

					dict.close();
					dict = null;
					YouTubeDataset.state.DICT_OPENED.isDone( false );

					// try to free as much memory as possible
					System.gc();

					// Read STIP features and prepare input file for clustering
					//PrepareKMeansInputFile();

					break;

				case 1:
					// Run KMeans clustering

					RunKMeans();

					break;

				case 2:
					ClipsToHistograms();
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
}
