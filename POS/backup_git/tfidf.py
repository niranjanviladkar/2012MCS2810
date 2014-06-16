#!/usr/bin/python

import itertools
import os, os.path
import re
import sys

if (len(sys.argv) != 4)\
	or ( ( sys.argv[1] != "train") and ( sys.argv[1] != "test") )\
	or ( ( sys.argv[2] != "obj" ) and ( sys.argv[2] != "noobj") )\
	or ( ( sys.argv[3] != "ppl" ) and ( sys.argv[3] != "noppl") ):
		print "USAGE : {0} [train|test] [obj|noobj] [ppl|noppl] ".format( sys.argv[0] )
		exit(-1)

if sys.argv[1] == "train":
	isTrain = True
else:
	isTrain = False

if sys.argv[2] == "obj":
	isObj = True
else:
	isObj = False

if sys.argv[3] == "ppl":
	isPpl = True
else:
	isPpl = False

projectRoot = "/misc/research/parags/mcs122810/ActivityRecognition/"

# This file contains true labels for testing clips.
# This file comes with the dataset.
if isTrain == True:
	trueLabelsFile = projectRoot + "dataset/Hollywood/ClipSets/all_labels_train.txt"
else:
	trueLabelsFile = projectRoot + "dataset/Hollywood/ClipSets/all_labels_test.txt"

# This is the directory which contains output of object detector 
if isTrain == True :
	obj_dir = projectRoot + "detectedOBJ/Hollywood2/train/"
else:
	obj_dir = projectRoot + "detectedOBJ/Hollywood2/test/"

# LibSVM directory
svm_dir = projectRoot + "libsvm-3.17/matlab/"

# this file contains names of clips
if isTrain == True :
	clipNamesFile = svm_dir + "training_names.txt"
else:
	clipNamesFile = svm_dir + "testing_names.txt"

# this file is original SVM input file
if isTrain == True :
	toBeAppendedFile = svm_dir + "Hollywood2.train"
else:
	toBeAppendedFile = svm_dir + "Hollywood2.test"

# appended output file
if isTrain == True :
	appendedFile = svm_dir + "Hollywood2_tfidf.train"
else:
	appendedFile = svm_dir + "Hollywood2_tfidf.test"

objModels = [ "" for x in xrange(10)]

objModels[0]="bicycle"
objModels[1]="bottle"
objModels[2]="bus"
objModels[3]="car"
objModels[4]="chair"
objModels[5]="diningtable"
objModels[6]="motorbike"
objModels[7]="person"
objModels[8]="sofa"
objModels[9]="tvmonitor"

thresholds = {	objModels[0] : -0.9, # bicycle 
		objModels[1] : -0.83, # bottel
		objModels[2] : -0.83, # bus
		objModels[3] : -0.2, # car
		objModels[4] : -0.77, # chair
		objModels[5] : -0.8, # diningtable
		objModels[6] : -0.9, # motorbike
		objModels[7] : -0.7, # person
		objModels[8] : -0.87, # sofa
		objModels[9] : -0.8, # tmonitor
	}

# Number of occurences of each object 
TotalObjCount = { objModels[0] : 0, # bicycle 
		objModels[1] : 0, # bottel
		objModels[2] : 0, # bus
		objModels[3] : 0, # car
		objModels[4] : 0, # chair
		objModels[5] : 0, # diningtable
		objModels[6] : 0, # motorbike
		objModels[7] : 0, # person
		objModels[8] : 0, # sofa
		objModels[9] : 0, # tmonitor
	}


for objOutputClip in os.listdir( obj_dir ):
	objFD = open( obj_dir + "/" + objOutputClip, "r" )
	for line in objFD:
		if re.search( "FRAME", line ):
			continue
		obj = line.split()[0]
		confidence = float( line.split()[1] )
		if float( confidence ) > float( thresholds[ obj ]):
			TotalObjCount[obj] += 1
	objFD.close()

# get number of occurences of object OBJ in clip
def getFreq( clipName, obj ):
	freq = 0
	objFD = open( obj_dir + "/" + clipName, "r" )
	for line in objFD:
		if re.search( "FRAME", line ):
			continue
		if not(re.search( obj, line) ):
			continue
		confidence = float(line.split()[1])
		if confidence > thresholds[obj]:
			freq += 1
	objFD.close()
	return freq

def getAvgPpl( clipName ):
	frames = 0
	numPplThisFrame = 0
	avgPplThisClip = 0.0

	objFD = open( obj_dir + "/" + clipName, "r" )

	for line in objFD :
		if re.search( "FRAME", line ):
			if frames != 0:
				avgPplThisClip = float( avgPplThisClip * (frames - 1) + numPplThisFrame ) / float( frames )
			numPplThisFrame = 0 
			frames += 1
		else:
			arr = line.split()
			obj = arr[0]
			if obj != "person":
				continue
			score = float(arr[1])
			threshold = thresholds[ "person" ]

			if score >= threshold :
				numPplThisFrame += 1

	if frames != 0:
		avgPplThisClip = float( avgPplThisClip * (frames - 1) + numPplThisFrame ) / float( frames )

	objFD.close()
	return avgPplThisClip

def termFreq( clipName, obj ):
	return getFreq( clipName, obj )

def docFreq( obj ):
	return TotalObjCount[obj]

# append features to original SVM features.
# clipName is the name of clip with extension like - actionacliptrain00001.avi
#
# vectorLine is complete SVM training instance line as a string
# example - "11 1:0.21 2:0.02 3:0.8 4: .... 200:0.31"
#
# isObj and isPpl are boolean flags to decide if obj and ppl are to be appended
def appendFeatures( clipName, vectorLine, isObj, isPpl ):
	initialNumFeatures	= int( vectorLine.split()[-1].split( ':' )[0] )
	curFeatureIndex		= initialNumFeatures + 1
	vectorLine += " "

	if isObj == True :
		for i in xrange( len(objModels) ):
			tf	= termFreq( clipName, objModels[i])
			idf 	= float ( 1/float( docFreq( objModels[i] ) ) )
			feature	= float(tf) * float(idf)
			vectorLine += str(curFeatureIndex) + ":" + str(feature) + " "
			curFeatureIndex += 1

	if isPpl == True :
		avgPplThisClip	= getAvgPpl( clipName )
		vectorLine     += str(curFeatureIndex) + ":" + str(avgPplThisClip) + " "

	return vectorLine

clipNamesFD 	= open( clipNamesFile, "r" )
toBeAppendedFD 	= open( toBeAppendedFile, "r" )
appendedFD 	= open( appendedFile, "w" )

for clip, vectorLine in itertools.izip( clipNamesFD, toBeAppendedFD ):
	clipName = clip.rstrip() + ".avi"
	output   = appendFeatures( clipName, vectorLine.rstrip(), isObj, isPpl )
	appendedFD.write( output + "\n" )

appendedFD.close()
toBeAppendedFD.close()
clipNamesFD.close()
