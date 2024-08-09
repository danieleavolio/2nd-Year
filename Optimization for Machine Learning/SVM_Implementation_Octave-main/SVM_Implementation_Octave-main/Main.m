clear;

%loading of the dataset
load dataset8.mat;

%dimension of X
[n,m] = size(X);

%grid of weight C
C_Grid = [1, 10, 100, 1000];
cStar_index = intmax();

%KFOLD and Model Selection Process

train_avg_metrics= zeros(1,5);
test_avg_metrics= zeros(1,5);

kFold10 = 0.9;
kFold5 = 0.8;

%seed set to 44 to have the same randomization
randn("seed",44)
rand("seed",44)

%shuffling n indexen to have a random permutation 
index = randperm(n);

%for the first level 10-fold cross validation the set xTrain, yTrain and xTest,yTest
%are created depending of the index of i. For i=1 xtrain,ytrain will be composed
%from the second fold to the last one; xtest,yest are just the first fold
%For i=10 the xtrain,ytrain goes from the first fold to the 9th.
%All the other case xtrain,ytrain are composed from 
%the entire dataset except the i-th fold
for i = 1:10
  if i == 1
    xTrain = X(index(round((i)*(1-kFold10)*n)+1:n),:);
    yTrain = y(index(round((i)*(1-kFold10)*n)+1:n),:);
    xTest = X(index(1:round((1-kFold10)*n)),:);
    yTest = y(index(1:round((1-kFold10)*n)),:);
  elseif i == 10
    xTrain = X(index(1:round(kFold10*n)),:);
    yTrain = y(index(1:round(kFold10*n)),:);
    xTest = X(index(round(kFold10*n)+1:n),:);
    yTest = y(index(round(kFold10*n)+1:n),:);
  else
    xTrain = X(index(1:round((i-1)*(1-kFold10)*n)),:);
    yTrain = y(index(1:round((i-1)*(1-kFold10)*n)),:);
    xTrain = [xTrain; X(index(round((i+1)*(1-kFold10)*n)+1:n),:)];
    yTrain = [yTrain; y(index(round((i+1)*(1-kFold10)*n)+1:n),:)];
    xTest = X(round(i*(1-kFold10)*n)+1:round((i+1)*(1-kFold10)*n),:);
    yTest = y(round(i*(1-kFold10)*n)+1:round((i+1)*(1-kFold10)*n),:);
  endif
  %for the 5-fold cross validation holds the same as above
  for j = 1:5
    printf("MODEL SELECTION, KFOLD5\n Running FOLD number: %d \n", j);
    n1 = size(xTrain)(1);
    index1 = randperm(n1);
    if j == 1
      xTrain5 = xTrain(index1(round((j)*(1-kFold5)*n1)+1:n1),:);
      yTrain5 = yTrain(index1(round((j)*(1-kFold5)*n1)+1:n1),:);
      xTest5 = xTrain(index1(1:round((1-kFold5)*n1)),:);
      yTest5 = yTrain(index1(1:round((1-kFold5)*n1)),:);
    elseif j == 5
      xTrain5 = xTrain(index1(1:round(kFold5*n1)),:);
      yTrain5 = yTrain(index1(1:round(kFold5*n1)),:);
      xTest5 = xTrain(index1(round(kFold5*n1)+1:n1),:);
      yTest5 = yTrain(index1(round(kFold5*n1)+1:n1),:);
    else
      xTrain5 = xTrain(index1(1:round((j-1)*(1-kFold5)*n1)),:);
      yTrain5 = yTrain(index1(1:round((j-1)*(1-kFold5)*n1)),:);
      xTrain5 = [xTrain5; xTrain(index1((j+1)*round((1-kFold5)*n1)+1:n1),:)];
      yTrain5 = [yTrain5; yTrain(index1((j+1)*round((1-kFold5)*n1)+1:n1),:)];
      xTest5 = xTrain(round(j*(1-kFold5)*n1)+1:round((j+1)*(1-kFold5)*n1),:);
      yTest5 = yTrain(round(j*(1-kFold5)*n1)+1:round((j+1)*(1-kFold5)*n1),:);
    endif
    c_avg_accuracy = zeros(1,size(C_Grid)(2));
    %SVM is lauched for every training set of the 5-fold cross validation
    %For each C the accuracy of the test set is stored as sum of the previous one
    %in the position k of a grid. For example, if k=1 c_grid(k)=1 and the accuracy
    %is stored in the grid c_avg_accuracy in position k
    %then the average is computed
    for k = 1:size(C_Grid)(2)
      [vStar, gammaStar] = SVM(xTrain5, yTrain5, C_Grid(k),0);
      c_avg_accuracy(k) = c_avg_accuracy(k) + calculateCorrectness(xTest5,yTest5,vStar,gammaStar)
    endfor
    c_avg_accuracy = c_avg_accuracy / 5;
    %the function "find" below gives us the indexes of the minimum values
    %if two or more minumun values are the same we take the first as index of C*
    %that will be used in the training set of the 10-fold cross validation
    cStar_index = find(c_avg_accuracy==min(c_avg_accuracy))(1);
    %disp(cStar_index)
  endfor
  printf("KFOLD10\n Running FOLD number: %d \n", i);
  [vStar, gammaStar, performanceIndicatorsTrain] = SVM(xTrain, yTrain, C_Grid(cStar_index),1,strcat("foldNumber", num2str(i)));
  #pause()
  [performanceIndicatorsTest] = SVM_Prediction(xTest, yTest, 0, vStar, gammaStar);
  #pause()
  train_avg_metrics = train_avg_metrics + performanceIndicatorsTrain;
  test_avg_metrics = test_avg_metrics + performanceIndicatorsTest;
endfor

train_avg_metrics = train_avg_metrics * 0.1;
test_avg_metrics = test_avg_metrics * 0.1;

printf("AVERAGE TRAINING SET PERFORMANCE INDICATOR (10-FOLD)\n");
sensitivity_train = train_avg_metrics(1)
specificity_train = train_avg_metrics(2)
accuracy_train = train_avg_metrics(3)
precision_train = train_avg_metrics(4)
fScore_train = train_avg_metrics(5)
printf("AVERAGE TEST SET PERFORMANCE INDICATOR (10-FOLD)\n");
sensitivity_test = test_avg_metrics(1)
specificity_test = test_avg_metrics(2)
accuracy_test = test_avg_metrics(3)
precision_test = test_avg_metrics(4)
fScore_test = test_avg_metrics(5)