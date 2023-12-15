clear;

load toy.mat;

#Calcolare la size dei punti

[numPoints, dim] = size(X);

Plus = [];
Minus = [];

for i = 1: numPoints
    if y(i) == 1
        Plus = [Plus; X(i, :)];
    else
        Minus = [Minus; X(i, :)];
    endif
end

#Sto il minimo di tutte le righe per la prima colonna
xMin = min(X(:, 1));
#Sto il massimo di tutte le righe per la prima colonna
xMax = max(X(:, 1));

#Lo faccio anche per la Y
yMin = min(X(:, 2));
yMax = max(X(:, 2));

axis([xMin, xMax, yMin, yMax]);
hold on;


scatter(Plus(:, 1), Plus(:, 2), 'r', 'o', 'filled');
scatter(Minus(:, 1), Minus(:, 2), 'b', 'x', 'filled');

