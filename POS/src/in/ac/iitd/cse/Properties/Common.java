/**
 * Copyright Niranjan Aniruddha Viladkar 2012 - 2013, All rights Reserved.
 */
package in.ac.iitd.cse.Properties;

/**
 * @author shree
 *
 */
public class Common
{
	public static enum DataSet
	{
		// @formatter:off - turns off code style formatting in eclipse 
		
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
}
