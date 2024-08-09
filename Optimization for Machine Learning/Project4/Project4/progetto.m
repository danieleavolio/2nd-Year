clear;

# load the dataset
load dataset4.mat;
[numPunti, dim] = size(X);

# shuffle the dataset
for i = 1 : 1000
  random_1 = randi([1, numPunti], 1);
  random_2 = randi([1, numPunti], 1);
  riga = X(random_1,:);
  label = y(random_1);
  X(random_1,:) = X(random_2,:);
  y(random_1) = y(random_2);
  X(random_2,:) = riga;
  y(random_2) = label;
endfor

# possible values of C
possible_C = [0.01; 0.05; 0.1];

sigma = 500;
delta = -1 / (2 * sigma);

# metrics
acc_train = 0;
sens_train = 0;
spec_train = 0;
prec_train = 0;
fscore_train = 0;

acc_test = 0;
sens_test = 0;
spec_test = 0;
prec_test = 0;
fscore_test = 0;

# perform 10-kross validation
for i = 0:9
  
  # split the dataset
  train_x_1 = [];
  train_y_1 = [];
  test_x_1 = [];
  test_y_1 = [];
  for j = 1:numPunti
    if j > (numPunti/10)*i && j <= (numPunti/10)*(i+1)
      test_x_1 = [test_x_1; X(j,:)];
      test_y_1 = [test_y_1; y(j)];
    else
      train_x_1 = [train_x_1; X(j,:)];
      train_y_1 = [train_y_1; y(j)];
    endif
  endfor
  
  # iterate over the possible values of C
  for t = 1:size(possible_C)(1)
    
    # second level accuracy
    acc_2 = 0;
  
    # perform 5-kross validation
    for w = 0:4
    
      # split the dataset
      train_x_2 = [];
      train_y_2 = [];
      test_x_2 = [];
      test_y_2 = [];
      for k = 1:size(train_x_1)(1)
        if k > (size(train_x_1)(1)/5)*w && k <= ((size(train_x_1)(1))/5)*(w+1)
          test_x_2 = [test_x_2; train_x_1(k,:)];
          test_y_2 = [test_y_2; train_y_1(k)];
        else
          train_x_2 = [train_x_2; train_x_1(k,:)];
          train_y_2 = [train_y_2; train_y_1(k)];
        endif
      endfor
    
      # prepare and solve the model 
      x0 = zeros(1,dim);
      numPosPoints = 0;
      for q = 1:(size(train_x_2)(1))
        if train_y_2(q) == 1
          x0 = x0 + train_x_2(q,:);
          numPosPoints = numPosPoints + 1;
        endif
      endfor
      x0 = x0 / numPosPoints;
      
      c = possible_C(t) * ones(size(train_x_2)(1)+1, 1);
      c(1) = 1;
      A = zeros(size(train_x_2)(1), size(train_x_2)(1)+1);
      b = [];
      lb = zeros(size(train_x_2)(1)+1, 1);
      ub = [];
      cType = [];
      varType = [];
      for q = 1:(size(train_x_2)(1))
        #b(q) = train_y_2(q) * norm(train_x_2(q,:) - x0)^2;
        b(q) = train_y_2(q) * (2 - (2 * (exp(delta * (norm(train_x_2(q,:) - x0)^2)))));
        A(q,1) = train_y_2(q);
        A(q,q+1) = 1;
        cType = [cType, "L"];
        # cType(q) = 'L';
        varType = [varType, "C"];
        # varType(q) = 'C';
      endfor
      varType = [varType, "C"];
      # varType(size(train_x_2)(1)+1) = 'C';
      
      [xStar,fStar] = glpk(c, A, b, lb, ub, cType, varType, 1);
      
      R = sqrt(xStar(1));
      
      # compute second level testing correctness
      rightPoints = 0;
      for q = 1:size(test_x_2)(1)
        #if test_y_2(q) == 1 && norm(test_x_2(q,:)-x0)^2 <= R^2
        if test_y_2(q) == 1 && (2 - (2 * (exp(delta * (norm(test_x_2(q,:) - x0)^2))))) <= R^2
          rightPoints = rightPoints + 1;
        endif
        #if test_y_2(q) == -1 && norm(test_x_2(q,:)-x0)^2 > R^2
        if test_y_2(q) == -1 && (2 - (2 * (exp(delta * (norm(test_x_2(q,:) - x0)^2))))) > R^2
          rightPoints = rightPoints + 1;
        endif
      endfor
      
      acc_2 = acc_2 + (rightPoints / size(test_x_2)(1));
      
    endfor 
    
    acc_2 = acc_2 / 5;
    
    # storing the better current value of C
    if t == 1
      C = possible_C(1);
      acc_2_max = acc_2;
    else
      if acc_2 > acc_2_max
        C = possible_C(t);
        acc_2_max = acc_2;
      endif
    endif
    
   endfor
   
  # prepare and solve the model with the best value of C
  x0 = zeros(1,dim);
  numPosPoints = 0;
  for q = 1:(size(train_x_1)(1))
    if train_y_1(q) == 1
      x0 = x0 + train_x_1(q,:);
      numPosPoints = numPosPoints + 1;
    endif
  endfor
  x0 = x0 / numPosPoints;
  
  c = C * ones(size(train_x_1)(1)+1,1);
  c(1) = 1;
  A = zeros(size(train_x_1)(1), size(train_x_1)(1)+1);
  b = [];
  lb = zeros(size(train_x_1)(1)+1,1);
  ub = [];
  cType = [];
  varType = [];
  for q = 1:size(train_x_1)(1)
    #b(q) = train_y_1(q) * norm(train_x_1(q,:)-x0)^2;
    b(q) = train_y_1(q) * (2 - (2 * (exp(delta * (norm(train_x_1(q,:) - x0)^2)))));
    A(q,1) = train_y_1(q);
    A(q,q+1) = 1;
    cType = [cType, "L"];
    varType = [varType, "C"];
  endfor
  varType = [varType, "C"];
  
  [xStar,fStar] = glpk(c, A, b, lb, ub, cType, varType, 1);
  
  R = sqrt(xStar(1));
  
  # compute first level training metrics
  truePos_train = 0;
  trueNeg_train = 0;
  falsePos_train = 0;
  falseNeg_train = 0;
  
  for q = 1:size(train_x_1)(1)
    if train_y_1(q) == 1
      #if norm(train_x_1(q,:)-x0)^2 <= R^2
      if (2 - (2 * (exp(delta * (norm(train_x_1(q,:) - x0)^2))))) <= R^2
        truePos_train = truePos_train + 1;
      else
        falseNeg_train = falseNeg_train + 1;
      endif
    else
      #if norm(train_x_1(q,:)-x0)^2 <= R^2
      if (2 - (2 * (exp(delta * (norm(train_x_1(q,:) - x0)^2))))) <= R^2
        falsePos_train = falsePos_train + 1;
      else
        trueNeg_train = trueNeg_train + 1;
      endif
    endif
  endfor
  
  acc_train = acc_train + ((truePos_train + trueNeg_train) / size(train_x_1)(1));
  sens_train = sens_train + (truePos_train / (truePos_train + falseNeg_train));
  spec_train = spec_train + (trueNeg_train / (trueNeg_train + falsePos_train));
  prec_train = prec_train + (truePos_train / (truePos_train + falsePos_train));
  fscore_train = 2 * (sens_train * prec_train) / (sens_train + prec_train);
  
  # compute first level testing metrics
  truePos_test = 0;
  trueNeg_test = 0;
  falsePos_test = 0;
  falseNeg_test = 0;
  
  for q = 1:size(test_x_1)(1)
    if test_y_1(q) == 1
      #if norm(test_x_1(q,:)-x0)^2 <= R^2
      if (2 - (2 * (exp(delta * (norm(test_x_1(q,:) - x0)^2))))) <= R^2
        truePos_test = truePos_test + 1;
      else
        falseNeg_test = falseNeg_test + 1;
      endif
    else
      #if norm(test_x_1(q,:)-x0)^2 <= R^2
      if (2 - (2 * (exp(delta * (norm(test_x_1(q,:) - x0)^2))))) <= R^2
        falsePos_test = falsePos_test + 1;
      else
        trueNeg_test = trueNeg_test + 1;
      endif
    endif
  endfor
  
  acc_test = acc_test + ((truePos_test + trueNeg_test) / size(test_x_1)(1));
  sens_test = sens_test + (truePos_test / (truePos_test + falseNeg_test));
  spec_test = spec_test + (trueNeg_test / (trueNeg_test + falsePos_test));
  prec_test = prec_test + (truePos_test / (truePos_test + falsePos_test));
  fscore_test = 2 * (sens_test * prec_test) / (sens_test + prec_test);
    
endfor

# average the metrics

acc_train = acc_train / 10;         # 0.8336
sens_train = sens_train / 10;       # 0.8082
spec_train = spec_train / 10;       # 0.8589
prec_train = prec_train / 10;       # 0.8513
fscore_train = fscore_train / 10;   # 0.8292
  
acc_test = acc_test / 10;           # 0.8325
sens_test = sens_test / 10;         # 0.8022
spec_test = spec_test / 10;         # 0.8644
prec_test = prec_test / 10;         # 0.8589
fscore_test = fscore_test / 10;     # 0.8296

