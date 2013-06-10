/**
 * 
 */
package in.ac.iitd.cse.Tests;

import in.ac.iitd.cse.Properties.Props;
import in.ac.iitd.cse.YouTubeClip.YTClip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import au.com.bytecode.opencsv.CSVReader;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.sussex.nlp.jws.WuAndPalmer;

/**
 * Reads CSV and finds classes of clips using similarity.<br/>
 * Reads STIP features of clips and prepares a KMeans Input file.
 * 
 * @author mcs122810 ( Niranjan A Viladkar )
 * 
 */
public class Test6
{

	private static List < YTClip >			allClips	= new ArrayList < YTClip >();

	private static ArrayList < ISynsetID >	roots		= new ArrayList < ISynsetID >();

	private static void getRoots( POS pos, IDictionary dict )
	{
		ISynset synset = null;
		Iterator < ISynset > iterator = null;
		List < ISynsetID > hypernyms = null;
		List < ISynsetID > hypernym_instances = null;
		iterator = dict.getSynsetIterator( pos );
		while ( iterator.hasNext() )
		{
			synset = iterator.next();
			hypernyms = synset.getRelatedSynsets( Pointer.HYPERNYM );
			hypernym_instances = synset.getRelatedSynsets( Pointer.HYPERNYM_INSTANCE );

			if ( hypernyms.isEmpty() && hypernym_instances.isEmpty() )
			{
				roots.add( synset.getID() );
			}
		}
	}

	private static void addClip( String clipName, String description, MaxentTagger tagger, WordnetStemmer stemmer )
	{
		boolean found = false;

		List < String > allVerbs = null;

		YTClip currentClip = null;

		for ( YTClip clip : allClips )
		{
			if ( clip.getName().equalsIgnoreCase( clipName ) )
			{
				found = true;

				allVerbs = clip.getVerbsList();

				currentClip = clip;

				break;
			}
		}

		if ( found == false )
		{
			allVerbs = new ArrayList < String >();

			currentClip = new YTClip( clipName );

			allClips.add( currentClip );
		}

		String[] tagged = tagger.tagString( description ).split( " " );

		// extract only the verbs

		for ( String string : tagged )
		{
			if ( string.contains( "_VB" ) )
			{
				for ( String stem : stemmer.findStems( string.split( "_" )[ 0 ], POS.VERB ) )
					if ( !stem.equalsIgnoreCase( "be" ) )
						allVerbs.add( stem );
			}
		}

		currentClip.setVerbsList( allVerbs );
	}

	private static String getMostFreqVerb( List < String > stemmedVerbsList )
	{
		Map < String, Integer > map = new HashMap < String, Integer >();

		for ( String stemmedVerb : stemmedVerbsList )
		{
			if ( map.get( stemmedVerb ) != null )
			{
				map.put( stemmedVerb, 1 + map.get( stemmedVerb ) );
			}
			else
				map.put( stemmedVerb, 1 );
		}

		Iterator < Entry < String, Integer >> iter = map.entrySet().iterator();
		int max = 0;
		String retVal = null;

		while ( iter.hasNext() )
		{
			Map.Entry < String, Integer > mEntry = iter.next();
			if ( max < mEntry.getValue() )
			{
				max = mEntry.getValue();
				retVal = mEntry.getKey();
			}
		}

		map = null;

		return retVal;
	}
	
	private static void PrepareKMeansInputFile() throws IOException
	{
		// read features for applying KMeans
		// take features from every clip as far as possible

		int featuresPerClip = Props.numOfDescriptorsForClustering / allClips.size();

		if ( featuresPerClip <= 0 )
			featuresPerClip = 1;

		// and prepare KMeans Input file

		BufferedWriter writer = new BufferedWriter( new FileWriter( Props.KMeansInputFile ) );

		System.err.println( "Started with Preparing KMeans Input File." );

		for ( YTClip clip : allClips )
		{
			// open corresponding features' file

			String featuresFile = Props.stipFeaturesDirPath + File.separator + clip.getName() + ".features";

			BufferedReader reader;
			try
			{
				reader = new BufferedReader( new FileReader( featuresFile ) );
			}
			catch ( Exception e )
			{
				// You need to extract STIP features before running this code.
				// Please make sure that path for features' directory has been provided correctly.

				System.err.println( "\nThis file should exists : " + featuresFile + "\n" );

				continue;
			}

			// add features to Kmeans clustering Input file.
			int featuresFromThisClip = 0;
			String feature = null;

			while ( ( feature = reader.readLine() ) != null && featuresFromThisClip++ < featuresPerClip )
			{
				writer.write( feature );

				// keep one feature per line

				writer.newLine();
			}

			// close features file

			reader.close();
		}

		writer.close();

		System.err.println( "Done with Preparing KMeans Input File." );
	}

