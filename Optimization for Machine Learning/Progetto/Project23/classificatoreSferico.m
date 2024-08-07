clear;
pkg load statistics;

% Funzione kernel gaussiano
function K = K(x, y, sigm)
    K = exp(-norm(x - y)^2 / (2 * sigm^2));
    % printf("K: %f\n", K);
end

%%%%%%HELPER FUNCTIONS%%%%%%
% the average training correctness;
% Punti classificati correttamente nel training set / numero di punti nel training set

function training_correctness = training_correctness(X_train_correct, X_train)
    training_correctness = size(X_train_correct, 1) / size(X_train, 1);
end

% the average training sensitivity;
% Punti POSITIVI classificati correttamente nel training set / numero di punti POSITIVI nel training set

function training_sensitivity = training_sensitivity(X_train_pos_correct, X_train_pos)
    training_sensitivity = size(X_train_pos_correct, 1) / size(X_train_pos, 1);
end

% the average training specificity;
% Punti NEGATIVI classificati correttamente nel training set / numero di punti NEGATIVI nel training set

function training_specificity = training_specificity(X_train_neg_correct, X_train_neg)
    training_specificity = size(X_train_neg_correct, 1) / size(X_train_neg, 1);
end

% Train precision
% Punti POSITIVI classificati correttamente / Punti POSITIVI classificati

function training_precision = training_precision(X_train_pos_correct, X_train_pos_classified)
    training_precision = size(X_train_pos_correct, 1) / size(X_train_pos_classified, 1);
end

% the average training F-score;
%2*sensitivity*precision/(sensitivity+precision)

function training_f_score = training_f_score(training_sensitivity, training_precision)
    training_f_score = 2 * training_sensitivity * training_precision / (training_sensitivity + training_precision);
end

% the average testing correctness (accuracy);
% Punti classificati correttamente nel test set / numero di punti nel test set

function testing_correctness = testing_correctness(X_test_correct, X_test)
    testing_correctness = size(X_test_correct, 1) / size(X_test, 1);
end

% the average testing sensitivity;
% Punti POSITIVI classificati correttamente nel test set / numero di punti POSITIVI nel test set

function testing_sensitivity = testing_sensitivity(X_test_pos_correct, X_test_pos)
    testing_sensitivity = size(X_test_pos_correct, 1) / size(X_test_pos, 1);
end

% the average testing specificity;
% Punti NEGATIVI classificati correttamente nel test set / numero di punti NEGATIVI nel test set

function testing_specificity = testing_specificity(X_test_neg_correct, X_test_neg)
    testing_specificity = size(X_test_neg_correct, 1) / size(X_test_neg, 1);
end

% Test precision
% Punti POSITIVI classificati correttamente / Punti POSITIVI classificati

function testing_precision = testing_precision(X_test_pos_correct, X_test_pos_classified)
    testing_precision = size(X_test_pos_correct, 1) / size(X_test_pos_classified, 1);
end

% the average testing F-score.

function testing_f_score = testing_f_score(testing_sensitivity, testing_precision)
    testing_f_score = 2 * testing_sensitivity * testing_precision / (testing_sensitivity + testing_precision);
end

% Spherical classifier with x0 being the baricenter of the positive points
%... and C = 10

load dataset23.mat;

% Perform a bilevel 10-fold cross validation, using, for the model selection,
% a 5-fold cross validation.

% 10-Fold Cross Validation
% Dividiamo il dataset in 10 parti
% Per ogni parte, usiamo 9 parti per il training e 1 per il test

% Numero di fold
numFold = 10;

possible_C = [0.01; 0.05; 0.1];
sigm = 1;

cv = cvpartition(y, 'KFold', numFold);

% Metriche per il training e il test

average_training_correctness = 0;
average_training_sensitivity = 0;
average_training_specificity = 0;
average_training_precision = 0;
average_training_f_score = 0;

average_testing_correctness = 0;
average_testing_sensitivity = 0;
average_testing_specificity = 0;
average_testing_precision = 0;
average_testing_f_score = 0;

