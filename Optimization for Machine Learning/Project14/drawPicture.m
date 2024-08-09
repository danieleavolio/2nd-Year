
%v, gamma = used to calculate the hyperplanes
%dataset= the entire dataset
function drawPicture(bags1,bags2,bags3,bags4,bags5,bags6,bags7,v, gamma, dataset, labelTitle)

%Print and file options
filename = strcat(labelTitle,".jpg");
fid = fopen (filename, "w");
set (0, "defaultaxesfontname", "Helvetica")

xxMin = min(dataset(:,1));
xxMax = max(dataset(:,1));

yyMin = min(dataset(:,2));
yyMax = max(dataset(:,2));

zzMin = min(dataset(:,3));
zzMax = max(dataset(:,3));

clf;

%If the setA has almost one point, draw them
if bags1 > 0
  scatter3(bags1(:,1), bags1(:,2), bags1(:,3), 'r','o','filled');
endif
hold on
if bags2 > 0
  scatter3(bags2(:,1), bags2(:,2), bags2(:,3), 'g','o','filled');
endif
if bags3 > 0
  scatter3(bags3(:,1), bags3(:,2), bags3(:,3), 'b','o','filled');
endif
if bags4 > 0
  scatter3(bags4(:,1), bags4(:,2), bags4(:,3), 'y','o','filled');
endif
if bags5 > 0
  scatter3(bags5(:,1), bags5(:,2), bags5(:,3), 'c');
endif
if bags6 > 0
  scatter3(bags6(:,1), bags6(:,2), bags6(:,3), 'm');
endif
if bags7 > 0
  scatter3(bags7(:,1), bags7(:,2), bags7(:,3), 'k');
endif



%Surfaces (hyperplanes) drawing

[X,Y] = meshgrid(xxMin:0.1:xxMax, yyMin:0.5:yyMax);

%Hyperplan H
Z = (gamma - v(1)*X - v(2)*Y) / v(3);
surf(X,Y,Z);



%Print options
title (labelTitle);
print (filename, "-djpg");

fclose (fid);
clear;

endfunction
