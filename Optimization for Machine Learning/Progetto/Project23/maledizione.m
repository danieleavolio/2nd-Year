clear;
% Spherical classifier with Gaussian kernel
load dataset23.mat;

% Hyperparameters
C = 10;
sigma = 1;

[numPoints, dim] = size(X);

% Calculate x0 (barycenter of positive points)
x0 = mean(X(y == 1, :), 1);

% Kernel function
K = @(x1, x2) exp(-sum((x1 - x2).^2, 2) / (2 * sigma));

% Set up the linear programming problem
numVar = 1 + numPoints;  % z and slack variables
c = [1; C * ones(numPoints, 1)];  % Objective function coefficients

% Construct constraint matrix A and vector b
A = zeros(numPoints, numVar);
b = zeros(numPoints, 1);

for i = 1:numPoints
    A(i, 1) = -y(i);  % -y_i * z
    A(i, i+1) = 1;    % slack variable
    b(i) = y(i) * (K(X(i,:), X(i,:)) + K(x0, x0) - 2*K(X(i,:), x0));
end

% Set up bounds and types
lb = zeros(numVar, 1);
ub = [];
ctype = repmat('L', numPoints, 1);
vartype = repmat('C', numVar, 1);

% Solve the linear programming problem
[xStar, fStar] = glpk(c, A, b, lb, ub, ctype, vartype, 1);

% Extract results
z = xStar(1);
slack = xStar(2:end);

% Plotting
figure;
hold on;
scatter(X(y==1,1), X(y==1,2), 'b', 'filled');
scatter(X(y==-1,1), X(y==-1,2), 'r', 'filled');
scatter(x0(1), x0(2), 100, 'k', 'filled');

% Plot decision boundary
[X1, X2] = meshgrid(linspace(min(X(:,1)), max(X(:,1)), 100), ...
                    linspace(min(X(:,2)), max(X(:,2)), 100));
XX = [X1(:) X2(:)];
decision = arrayfun(@(i) K(XX(i,:), x0) - z/2, 1:size(XX,1));
decision = reshape(decision, size(X1));
contour(X1, X2, decision, [0 0], 'k', 'LineWidth', 2);

xlabel('Feature 1');
ylabel('Feature 2');
title('Spherical Separation with Gaussian Kernel');
legend('Positive', 'Negative', 'Center (x0)', 'Decision Boundary');
hold off;
