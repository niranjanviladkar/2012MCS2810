#!/usr/bin/python
import re
import sys

ppl = 1

if (len(sys.argv) != 3) or ( (sys.argv[1] != "train") and (sys.argv[1] != "test" ) ) or ( (sys.argv[2] != "obj") and (sys.argv[2] != "noobj") ):
	print "USAGE : {0} [train|test] [obj|noobj]".format( sys.argv[0] )
	exit(-1)
else:
	type = sys.argv[1]

if sys.argv[2] == "obj" :
	processObj = 1
else:
	processObj = 0


rulesFile = "rules.mln"

rulesFD = open( rulesFile, "w" )

rulesFD.write( "/*\n" )
rulesFD.write( "*  variables start with a lower case.\n" )
rulesFD.write( "*  so, for example,\n" )
rulesFD.write( "*  activity = { Run, Hug, Kiss, Eat, Fight } etc.\n" )
rulesFD.write( "*  'a' of activity has to be lower cased.\n" )
rulesFD.write( "*  first letter of each constant has to be upper cased.\n" )
rulesFD.write( "*  so, 'R' of run is upper cased.\n" )
rulesFD.write( "*  \n" )
rulesFD.write( "*  After the declaration of types and constants, come formulas.\n" )
rulesFD.write( "*/\n" )
rulesFD.write( "\n" )
rulesFD.write( "HasActivity(clip,activity)\n" )
rulesFD.write( "\n" )
rulesFD.write( "ActivityConf_NI_TO_N2(clip,activity)\n" )
rulesFD.write( "ActivityConf_N2_TO_N15(clip,activity)\n" )
rulesFD.write( "ActivityConf_N15_TO_N1(clip,activity)\n" )
rulesFD.write( "ActivityConf_N1_TO_N05(clip,activity)\n" )
rulesFD.write( "ActivityConf_N05_TO_P0(clip,activity)\n" )
rulesFD.write( "ActivityConf_P0_TO_P05(clip,activity)\n" )
rulesFD.write( "ActivityConf_P05_TO_P1(clip,activity)\n" )
rulesFD.write( "ActivityConf_P1_TO_P15(clip,activity)\n" )
rulesFD.write( "ActivityConf_P15_TO_P2(clip,activity)\n" )
rulesFD.write( "ActivityConf_P2_TO_PI(clip,activity)\n" )
rulesFD.write( "\n" )
rulesFD.write( "ActivityConf_NI_TO_N2(c,a) => HasActivity(c,a)\n" )
rulesFD.write( "ActivityConf_N2_TO_N15(c,a) => HasActivity(c,a)\n" )
rulesFD.write( "ActivityConf_N15_TO_N1(c,a) => HasActivity(c,a)\n" )
rulesFD.write( "ActivityConf_N1_TO_N05(c,a) => HasActivity(c,a)\n" )
rulesFD.write( "ActivityConf_N05_TO_P0(c,a) => HasActivity(c,a)\n" )
rulesFD.write( "ActivityConf_P0_TO_P05(c,a) => HasActivity(c,a)\n" )
rulesFD.write( "ActivityConf_P05_TO_P1(c,a) => HasActivity(c,a)\n" )
rulesFD.write( "ActivityConf_P1_TO_P15(c,a) => HasActivity(c,a)\n" )
rulesFD.write( "ActivityConf_P15_TO_P2(c,a) => HasActivity(c,a)\n" )
rulesFD.write( "ActivityConf_P2_TO_PI(c,a) => HasActivity(c,a)\n" )
rulesFD.write( "\n" )

