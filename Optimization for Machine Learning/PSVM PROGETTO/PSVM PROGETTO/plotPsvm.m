function plotPsvm(XFirstLevelTrain, yFirstLevelTrain, vStar, gammaStar, foldIndex)


  %clf lo utilizziamo per svuotare il foglio
	clf;
	% Plotting the points of the sets,hold on serve per non svuotare il foglio e continuare a disegnare
	hold on;
	[numRows, numColumns] = size(XFirstLevelTrain);
	positiveSet = [];
	negativeSet = [];

  
  
	for i = 1: numRows
	   if yFirstLevelTrain(i) == 1
		 positiveSet = [positiveSet; XFirstLevelTrain(i,:)];%Usando il punto e virgola in positiveSet; vul dire che crei una nuova riga,infatti mettiamo X(i,:),quindi la riga
	   else
		 negativeSet = [negativeSet; XFirstLevelTrain(i,:)];
	   endif
	end

  
  
	xxMin = min(XFirstLevelTrain(:,1));
	xxMax = max(XFirstLevelTrain(:,1));
	yyMin = min(XFirstLevelTrain(:,2));
	yyMax = max(XFirstLevelTrain(:,2));
	axis([xxMin xxMax yyMin yyMax]);
  %Comando utilizzato per disegnare i punti,gli passiamo coordinate di X,cordinate di Y,colore,forma dl punto,e colorato dentro filled
	scatter(positiveSet(:,1), positiveSet(:,2), 'b', 'o', 'filled');
	scatter(negativeSet(:,1), negativeSet(:,2), 'r', 'o', 'filled');

	% Plotting H
	abscissa = -10:1:110;
  %abscissa=xxMin:0.1:xxMax;
  %Perche l'iperpiano è V1X1+V2X2=gamma,Quindi l'ordinata X2=Gamma-V1X1/V2
	ordinate = (gammaStar - vStar(1)* abscissa)/vStar(2);
	plot(abscissa, ordinate, 'k');
	
	% Plotting H+;
	gammaPlus = gammaStar + 1;
	ordinate = (gammaPlus - vStar(1)* abscissa)/vStar(2);
	plot(abscissa, ordinate, 'b');
	
	% Plotting H-;
	gammaMinus = gammaStar - 1;
	ordinate = (gammaMinus - vStar(1)* abscissa)/vStar(2);
	plot(abscissa, ordinate, 'r');
	
	legend('H+','H-','H','Location','northwestoutside');
	
	fig = strcat("fold_", num2str(foldIndex));
	print("-djpg", fig);
	
endfunction