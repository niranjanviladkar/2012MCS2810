#!/bin/bash

#export LD_LIBRARY_PATH="/misc/research/parags/mcs122810/ActivityRecognition/stip-setup"

clipDir="/misc/research/parags/mcs122810/ActivityRecognition/dataset/Hollywood1/hollywood/short"
outDir="/misc/research/parags/mcs122810/ActivityRecognition/features/Hollywood1"
stipDir="/misc/research/parags/mcs122810/ActivityRecognition/stip-setup"

ls -1Sr $clipDir | cut -f1 -d "." > clipNames
mkdir -p $outDir

cat clipNames | while read line 
do
	echo "$line" > video-list.txt
	size=`ls -s $clipDir/"${line}".avi | sed "s/^ *//g" | cut -f1 -d " "`
	#if [ 4000 -gt $size ]; then

		echo "$line.avi started : `date` size : $size" 
		echo ${stipDir}/bin/stipdet -i video-list.txt -vpath $clipDir/ -o "$outDir/$line.txt" -vis no >/dev/null 2>/dev/null 
		
		# TODO : some clips are short. so no STIPs are detected and thus no $line.txt file is created
		# make sure that empty $line.features file is created.

		tail -n +3 "$outDir/$line.txt" | while read feature
		do
			# echo $feature | cut -f10- -d ' ' >> "$outDir/$line.features"
		done;
		# rm "$outDir/$line.txt"
		echo "$line.avi done : `date`" 
		#echo "---------" 
	#else
	#	echo "$line.avi skipped : `date` size : $size"
	#fi;
done;

echo "Done : `date`" 
echo "--------------------------------------" 
echo "--------------------------------------" 
echo "--------------------------------------"
exit 0
