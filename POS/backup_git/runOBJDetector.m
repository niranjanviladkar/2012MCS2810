function runOBJDetector( from, to )

projectRoot = '/misc/research/parags/mcs122810/ActivityRecognition/';
modelDir    = fullfile( projectRoot, 'ObjectDetector/voc-release4.01/VOC2007/' );
clipDir     = fullfile( projectRoot, 'dataset/Hollywood/AVIClips05' );
outDir      = fullfile( projectRoot, 'detectedOBJ/Hollywood2/' );
logFile     = fullfile( projectRoot, 'ObjectDetector/voc-release4.01/log_AR_');

% find number of test clips
clipList = dir( fullfile( clipDir, 'actioncliptest*.avi' ) );

% list all *_final.mat files i.e. models in madelDir
modelList = dir( fullfile(modelDir, '*_final.mat'));


% find current number of log files to find name for next log file
temp = dir( strcat( logFile, '*.log' ) );
temp = 1 + length( temp );
logFile = strcat( logFile, int2str(temp), '.log' );
logid = fopen( logFile, 'a' );


fprintf( logid, 'from = %d to = %d length(clipList) = %d\n', from, to, length(clipList) );

% dont go out of bound
if ( to > length( clipList ) )
	to = length( clipList );
end

for clipIndex = from:to
	clipName = clipList( clipIndex ).name;

	if( exist( fullfile( outDir, clipName ) ) ~= 0 )
		continue
	end

	mov = 0;
	attempts = 0;
	while ( 1 )
		mov = mmreader( fullfile( clipDir, clipName ) );
		attempts = attempts + 1;
		if( mov.NumberOfFrames < 50 )
			mov = 0;
		else
			break;
		end

		if( attempts > 10 )
			break;
		end
	end

	c = clock;
	fprintf( logid, '%s (%d out of %d) at %2.2d/%2.2d/%d - %2.2d:%2.2d:%2.2d - %d frames to process. \n', clipName, clipIndex, length( clipList ), c(3), c(2), c(1), c(4), c(5), int32(c(6)), mov.NumberOfFrames );
	
	if( attempts > 10 )
		fprintf( logid, 'ERROR - %s %d frames detected after 10 attempts\n', clipName, mov.NumberOfFrames );
		continue
	end

	fid = fopen( fullfile( outDir, clipName ) , 'w');

	for i = 1 : mov.FrameRate : mov.NumberOfFrames
		thisFrame = read(mov, i);
		fprintf( fid, 'FRAME %d\n', int32(i) );

		% now run object detectors over this frame

		for j = 1:length( modelList )
	
			modelName = modelList(j).name;
			modelPath = fullfile( modelDir, modelName );
			load (modelPath);
			
			% threshold is chosen as -1 from experiments.
			% we need weak detections also along with the score.
			[dets, boxes, info] = imgdetect( thisFrame, model, -0.9 );
			if ( ~isempty(dets) )
				
				% model.class is the detected object class eg. car, aeroplane etc.
				% dets is matrix with 6 columns representing all the detections of
				% objects concering model. One row for one detection.
			        % The last column represents score of detection. 
				
				% The matrix rows are sorted on the score, hence maximum
				% score is obtained from the 1st row, 6th column.

				% det1, det2, det3 are successive refinements over dets.
				% finally, nms does non maximal suppression which clubs detections
				% with overlap over each other of 0.5 or more.
				
				% remember, overlap has to be greater than 0. Actions like Hug, kiss
				% will have two persons getting detected, but the corresponding
				% detections will overlap with each other significantly as the real
				% objects (two persons) will be close to each other.

				% Do not keep overlap more than 0.5 as it causes multiple detections
				% of same object.

				bboxpred = model.bboxpred;
				[ det1 all1 ] = clipboxes( thisFrame, dets, boxes );
				[ det2 all2 ] = bboxpred_get( bboxpred, det1, reduceboxes( model, all1 ) );
				[ det3 all3 ] = clipboxes( thisFrame, det2, all2 );
				I = nms( det3, 0.5 ); % represents number of detections
				det4 = det3( I, : );
				
				% now write result to the output file.

				for k = 1:size( det4, 1 )
					% now print detected object and its score
					fprintf( fid, '%s %f\n', model.class, det4(k, 5) );
				end
			end
		end
	end
	fclose( fid );
end
c = clock;
fprintf( logid, 'Done at %2.2d/%2.2d/%d - %2.2d:%2.2d:%2.2d\n', c(3), c(2), c(1), c(4), c(5), int32(c(6)) );
fclose( logid );
exit;
