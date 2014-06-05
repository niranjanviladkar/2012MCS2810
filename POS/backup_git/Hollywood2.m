[trainY trainX] = libsvmread( 'Hollywood2.train' );
[testY testX] = libsvmread( 'Hollywood2.train' );

%scaleFactor=10;

%a=trainX;
%b=testX;

%for i = 1:length( trainX )
	% zero mean
	% trainX( :, i ) = trainX( :, i ) - mean( a( :, i ) );
	
	% unit variance
	% trainX( :, i ) = trainX( :, i ) /  std( a( :, i ) );
%	m = max( a(:, i) );
%	if( m > 0 )
%		trainX( :, i ) = a( :, i ) / m * scaleFactor;
%	end
%end

%for i = 1:length( testX )
	% zero mean test data - normalize as per training data
	% testX( :, i ) = testX( :, i ) - mean( a( :, i ) );
	% unit variance
	% testX( :, i ) = testX( :, i ) /  std( a( :, i ) );
%	m = max( b(:, i) );
%	if( m > 0 )
%		testX( :, i ) = b( :, i ) / m * scaleFactor;
%	end
%end;

% train one-vs-rest with RBF kernel. C = 50 and gamma = 0.01
% model = ovrtrain( trainY, trainX, '-q -t 2 -c 150 -g 0.001'); % 1500, 1 0.1486
model = ovrtrain( trainY, trainX, '-q -g 0.01 -c 100'); % c = 3000, 0.1521

display('Training Done.');

% pred is prediction output, ac is accuracy, decv is decision values
[pred ac decv] = ovrpredict(testY, testX, model);

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
	if( (true_positives + false_positives) > 0 )
		precision = true_positives / ( true_positives + false_positives );
	else
		precision = 0;
	end

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

hw2_pred = fopen( 'Hollywood2.predictions', 'w' );

for i = 1 : length(pred)
	fprintf( hw2_pred, '%d\n', pred(i));
end
fclose( hw2_pred );

dlmwrite( 'Hollywood2.decv', decv, ' ' );
