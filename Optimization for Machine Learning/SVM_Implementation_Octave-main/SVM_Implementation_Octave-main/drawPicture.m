%This function draws the points and the hyperplanes H, H+ and H-
%SetA = set of points labelled with +1
%SetB = set of points labelled with -1
%v, gamma = used to calculate the hyperplanes
%dataset= the entire dataset
function drawPicture(setA, setB, v, gamma, dataset, labelTitle)

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
if setA > 0
  scatter3(setA(:,1), setA(:,2), setA(:,3), 'b','o','filled');
endif

hold on;

%If the setB has almost one point, draw them
if setB > 0
  scatter3(setB(:,1), setB(:,2), setB(:,3), 'r','o','filled');
endif

%Surfaces (hyperplanes) drawing

[X,Y] = meshgrid(xxMin:0.1:xxMax, yyMin:0.5:yyMax);

%Hyperplan H
Z = (gamma - v(1)*X - v(2)*Y) / v(3);
surf(X,Y,Z);

%Hyperplan H+
hold on;
Z = (gamma + 1 - v(1)*X - v(2)*Y) / v(3);
surf(X,Y,Z);

%Hyperplan H-
hold on;
Z = (gamma - 1 - v(1)*X - v(2)*Y) / v(3);
surf(X,Y,Z);

%Print options
title (labelTitle);
print (filename, "-djpg");

fclose (fid);
  
endfunction