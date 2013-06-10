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
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * @author mcs122810
 * 
 */
public class Test4
{

	private static List < YTClip >			allClips	= new ArrayList < YTClip >();

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

		// Create stemmer

		WordnetStemmer wordNetStemmer = new WordnetStemmer( dict );

		String[] nextLine;

		// / Read CSV file for descriptions.

		// for ( int i = 0; i < Props.numLinesInCSV; i++ )
		// {
		// nextLine = csvReader.readNext();
		// addClip( nextLine[ 0 ], nextLine[ 1 ], tagger, wordNetStemmer );
		// }

		int linesDone = 0;
		while ( ( nextLine = csvReader.readNext() ) != null )
		{
			linesDone++;

			if ( linesDone > 0 && linesDone <= 86 )
			{
				addClip( nextLine[ 0 ], nextLine[ 1 ], tagger, wordNetStemmer );

				if ( linesDone % 1000 == 0 )
				{
					System.gc();
				}
				if ( linesDone % 1000 == 0 )
				{
					System.out.println( "Lines Done = " + linesDone );
				}
			}
		}

		tagger = null;

		wordNetStemmer = null;

		csvReader = null;

		dict.close();

		int clipIndex = 0;

		for ( YTClip clip : allClips )
		{
			System.out.print( "CLIP : " + ++clipIndex + ". " + clip.getName() + " : " );

			clip.setMostFreqVerb( getMostFreqVerb( clip.getVerbsList() ) );

			clip.addLabel( clip.getMostFreqVerb() );

			System.out.println( clip.getLabel() );

			clip.setVerbsList( null );
		}
	}

}
