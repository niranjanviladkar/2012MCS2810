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

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import au.com.bytecode.opencsv.CSVReader;

public class Test3
{

	private static List < YTClip >	allClips	= new ArrayList < YTClip >();

	private static void addClip( String clipName, String description )
	{
		for ( YTClip clip : allClips )
		{
			if ( clip.getName().equalsIgnoreCase( clipName ) )
			{
				clip.addDescription( description );
				return;
			}
		}

		YTClip clip = new YTClip( clipName );
		clip.addDescription( description );

		allClips.add( clip );
	}

	private static void tagAndStemAllClips( MaxentTagger tagger, WordnetStemmer wordNetStemmer )
	{
		for ( YTClip clip : allClips )
		{
			for ( String dscr : clip.getDescriptionsList() )
			{
				String[] tagged = tagger.tagString( dscr ).split( " " );

				List < String > allVerbs = clip.getVerbsList();

				// extract only the verbs

				for ( String string : tagged )
				{
					if ( string.contains( "_VB" ) )
					{
						for ( String stem : wordNetStemmer.findStems( string.split( "_" )[ 0 ], POS.VERB ) )
							if ( !stem.equalsIgnoreCase( "be" ) )
								allVerbs.add( stem );
					}
				}

				clip.setVerbsList( allVerbs );
				clip.setDescriptionsList( null );
			}
		}
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
		Props.descriptionsCSVPath = "/misc/buffer/Softwares/MachineLearning_Data/DescriptionsForYouTubeClips/"
				+ "Descriptions.csv";

		// Create a CSV file reader
		
		CSVReader csvReader = new CSVReader( new FileReader( Props.descriptionsCSVPath ) );

		// Initialize the tagger

		MaxentTagger tagger = new MaxentTagger( "/misc/buffer/Softwares/MachineLearning_Data/stanford-postagger-2012-11-11/models/"
				+ "english-bidirectional-distsim.tagger" );

		// Initialize dictionary

		String path = "/misc/buffer/Softwares/MachineLearning_Data/WordNet-3.0/dict";

		URL url = new URL( "file", null, path );

		IDictionary dict = new Dictionary( url );

		dict.open();

		// Create stemmer

		WordnetStemmer wordNetStemmer = new WordnetStemmer( dict );

		String[] nextLine;

		for ( int i = 0; i < 200; i++ )
		{
			nextLine = csvReader.readNext();
			addClip( nextLine[ 0 ], nextLine[ 1 ] );
		}

		int clipIndex = 0;
		//
		// for ( YTClip clip : allClips )
		// {
		// System.out.println( "CLIP : " + clipIndex++ + ". " + clip.getName()
		// );
		//
		// int descIndex = 0;
		//
		// for ( String desc : clip.getDescriptionsList() )
		// {
		// System.out.println( "\t" + descIndex++ + desc );
		// }
		// }

		tagAndStemAllClips( tagger, wordNetStemmer );
		
		tagger = null;

		clipIndex = 0;

		for ( YTClip clip : allClips )
		{
			System.out.print( "CLIP : " + ++clipIndex + ". " + clip.getName() + " : " );

			// System.out.print( "\n\t " );
			//
			// for ( String desc : clip.getVerbsList() )
			// {
			// System.out.print( desc + " " );
			// }
			//
			// System.out.println( "__" );

			System.out.println( getMostFreqVerb( clip.getVerbsList() ) );
			clip.setVerbsList( null );
		}

		// while ( ( nextLine = csvReader.readNext() ) != null )
		// {
		// // nextLine[] is an array of values from the line
		// }

	}

}