if processObj == 1:
	rulesFD.write( "ObjPresent(clip,object)\n" )
	rulesFD.write("ObjPresent(c,\"diningtable\") => HasActivity(c,\"StandUp\")\n")
	rulesFD.write("ObjPresent(c,\"sofa\") => HasActivity(c,\"StandUp\")\n")
	rulesFD.write("ObjPresent(c,\"chair\") => HasActivity(c,\"StandUp\")\n")
	rulesFD.write("ObjPresent(c,\"sofa\") => HasActivity(c,\"SitDown\")\n")
	rulesFD.write("ObjPresent(c,\"chair\") => HasActivity(c,\"SitDown\")\n")
	rulesFD.write("ObjPresent(c,\"bus\") => HasActivity(c,\"Run\")\n")
	rulesFD.write("ObjPresent(c,\"motorbike\") => HasActivity(c,\"Run\")\n")
	rulesFD.write("ObjPresent(c,\"bicycle\") => HasActivity(c,\"Run\")\n")
	rulesFD.write("ObjPresent(c,\"car\") => HasActivity(c,\"Run\")\n")
	rulesFD.write("ObjPresent(c,\"bus\") => HasActivity(c,\"GetOutCar\")\n")
	rulesFD.write("ObjPresent(c,\"motorbike\") => HasActivity(c,\"GetOutCar\")\n")
	rulesFD.write("ObjPresent(c,\"bicycle\") => HasActivity(c,\"GetOutCar\")\n")
	rulesFD.write("ObjPresent(c,\"car\") => HasActivity(c,\"GetOutCar\")\n")
	rulesFD.write("ObjPresent(c,\"bottle\") => HasActivity(c,\"Eat\")\n")
	rulesFD.write("ObjPresent(c,\"diningtable\") => HasActivity(c,\"Eat\")\n")
	rulesFD.write("ObjPresent(c,\"sofa\") => HasActivity(c,\"Eat\")\n")
	rulesFD.write("ObjPresent(c,\"chair\") => HasActivity(c,\"Eat\")\n")
	rulesFD.write("ObjPresent(c,\"bus\") => HasActivity(c,\"DriveCar\")\n")
	rulesFD.write("ObjPresent(c,\"motorbike\") => HasActivity(c,\"DriveCar\")\n")
	rulesFD.write("ObjPresent(c,\"car\") => HasActivity(c,\"DriveCar\")\n")
	rulesFD.write("ObjPresent(c,\"tvmonitor\") => HasActivity(c,\"AnswerPhone\")\n")
	rulesFD.write("ObjPresent(c,\"sofa\") => HasActivity(c,\"AnswerPhone\")\n")
	rulesFD.write("ObjPresent(c,\"chair\") => HasActivity(c,\"AnswerPhone\")\n")
	rulesFD.write("ObjPresent(c,\"sofa\") => HasActivity(c,\"SitUp\")\n")
	rulesFD.write("ObjPresent(c,\"chair\") => HasActivity(c,\"Kiss\")\n") 
	rulesFD.write("ObjPresent(c,\"sofa\") => HasActivity(c,\"Kiss\")\n")
	rulesFD.write( "\n" )
