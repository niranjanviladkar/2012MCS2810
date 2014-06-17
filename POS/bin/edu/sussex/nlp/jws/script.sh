#!/bin/bash

for i in *.java
do
	echo "sed '1 s/\/\///' $i > temp; cat temp > $i"
done
