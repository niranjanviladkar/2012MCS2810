[trainY trainX] = libsvmread( 'Hollywood2.train' );
%[testY testX] = libsvmread( 'Hollywood2.test' );

RBF = 0
kfold = length(trainY)

clow = 1; cstep= 3; chigh= 7;
glow = -13; gstep= 4; ghigh= 3;

fid = fopen( 'Hollywood2_CV.output', 'w' );
%fid = 1 % stdout

bestcv = 0;
if( RBF == 1 ),
	for log2c = clow : cstep : chigh,
		for log2g = glow : gstep : ghigh,
			cmd = ['-q -c ', num2str(2^log2c), ' -g ', num2str(2^log2g)];
			cv = get_cv_ac( trainY, trainX, cmd, kfold );
			if (cv >= bestcv),
				bestcv = cv; bestc = 2^log2c; bestg = 2^log2g;
			end
			fprintf( fid, '%g %g %g (best c=%g, g=%g, rate=%g)\n', log2c, log2g, cv, bestc, bestg, bestcv);
			fprintf( 1, '%g %g %g (best c=%g, g=%g, rate=%g)\n', log2c, log2g, cv, bestc, bestg, bestcv);
		end
	end
else

	for log2c = clow : cstep : chigh 
		cmd = [ '-q -c ', num2str( 2^log2c ) ];
		cv = get_cv_ac( trainY, trainX, cmd, kfold );
		if( cv >= bestcv ),
			bestcv = cv; bestc = 2^log2c;
		end
		fprintf( fid, '%g %g ( best c = %g, rate = %g )\n', log2c, cv, bestc, bestcv);
		fprintf( 1, '%g %g ( best c = %g, rate = %g )\n', log2c, cv, bestc, bestcv);
	end
end

fclose( fid );
