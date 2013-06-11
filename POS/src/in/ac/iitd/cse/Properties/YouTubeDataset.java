package in.ac.iitd.cse.Properties;

/**
 * @author mcs122810
 * 
 */
public class YouTubeDataset
{

	/**
	 * This CSV contains two fields : <br/>
	 * <ol>
	 * <ul>
	 * First field is name of the clip without extension <br/>
	 * then comma as field separator
	 * </ul>
	 * <ul>
	 * then Second field is natural language description of the video - Should
	 * not contain comma.
	 * </ul>
	 * </ol>
	 */
	public static String	descriptionsCSVPath				= "/misc/buffer/Softwares/MachineLearning_Data/"
																	+ "DescriptionsForYouTubeClips/Descriptions.csv";

	/**
	 * Length of one STIP descriptor.<br/>
	 * Typically this is 90 + 72 = 162. ( HoG + HoF )
	 */
	public static int		DescriptorLength				= 162;

	/**
	 * Path to the directory which will contain final clips represented as
	 * histograms over KMeans clusters.
	 */
	public static String	histogramDirPath				= "/media/NIRANJAN/Project/detected/histograms";

	/**
	 * Input file used for KMeans clustering.
	 */
	public static String	KMeansInputFile					= "/media/NIRANJAN/Project/detected/KMeansInputFile.data";

	/**
	 * Input file used for KMeans clustering.
	 */
	public static String	KMeansOutputFile				= "/media/NIRANJAN/Project/detected/KMeansOutputFile.data";

	/**
	 * Value of K used for KMeans clustering.
	 */
	public static int		KMeansNumClusters				= 200;

	/**
	 * Path to the directory which will contain final clip labels.
	 */
	public static String	labelDirPath					= "/media/NIRANJAN/Project/detected/labels";

	/***
	 * How many lines from CSV need to be read? <br/>
	 * Default is zero.
	 ***/
	public static int		numLinesInCSV					= 90000;

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
	public static String	stipFeaturesDirPath				= "/media/NIRANJAN/Project/detected";

	/*** Tagger model file used by MaxentTagger. ***/
	public static String	taggerFilePath					= "/misc/buffer/Softwares/MachineLearning_Data/"
																	+ "stanford-postagger-2012-11-11/models/"
																	+ "english-bidirectional-distsim.tagger";

	/*** Directory location of folder dict of WordNet-3.0. ***/
	public static String	wordNet_3_0_DictFolderPath		= "/misc/buffer/Softwares/MachineLearning_Data/"
																	+ "WordNet-3.0/dict";
}
