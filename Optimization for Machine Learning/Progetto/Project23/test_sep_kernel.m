
% CODICE DI PROVA SENZA KFOLD%
clear;
pkg load statistics;



load dataset23.mat;

% C è un hyperparametro che va tunato
C = 0.5;

% C deve essere >= 1/2r, con r = min{|POS|, |NEG|}

% σ
sigm = 1;

% Scaliamo i punti di 1/10
for i = 1:size(X, 1)
    X(i, :) = X(i, :) / 5;
end

% Number of points è il numero di punti nel dataset
% dim è la dimensione dello spazio
[numPoints, dim] = size(X);

% x0 è il baricentro dei pòunti positivi
x0 = zeros(1, dim);

numPosPoints = 0;
posPoints = [];
negPoints = [];

%Scorriamo i punti
for i = 1:numPoints
    % se il punto è positivo
    if y(i) == 1
        %aggiungiamo il punto al baricentro
        x0 = x0 + X(i, :);
        %incrementiamo il numero di punti positivi
        numPosPoints = numPosPoints + 1;
        %aggiungiamo il punto ai punti positivi
        posPoints = [posPoints; X(i, :)];
    else
        %altrimenti lo aggiungiamo ai punti negativi
        negPoints = [negPoints; X(i, :)];
    endif

end

% x0 è ora il baricentro
x0 = x0 / numPosPoints;

% printf("Baricentro: %f\n", x0);

% Risolvi il problema di programmazione lineare
% Assumiamo che la soluzione sia [Z e_1..e_numPoints]
% 1 per Z (R^2)

% numPoints per e_1 .. e_numPoints
numVar = 1 + numPoints;

% Costruiamo la matrice A e il vettore b
A = zeros(numPoints, numVar);

for i = 1:numPoints

    % Z ha coefficiente 1 se il punto è positivo, -1 se il punto è negativo
    A(i, 1) = y(i);

    A(i, i + 1) = 1;

    % La parte destra è la norma al quadrato con segno - se il punto è negativo
    % e + se il punto è positivo
    % b(i) = y(i) * norm(X(i,:)-x0)^2;

    % Applicazione del kernel gaussian per separazione sferica con kernel
    b(i) = y(i) * (K(X(i, :), X(i, :), sigm) + K(x0, x0, sigm) - 2 * K(X(i, :), x0, sigm));

    % tipo di vincolo
    cType(i) = "L";

end

% Lower bound e upper bound
ub = [];

% tutte le variabili hanno lower bound uguale a zero
lb = zeros(numVar, 1);

% Il vettore dei costi è 1 per Z e C per tutte le altre variabili
c = ones(numVar, 1);

% Qui stiamo moltiplicando per C tutte le variabili tranne Z, e siccome
%tutte hanno 1 tranne z, avremo C per tutte (esclusa Z)
c(2:numVar) = c(2:numVar) * C;

% Tipo di variabile
varType = repmat("C", 1, numVar);

% Senso del problema, cioè massimizzare o minimizzare
% In questo caso minimizziamo
sense = 1;

% Risolviamo il problema di programmazione lineare
[xStar, fStar] = glpk(c, A, b, lb, ub, cType, varType, sense);

%%%%% Grafica %%%%%

xMin = min(X(:, 1));
xMax = max(X(:, 1));

yMin = min(X(:, 2));
yMax = max(X(:, 2));

% axis([xMin xMax yMin yMax]);

% clf;
% hold on;

% % % Punti positivi
% % scatter(posPoints(:, 1), posPoints(:, 2), 'b', 'o', 'filled');

% % % Punti negativi
% % scatter(negPoints(:, 1), negPoints(:, 2), 'r', 'o', 'filled');

% R è il raggio della sfera
z = xStar(1);
R = sqrt(z);
printf("Raggio della sfera: %f\n", R);

% Disegna la sfera
% drawSphere(x0, R, 'k');

% Controlliamo se i punti sono ben classificati
% Scorriamo i punti positivi

% Precision=TP/(TP+FP)
TP = 0;
TN = 0;
FP = 0;
FN = 0;

printf("Raggio^2 = %d\n", R^2);

for i = 1:numPoints

    printf("Punto con kernel: %f\n", K(X(i, :), X(i, :), sigm) + K(x0, x0, sigm) - 2 * K(X(i, :), x0, sigm));
    % Se il punto è positivo
    if y(i) == 1
        if (K(X(i, :), X(i, :), sigm) + K(x0, x0, sigm) - 2 * K(X(i, :), x0, sigm) <= R^2)
            TP = TP+1;
        else
            FN = FN+1;
        endif
    else
        if (K(X(i, :), X(i, :), sigm) + K(x0, x0, sigm) - 2 * K(X(i, :), x0, sigm) > R^2)
            TN = TN+1;
        else
            FP = FP +1;
        endif
    endif
end

printf("True Positives: %d \n", TP);
printf("True Negatives: %d \n", TN);
printf("False Positives: %d \n", FP);
printf("False Negatives: %d \n", FN);


corr = metrics.correctness(TP+TN, numPoints);

sens = metrics.sensitivity(TP, size(posPoints, 1));

spec = metrics.specificity(TN, size(negPoints, 1));

prec = metrics.precision(TP, TP+FP);

f = metrics.f_score(sens, prec);

printf("Correctness: %f\n", corr);
printf("Sensitivity: %f\n", sens);
printf("Specificity: %f\n", spec);
printf("Precision: %f\n", prec);
printf("F-score: %f\n", f);

plotDataset(posPoints, negPoints, x0, "Test dataset");
metrics = [corr, sens, spec, prec, f];
labels = ["Correctness", "Sensitivity", "Specificity", "Precision", "F-score"];

plotMetrics(metrics, labels, "Metrics");