#	rulesFD.write("ObjPresent(c,\"car\") => HasActivity(c,\"SitUp\")\n")
#	rulesFD.write("ObjPresent(c,\"bicycle\") => HasActivity(c,\"SitUp\")\n")
#	rulesFD.write("ObjPresent(c,\"motorbike\") => HasActivity(c,\"SitUp\")\n")
#	rulesFD.write("ObjPresent(c,\"bus\") => HasActivity(c,\"SitUp\")\n")
#	rulesFD.write("ObjPresent(c,\"bicycle\") => HasActivity(c,\"AnswerPhone\")\n")
#	rulesFD.write("ObjPresent(c,\"motorbike\") => HasActivity(c,\"AnswerPhone\")\n")
#	rulesFD.write("ObjPresent(c,\"sofa\") => HasActivity(c,\"DriveCar\")\n")
#	rulesFD.write("ObjPresent(c,\"tvmonitor\") => HasActivity(c,\"DriveCar\")\n")
#	rulesFD.write("ObjPresent(c,\"car\") => HasActivity(c,\"Eat\")\n")
#	rulesFD.write("ObjPresent(c,\"bicycle\") => HasActivity(c,\"Eat\")\n")
#	rulesFD.write("ObjPresent(c,\"car\") => HasActivity(c,\"FightPerson\")\n")
#	rulesFD.write("ObjPresent(c,\"bicycle\") => HasActivity(c,\"FightPerson\")\n")
#	rulesFD.write("ObjPresent(c,\"chair\") => HasActivity(c,\"GetOutCar\")\n")
#	rulesFD.write("ObjPresent(c,\"bottle\") => HasActivity(c,\"GetOutCar\")\n")
#	rulesFD.write("ObjPresent(c,\"tvmonitor\") => HasActivity(c,\"GetOutCar\")\n")
	rulesFD.write("ObjPresent(c,\"car\") => HasActivity(c,\"HandShake\")\n")
	rulesFD.write("ObjPresent(c,\"bicycle\") => HasActivity(c,\"HandShake\")\n")
	rulesFD.write("ObjPresent(c,\"motorbike\") => HasActivity(c,\"HandShake\")\n")
	rulesFD.write("ObjPresent(c,\"car\") => HasActivity(c,\"HugPerson\")\n")
	rulesFD.write("ObjPresent(c,\"bicycle\") => HasActivity(c,\"HugPerson\")\n")
	rulesFD.write("ObjPresent(c,\"bus\") => HasActivity(c,\"HugPerson\")\n")
	rulesFD.write("ObjPresent(c,\"bicycle\") => HasActivity(c,\"Kiss\")\n")
	rulesFD.write("ObjPresent(c,\"motorbike\") => HasActivity(c,\"Kiss\")\n")
	rulesFD.write("ObjPresent(c,\"bus\") => HasActivity(c,\"Kiss\")\n")
#	rulesFD.write("ObjPresent(c,\"diningtable\") => HasActivity(c,\"Run\")\n")
#	rulesFD.write("ObjPresent(c,\"bottle\") => HasActivity(c,\"Run\")\n")
	rulesFD.write("ObjPresent(c,\"car\") => HasActivity(c,\"SitDown\")\n")
	rulesFD.write("ObjPresent(c,\"motorbike\") => HasActivity(c,\"SitDown\")\n")
	rulesFD.write("ObjPresent(c,\"bus\") => HasActivity(c,\"SitDown\")\n")
	rulesFD.write("ObjPresent(c,\"car\") => HasActivity(c,\"StandUp\")\n")
	rulesFD.write("ObjPresent(c,\"bicycle\") => HasActivity(c,\"StandUp\")\n")
	rulesFD.write("ObjPresent(c,\"bus\") => HasActivity(c,\"StandUp\")\n")
	rulesFD.write( "\n" )

	
if ppl == 1:
	rulesFD.write("NumPersons_0_TO_1(clip)\n")
	rulesFD.write("NumPersons_1_TO_15(clip)\n")
	rulesFD.write("NumPersons_15_TO_2(clip)\n")
	rulesFD.write("NumPersons_2_TO_I(clip)\n")
	rulesFD.write("NumPersons_0_TO_1(c) => HasActivity(c,+a)\n")
	rulesFD.write("NumPersons_1_TO_15(c) => HasActivity(c,+a)\n")
	rulesFD.write("NumPersons_15_TO_2(c) => HasActivity(c,+a)\n")
	rulesFD.write("NumPersons_2_TO_I(c) => HasActivity(c,+a)\n")
	


rulesFD.close()

projectRoot = "/misc/research/parags/mcs122810/ActivityRecognition/"

# this file should contain names of avi clips without .avi extension
# that are actually used for training SVM. The file should contain names 
# in the order in which these clips are read from the disk.
if type == "train":
	clipNames_file = projectRoot + "libsvm-3.17/matlab/training_names.txt"
else:
	clipNames_file = projectRoot + "libsvm-3.17/matlab/testing_names.txt"

