#!/usr/bin/python

import re

outputOfInferFile = "outputOfInfer.mln"
append_testDbFile = "append_test.db"

# all classes file
classesFile = '/misc/research/parags/mcs122810/ActivityRecognition/dataset/Hollywood/ClipSets/classes.txt'

classes = {} 
i = 0
classFD = open( classesFile, "r" )
for line in classFD:
	classes[i] = line.rstrip( "\n" )
	i += 1
classFD.close()

def getInferredActivity( clip ):
	i = 0
	maxProb = -1
	inferredActivity = ""
	outputOfInferFD = open( outputOfInferFile, "r" )
	for line in outputOfInferFD:
		if re.search( clip, line ):
			lineArray = line.split()
			activity = lineArray[0].split('"')[3]
			probability = float( lineArray[1] )

			if probability > maxProb:
				maxProb = probability
				inferredActivity = activity
	outputOfInferFD.close()
	return inferredActivity

def getAllInferred ():
	i = 0
	vector = {}
	prevClip = ""
	outputOfInferFD = open( outputOfInferFile, "r" )
	for line in outputOfInferFD:
		lineArray = line.split()
		clipName = lineArray[0].split('"')[1]
		if clipName != prevClip:
			prevClip = clipName
			vector[i] = getInferredActivity( clipName )
			i += 1

	outputOfInferFD.close()
	return vector

def getInferredVector( action ):
	vector = {}
	i = 0
	outputOfInferFD = open( outputOfInferFile, "r" )
	for line in outputOfInferFD:
		if re.search( action, line ):
			lineArray = line.split()
			clipName = lineArray[0].split('"')[1]
			probability = float(lineArray[1])

			if getInferredActivity( clipName ) == action :
				vector[i] = 1
			else:
				vector[i] = 0
			i += 1

	outputOfInferFD.close()
	return vector

def getTrueVector( action ):
	vector = {}
	i = 0
	trueTestFD = open( append_testDbFile, "r" )
	for line in trueTestFD:
		if re.search( action, line ):
			vector[i] = 1
		else:
			vector[i] = 0
		i += 1
	trueTestFD.close()
	return vector

def getAllTrue():
	vector = {}
	i = 0
	trueTestFD = open( append_testDbFile, "r" )
	for line in trueTestFD:
		lineArray = line.split('"')
		vector[i] = lineArray[3]
		i += 1
	trueTestFD.close()
	return vector



def negate( v ):
	vector = {}
	i = 0
	for i in xrange( len( v ) ):
		vector[i] = int( not( v[i] ) )
	return vector

def andAll( v1, v2 ):
	vector = {}
	i = 0
	for i in xrange( len( v1 ) ):
		vector[i] = int( bool(v1[i]) & bool(v2[i]) )
	return vector

def sum( v ):
	sum = 0
	i = 0
	for i in xrange( len( v ) ):
		sum += v[i]
	return sum

def show( v ):
	i = 0
	for i in xrange( len( v ) ):
		print "{0}:{1}".format( i, v[i] )

def showTwo( v1, v2 ):
	i = 0
	for i in xrange( len(v1) ):
		print "{0}{1}".format(v1[i], v2[i])
def getPrecision( action ):
	inferredVector = getInferredVector( action )
	trueVector = getTrueVector( action )

#	show( inferredVector )
#	showTwo( trueVector, inferredVector )

	truePositiveSum = sum( andAll( trueVector, inferredVector ) )
	falsePositiveSum = sum ( andAll( negate(trueVector), inferredVector ) )

	print "{2} : tp = {0}, fp = {1}, true = {3}, infer = {4}".format(truePositiveSum, falsePositiveSum, action, sum(trueVector), sum(inferredVector))
	#print "{0}, {1}".format(truePositiveSum, falsePositiveSum)

	precision = float( truePositiveSum ) / float ( truePositiveSum + falsePositiveSum )
	return precision

def getMicroAccuracyVector( v1, v2 ):
	i = 0
	vector = {}
	for i in xrange( len(v1) ):
		if v1[i] == v2[i]:
			vector[i] = 1
		else:
			vector[i] = 0
	return vector

#precision = getPrecision( classes[4] )
#print classes[4] + "\t" + str(precision)
#exit()


allPrecisionsVector = {}
for i in xrange( len( classes ) ):
	allPrecisionsVector[i] = getPrecision( classes[i] )
	print classes[i] + "\t" + str(allPrecisionsVector[i])

for i in xrange( len( classes ) ):
	print allPrecisionsVector[i]

aap = sum( allPrecisionsVector ) / len( allPrecisionsVector ) 

print "aap" + "\t" + str( aap )

inferredLabels = getAllInferred()
trueLabels = getAllTrue()

microAcVec = getMicroAccuracyVector( inferredLabels, trueLabels )
microAccuracy = float ( sum( microAcVec ) ) / float ( len( microAcVec ) )

print "accuracy = {0}\n".format( microAccuracy )
exit()
