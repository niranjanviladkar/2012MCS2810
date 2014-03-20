#!/bin/bash

projectRoot="/misc/research/parags/mcs122810/ActivityRecognition/"
learn_weights=`echo $projectRoot/alchemy/alchemy-2/bin/learnwts`
training_names_file=`echo $projectRoot/libsvm-3.17/matlab/training_names.txt`
#testing_names_file=`echo $projectRoot/libsvm-3.17/matlab/testing_names.txt`

rules_mln="rules.mln"
output_mln="ouput.mln"

all_db=`cat $training_names_file | sed 's/^/train_db\//' | sed ':a;N;$!ba;s/\n/.db,/g' | sed 's/$/.db/'`
# above complicated sed command is taken from : 
# http://stackoverflow.com/questions/1251999/sed-how-can-i-replace-a-newline-n

$learn_weights -i $rules_mln -o $output_mln -t $all_db -ne HasActivity -queryEvidence