# These are the predictions ( and not the true labels ) made by learnt SVM mdel.
# These file should be obtained from matlab ( while runnng Hollywood2.m )
if type == "train" :
	svm_predictions_file = projectRoot + "libsvm-3.17/matlab/Hollywood2_tfidf.train.predictions"
else:
	svm_predictions_file = projectRoot + "libsvm-3.17/matlab/Hollywood2_tfidf.test.predictions"

# This file contains a matrix having one row per clip.
# Each row has 12 columns (as there are 12 acitivities)
# each cell represents decision value output coming from SVM
# higher the decv value, more is the confidence.
# The nth row in this file corresponds to nth clip from
# testing file names, so the order is atmost important.

# Important - for training, this file should be obtained from SVM using
# both training and testing datasets as training only.
if type == "train" :
	decv_file = projectRoot + "libsvm-3.17/matlab/Hollywood2_tfidf.train.decv"
else:
	decv_file = projectRoot + "libsvm-3.17/matlab/Hollywood2_tfidf.test.decv"

# This file contains true labels for testing clips.
# This file comes with the dataset.
if type == "train" :
	true_labels = projectRoot + "dataset/Hollywood/ClipSets/all_labels_train.txt"
else:
	true_labels = projectRoot + "dataset/Hollywood/ClipSets/all_labels_test.txt"

# This is the directory which contains output of object detector 
if type == "train" :
	obj_dir = projectRoot + "detectedOBJ/Hollywood2/train"
else:
	obj_dir = projectRoot + "detectedOBJ/Hollywood2/test"

if type == "train" :
	output_file = "train.db"
else:
	output_file = "test.db"
	output_file2 = "append_test.db"

total = 0
fd = open( svm_predictions_file, "r")
for line in fd:
	total += 1

activities = {}
activities[0] = "AnswerPhone"
activities[1] = "DriveCar"
activities[2] = "Eat"
activities[3] = "FightPerson"
activities[4] = "GetOutCar"
activities[5] = "HandShake"
activities[6] = "HugPerson"
activities[7] = "Kiss"
activities[8] = "Run"
activities[9] = "SitDown"
activities[10] = "SitUp"
activities[11] = "StandUp"

objModels = {}
objModels[0] = "bicycle"
objModels[1] = "bottle"
objModels[2] = "bus"
objModels[3] = "car"
objModels[4] = "chair"
objModels[5] = "diningtable"
objModels[6] = "motorbike"
objModels[7] = "person"
objModels[8] = "sofa"
objModels[9] = "tvmonitor"

thresholds = {}
thresholds[ "bicycle" ] = -0.9
thresholds[ "bottle" ] = -0.83
thresholds[ "bus" ] = -0.83
thresholds[ "car" ] = -0.2
thresholds[ "chair" ] = -0.77
thresholds[ "diningtable" ] = -0.8
thresholds[ "motorbike" ] = -0.9
thresholds[ "person" ] = -0.7
thresholds[ "sofa" ] = -0.87
thresholds[ "tvmonitor" ] = -0.8

def getIthLine( file, ith ):
	retLine = ""
	fd = open( file, "r" )
	for i, line in enumerate( fd ):
		if i == ( ith - 1 ):
			retLine = line.rstrip( "\n" )
			break
	fd.close()
	return retLine

def getTLL( clipName ):
	retLine = ""
	fd = open( true_labels, "r" )
	for line in fd:
		if re.search( clipName, line ):
			retLine = line.rstrip( "\n" )
			break
	fd.close()
	return retLine

outFD = open( output_file, "w" )
if type == "test" :
	outFD2 = open( output_file2, "w" )

svm_predictFD = open( svm_predictions_file, "r" )
i = 0
for label in svm_predictFD:
	i += 1
	clipName = getIthLine( clipNames_file, i )
	decv_line = getIthLine( decv_file, i )
	true_lbl_line = getTLL( clipName )
	true_lbl = ""

