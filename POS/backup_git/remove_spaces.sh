#!/bin/bash

clipDir="/misc/research/parags/mcs122810/ActivityRecognition/dataset/Hollywood1/hollywood/videoclips"
outDir="/misc/research/parags/mcs122810/ActivityRecognition/dataset/Hollywood1/hollywood/videoclips_nospace"

mkdir -p $outDir

ls -1 $clipDir | while read line
do
	newName=`echo "$line" | sed s/\ //g`
	mv $clipDir/"$line" $outDir/$newName
done;
exit 0;
