/**
 * Test Class
 */
package in.ac.iitd.cse.Tests;

import java.io.IOException;
import java.util.ArrayList;

import stemmer.Stemmer;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * @author mcs122810
 * 
 */
public class Test1
{

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void main( String[] args ) throws IOException, ClassNotFoundException
	{
		ArrayList < String > allVerbs = new ArrayList < String >();

		Stemmer stemmer = new Stemmer();

		// Initialize the tagger

		MaxentTagger tagger = new MaxentTagger( "/misc/buffer/Softwares/MachineLearning_Data/stanford-postagger-2012-11-11/models/"
				+ "english-bidirectional-distsim.tagger" );

		// The sample string

		String sample = "I just ate an apple. He will be playing. "
				+ "Girl is beautiful. Bird flew. I trkjgyed. He marked. " + "He will be marking. He marks."
				+ "Ahmose I (sometimes written "
				+ "Amosis I and meaning The Moon is Born) was a pharaoh of ancient Egypt "
				+ "and the founder of the Eighteenth dynasty. He was a member of the "
				+ "Theban royal house, the son of pharaoh Tao II Seqenenre and brother"
				+ " of the last pharaoh of the Seventeenth dynasty, King Kamose. Sometime"
				+ " during the reign of his father or grandfather, Thebes rebelled against"
				+ " the Hyksos, the rulers of Lower Egypt. When he was seven his father"
				+ " was killed, and when he was about ten his brother died of unknown causes,"
				+ " after reigning only three years. Ahmose I assumed the throne after the"
				+ " death of his brother, and upon coronation became known as Neb-Pehty-Re"
				+ " (The Lord of Strength is Re). During his reign he completed the conquest"
				+ " and expulsion of the Hyksos from the delta region, restored Theban rule over"
				+ " the whole of Egypt and successfully reasserted Egyptian power in its formerly"
				+ " subject territories of Nubia and Canaan. He then reorganized the administration"
				+ " of the country, reopened quarries, mines and trade routes and began massive"
				+ " construction projects of a type that had not been undertaken since the time of"
				+ " the Middle Kingdom. This building program culminated in the construction of"
				+ " the last pyramid built by native Egyptian rulers. Ahmose's reign laid the"
				+ " foundations for the New Kingdom, under which Egyptian power reached its peak. ";

		// The tagged string

		String tagged = tagger.tagString( sample );

		// separate each tagged word

		String[] s = tagged.split( " " );

		// extract only the verbs

		for ( String string : s )
		{
			if ( string.contains( "_VB" ) )
			{
				allVerbs.add( string.split( "_" )[ 0 ] );
			}
		}

		// stem verbs

		for ( String string : allVerbs )
		{
			stemmer.add( string.toCharArray(), string.length() );
			stemmer.stem();
			System.out.println( string + " : " + stemmer.toString() );
		}
		
		System.setProperty("wordnet.database.dir", "/misc/buffer/Softwares/MachineLearning_Data/WordNet-3.0/dict");
		allVerbs.clear();
		
	}
}
