clear;
pkg load statistics;

load dataset23.mat;

% check if output folder
if ~exist('output', 'dir')
    mkdir('output');
end

% Numero di fold per la cross validation
numFold = 10;

%C deve essere >= 1/2r
%con r = min{|POS|, |NEG|}
possible_C = [0.01; 0.05; 0.1; 0.5; 0.9; 1; 2; 5; 10];
sigm = 5;

% Array per C, correttezza, fold durante i second level fold
c_values = [];

%array per tenere conto di fold,c,correttezza, sensibilità, specificità, precisione e f-score
final_values = [];

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

%Ci scorriamo ogni fold
for i = 1:numFold
    printf("Fold %d\n", i);
    trainIdx = training(cv, i);
    testIdx = test(cv, i);

    % printf("Size train: %d\n", sum(trainIdx));
    % printf("Size test: %d\n", sum(testIdx));

    % 5-fold cross validation per la scelta del modello
    cvModel = cvpartition(y(trainIdx), 'KFold', 5);

    t_correctness = 0;

    %Settiamo una c iniziale giusto per iniziare
    best_C = possible_C(1);

    for pc = 1:size(possible_C, 1)
        C = possible_C(pc);

        %Per ogni C, calcoliamo la correttezza per ogni fold del train di 1o livello
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

            % IMPOSTAZIONE DEL PROBLEMA PER SEPARAZIONE SFERICA

            % Calcolo del baricentro dei punti positivi
            [numPointsTrain, dim] = size(X_train);
            x0 = zeros(1, dim);
            numPosPoints = sum(y_train == 1);
            posPoints = X_train(y_train == 1, :);
            negPoints = X_train(y_train ~= 1, :);

            if numPosPoints > 0
                x0 = sum(posPoints, 1) / numPosPoints;
            end

            % Costruzione del problema di programmazione lineare
            numVar = 1 + numPointsTrain;
            A = zeros(numPointsTrain, numVar);
            b = zeros(numPointsTrain, 1);
            cType = repmat("L", numPointsTrain, 1);

            for k = 1:numPointsTrain
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
            test_correttamente_classificati = 0;
            R_squared = R^2;

            for k = 1:size(X_test, 1)

                % Se è un punto POSITIVO
                if y_test(k) == 1
                    % Dalla teoria ||x-x0||^2 = ||x^t*x|| + ||x0^t*x0|| - 2*x^t*x0
                    % Applicando il kernel gaussiano
                    % K(x, x) + K(x0, x0) - 2*K(x, x0)

                    % Se questo valore è minore o uguale a R^2, il punto è correttamente classificato
                    if K(X_test(k, :), X_test(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_test(k, :), x0, sigm) <= R_squared
                        test_correttamente_classificati = test_correttamente_classificati + 1;
                    endif
                else
                    if K(X_test(k, :), X_test(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_test(k, :), x0, sigm) > R_squared
                        test_correttamente_classificati = test_correttamente_classificati + 1;
                    endif
                endif
            end

            % Notare che ci interessano solamente i TP e i TN perché la correctnes è TP+TN/Punti nel test set
            % Calcoliamo la correttezza
            f_correctness = metrics.correctness(test_correttamente_classificati, size(X_test, 1));
            % Aggiungiamo la correttezza di c e la correctness per il fold j del fold i
            c_values = [c_values; C, f_correctness, j, i];

            printf("Correttezza: %f\n", f_correctness);

            % Se la correttezza è maggiore della correttezza attuale, aggiorniamo il valore
            % E prendiamo questo valore di C come migliore valore del parametro
            if f_correctness > t_correctness
                t_correctness = f_correctness;
                best_C = C;
            endif

        end

        % Fine 5-fold cross validation
        printf("Fine 5-fold cross validation\n");
        printf("Miglior C: %f\n", best_C);
    end

    printf("C migliore per il training: %f\n", best_C);

    % Avendo il migliore C, ora possiamo effettivamente fare il training sul fold i di primo livello
    % Training set
    X_train = X(trainIdx, :);
    y_train = y(trainIdx);

    % Test set
    X_test = X(testIdx, :);
    y_test = y(testIdx);

    [numPoints, dim] = size(X_train);
    x0 = zeros(1, dim);
    numPosPoints = sum(y_train == 1);
    posPoints = X_train(y_train == 1, :);
    negPoints = X_train(y_train ~= 1, :);

    if numPosPoints > 0
        x0 = sum(posPoints, 1) / numPosPoints;
    end

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
    R_squared = R^2;

    TP_TRAIN = 0;
    FP_TRAIN = 0;
    TN_TRAIN = 0;
    FN_TRAIN = 0;

    TP_TEST = 0;
    FP_TEST = 0;
    TN_TEST = 0;
    FN_TEST = 0;

    % Grave errore commesso prima, non ho calcolato i punti positivi e negativi per il test set
    % Questo portava sensitività e specificità ad avere valori bassissimi
    posPointsTest = X_test(y_test == 1, :);
    negPointsTest = X_test(y_test ~= 1, :);

    % Qui siccome abbiamo bisogno di tutte le metriche, calcoliamo effettivametne TN, FN, TP, FP per il training e il test set
    for k = 1:size(X_train, 1)

        if y_train(k) == 1

            if K(X_train(k, :), X_train(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_train(k, :), x0, sigm) <= R_squared
                TP_TRAIN = TP_TRAIN + 1;
            else
                FN_TRAIN = FN_TRAIN + 1;
            endif

        else

            if K(X_train(k, :), X_train(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_train(k, :), x0, sigm) > R_squared
                TN_TRAIN = TN_TRAIN + 1;
            else
                FP_TRAIN = FP_TRAIN + 1;
            endif

        endif

    end

    for k = 1:size(X_test, 1)

        if y_test(k) == 1

            if K(X_test(k, :), X_test(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_test(k, :), x0, sigm) <= R_squared
                TP_TEST = TP_TEST + 1;
            else
                FN_TEST = FN_TEST + 1;
            endif

        else

            if K(X_test(k, :), X_test(k, :), sigm) + K(x0, x0, sigm) - 2 * K(X_test(k, :), x0, sigm) > R_squared
                TN_TEST = TN_TEST + 1;
            else
                FP_TEST = FP_TEST + 1;
            endif

        endif

    end


    printf("TP testing: %d\n", TP_TEST);
    printf("TN testing: %d\n", TN_TEST);
    printf("FP testing: %d\n", FP_TEST);
    printf("FN testing: %d\n", FN_TEST);

    fold_training_correctness = metrics.correctness(TP_TRAIN + TN_TRAIN, size(X_train, 1));
    fold_training_sensitivity = metrics.sensitivity(TP_TRAIN, size(posPoints, 1));
    fold_training_specificity = metrics.specificity(TN_TRAIN, size(negPoints, 1));
    fold_training_precision = metrics.precision(TP_TRAIN, TP_TRAIN + FP_TRAIN);
    fold_training_f_score = metrics.f_score(fold_training_sensitivity, fold_training_precision);

    fold_testing_correctness = metrics.correctness(TP_TEST + TN_TEST, size(X_test, 1));
    fold_testing_sensitivity = metrics.sensitivity(TP_TEST, size(posPointsTest, 1));
    fold_testing_specificity = metrics.specificity(TN_TEST, size(negPointsTest, 1));
    fold_testing_precision = metrics.precision(TP_TEST, TP_TEST + FP_TEST);
    fold_testing_f_score = metrics.f_score(fold_testing_sensitivity, fold_testing_precision);

    final_values = [final_values; i, best_C, fold_training_correctness, fold_training_sensitivity, fold_training_specificity, fold_training_precision, fold_training_f_score, fold_testing_correctness, fold_testing_sensitivity, fold_testing_specificity, fold_testing_precision, fold_testing_f_score];

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

    
    % Test per controllare che il fold funzioni e generi effettivamente punti diversi
    % plotDataset(posPointsTest, negPointsTest, x0, "Test dataset");

end

% Salviamo i valori di C_values in un csv che ha come header
% C, Correttezza, Fold

headers_1 = 'C,Correttezza,Fold\n';

output_path = fullfile('output', 'c_values.csv');
writeCsv(headers_1, c_values, output_path);

headers_2 = 'Fold,C,Training Correctness,Training Sensitivity,Training Specificity,Training Precision,Training F-Score,Testing Correctness,Testing Sensitivity,Testing Specificity,Testing Precision,Testing F-Score\n';

output_path = fullfile('output', 'final_values.csv');
writeCsv(headers_2, final_values, output_path);


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

% Crea un csv che contiene i valori medi delle metriche
average_metrics = [average_training_correctness, average_training_sensitivity, average_training_specificity, average_training_precision, average_training_f_score, average_testing_correctness, average_testing_sensitivity, average_testing_specificity, average_testing_precision, average_testing_f_score];
headers_3 = 'Average Training Correctness,Average Training Sensitivity,Average Training Specificity,Average Training Precision,Average Training F-Score,Average Testing Correctness,Average Testing Sensitivity,Average Testing Specificity,Average Testing Precision,Average Testing F-Score\n';

output_path = fullfile('output', 'average_metrics.csv');
writeCsv(headers_3, average_metrics, output_path);


% PLOTTING
labels = {'Correctness', 'Sensitivity', 'Specificity', 'Precision', 'F-Score'};
metrics = [average_training_correctness, average_training_sensitivity, average_training_specificity, average_training_precision, average_training_f_score];

plotMetrics(metrics, labels, 'Training Metrics', "#D62828");

metrics = [average_testing_correctness, average_testing_sensitivity, average_testing_specificity, average_testing_precision, average_testing_f_score];
plotMetrics(metrics, labels, 'Testing Metrics', "#003049");

labels = {'Correctness', 'Sensitivity', 'Specificity', 'Precision', 'F-Score'};
metrics_training = [average_training_correctness, average_training_sensitivity, average_training_specificity, average_training_precision, average_training_f_score];
metrics_testing = [average_testing_correctness, average_testing_sensitivity, average_testing_specificity, average_testing_precision, average_testing_f_score];

metrics_combined = [metrics_training; metrics_testing];
plotMetricsCombined(metrics_combined', labels, 'Combined Metrics', "#003049", "#D62828");