for i = 1:numFold
    printf("Fold %d\n", i);
    trainIdx = training(cv, i);
    testIdx = test(cv, i);

    % printf("Size train: %d\n", sum(trainIdx));
    % printf("Size test: %d\n", sum(testIdx));

    % 5-fold cross validation per la scelta del modello
    cvModel = cvpartition(y(trainIdx), 'KFold', 5);

    t_correctness = 0;

    best_C = possible_C(1);

    for pc = 1:size(possible_C, 1)
        C = possible_C(pc);

        for j = 1:5
            % printf("Fold model %d\n", j);
            trainIdxModel = training(cvModel, j);
            testIdxModel = test(cvModel, j);

            % printf("Size train model: %d\n", sum(trainIdxModel));
            % printf("Size test model: %d\n", sum(testIdxModel));

            % Training set
            X_train = X(trainIdxModel, :);
            y_train = y(trainIdxModel);

            % Test set
            X_test = X(testIdxModel, :);
            y_test = y(testIdxModel);

            % Calcolo del baricentro dei punti positivi
            [numPoints, dim] = size(X_train);
            x0 = zeros(1, dim);
            numPosPoints = sum(y_train == 1);
            posPoints = X_train(y_train == 1, :);
            negPoints = X_train(y_train ~= 1, :);

            if numPosPoints > 0
                x0 = sum(posPoints, 1) / numPosPoints;
            end

            % Baricento del modello i del fold j
            % printf("Baricentro: %f\n", x0);

            % Costruzione del problema di programmazione lineare
            numVar = 1 + numPoints;
            A = zeros(numPoints, numVar);
            b = zeros(numPoints, 1);
            cType = repmat("L", numPoints, 1);

            for k = 1:numPoints
                A(k, 1) = y_train(k);
                A(k, k + 1) = 1;
                b(k) = y_train(k) * (K(X_train(k, :), X_train(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_train(k, :), x0, sigm));
            end

            ub = [];
            lb = zeros(numVar, 1);
            c = ones(numVar, 1);
            c(2:numVar) = c(2:numVar) * C;
            varType = repmat("C", 1, numVar);
            sense = 1;

            [xStar, fStar] = glpk(c, A, b, lb, ub, cType, varType, sense);
            R = sqrt(xStar(1));

            % Calcolo della correttezza
            % Calcoliamo i punti correttamente classificati
            % CORRECTNESS= Correttamente classificati nel test set / numero di punti nel test set
            test_correttamente_classificati = [];
            R_squared = R^2;

            for k = 1:size(X_test, 1)

                if y_test(k) == 1

                    if K(X_test(k, :), X_test(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_test(k, :), x0, sigm) <= R_squared
                        test_correttamente_classificati = [test_correttamente_classificati; X_test(k, :)];
                    endif

                else

                    if K(X_test(k, :), X_test(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_test(k, :), x0, sigm) > R_squared
                        test_correttamente_classificati = [test_correttamente_classificati; X_test(k, :)];
                    endif

                endif

            end

            % Calcoliamo la correttezza
            correctness = testing_correctness(test_correttamente_classificati, X_test);
            printf("Correttezza: %f\n", correctness);

            if correctness > t_correctness
                t_correctness = correctness;
                best_C = C;
            endif

        end

        % Fine 5-fold cross validation
        printf("Fine 5-fold cross validation\n");
        printf("Miglior C: %f\n", best_C);
    end

    % Avendo il migliore C, ora possiamo effettivamente fare il training sul fold i di primo livello
    % Training set
    X_train = X(trainIdx, :);
    y_train = y(trainIdx);

    % Test set
    X_test = X(testIdx, :);
    y_test = y(testIdx);

    % Calcolo del baricentro dei punti positivi
    [numPoints, dim] = size(X_train);
    x0 = zeros(1, dim);
    numPosPoints = sum(y_train == 1);
    posPoints = X_train(y_train == 1, :);
    negPoints = X_train(y_train ~= 1, :);

    if numPosPoints > 0
        x0 = sum(posPoints, 1) / numPosPoints;
    end

    % Baricento del modello i del fold j
    % printf("Baricentro: %f\n", x0);

    % Costruzione del problema di programmazione lineare
    numVar = 1 + numPoints;
    A = zeros(numPoints, numVar);
    b = zeros(numPoints, 1);
    cType = repmat("L", numPoints, 1);

    for k = 1:numPoints
        A(k, 1) = y_train(k);
        A(k, k + 1) = 1;
        b(k) = y_train(k) * (K(X_train(k, :), X_train(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_train(k, :), x0, sigm));
    end

    ub = [];
    lb = zeros(numVar, 1);
    c = ones(numVar, 1);
    c(2:numVar) = c(2:numVar) * best_C;
    varType = repmat("C", 1, numVar);
    sense = 1;

    [xStar, fStar] = glpk(c, A, b, lb, ub, cType, varType, sense);
    R = sqrt(xStar(1));

    % Calcolo della correttezza
    % Calcoliamo i punti correttamente classificati
    % CORRECTNESS= Correttamente classificati nel test set / numero di punti nel test set
    test_correttamente_classificati = [];
    R_squared = R^2;

    TP_TRAIN = [];
    FP_TRAIN = [];
    TN_TRAIN = [];
    FN_TRAIN = [];

    TP_TEST = [];
    FP_TEST = [];
    TN_TEST = [];
    FN_TEST = [];

    for k = 1:size(X_train, 1)

        if y_train(k) == 1

            if K(X_train(k, :), X_train(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_train(k, :), x0, sigm) <= R_squared
                TP_TRAIN = [TP_TRAIN; X_train(k, :)];
            else
                FN_TRAIN = [FN_TRAIN; X_train(k, :)];
            endif

        else

            if K(X_train(k, :), X_train(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_train(k, :), x0, sigm) > R_squared
                TN_TRAIN = [TN_TRAIN; X_train(k, :)];
            else
                FP_TRAIN = [FP_TRAIN; X_train(k, :)];
            endif

        endif

    end

    for k = 1:size(X_test, 1)

        if y_test(k) == 1

            if K(X_test(k, :), X_test(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_test(k, :), x0, sigm) <= R_squared
                TP_TEST = [TP_TEST; X_test(k, :)];
            else
                FN_TEST = [FN_TEST; X_test(k, :)];
            endif

        else

            if K(X_test(k, :), X_test(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_test(k, :), x0, sigm) > R_squared
                TN_TEST = [TN_TEST; X_test(k, :)];
            else
                FP_TEST = [FP_TEST; X_test(k, :)];
            endif

        endif

    end

    fold_training_correctness = training_correctness([TP_TRAIN; TN_TRAIN], X_train);
    fold_training_sensitivity = training_sensitivity(TP_TRAIN, posPoints);
    fold_training_specificity = training_specificity(TN_TRAIN, negPoints);
    fold_training_precision = training_precision(TP_TRAIN, [TP_TRAIN; FP_TRAIN]);
    fold_training_f_score = training_f_score(fold_training_sensitivity, fold_training_precision);

    fold_testing_correctness = testing_correctness([TP_TEST; TN_TEST], X_test);
    fold_testing_sensitivity = testing_sensitivity(TP_TEST, posPoints);
    fold_testing_specificity = testing_specificity(TN_TEST, negPoints);
    fold_testing_precision = testing_precision(TP_TEST, [TP_TEST; FP_TEST]);
    fold_testing_f_score = testing_f_score(fold_testing_sensitivity, fold_testing_precision);

    average_training_correctness = average_training_correctness + fold_training_correctness;
    average_training_sensitivity = average_training_sensitivity + fold_training_sensitivity;
    average_training_specificity = average_training_specificity + fold_training_specificity;
    average_training_precision = average_training_precision + fold_training_precision;
    average_training_f_score = average_training_f_score + fold_training_f_score;

    average_testing_correctness = average_testing_correctness + fold_testing_correctness;
    average_testing_sensitivity = average_testing_sensitivity + fold_testing_sensitivity;
    average_testing_specificity = average_testing_specificity + fold_testing_specificity;
    average_testing_precision = average_testing_precision + fold_testing_precision;
    average_testing_f_score = average_testing_f_score + fold_testing_f_score;

end

average_training_correctness = average_training_correctness / numFold;
average_training_sensitivity = average_training_sensitivity / numFold;
average_training_specificity = average_training_specificity / numFold;
average_training_precision = average_training_precision / numFold;
average_training_f_score = average_training_f_score / numFold;

average_testing_correctness = average_testing_correctness / numFold;
average_testing_sensitivity = average_testing_sensitivity / numFold;
average_testing_specificity = average_testing_specificity / numFold;
average_testing_precision = average_testing_precision / numFold;
average_testing_f_score = average_testing_f_score / numFold;

printf("Average Training Correctness: %f\n", average_training_correctness);
printf("Average Training Sensitivity: %f\n", average_training_sensitivity);
printf("Average Training Specificity: %f\n", average_training_specificity);
printf("Average Training Precision: %f\n", average_training_precision);
printf("Average Training F-Score: %f\n", average_training_f_score);

printf("Average Testing Correctness: %f\n", average_testing_correctness);
printf("Average Testing Sensitivity: %f\n", average_testing_sensitivity);
printf("Average Testing Specificity: %f\n", average_testing_specificity);
printf("Average Testing Precision: %f\n", average_testing_precision);
printf("Average Testing F-Score: %f\n", average_testing_f_score);

% bar plot delle metriche
% Training
figure;
bar([average_training_correctness, average_training_sensitivity, average_training_specificity, average_training_precision, average_training_f_score]);
set(gca, 'xticklabel', {'Correctness', 'Sensitivity', 'Specificity', 'Precision', 'F-Score'});
title('Training Metrics');
xlabel('Metric');
ylabel('Value');

% Testing
figure;
bar([average_testing_correctness, average_testing_sensitivity, average_testing_specificity, average_testing_precision, average_testing_f_score]);
set(gca, 'xticklabel', {'Correctness', 'Sensitivity', 'Specificity', 'Precision', 'F-Score'});
title('Testing Metrics');
xlabel('Metric');
ylabel('Value');

% % C è un hyperparametro che va tunato
% C = 10;

% % C deve essere >= 1/2r, con r = min{|POS|, |NEG|}

% % σ
% sigm = 1;

% % Number of points è il numero di punti nel dataset
% % dim è la dimensione dello spazio
% [numPoints, dim] = size(X);

% % x0 è il baricentro dei punti positivi
% x0 = zeros(1, dim);

% numPosPoints = 0;
% posPoints = [];
% negPoints = [];

% %Scorriamo i punti
% for i = 1:numPoints
%     % se il punto è positivo
%     if y(i) == 1
%         %aggiungiamo il punto al baricentro
%         x0 = x0 + X(i, :);
%         %incrementiamo il numero di punti positivi
%         numPosPoints = numPosPoints + 1;
%         %aggiungiamo il punto ai punti positivi
%         posPoints = [posPoints; X(i, :)];
%     else
%         %altrimenti lo aggiungiamo ai punti negativi
%         negPoints = [negPoints; X(i, :)];
%     endif

% end

% % x0 è ora il baricentro
% x0 = x0 / numPosPoints;

% % printf("Baricentro: %f\n", x0);

% % Risolvi il problema di programmazione lineare
% % Assumiamo che la soluzione sia [Z e_1..e_numPoints]
% % 1 per Z (R^2)

% % numPoints per e_1 .. e_numPoints
% numVar = 1 + numPoints;

% % Costruiamo la matrice A e il vettore b
% A = zeros(numPoints, numVar);

% for i = 1:numPoints

%     % Z ha coefficiente 1 se il punto è positivo, -1 se il punto è negativo
%     A(i, 1) = y(i);

%     % e_i ha coefficiente 1
%     % Dove e_i è il punto i-esimo
%     A(i, i + 1) = 1;

%     % La parte destra è la norma al quadrato con segno - se il punto è negativo
%     % e + se il punto è positivo
%     % b(i) = y(i) * norm(X(i,:)-x0)^2;

%     % Applicazione del kernel gaussian per separazione sferica con kernel

%     % printf("Primo kernel: %f\n", K(X(i, :), X(i, :), sigm));
%     % printf("Secondo kernel: %f\n", K(x0, x0, sigm));
%     % printf("Terzo kernel: %f\n", K(X(i, :), x0, sigm));

%     b(i) = y(i) * (K(X(i, :), X(i, :), sigm) + K(x0, x0, sigm) - 2 * K(X(i, :), x0, sigm));

%     % tipo di vincolo
%     cType(i) = "L";

% end

% % Lower bound e upper bound
% ub = [];

% % tutte le variabili hanno lower bound uguale a zero
% lb = zeros(numVar, 1);

% % Il vettore dei costi è 1 per Z e C per tutte le altre variabili
% c = ones(numVar, 1);
% % Qui stiamo moltiplicando per C tutte le variabili tranne Z, e siccome
% %tutte hanno 1 tranne z, avremo C per tutte (esclusa Z)
% c(2:numVar) = c(2:numVar) * C;

% % Tipo di variabile
% varType = repmat("C", 1, numVar);

% % Senso del problema, cioè massimizzare o minimizzare
% % In questo caso minimizziamo
% sense = 1;

% % Risolviamo il problema di programmazione lineare
% [xStar, fStar] = glpk(c, A, b, lb, ub, cType, varType, sense);

% %%%%% Grafica %%%%%

% xMin = min(X(:, 1));
% xMax = max(X(:, 1));

% yMin = min(X(:, 2));
% yMax = max(X(:, 2));

% axis([xMin xMax yMin yMax]);

% clf;
% hold on;

% % Punti positivi
% scatter(posPoints(:, 1), posPoints(:, 2), 'b', 'o', 'filled');

% % Punti negativi
% scatter(negPoints(:, 1), negPoints(:, 2), 'r', 'o', 'filled');

% % R è il raggio della sfera
% z = xStar(1);
% R = sqrt(z);
% printf("Raggio della sfera: %f\n", R);

% % Disegna la sfera
% % drawSphere(x0, R, 'k');

% % Controlliamo se i punti sono ben classificati
% % Scorriamo i punti positivi

% % Precision=TP/(TP+FP)

% correttiPos = 0;
% correttiNeg = 0;

% for i = 1:numPoints
%     % Se il punto è positivo
%     if y(i) == 1

%         if K(X(i, :), X(i, :), sigm) + K(x0, x0, sigm) - 2 * K(X(i, :), x0, sigm) <= z
%             correttiPos = correttiPos + 1;
%         endif

%     else
%         if K(X(i, :), X(i, :), sigm) + K(x0, x0, sigm) - 2 * K(X(i, :), x0, sigm) > z
%             correttiNeg = correttiNeg + 1;
%         endif

%     endif

% end

% printf("Punti pos corr: %d\n", correttiPos);
% printf("Punti neg corr: %d\n", correttiNeg);
