% Cleaning workspace, loading dataset and loading library for k-fold testing
clear;
pkg load statistics;
load dataset11.mat;

% Create a vector of all possibile values of C (Model Selection)
cValues = [1, 10, 100, 1000];

% Partitioning dataset using function cvpartition (from statistics package)
firstLevelNumFolds = 10;
secondLevelNumFolds = 5;
%Dividiamo il set in 10 subset per fare 
firstLevelPartition = cvpartition(y, 'KFold', firstLevelNumFolds);
%Fa la suddivisione 360-40 per train e test

% Variables used to compute the metrics of the first level fold
trainMetrics = [0; 0; 0; 0];
testMetrics = [0; 0; 0; 0];
metricNames = {" correctness--->"; " sensitivity--->"; " specificity--->"; " F-score--->"};

for i = 1: firstLevelNumFolds
    disp(strcat("First-level fold n.", int2str(i)));
 
    % This vector contains 1 for each point of the test set, zero otherwise
    % We're getting the indices of the points belonging to the i-th fold
    % This is performed firstLevelNumFolds times, each time we get a different
    % set of indices because first we ask for the first fold, then second etc.
    firstLevelTestIndices = test(firstLevelPartition, i);

 
    % Filling the first-level dataset partitions according to the above vector
    %Riempio 
    XTrainFirstLevel = [];
    yTrainFirstLevel = [];
    XTestFirstLevel = [];
    yTestFirstLevel = [];
    for k = 1: length(firstLevelTestIndices)
        if firstLevelTestIndices(k) == 1
            XTestFirstLevel = [XTestFirstLevel; X(k, :)];
            yTestFirstLevel = [yTestFirstLevel; y(k)];
        else
            XTrainFirstLevel = [XTrainFirstLevel; X(k, :)];
            yTrainFirstLevel = [yTrainFirstLevel; y(k)];
        endif
    endfor
 
    % Partitioning the first-level training set to create second-level folds
    secondLevelPartition = cvpartition(yTrainFirstLevel, 'KFold', secondLevelNumFolds);
    % Used to compute the metrics for each C
    cAccuracies = [0, 0, 0, 0];
 
    % Evaluate c value
    for cIndex = 1:length(cValues)
        for j = 1: secondLevelNumFolds
            % Fill second-level fold dataset
            XTrainSecondLevel = [];
            yTrainSecondLevel = [];
            XTestSecondLevel = [];
            yTestSecondLevel = [];
            secondLevelIndices = test(secondLevelPartition, j);
            for k = 1: length(secondLevelIndices)
                if secondLevelIndices(k) == 1
                    XTestSecondLevel = [XTestSecondLevel; XTrainFirstLevel(k, :)];
                    yTestSecondLevel = [yTestSecondLevel; yTrainFirstLevel(k)];
                else
                    XTrainSecondLevel = [XTrainSecondLevel; XTrainFirstLevel(k, :)];
                    yTrainSecondLevel = [yTrainSecondLevel; yTrainFirstLevel(k)];
                endif
            endfor
         
            % Fitting the model on the second-level dataset
            xStar = fitPsvm(XTrainSecondLevel, yTrainSecondLevel, cValues(cIndex));
         
            % Computing the accuracy of this second-level fold with a certain c
            numColumns = size(XTrainSecondLevel)(2);
            vStarSecondLevel = xStar(1:numColumns);
            gammaStarSecondLevel = xStar(numColumns + 1);
            numCorrectlyClassified = getNumCorrectlyClassified(XTestSecondLevel, ...
            yTestSecondLevel, vStarSecondLevel, gammaStarSecondLevel);
            cAccuracies(cIndex) = cAccuracies(cIndex) + numCorrectlyClassified;
        endfor
        % Averaging accuracies for a certain C value
        cAccuracies(cIndex) = cAccuracies(cIndex) / secondLevelNumFolds;
    endfor
    % Extracting the one the one with the best testing correctness
    cStar = max(cAccuracies);
    % Training the model with first level training set and C*
    xStarFirstLevel = fitPsvm(XTrainFirstLevel, yTrainFirstLevel, cStar);
 
    numCols = size(XTrainFirstLevel)(2);
    vStarFirstLevel = xStarFirstLevel(1:numCols);
    gammaStarFirstLevel = xStarFirstLevel(numCols + 1);
 
    % Calculate training/testing metrics for the current first-level fold
    trainFoldMetrics = getPerformanceMetrics(XTrainFirstLevel, yTrainFirstLevel, vStarFirstLevel, gammaStarFirstLevel);
    testFoldMetrics = getPerformanceMetrics(XTestFirstLevel, yTestFirstLevel, vStarFirstLevel, gammaStarFirstLevel);
 
    % Summing up metrics
    for k = 1:4
        trainMetrics(k) = trainMetrics(k) + trainFoldMetrics(k);
        testMetrics(k) = testMetrics(k) + testFoldMetrics(k);
    endfor
 
    % Plotting the fold
	plotPsvm(XTrainFirstLevel, yTrainFirstLevel, vStarFirstLevel, gammaStarFirstLevel, i);
	%pause(10);
 
endfor

% Averaging metrics and printing them
for k = 1:4
    trainMetrics(k) = trainMetrics(k) / firstLevelNumFolds;
    disp(strcat(strcat("Average training ", metricNames{k}), num2str(trainMetrics(k))));
    testMetrics(k) = testMetrics(k) / firstLevelNumFolds;
    disp(strcat(strcat("Average testing ", metricNames{k}), num2str(testMetrics(k))));
 
endfor

