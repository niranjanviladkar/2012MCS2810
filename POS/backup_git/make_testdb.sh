#!/bin/bash

int_to_str() {
	case $1 in
		1)
			echo \"one\";;
		2)
			echo \"two\";;
		3)
			echo \"three\";;
		4)
			echo \"four\";;
		5)
			echo \"five\";;
		6)
			echo \"six\";;
		7)
			echo \"seven\";;
		8)
			echo \"eight\";;
		9)
			echo \"nine\";;
		10)
			echo \"ten\";;
		11)
			echo \"eleven\";;
		12)
			echo \"twelve\";;
	esac
}

projectRoot="/misc/research/parags/mcs122810/ActivityRecognition/"

# this file should contain names of avi clips without .avi extension
# that are actually used for training SVM. The file should contain names 
# in the order in which these clips are read from the disk.
training_names_file=`echo $projectRoot/libsvm-3.17/matlab/training_names.txt`

# similarly, this file contains ordered names of testing files.
# Both of training and testing names files should be obtained from java code
# which processes these files to train SVM.
testing_names_file=`echo $projectRoot/libsvm-3.17/matlab/testing_names.txt`

# These are the predictions ( and not the true labels ) made by learnt SVM mdel.
# These file should be obtained from matlab ( while runnng Hollywood2.m )
svm_predictions_file=`echo $projectRoot/libsvm-3.17/matlab/Hollywood2.predictions`

# This file contains a matrix having one row per clip.
# Each row has 12 columns (as there are 12 acitivities)
# each cell represents decision value output coming from SVM
# higher the decv value, more is the confidence.
# The nth row in this file corresponds to nth clip from
# testing file names, so the order is atmost important.
decv_file=`echo $projectRoot/libsvm-3.17/matlab/Hollywood2.decv`

# This file contains true labels for testing clips.
# This file comes with the dataset.
true_test_labels=`echo $projectRoot/dataset/Hollywood/ClipSets/all_labels_test.txt`

debug=0

i=1
total=`wc -l $svm_predictions_file | cut -f1 -d ' '`
mkdir -p test_db
cat $svm_predictions_file | while read label 
do
	test_file=`tail -n+$i $testing_names_file | head -n1`
	decv_line=`tail -n+$i $decv_file | head -n1`
	num_cols=`echo $decv_line | wc -w`
	output_file=`echo test_db/$test_file.db`

	true_lbl_line=`cat $true_test_labels | grep $test_file`
	line1=`echo $true_lbl_line | cut -f2- -d ' '`

	rm -f $output_file 2>/dev/null
	echo Processing $test_file.avi... \( $i out of $total\)
	
	if [ $debug -eq 0 ]; then
		j=1
		while [ ! -z "$true_lbl_line" ]; do
			this_lbl=`echo $line1 | cut -f1 -d ' '`
			if [ "$this_lbl" = "1" ]; then
				t=$(int_to_str $j)
				echo "HasActivity(\"$test_file\",$t)" > $output_file
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
		echo "HasActivity(\"$test_file\",$t)" > $output_file
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
	i=`echo $i + 1 | bc`
done;


