/**
 * Copyright IIT Delhi 2012 - 2013, All rights Reserved.
 */
package in.ac.iitd.cse.Properties;

/**
 * @author mcs122810
 * 
 */
public class Common
{
	public static enum DataSet
	{
		// @formatter:off - turns off code style formatting in eclipse 
		
		HOLLYWOOD1( false ),
		HOLLYWOOD2( false ), 
		YOUTUBE( false );
		
		// @formatter:on - turns on code style formatting in eclipse

		private boolean	isCurrent;

		DataSet( boolean b )
		{
			this.isCurrent = b;
		}

		public void currentDS( boolean b )
		{
			this.isCurrent = b;
		}

		public boolean currentDS()
		{
			return isCurrent;
		}
	}

	public static enum Classifier
	{
		// @formatter:off - turns off code style formatting in eclipse 
		
		WEKA( false ),
		MEKA( false ),
		LIBSVM( false );
		
		// @formatter:on - turns on code style formatting in eclipse

		private boolean	isCurrent;

		Classifier( boolean b )
		{
			this.isCurrent = b;
		}

		public void setCurrentClassifier( boolean b )
		{
			this.isCurrent = b;
		}

		public boolean getCurrentClassifier()
		{
			return isCurrent;
		}
	}

	public static enum state
	{
		// @formatter:off - turns off code style formatting in eclipse 
		
		TAGGER_INITIALISED( false ), 
		STEMMER_INITIALISED( false ), 
		DICT_OPENED( false ), 
		ALL_TRAINING_CLIPS_INITIALISED( false ), 
		ALL_TESTING_CLIPS_INITIALISED( false ),
		ALL_CLIPS_INITIALISED( false ),
		SIMILARITY_DONE( false ), 
		KMEANS_INPUT_PREPARED( false ), 
		ROOTS_INITIALISED( false ), 
		CLIP_TO_HISTOGRAM_DONE( false );
		
		// @formatter:on - turns on code style formatting in eclipse

		private boolean	isDone;

		state( boolean b )
		{
			this.isDone = b;
		}

		public void isDone( boolean b )
		{
			this.isDone = b;
		}

		public boolean isDone()
		{
			return isDone;
		}
	}
}
