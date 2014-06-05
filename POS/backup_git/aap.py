#!/usr/bin/python

import re


projectRoot = "/misc/research/parags/mcs122810/ActivityRecognition/"

outputOfInferFile = projectRoot + "alchemy/project5_analysis_1db/outputOfInfer.mln"

append_testDbFile = projectRoot + "alchemy/project5_analysis_1db/append_test.db"

# all classes file
classesFile = projectRoot + 'dataset/Hollywood/ClipSets/classes.txt'

classes = {} 
i = 0
classFD = open( classesFile, "r" )
for line in classFD:
	classes[i] = line.rstrip( "\n" )
	i += 1
classFD.close()

#for i in xrange(12):
#	print classes[i]

def getInferredVector( action ):
	vector = {}
	i = 0
	outputOfInferFD = open( outputOfInferFile, "r" )
	for line in outputOfInferFD:
		if re.search( action, line ):
			lineArray = line.split()
			clipName = lineArray[0].split('"')[1]
			probability = float(lineArray[1])

			if probability > 0.9:
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

	#print "{0}, {1}".format(truePositiveSum, falsePositiveSum)

	precision = float( truePositiveSum ) / float ( truePositiveSum + falsePositiveSum )
	return precision


#precision = getPrecision( classes[4] )
#print classes[4] + "\t" + str(precision)
#exit()


allPrecisionsVector = {}
for i in xrange( len( classes ) ):
	allPrecisionsVector[i] = getPrecision( classes[i] )
	print classes[i] + "\t" + str(allPrecisionsVector[i])

for i in xrange( len( classes ) ):
	allPrecisionsVector[i] = getPrecision( classes[i] )
	print allPrecisionsVector[i]
print "aap" + "\t" + str( sum( allPrecisionsVector ) / len( allPrecisionsVector ) )
exit()
#print "aap" + "\t" + str( sum( allPrecisionsVector ) / len( allPrecisionsVector ) )
#exit()


#for i in xrange( len(inferredVector) ):
#	print inferredVector[i]

#for i in xrange( len( trueVector ) ):
#	print trueVector[i]


