#!/bin/bash

int_to_str() {
	case $1 in
		1)
			echo \"AnswerPhone\";;
		2)
			echo \"DriveCar\";;
		3)
			echo \"Eat\";;
		4)
			echo \"FightPerson\";;
		5)
			echo \"GetOutCar\";;
		6)
			echo \"HandShake\";;
		7)
			echo \"HugPerson\";;
		8)
			echo \"Kiss\";;
		9)
			echo \"Run\";;
		10)
			echo \"SitDown\";;
		11)
			echo \"SitUp\";;
		12)
			echo \"StandUp\";;
	esac
}

projectRoot="/misc/research/parags/mcs122810/ActivityRecognition/"

# similarly, this file contains ordered names of testing files.
# Both of training and testing names files should be obtained from java code
# which processes these files to train SVM.
testing_names_file=`echo $projectRoot/libsvm-3.17/matlab/testing_names.txt`

# These are the predictions ( and not the true labels ) made by learnt SVM mdel.
# These file should be obtained from matlab ( while runnng Hollywood2.m )
svm_predictions_file=`echo $projectRoot/libsvm-3.17/matlab/Hollywood2.test.predictions`

# This file contains a matrix having one row per clip.
# Each row has 12 columns (as there are 12 acitivities)
# each cell represents decision value output coming from SVM
# higher the decv value, more is the confidence.
# The nth row in this file corresponds to nth clip from
# testing file names, so the order is atmost important.
decv_file=`echo $projectRoot/libsvm-3.17/matlab/Hollywood2.test.decv`

# This file contains true labels for testing clips.
# This file comes with the dataset.
true_test_labels=`echo $projectRoot/dataset/Hollywood/ClipSets/all_labels_test.txt`

# This is the directory which contains output of object detector 
obj_test_dir=`echo $projectRoot/detectedOBJ/Hollywood2/test`


debug=0
processObj=1

i=1
total=`wc -l $svm_predictions_file | cut -f1 -d ' '`
#mkdir -p test_db
if [ $processObj -eq 1 ]; then
	# determine which object detectors are run on the clips
	# this information is obtained from the VOC directory 
	# inside the object detector code.
	# basically, we are looking for VOC2007/*_final.mat

#	modelDir=`echo $projectRoot/ObjectDetector/voc-release4.01/VOC2007/`
#	#declare -a objModels;
#	index=0;
#
#	ls -1 $modelDir/*_final.mat | while read model
#	do
#		echo $index
#		objModels=\(${objModels[*]} "`basename $model | cut -f1 -d '_'`"\)
#		index=`echo $index + 1 | bc`
#	done
#	#echo ${objModels[*]}
#	exit
	#objModels=\(bicycle bottle bus car chair diningtable motorbike person sofa tvmonitor\)
	# TODO - remove this hard coding eventually.
	# refer to - http://www.thegeekstuff.com/2010/06/bash-array-tutorial/
	declare -a objModels
	objModels[0]=bicycle
	objModels[1]=bottle
	objModels[2]=bus
	objModels[3]=car
	objModels[4]=chair
	objModels[5]=diningtable
	objModels[6]=motorbike
	objModels[7]=person
	objModels[8]=sofa
	objModels[9]=tvmonitor
	#echo ${objModels[*]}
	#exit
	function getThreshold {
		case $1 in
			bicycle)
				echo -0.9
				;;
			bottle)
				echo -0.83
				;;
			bus)
				echo -0.83
				;;
			car)
				echo -0.2
				;;
			chair)
				echo -0.77
				;;
			diningtable)
				echo -0.8 
				;;
			motorbike)
				echo -0.9
				;;
			person)
				echo -0.7
				;;
			sofa)
				echo -0.87
				;;
			tvmonitor)
				echo -0.8
				;;
		esac
	}

fi

output_file="test.db"
output_file2="append_test.db"
rm -rf $output_file $output_file2
touch $output_file
touch $output_file2


cat $svm_predictions_file | while read label 
do
	test_file=`tail -n+$i $testing_names_file | head -n1`
	decv_line=`tail -n+$i $decv_file | head -n1`
	num_cols=`echo $decv_line | wc -w`
	#output_file=`echo test_db/$test_file.db`

	true_lbl_line=`cat $true_test_labels | grep $test_file`
	line1=`echo $true_lbl_line | cut -f2- -d ' '`

	#rm -f $output_file 2>/dev/null
	echo Processing $test_file.avi... \( $i out of $total\)
	
	if [ $debug -eq 0 ]; then
		j=1
		while [ ! -z "$true_lbl_line" ]; do
			this_lbl=`echo $line1 | cut -f1 -d ' '`
			if [ "$this_lbl" = "1" ]; then
				t=$(int_to_str $j)
				echo "HasActivity(\"$test_file\",$t)" >> $output_file2
			fi

			line2=`echo $line1 | cut -f2- -d ' '`
			if [ "$line1" = "$line2" ]; then
				break
			fi

			line1=$line2
			j=`echo $j+1 | bc`
		done	
	else
		t=$(int_to_str $label)
		echo "HasActivity(\"$test_file\",$t)" >> $output_file2
	fi

	j=1
	while [ $j -le $num_cols ]; do
		decv_val=`echo $decv_line | cut -f$j -d ' '`
		t=$(int_to_str $j)

		echo | awk -v n1=$decv_val -v n2=$t -v file=$test_file '{ 
		if( n1 < -2 )
			printf( "ActivityConf_NI_TO_N2(\"%s\",%s)\n", file, n2 );
		else if( n1 < -1 )
			printf( "ActivityConf_N2_TO_N1(\"%s\",%s)\n", file,  n2 );
		else if( n1 < 0 )
			printf( "ActivityConf_N1_TO_P0(\"%s\",%s)\n", file,  n2 );
		else if( n1 < 1 )
			printf( "ActivityConf_P0_TO_P1(\"%s\",%s)\n", file,  n2 );
		else if( n1 < 2 )
			printf( "ActivityConf_P1_TO_P2(\"%s\",%s)\n", file,  n2 );
		else
			printf( "ActivityConf_P2_TO_PI(\"%s\",%s)\n", file,  n2 );
		}' >> $output_file

		j=`echo $j + 1 | bc`
	done

#	if [ $processObj -eq 1 ]; then
#		for j in "${objModels[@]}"
#		do
#			grep -m 1 $j $obj_test_dir/$test_file.avi >/dev/null 2>&1
#			temp=$?
#			if [ $temp -eq 0 ]; then
#				echo "ObjPresent(\"$test_file\",\"$j\")" >> $output_file
#			fi
#		done	
#	fi

	if [ $processObj -eq 1 ]; then
		cat $obj_test_dir/$test_file.avi | while read line
	do
		case "$line" in
			*FRAME*)
				;;
			*)
				obj=`echo $line | cut -f1 -d ' '`
				score=`echo $line | cut -f2 -d ' '`
				threshold=`getThreshold $obj`

				echo | awk -v file=$test_file -v o=$obj -v s=$score -v t=$threshold '{
					if( s >= t )
						printf( "ObjPresent(\"%s\",\"%s\")\n", file, o );
				}' >> $output_file
				;;
		esac
	done
	fi

	i=`echo $i + 1 | bc`
done;


