clear;

% Spherical classifier with x0 being the baricenter of the positive points
... and C = 10

load toy2.mat;

% fix C
C = 10;

[numPoints, dim] = size(X);

numPoints
dim

% Start computing x0
x0 = zeros(1,dim);

numPosPoints = 0;

posPoints = [];
negPoints = [];

for i= 1: numPoints

  if y(i) == 1
    x0 = x0 + X(i,:);
    numPosPoints = numPosPoints + 1;
    posPoints = [posPoints; X(i,:)];
  else
    negPoints = [negPoints; X(i,:)];
  endif

end

% x0 is now the baricenter
x0 = x0 / numPosPoints;

% solving the linear program


% assume sol = [Z e_1..e_numPoints]

% 1 for Z (R^2)
% numPoints for e_1 .. e_numPoints
numVar = 1 + numPoints;


A = zeros(numPoints, numVar);

for i=1:numPoints

  A(i,1) = y(i); %Z has coefficient 1 if the point is positive, -1 if the point is negative

  A(i,i+1) = 1; % e_1..e_numPoints has coefficient 1

  %the right hand side is the squared norm with sign - if the point is negative
  ... + if the point is positive
  b(i) = y(i) * norm(X(i,:)-x0)^2;


  % type of constraint
  cType(i) = "L";

end

% lower bound and upper bound on the variables
ub = [];

% all the variables have lower bound equal to zero
lb = zeros(numVar,1);

% the cost vector is 1 for Z and C for all the other variables
c = ones(numVar,1);
c(2:numVar) = c(2:numVar) * C;

% type of variable
varType = repmat("C",1,numVar);

sense = 1;

[xStar,fStar] = ...
glpk(c, A, b, lb, ub, cType, varType, sense);

%%%%% DRAWING %%%%%

xMin = min(X(:,1));
xMax = max(X(:,1));

yMin = min(X(:,2));
yMax = max(X(:,2));

axis([xMin xMax yMin yMax]);

clf;
hold on;

% drawing the positive points
scatter(posPoints(:,1),posPoints(:,2),'b','o','filled');

% drawing the negative points
scatter(negPoints(:,1),negPoints(:,2),'r','o','filled');

% R is the radius of the sphere
z = xStar(1);
R = sqrt(z);

% drawing points and sphere
drawSphere(x0, R, 'k');







