function [accuracy, recall, specificity, precision, f_score] = stats(y, y_hat)
    # correct predictions ratio
    accuracy = accuracy(y, y_hat);
    # positive points captured ratio
    recall = recall(y, y_hat);
    # negative points captured ratio
    specificity = specificity(y, y_hat);
    # positive prediction accuracy
    precision = precision(y, y_hat);
    # harmonic mean of precision and recall
    f_score = f_score(y, y_hat);
end


function accuracy = accuracy(y, y_hat)
    # Accuracy
    # Total number of correct predictions
    # True Positive + True Negative / Total
    accuracy = sum(y == y_hat) / length(y);
end


function true_rate = true_rate(y, y_hat, label)
    # True Rate
    # Ratio of correctly predicted observations in a given class to the total number of observations in that class
    # True Label / (True Label + False not-Label)
    # True Label / Total Actual Label
    t = sum(y == label & y_hat == label);
    f = sum(y == label & y_hat != label);
    if t == 0 && f == 0
        warning('No example labelled (%d)\n', label);
        disp(y');
        true_rate = 0;
        return
    end
    true_rate = t / (t + f);
end


function recall = recall(y, y_hat)
    # Recall, also known as sensitivity or true positive rate (TPR)
    # Ratio of correctly predicted positive observations to all observations in actual class
    # True Positive / (True Positive + False Negative)
    # True Positive / Total Actual Positive
    recall = true_rate(y, y_hat, 1);
end


function precision = precision(y, y_hat)
    # Precision
    # Ratio of correctly predicted positive observations to the total predicted positive observations
    # True Positive / (True Positive + False Positive)
    # True Positive / Total Predicted Positive
    total_predicted_positive = sum(y_hat == 1);
    if total_predicted_positive == 0
        warning('No predicted positive examples');
        precision = 0;
        return
    end
    tp = sum(y == 1 & y_hat == 1);
    precision = tp / total_predicted_positive;
end


function specificity = specificity(y, y_hat)
    # Specificity, also known as the true negative rate (TNR)
    # Ratio of correctly predicted negative observations to all observations in actual class
    # True Negative / (True Negative + False Positive)
    # True Negative / Total Actual Negative
    specificity = true_rate(y, y_hat, -1);
end


function f_score = f_score(y, y_hat)
    precision = precision(y, y_hat);
    recall = recall(y, y_hat);
    f_score = 2 * precision * recall / (precision + recall);
end
