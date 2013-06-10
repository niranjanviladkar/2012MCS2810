CLASSPATH=/home/mtech/mcs122810/workspaceEclipse/POS/bin:/misc/buffer/Softwares/MachineLearning_Data/ExternalJars/jaws-bin.jar:/misc/buffer/Softwares/MachineLearning_Data/ExternalJars/stanford-postagger.jar:/misc/buffer/Softwares/MachineLearning_Data/ExternalJars/opencsv-2.3.jar:/misc/buffer/Softwares/MachineLearning_Data/ExternalJars/JWI2.2.4_src.jar:/misc/buffer/Softwares/MachineLearning_Data/ExternalJars/ajt-2.9.jar:/misc/buffer/Softwares/MachineLearning_Data/ExternalJars/commons-math-1.2.jar:/misc/buffer/Softwares/MachineLearning_Data/ExternalJars/Jama-1.0.2.jar:/misc/buffer/Softwares/MachineLearning_Data/ExternalJars/libsvm.jar:/misc/buffer/Softwares/MachineLearning_Data/ExternalJars/weka.jar

run: compile
	 java -Dfile.encoding=ISO-8859-1 -classpath ${CLASSPATH} in.ac.iitd.cse.Tests.Hollywood2

compile:
	find src -iname "*.java" > sourceFiles;
	javac -classpath ${CLASSPATH} -d bin @sourceFiles;
	rm sourceFiles;
clean:
	rm -rf bin/*

