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
	private String			name;
	private List < String >	verbsList;
	private String			mostFreqVerb;
	private List < String >	label;
	private String labelAsString;
	private int				height;
	private boolean			enableHeightRestriction;
	private int[]			histogram;

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

}
