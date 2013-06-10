/**
 * 
 */
package in.ac.iitd.cse.Tests;

import in.ac.iitd.cse.Properties.Props;
import in.ac.iitd.cse.YouTubeClip.YTClip;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
 * Reads CSV and finds classes of clips using similarity.
 * 
 * @author mcs122810 ( Niranjan A Viladkar )
 * 
 */
public class Test5
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

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main( String[] args ) throws IOException, ClassNotFoundException
	{
		// How many lines from CSV need to be read?

		Props.numLinesInCSV = 430;

		// Create a CSV file reader

		CSVReader csvReader = new CSVReader( new FileReader( Props.descriptionsCSVPath ) );

		// Initialize the tagger

		MaxentTagger tagger = new MaxentTagger( Props.taggerFilePath );

		// Initialize dictionary

		String path = Props.wordNet_3_0_DictFolderPath;

		URL url = new URL( "file", null, path );

		IDictionary dict = new Dictionary( url );

		dict.open();

		getRoots( POS.VERB, dict );

		// Initialize WordNet Similarity

		WuAndPalmer wuAndPalmer = new WuAndPalmer( dict, roots );

		// Create stemmer

		WordnetStemmer wordNetStemmer = new WordnetStemmer( dict );

		String[] nextLine;

		// Read CSV file for descriptions.

		int linesDone = 0;

		System.err.println( "Started processing CSV." );

		while ( ( nextLine = csvReader.readNext() ) != null )
		{
			linesDone++;

			if ( linesDone > 0 && linesDone <= 2000 )
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

		System.err.println( "Done with CSV processing." );

		tagger = null;

		wordNetStemmer = null;

		csvReader = null;

		// set labels of each video clip initially to the most frequent label

		for ( YTClip clip : allClips )
		{
			clip.setMostFreqVerb( getMostFreqVerb( clip.getVerbsList() ) );

			clip.addLabel( clip.getMostFreqVerb() );

			clip.setVerbsList( null );
		}

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

		for ( YTClip clip : allClips )
		{
			System.out.print( clip.getName() + "\t" );

			// print all labels 
			for ( String lbl : clip.getLabel() )
			{
				System.out.print( lbl + ", " );
			}

			System.out.println( "" );

		}
		dict.close();
	}
}
