[trainY trainX] = libsvmread( 'Hollywood2.train' );
[testY testX] = libsvmread( 'Hollywood2.train' );

param = '-q -t 2 -c 50 -g 0.01';

len = length( trainY );
labelSet = unique( trainY );

pred = zeros( len, 1 );
decv = zeros( len, length( labelSet ) );

for i = 1:len
	testInd = [i]; 		% pick one index for testing
	
	trainInd = [1:len]';
	trainInd([i]) = [];	% leave one out and train on others

	% train.
	model = ovrtrain( trainY( trainInd ), trainX( trainInd, : ), param );
	
	% test. p is prediction output, d is decision values' row vector
	[ p, ~, d ] = ovrpredict( trainY( testInd ), trainX( testInd, : ), model );

	pred(i) = p(1);     %store prediction
	decv(i,:) = d(1,:); %store decision value
end

display('Training Done.');


% labelSet is set of all the labels
labelSet = model.labelSet;
avg_precisions = zeros( length(labelSet), 1 );

for i = 1:length( labelSet )
	currentLabel = labelSet( i );

	% find out instances in original data where current label is positive
	truelabels = double( testY == currentLabel );

	% find out instances in classifier output where current label is positive
	classout = double( pred == currentLabel );

	% tp should be 1 in both vectors
	true_positives = sum( double( truelabels & classout ) );

	% fp should be 0 in truelabels and 1 in classifier output
	false_positives = sum( double( (~truelabels) & classout ) );

	% calculate precision
	precision = true_positives / ( true_positives + false_positives );

	avg_precisions( i ) = precision;
end

%display( 'Average precisions for all classes : ' );
display( avg_precisions );

display( 'Average Average Precision : ' );
display( mean(avg_precisions) );

confusionMatrix = zeros( length(labelSet), length(labelSet) );

for i = 1:length( labelSet )
	l1 = labelSet( i );

	truelabels = double( testY == l1 );

	for j = 1:length( labelSet )
		l2 = labelSet( j );

		classout = double( pred == l2 );

		confusions = sum( double( truelabels & classout ) );

		confusionMatrix( i, j ) = confusions;
	end
end

display( 'Confusion Matrix is : ' );

display( confusionMatrix );

exit;
