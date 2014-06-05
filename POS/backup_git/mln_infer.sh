#!/bin/bash


projectRoot="/misc/research/parags/mcs122810/ActivityRecognition/"
infer=`echo $projectRoot/alchemy/alchemy-2/bin/infer`
testing_names_file=`echo $projectRoot/libsvm-3.17/matlab/testing_names.txt`

output_mln="output.mln"
all_db="test.db"

# all_db=`cat $testing_names_file | sed 's/^/test_db\//' | sed ':a;N;$!ba;s/\n/.db,/g' | sed 's/$/.db/'`
# above complicated sed command is taken from : 
# http://stackoverflow.com/questions/1251999/sed-how-can-i-replace-a-newline-n

$infer -i $output_mln -e $all_db -r outputOfInfer.mln -q HasActivity
#$infer -i $output_mln -e $all_db -r outputOfInfer.mln -q HasActivity -queryEvidence
