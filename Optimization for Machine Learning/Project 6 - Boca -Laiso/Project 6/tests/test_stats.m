clear;

file_path = fileparts(mfilename('fullpath'));
src_path = fullfile(file_path, '../src');
addpath(src_path);

test "stats"
    % 10 random labels with 2 classes (+1 and -1)
    y =     [ +1 +1 +1 +1 +1 -1 -1 -1 -1 -1 ]';
    y_hat = [ +1 +1 +1 +1 +1 +1 -1 -1 -1 -1 ]';

    % 5 true positives, 4 true negatives, 1 false positive, 0 false negative

    % 9 correct predictions out of 10 => accuracy = 90% = 0.9
    % recall = 5 / (5 + 0) = 1
    % specificity = 4 / (4 + 1) = 0.8
    % precision = 5 / (5 + 1) = 0.8333
    % f_score = 2 * (precision * recall) / (precision + recall) = 0.9091

    [accuracy, recall, specificity, precision, f_score] = stats(y, y_hat)
    tol = 1e-03;
    assert (accuracy, 0.9);
    assert (recall, 1);
    assert (specificity, 0.8);
    assert (precision, 0.83333, tol);
    assert (f_score, 0.9091, tol);
