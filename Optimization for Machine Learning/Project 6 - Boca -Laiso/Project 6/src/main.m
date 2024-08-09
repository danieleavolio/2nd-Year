clear;

version = OCTAVE_VERSION;
major = str2num(version(1));
if (major > 7)
    printf("Octave version must be < 8.0.0, running Octave version: %s\n", version);
    return;
endif

# Store start time
seconds = num2str(floor(time()));

# Set seed for reproducibility
rand('seed', 17);

# Load dataset
dataset = dataset.from_file("../data/dataset6.mat").shuffle();
dataset.summary();
dataset.head();

# Number of folds for k-folds cross validation
folds = 10;
# Number of folds for bilevel cross validation
bilevel_folds = 5;

# Save results
train_accuracy_history = [];
train_recall_history = [];
train_specificity_history = [];
train_precision_history = [];
train_f_score_history = [];

test_accuracy_history = [];
test_recall_history = [];
test_specificity_history = [];
test_precision_history = [];
test_f_score_history = [];

best_Cs = [];

# Grid search for C
C_grid = [0.01, 0.1, 1, 10, 100, 1000];

# Training loop
for fold = 1:folds

    printf("\nFold %d\n", fold);
    [train, test] = k_folds(dataset, fold, folds);

    printf("\ttrain (+) (%d/%d) \n\ttest  (+) ( %d/ %d)\n\n", sum(train.y == 1), size(train.X, 1), sum(test.y == 1), size(test.X, 1));

    best_C = 0;
    best_accuracy = 0;

    # Select best C using bilevel cross validation
    for C = C_grid

        printf("\tC = %f\n", C);
        accuracies = [];
        #Qui effettuiamo la validazione per trovare gli iperparametri migliori per poi addestrare il modello
        for bilevel_fold = 1:bilevel_folds
            # Bilevel cross validation

            [bilevel_train, bilevel_val] = k_folds(train, bilevel_fold, bilevel_folds);

            [alphas, b, sv_x, sv_y] = svm(bilevel_train, C);
            y_hat = predict(bilevel_val.X, alphas, b, sv_x, sv_y);
            [accuracy, recall, specificity, precision, f_score] = stats(bilevel_val.y, y_hat);

            accuracies = [accuracies; accuracy];
        end
        printf("\tMean bilevel accuracy: %f\n\n", mean(accuracies));

        # Select best C based on bilevel accuracy
        if mean(accuracies) > best_accuracy
            best_C = C;
            best_accuracy = mean(accuracies);
        end
    end

    printf("\tBest C for fold %d: %f\n\twith accuracy: %f\n\n", fold, best_C, best_accuracy);
    best_Cs = [best_Cs; best_C];
    #In questa fase addestriamo solo con lo scopo di validare
    # Train on the whole train set
    [alphas, b, sv_x, sv_y] = svm(train, best_C);

    # Evaluate on train set
    y_hat = predict(train.X, alphas, b, sv_x, sv_y);
    [accuracy, recall, specificity, precision, f_score] = stats(train.y, y_hat);

    printf("\tTrain accuracy:    %f\n", accuracy);
    printf("\tTrain recall:      %f\n", recall);
    printf("\tTrain specificity: %f\n", specificity);
    printf("\tTrain precision:   %f\n", precision);
    printf("\tTrain f_score:     %f\n", f_score);

    # Save results
    train_accuracy_history = [train_accuracy_history; accuracy];
    train_recall_history = [train_recall_history; recall];
    train_specificity_history = [train_specificity_history; specificity];
    train_precision_history = [train_precision_history; precision];
    train_f_score_history = [train_f_score_history; f_score];

    # Evaluate on test set
    y_hat = predict(test.X, alphas, b, sv_x, sv_y);
    [accuracy, recall, specificity, precision, f_score] = stats(test.y, y_hat);

    printf("\tTest  accuracy:    %f\n", accuracy);
    printf("\tTest  recall:      %f\n", recall);
    printf("\tTest  specificity: %f\n", specificity);
    printf("\tTest  precision:   %f\n", precision);
    printf("\tTest  f_score:     %f\n", f_score);

    # Save results
    test_accuracy_history = [test_accuracy_history; accuracy];
    test_recall_history = [test_recall_history; recall];
    test_specificity_history = [test_specificity_history; specificity];
    test_precision_history = [test_precision_history; precision];
    test_f_score_history = [test_f_score_history; f_score];

end

# Save results
mkdir ("..", "results");
save("-text", ["../results/train_accuracy_history_" seconds "_.txt"], "train_accuracy_history");
save("-text", ["../results/train_recall_history_" seconds "_.txt"], "train_recall_history");
save("-text", ["../results/train_specificity_history_" seconds "_.txt"], "train_specificity_history");
save("-text", ["../results/train_precision_history_" seconds "_.txt"], "train_precision_history");
save("-text", ["../results/train_f_score_history_" seconds "_.txt"], "train_f_score_history");

save("-text", ["../results/test_accuracy_history_" seconds "_.txt"], "test_accuracy_history");
save("-text", ["../results/test_recall_history_" seconds "_.txt"], "test_recall_history");
save("-text", ["../results/test_specificity_history_" seconds "_.txt"], "test_specificity_history");
save("-text", ["../results/test_precision_history_" seconds "_.txt"], "test_precision_history");
save("-text", ["../results/test_f_score_history_" seconds "_.txt"], "test_f_score_history");

save("-text", ["../results/best_Cs_" seconds "_.txt"], "best_Cs");

# Print results
printf("\n\n");
printf("Train accuracy:    %f\n", mean(train_accuracy_history));
printf("Train recall:      %f\n", mean(train_recall_history));
printf("Train specificity: %f\n", mean(train_specificity_history));
printf("Train precision:   %f\n", mean(train_precision_history));
printf("Train f_score:     %f\n", mean(train_f_score_history));

printf("\n");
printf("Test  accuracy:    %f\n", mean(test_accuracy_history));
printf("Test  recall:      %f\n", mean(test_recall_history));
printf("Test  specificity: %f\n", mean(test_specificity_history));
printf("Test  precision:   %f\n", mean(test_precision_history));
printf("Test  f_score:     %f\n", mean(test_f_score_history));

printf("\n\nDone!\n");
