#!/usr/bin/python

import re

#Intuition :
#We need to check if the training data has signal in it.
#We do following :
#For each activity, we need to find in each clip of that activity,
#iwhat is the average number of times a given object occurs.
#Do this for each object per activity.
#
#We have 12 actions, 10 objects. So it will be a 12 by 10 matrix.
#


projectRoot = "/misc/research/parags/mcs122810/ActivityRecognition/"

# This file contains true labels for testing clips.
# This file comes with the dataset.
true_train_labels = projectRoot + "dataset/Hollywood/ClipSets/all_labels_train.txt"

# all classes file
classes = projectRoot + 'dataset/Hollywood/ClipSets/classes.txt'

# This is the directory which contains output of object detector 
obj_train_dir = projectRoot + "detectedOBJ/Hollywood2/train/"


# 12 rows and 10 columns matrix
Matrix = [[0 for x in xrange(10)] for x in xrange(12)]
ActivityClipCount = [ 0 for x in xrange(12) ]
objModels = [ "" for x in xrange(10)]
Activities = [ "" for x in xrange(12) ]

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
#print thresholds
#exit(0)

i = 0
classesFile = open( classes, "r" )
for line in classesFile:
	Activities[i] = line.rstrip("\n")
	i += 1
classesFile.close()

trueTrainFile = open( true_train_labels, "r" )
temp = 0
# read each line in true labels file
for line in trueTrainFile:
	#print line
	fields = line.split()
	clipName = fields[0]
#	print "Processing " + clipName

	# look for "1" representing presence of activity
	for i in range(1,13):

		# if this activity is present
		if fields[i] == "1" :
			row = i - 1 # row number corresponding to the activity
			col = 0     # column number corresponds to the object
			ActivityClipCount[ row ] += 1 # count number of clips with this activity

			# for each object in our object models
			for obj in objModels:
				objFile = open( obj_train_dir + clipName + ".avi", "r" )
				# count the number of occurences of this object
				count = 0
				for objLine in objFile:
					if re.search( obj, objLine ):
						scoreLine = objLine.split()
						#if float(scoreLine[1]) > -0.8: 
						if float(scoreLine[1]) > thresholds[ obj ]: 
							count += 1
				objFile.close()
				Matrix[row][col] += count # fill corresponding entry
				col += 1
	temp += 1
	if temp == 1000 :
		break
trueTrainFile.close()

#for i in range(len(ActivityClipCount)):
#	if ActivityClipCount[i] != 0:
#		for j in range(len(Matrix[0])):
#			Matrix[i][j] /= ActivityClipCount[i]
toShow = 0
print "{0:12}".format(''),
for i in range(10):
#	if i == toShow:
		print "{0:>12}".format( objModels[i] ),

print
print "{0:12}".format('Thresholds'),
for i in range(10):
#	if i == toShow:
		print "{0:>12.4f}".format( thresholds[ objModels[i] ] ),

print


for i in range(12):
	print "{0:<12}".format(Activities[i]),
	for j in range(10):
		#print "%4.4f" % Matrix[i][j],
		#print "{:5.5f}".format( Matrix[i][j]),
#		if j == toShow :
			print "{0:>12d}".format( Matrix[i][j] ),
	print
print

for i in range(12):
	print "{0:>12}".format(Activities[i]),
print
for i in range(len(ActivityClipCount)):
	print "{0:>12}".format( ActivityClipCount[i] ) ,
print
