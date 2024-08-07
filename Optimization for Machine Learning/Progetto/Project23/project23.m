% ### Master’s Degree in Artificial Intelligence and Computer Science
% **OPTIMIZATION FOR MACHINE LEARNING - 6 CFU**

% #### Project N. 23

% Given the dataset `dataset3.mat`, where `X` is the matrix whose rows contain the points to be classified and `y` is the array of the corresponding class labels, perform a spherical separation aimed at separating the set A from the set B, based on the following guidelines:

% 1. **Kernel Choice**
%    - Use the Gaussian kernel with σ = 1.

% 2. **Set Selection**
%    - Choose the sets A and B at your convenience, between the two sets of points (positive with label +1 and negative with label -1).

% 3. **Cross-Validation**
%    - Perform a bilevel 10-fold cross validation.
%    - For model selection, use a 5-fold cross validation.
%    - In fixing the grid of C, consider only the cases such that:
%      \[
%      C \geq \frac{1}{2r}
%      \]
%      where \( r = \min\{m, k\} \), with \( m = |A| \) and \( k = |B| \).

% 4. **Sphere Center**
%    - Each time, fix the center \( x_0 \) of the separating sphere \( S(x_0, R) \):
%      \[
%      S(x_0, R) \triangleq \{ x \in \mathbb{R}^n \mid \|x - x_0\|^2 = R^2 \}
%      \]
%      as the barycenter of the set A.

% 5. **Performance Indexes**
%    - Compute the following performance indexes:
%      - Average training correctness
%      - Average training sensitivity
%      - Average training specificity
%      - Average training F-score
%      - Average testing correctness (accuracy)
%      - Average testing sensitivity
%      - Average testing specificity
%      - Average testing F-score



clear;

% Load data
load dataset23.mat

% Print the data
X;

% Definizione della funzione kernel gaussiana
% Questa funzione calcola la distanza euclidea quadrata tra due insiemi di punti
% e applica la funzione kernel gaussiana
function K = gaussianKernel(X1, X2, sigma)
    % Calculate the squared Euclidean distance
    dist2 = pdist2(X1, X2, 'euclidean').^2;
    % Apply the Gaussian kernel function
    K = exp(-dist2 / (2 * sigma^2));
end





% Formula separazione sferica

%min {z, \Xsi, \Psi} z + C \sum_{i=1}^m \Xsi_i + C \sum_{l=1}^k \Psi_l

%                    Xsi_i >= ||a_i - x_0||^2 - z, i = 1, ..., m
%                    Psi_i >= z - ||b_l - x_0||^2, i = 1, ..., m
%                    Xsi_i >= 0, i = 1, ..., m
%                    Psi_i >= 0, i = 1, ..., k
%                    z >= 0


[numPoints, dim] = size(X);

numVar = 1 + numPoints;
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

% Plot points

hold on;

scatter(Positive(:, 1), Positive(:, 2), 'r', 'filled');
scatter(Negative(:, 1), Negative(:, 2), 'b', 'filled');

waitforbuttonpress();


% Troviamo la media per il baricentro sui positivi
hold on;

% calculate using mean function
mediaX = mean(Positive(:, 1));
mediaY = mean(Positive(:, 2));

% Plot baricentro
scatter(mediaX, mediaY, 'g', 'filled');

waitforbuttonpress();

%Salviamo le medie in un vettore X0
x0 = [mediaX mediaY];


%Trasformiamo la formula in forma normale

%min {z, \Xsi, \Psi} z + C \sum_{i=1}^m \Xsi_i + C \sum_{l=1}^k \Psi_l

%                    Xsi_i  + z >= + ||a_i - x_0||^2, i = 1, ..., m
%                    Psi_i  - z >= - ||b_l - x_0||^2, i = 1, ..., k
%                    Xsi_i >= 0, i = 1, ..., m
%                    Psi_i >= 0, i = 1, ..., k
%                    z >= 0

%Definiamo il vettore c
c = ones(numVar, 1);

% Cambiamo il valore di c dopo 1  a fine
for i=2:numVar
    c(i) = 1;
end

%Definiamo la matrice A

A = zeros(numPoints, numVar);

for i=1:numPoints
    A(i, 1+i) = 1; %Per Xsi e Psi
    A(i, 1) = y(i);
end

%Definiamo il vettore b
b = zeros(numPoints, 1);

% Assegnazione dei valori
for i=1:numPoints
    b(i) = norm(X(i, :) - x0)^2 * y(i);
end

%Definiamo il vettore lb
lb = zeros(numVar, 1);

%Definiamo il vettore ub
ub = [];

%Definiamo il vettore ctype
ctype = repmat("L", 1, numPoints);

%Definiamo il vettore vartype
vartype = repmat("C", 1, numVar);

%Definiamo il vettore sense
sense = 1;

%Risoluzione del problema
[xopt, fopt, status, extra] = glpk(c, A, b, lb, ub, ctype, vartype, sense);

% Stampiamo una circonferenza partendo dal baricentro e con
%raggio pari
drawCircumference(x0, sqrt(fopt),'k');
waitforbuttonpress();
