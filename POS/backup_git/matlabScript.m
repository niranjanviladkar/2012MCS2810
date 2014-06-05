X = load( 'KMeansInputFile.data', '-ascii');
k = 500;
maxIter = 200;
initial = length(X);

fprintf( '%s : Started.\nIntial number of vectors: %d\nNum of Clusters: %d\n', date, initial, k );

opts = statset('Display', 'iter', 'MaxIter', maxIter );
[~, C] = kmeans( X, k, 'emptyaction', 'singleton', 'Options', opts );

fprintf( '%s : Finished\n', date );
dlmwrite('KMeansOutputFile_500.data', C, ' ');
exit;