#	print "Processing {0}.avi ( {1} out of {2} )".format( clipName, i, total )
	tll_array = true_lbl_line.split()
	for j in xrange( len(tll_array) ):
		if tll_array[j] == "1":
			true_lbl = activities[ j - 1 ]
			if type == "train":
				outFD.write( "HasActivity(\"{0}\",\"{1}\")\n".format(clipName, activities[j - 1] ) )
			else:
				outFD2.write( "HasActivity(\"{0}\",\"{1}\")\n".format(clipName, activities[j - 1] ) )

	decv_array = decv_line.split()
	for j in xrange( len(decv_array) ):
		n1 = float(decv_array[j])
		if n1 < -2: 
			outFD.write( "ActivityConf_NI_TO_N2(\"{0}\",\"{1}\")\n".format( clipName, activities[j] ) );
		elif n1 < -1.5:
			outFD.write( "ActivityConf_N2_TO_N15(\"{0}\",\"{1}\")\n".format( clipName, activities[j] ) );
		elif n1 < -1:
			outFD.write( "ActivityConf_N15_TO_N1(\"{0}\",\"{1}\")\n".format( clipName, activities[j] ) );
		elif n1 < -0.5:
			outFD.write( "ActivityConf_N1_TO_N05(\"{0}\",\"{1}\")\n".format( clipName, activities[j] ) );
		elif n1 < 0:
			outFD.write( "ActivityConf_N05_TO_P0(\"{0}\",\"{1}\")\n".format( clipName, activities[j] ) );
		elif n1 < 0.5:
			outFD.write( "ActivityConf_P0_TO_P05(\"{0}\",\"{1}\")\n".format( clipName, activities[j] ) );
		elif n1 < 1:
			outFD.write( "ActivityConf_P05_TO_P1(\"{0}\",\"{1}\")\n".format( clipName, activities[j] ) );
		elif n1 < 1.5:
			outFD.write( "ActivityConf_P1_TO_P15(\"{0}\",\"{1}\")\n".format( clipName, activities[j] ) );
		elif n1 < 2:
			outFD.write( "ActivityConf_P15_TO_P2(\"{0}\",\"{1}\")\n".format( clipName, activities[j] ) );
		else :
			outFD.write( "ActivityConf_P2_TO_PI(\"{0}\",\"{1}\")\n".format( clipName, activities[j] ) );

	if (processObj == 1) or (ppl == 1) :
		frames = 0
		objFD = open ( obj_dir + "/" + clipName + ".avi", "r" )
		numPplThisFrame = 0
		avgPplThisClip = 0.0
		
		for line in objFD:
			if re.search( "FRAME", line ):
				if frames != 0:
					avgPplThisClip = float( avgPplThisClip * (frames - 1) + numPplThisFrame ) / float( frames )
				numPplThisFrame = 0
				frames += 1
			else:
				arr = line.split()
				obj = arr[0]
				score = float(arr[1])
				threshold = thresholds[obj]

				if score >= threshold :
					if processObj == 1 :
						outFD.write( "ObjPresent(\"{0}\",\"{1}\")\n".format( clipName, obj ) )

					if obj == "person" :
						numPplThisFrame += 1

		if frames != 0:
			avgPplThisClip = float( avgPplThisClip * (frames - 1) + numPplThisFrame ) / float( frames )
			if ppl == 1:
				if avgPplThisClip < 1:
					outFD.write( "NumPersons_0_TO_1(\"{0}\")\n".format( clipName ) )
				elif avgPplThisClip < 1.5:
					outFD.write( "NumPersons_1_TO_15(\"{0}\")\n".format( clipName ) )
				elif avgPplThisClip < 2:
					outFD.write( "NumPersons_15_TO_2(\"{0}\")\n".format( clipName ) )
				elif avgPplThisClip >= 2:
					outFD.write( "NumPersons_2_TO_I(\"{0}\")\n".format( clipName ) )

				
		
#		print "{0}\t{1}".format( avgPplThisClip, true_lbl )
		

		objFD.close()

outFD.close()
if type == "test":
	outFD2.close()

