/**
 * 
 */
package in.ac.iitd.cse.Classifier;

import in.ac.iitd.cse.YouTubeClip.YTClip;

import java.util.List;

/**
 * @author mcs122810
 * 
 */
public class LibSvmClassifier extends PrepareInstance
{
	public void Train()
	{
		System.out.println( "Use libsvm." );
	}

	public void Test()
	{
		System.out.println( "Use libsvm." );
	}

	/**
	 * @throws Exception
	 */
	public LibSvmClassifier() throws Exception
	{
		super();
	}
	
	public LibSvmClassifier( List < YTClip > trainingList, List< YTClip> testingList ) throws Exception
	{
		super( trainingList, testingList );
	}

}
