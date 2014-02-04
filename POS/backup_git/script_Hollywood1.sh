#!/bin/bash

clipDir="/misc/research/parags/mcs122810/ActivityRecognition/dataset/Hollywood1/hollywood/videoclips_nospace"
outDir="/misc/research/parags/mcs122810/ActivityRecognition/features/Hollywood1"
stipDir="/misc/research/parags/mcs122810/ActivityRecognition/stip-setup"

export LD_LIBRARY_PATH=$stipDir

# use names of clips and frame infor from clipNames file.
# this file is assumend to be created before this script
# format of files is same as format of video-list.txt
# <clipname without extension> <start frame> <end frame>

mkdir -p $outDir

cat clipNames | while read line 
do
	echo "$line" > video-list.txt
	clipName=`echo $line | cut -f1 -d ' '`

	echo "$clipName.avi started : `date` size : $size" 

	# create new features' file name for multiple extractions out of same avi clip.
	dest=$clipName
	i=1;
	while [ -s "$dest.features" ]; do
		dest=`echo $dest | cut -f1 -d '_' `
		dest=`echo $dest\_$i`
		i=`echo $i + 1 | bc`
	done;


	${stipDir}/bin/stipdet -i video-list.txt -vpath $clipDir/ -o "$outDir/$dest.txt"  -vis no #>/dev/null 2>/dev/null 
	
	if [ ! -f "$outDir/$dest.txt" ]; then
		touch "$outDir/$dest.features" # create empty file
	else
		tail -n +3 "$outDir/$dest.txt" | while read feature
		do
			echo $feature | cut -f10- -d ' ' >> "$outDir/$dest.features"
		done;
		rm "$outDir/$dest.txt"
	fi;
	echo "$clipName.avi done : `date`" 
	echo "---------" 
done;

echo "Done : `date`" 
echo "--------------------------------------" 
echo "--------------------------------------" 
echo "--------------------------------------"
exit 0
