%Given a set, the SVM calculates v and gamma in order to give us an hyperplane
%X = Dataset
%y = Class labels
%C = Weight
%draw (0,1) = If we want to draw the hyperplanes
function [vSegnato, gammaSegnato,accuracy] = SVM(X, y, C, draw)

x0 = []; %Starting point

[numPoints, numCol] = size(X);
numVar = numCol + 1 + numPoints; %n + 1 + m + k


A_in = zeros(numPoints, numVar); %Constraint matrix

A = []; %Empty because no equality constraint
bag1=[];
bag2=[];
bag3=[];
bag4=[];
bag5=[];
bag6=[];
bag7=[];
for i=1:numPoints
  if(i>=1 & i<=8)
    bag1=[bag1;X(i,:)];
   endif
   if(i>=9 & i<=16)
    bag2=[bag2;X(i,:)];
   endif
   if(i>=17 & i<=24)
    bag3=[bag3;X(i,:)];
   endif
   if(i>=25 & i<=32)
    bag4=[bag4;X(i,:)];
   endif
   if(i>=33 & i<=40)
    bag5=[bag5;X(i,:)];
   endif
   if(i>=41 & i<=48)
    bag6=[bag6;X(i,:)];
   endif
   if(i>=49 & i<=56)
    bag7=[bag7;X(i,:)];
   endif

  %Filling the constraint matrix

  A_in(i,1:numCol) = y(i) * X(i,:); %v
  A_in(i,numCol+1) = -y(i); %gamma
  A_in(i,numCol+1+i) = 1; %phi

endfor

%Right side of the constraint matrix
A_ub = [];
A_lb = ones(numPoints, 1);

b=[]; %Empty because no equality constraint

%Upper bounds
ub = [];

%Lower bounds
lb = zeros(numVar, 1);
lb(1:numCol + 1) = -inf;

%Objective function
H = zeros(numVar, numVar);
for i = 1: 4
		H(i, i) = 1;
endfor

q = [zeros(numCol + 1,1); C * ones(numPoints,1)]; %Vector of the costs

[xStar, fStar] = qp (x0, H, q, A, b, lb, ub, A_lb, A_in, A_ub);

vSegnato = xStar(1:numCol);
gammaSegnato = xStar(numCol+1);
if draw == 1
  drawPicture(bag1,bag2,bag3,bag4,bag5,bag6,bag7,vSegnato,gammaSegnato,X, "Grafico");
end
accuracy=calculateCorrectness(X,y,vSegnato,gammaSegnato);




endfunction
