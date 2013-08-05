/**
 * 
 */
package in.ac.iitd.cse.YouTubeClip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mcs122810
 *
 */
public class YTClip
{
	/**
	 * Name of the AVI file without extension.
	 */
	private String			name;

	/**
	 * All the verbs appearing in all the descriptions of this clip.
	 */
	private List < String >	verbsList;

	/**
	 * Most frequent verb used in all the descriptions of the clip.
	 */
	private String			mostFreqVerb;

	/**
	 * Label given after Hierarchical Agglomerative Clustering.
	 */
	private List < String >	label;

	/**
	 * Label given after Hierarchical Agglomerative Clustering. Represented as a
	 * single string.
	 */
	private String			labelAsString;

	/**
	 * Label read from file. - Completely supervised learning.
	 */
	private int				labelAsInt;

	/**
	 * Height in Hierarchical Agglomerative Clustering.
	 */
	private int				height;

	/**
	 * Cut off HAC at certain height.
	 */
	private boolean			enableHeightRestriction;

	/**
	 * A Clip is represented as histogram over clusters of HoGHoF features.
	 */
	private int[]			histogram;

	/**
	 * Number of HoGHoF features for the clip.
	 */
	private int				numOfFeatures;

	/**
	 * @param name
	 * @throws IOException
	 */
	public YTClip( String clipName )
	{
		this.name = clipName;
		verbsList = new ArrayList < String >();
		label = new ArrayList < String >();
	}

	public void addLabel( String lbl )
	{
		for ( String str : label )
		{
			if ( str.equals( lbl ) )
				return;
		}
		label.add( lbl );
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public List < String > getVerbsList()
	{
		return verbsList;
	}

	public void setVerbsList( List < String > verbsList )
	{
		this.verbsList = verbsList;
	}

	public String getMostFreqVerb()
	{
		return mostFreqVerb;
	}

	public void setMostFreqVerb( String mostFreqVerb )
	{
		this.mostFreqVerb = mostFreqVerb;
	}

	public List < String > getLabel()
	{
		return label;
	}

	public void setLabel( List < String > label )
	{
		this.label = label;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight( int height )
	{
		this.height = height;
	}

	public boolean isEnableHeightRestriction()
	{
		return enableHeightRestriction;
	}

	public void setEnableHeightRestriction( boolean enableHeightRestriction )
	{
		this.enableHeightRestriction = enableHeightRestriction;
	}

	public int[] getHistogram()
	{
		return histogram;
	}

	public void setHistogram( int[] histogram )
	{
		this.histogram = histogram;
	}

	public void incrHistogram( int index )
	{
		this.histogram[ index ]++;
	}

	public String getLabelAsString()
	{
		return labelAsString;
	}

	public void setLabelAsString( String labelAsString )
	{
		this.labelAsString = labelAsString;
	}

	public int getLabelAsInt()
	{
		return labelAsInt;
	}

	public void setLabelAsInt( int labelAsInt )
	{
		this.labelAsInt = labelAsInt;
	}

	public int getNumOfFeatures()
	{
		return numOfFeatures;
	}

	public void setNumOfFeatures( int numOfFeatures )
	{
		this.numOfFeatures = numOfFeatures;
	}

}
