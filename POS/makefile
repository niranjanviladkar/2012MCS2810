CLASSPATH=/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/bin:/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/lib/ajt-2.9.jar:/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/lib/commons-math-1.2.jar:/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/lib/Jama-1.0.2.jar:/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/lib/jaws-bin.jar:/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/lib/JWI2.2.4_src.jar:/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/lib/libsvm.jar:/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/lib/opencsv-2.3.jar:/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/lib/stanford-postagger.jar:/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/lib/weka.jar:/home/mtech/mcs122810/Project/ActivityRecog/2012MCS2810/POS/lib/meka-1.3.jar

run: compile
	 java -Dfile.encoding=ISO-8859-1 -classpath ${CLASSPATH} in.ac.iitd.cse.Tests.Hollywood2

compile:
	find src -iname "*.java" > sourceFiles;
	javac -classpath ${CLASSPATH} -d bin @sourceFiles;
	rm sourceFiles;
clean:
	rm -rf bin/*

