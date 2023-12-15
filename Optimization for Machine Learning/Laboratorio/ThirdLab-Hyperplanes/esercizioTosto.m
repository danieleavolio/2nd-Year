clear;

%Load dataset
load impossible.mat;

[numPoints, dim] = size(X);

numVar = dim + 1 + numPoints;
Positive = [];
Negative = [];

for i = 1 :numPoints
    if y(i) == 1
        Positive = [Positive; X(i, :)];
    else
        Negative = [Negative; X(i, :)];
    endif
end

m = size(Positive, 1);

k = size(Negative, 1);

%Vettore c

c = zeros(1,numVar);

for i = 1:numPoints
    if y(i) == 1
        c(dim+1+i) = 1/m;
    else
        c(dim+1+i) = 1/k;
    endif
end

A = zeros(numPoints, numVar);



for i=1:numPoints

    A(i, 1:dim) = X(i, :) * y(i); %per v^tai e vt bl
    A(i, dim+1) = -y(i); %per gamma
    A(i, dim+1+i) = 1; %per Xi e Psi

    %Possimao fare questa cosa
    ctype(i) = "L";
end

b = ones(numPoints, 1);



ub = []; %inf
lb = zeros(numVar, 1); %0
lb(1:dim+1) = -inf; % -inf

vartype = repmat("C", numVar, 1);


sense = 1;

[xopt, fopt, status, extra] = glpk(c, A, b, lb, ub, ctype, vartype, sense);

v = xopt(1:dim);
gamma = xopt(dim+1);

drawDataset(X, numPoints, y);

hold on;

drawHyperplane(X,v,gamma);
waitfor(gcf);



