% Drawing in 3D
clear;

load toy3D.mat;

[numPoints,dim] = size(X);

posPoints = [];
negPoints = [];

for i=1: numPoints

  if y(i) == 1
    posPoints = [posPoints; X(i,:)];
  else
    negPoints = [negPoints; X(i,:)];
  endif

end

% drawing the points

xxMin = min(X(:,1));
xxMax = max(X(:,1));

yyMin = min(X(:,2));
yyMax = max(X(:,2));

zzMin = min(X(:,3));
zzMax = max(X(:,3));


% measure gives the size of the points
% the default is 8
hold on;

measure = 100;

scatter3(posPoints(:,1),posPoints(:,2),...
posPoints(:,3),measure,'b','filled');

scatter3(negPoints(:,1),negPoints(:,2),...
negPoints(:,3),'r','filled');

% drawing the hyperplane 2x+6y-4z = 5;

[X,Y] = meshgrid(xxMin:0.1:xxMax,...
yyMin:0.5:yyMax);

Z = (5-2*X-6*Y)/(-4);
surf(X,Y,Z);


% generating a sphere with 20 faces,
% centered in 0 and with radius 1

[x y z] = sphere;
surf(x,y,z);
axis equal;

% drawing the sphere S(x0,R) = S([1 3 1], 10)
x0 = [1 3 1];
R = 10;

[x y z] = sphere(30);
h = surf(x0(1) + R*x, x0(2) + R*y, x0(3) + R*z);

% Facealpha gives the degree of opacity
% setting 0.0 means transparent
set(h,'Facealpha',0.5);










