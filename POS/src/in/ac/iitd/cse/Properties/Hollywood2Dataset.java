package in.ac.iitd.cse.Properties;

/**
 * @author mcs122810
 * 
 */
public class Hollywood2Dataset
{
	private static String	projectRoot						= "/misc/research/parags/mcs122810/ActivityRecognition/";

	/**
	 * Length of one STIP descriptor.<br/>
	 * Typically this is 90 + 72 = 162. ( HoG + HoF )
	 */
	public static int		DescriptorLength				= 162;

	/**
	 * Path to the directory which will contain final clips represented as
	 * histograms over KMeans clusters.
	 */
	public static String	histogramDirPath				= projectRoot + "histograms/Hollywood2";

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
	public static int		KMeansNumClusters				= 200;

	/**
	 * Path to the directory which will contain final clip labels.
	 */
	public static String	labelDirPath					= projectRoot + "dataset/Hollywood/ClipSets3/ClipSets";

	/**
	 * Number of sampled descriptors to be used for KMeans clustering.
	 */
	public static int		numOfDescriptorsForClustering	= 500000;

	/**
	 * Verbs having similarity score above this threshold are considered
	 * similar.
	 **/
	public static double	similarityThreashold			= 0.6;

	/**
	 * Path to the directory which contains STIP HoG - HoF features.<br/>
	 * These features are Extracted using<br/>
	 * ``http://www.irisa.fr/vista/Equipe/People/Laptev
	 * /download/stip-2.0-linux.zip"<br/>
	 * Strip out extra output and keep ONLY last 162 fields.<br/>
	 * 1 Descriptor per line.
	 */
	public static String	stipFeaturesDirPath				= projectRoot + "features/Hollywood";

	/*** Tagger model file used by MaxentTagger. ***/
	public static String	taggerFilePath					= "/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/"
																	+ "POS/tagger/english-bidirectional-distsim.tagger";

	/*** Directory location of folder dict of WordNet-3.0. ***/
	public static String	wordNet_3_0_DictFolderPath		= "/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/"
																	+ "POS/dict";
}
