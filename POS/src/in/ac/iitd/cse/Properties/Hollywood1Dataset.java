/**
 * 
 */
package in.ac.iitd.cse.Properties;

/**
 * @author mcs122810
 * 
 */
public class Hollywood1Dataset
{
	private static String	projectRoot						= "/misc/research/parags/mcs122810/ActivityRecognition/";

	public static String	ClipsDirPath					= projectRoot + "dataset/Hollywood1/hollywood/short/";

	/**
	 * Length of one STIP descriptor.<br/>
	 * Typically this is 90 + 72 = 162. ( HoG + HoF )
	 */
	public static int		DescriptorLength				= 162;

	/**
	 * Path to the directory which will contain final clips represented as
	 * histograms over KMeans clusters.
	 */
	public static String	histogramDirPath				= projectRoot + "histograms/Hollywood1/";

	/**
	 * Labels for video clips for clean training.
	 */
	public static String	labelsFile_trainClean			= projectRoot
																	+ "dataset/Hollywood1/hollywood/annotations_nospace/train_clean.txt";

	/**
	 * Labels for video clips for clean testing.
	 */
	public static String	labelsFile_testClean			= projectRoot
																	+ "dataset/Hollywood1/hollywood/annotations_nospace/test_clean.txt";

	/**
	 * Labels for video clips for auto training.
	 */
	public static String	labelsFile_trainAuto			= projectRoot
																	+ "dataset/Hollywood1/hollywood/annotations_nospace/train_auto.txt";

	/**
	 * Path to the libsvm directory.
	 */
	public static String	libsvmDir						= projectRoot + "libsvm-3.17/matlab/";

	/**
	 * Input file used for KMeans clustering.
	 */
	public static String	KMeansInputFile					= projectRoot + "stip-extraction/KMeansInputFile.data";

	/**
	 * Input file used for KMeans clustering.
	 */
	public static String	KMeansOutputFile				= projectRoot + "stip-extraction/KMeansOutputFile.data";

	/**
	 * Value of K used for KMeans clustering.
	 */
	public static int		KMeansNumClusters				= 1000;

	/**
	 * Path to the directory which will contain final clip labels.
	 */
	public static String	labelDirPath					= projectRoot + "dataset/Hollywood1/hollywood/annotations_nospace/";

	/**
	 * Number of sampled descriptors to be used for KMeans clustering.
	 */
	public static int		numOfDescriptorsForClustering	= 100000;

	//	public static String	previousRunDir					= projectRoot + "previousRun/Hollywood2";

	/**
	 * Path to the directory which contains STIP HoG - HoF features.<br/>
	 * These features are Extracted using<br/>
	 * ``http://www.irisa.fr/vista/Equipe/People/Laptev
	 * /download/stip-2.0-linux.zip"<br/>
	 * Strip out extra output and keep ONLY last 162 fields.<br/>
	 * 1 Descriptor per line.
	 */
	public static String	stipFeaturesDirPath				= projectRoot + "features/Hollywood1/";
}
