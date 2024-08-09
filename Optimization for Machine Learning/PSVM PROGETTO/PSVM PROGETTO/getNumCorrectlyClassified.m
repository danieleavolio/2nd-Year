function numCorrectlyClassified = getNumCorrectlyClassified(X, y, vStar, gammaStar)

	numErrors = 0;
	for i = 1: length(y)
		if dot(vStar', X(i,:), 2) >= gammaStar % if the point belongs to A+...la funzione dot poteva essere scritta anche cosi vStar * X(i),è uguale
			  if y(i) == - 1 % ... and it is actually negative...
				numErrors = numErrors + 1; % ...then it's an error.
			endif
		elseif dot(vStar', X(i,:), 2) <= gammaStar % if the point belongs to A-...
			if y(i) == + 1 % ... and it is actually positive...
				numErrors = numErrors + 1; % ...then it's an error.
			endif
		endif
	endfor
	numCorrectlyClassified = size(X)(1) - numErrors;

	%disp(strcat("Second level: numCorrectlyClassified: ", num2str(numCorrectlyClassified)));
	%disp(strcat("Second level: numErrors: ", num2str(numErrors)));
	%disp(strcat("Second level: Size(X)(1): ", num2str(size(X)(1))));

endfunction