	private static void SimilarityComparisons( IDictionary dict )
	{
		System.err.println( "Started with Similarity comparisons." );

		getRoots( POS.VERB, dict );

		// Initialize WordNet Similarity

		WuAndPalmer wuAndPalmer = new WuAndPalmer( dict, roots );

		// Compare verbs for similarity and update labels

		for ( int i = 0; i < allClips.size() - 1; i++ )
		{
			for ( int j = i + 1; j < allClips.size(); j++ )
			{
				YTClip clip1 = allClips.get( i );
				YTClip clip2 = allClips.get( j );

				List < String > label1 = clip1.getLabel();
				List < String > label2 = clip2.getLabel();

				double maxScore = 0.0;

				// compare labels from one list with another

				for ( String lbl1 : label1 )
				{
					for ( String lbl2 : label2 )
					{
						double tempScore = wuAndPalmer.wup( lbl1, 1, lbl2, 1, "v" );

						maxScore = tempScore > maxScore ? tempScore : maxScore;
					}
				}

				// if similarity is more than threshold, merge two labels' lists.

				if ( maxScore > Props.similarityThreashold )
				{
					// add all label1 labels

					List < String > newLabel = new ArrayList < String >( label1 );

					// add those label2 labels that are not in lable1

					for ( String lbl2 : label2 )
					{
						if ( newLabel.contains( lbl2 ) == false )
						{
							newLabel.add( lbl2 );
						}
					}

					// assign new labels to both clips

					clip1.setLabel( newLabel );
					clip2.setLabel( newLabel );
				}

			}
		}

		System.err.println( "Done with Similarity comparisons." );
	}

	private static void CSVProcessing( MaxentTagger tagger, WordnetStemmer wordNetStemmer ) throws IOException
	{
		String[] nextLine;

		// Read CSV file for descriptions.

		int linesDone = 0;

		System.err.println( "Started processing CSV." );

		// Create a CSV file reader

		CSVReader csvReader = new CSVReader( new FileReader( Props.descriptionsCSVPath ) );

		while ( ( nextLine = csvReader.readNext() ) != null )
		{
			linesDone++;

			if ( linesDone > 0 && linesDone <= Props.numLinesInCSV )
			{
				addClip( nextLine[ 0 ], nextLine[ 1 ], tagger, wordNetStemmer );

				if ( linesDone % 1000 == 0 )
				{
					System.gc();
				}
				if ( linesDone % 1000 == 0 )
				{
					System.err.println( "Lines Done = " + linesDone );
				}
			}
		}

		csvReader = null;

		System.err.println( "Done with CSV processing." );
	}

	private static void RunKMeans() throws IOException
	{
		//		// load data
		//		Dataset data = FileHandler.loadDataset( new File( Props.KMeansInputFile ), " " );
		//
		//		KMeans kMeans = new KMeans( Props.KMeansNumClusters, 1 );
		//
		//		System.err.println( "Started KMeans Clustering ." );
		//
		//		Dataset[] clusters = kMeans.cluster( data );
		//
		//		System.err.println( "Done KMeans Clustering." );
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main( String[] args ) throws IOException, ClassNotFoundException
	{
		//******************************************************************
		//* INITIALIZATIONS - BEGIN
		//******************************************************************
		// Initialize the tagger

		MaxentTagger tagger = new MaxentTagger( Props.taggerFilePath );

		// Initialize dictionary

		String path = Props.wordNet_3_0_DictFolderPath;

		URL url = new URL( "file", null, path );

		IDictionary dict = new Dictionary( url );

		dict.open();

		// Create stemmer

		WordnetStemmer wordNetStemmer = new WordnetStemmer( dict );

		//******************************************************************
		//* INITIALIZATIONS - END
		//******************************************************************

		// Process CSV to get set of verbs for each clip

		CSVProcessing( tagger, wordNetStemmer );

		tagger = null;

		wordNetStemmer = null;

		// set labels of each video clip initially to the most frequent label

		for ( YTClip clip : allClips )
		{
			clip.setMostFreqVerb( getMostFreqVerb( clip.getVerbsList() ) );

			clip.addLabel( clip.getMostFreqVerb() );

			clip.setVerbsList( null );
		}

		// make labels of each clip using WordNet Similarity

		SimilarityComparisons( dict );

		dict.close();

		// try to free as much memory as possible

		System.gc();

		// Read STIP features and prepare input file for clustering

		PrepareKMeansInputFile();

		// Run KMeans clustering

		RunKMeans();

	}
}
