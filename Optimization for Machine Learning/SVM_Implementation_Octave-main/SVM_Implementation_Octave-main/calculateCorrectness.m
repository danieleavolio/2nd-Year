%This function calculate the testing correctness or training correctness, based
%on what we give as parameter X
%The average testing correctness measures the generalization capability of a 
%classifier, i.e. the capability to correctly classify the new data.
%The average training correctness measures the quality of the optimization
%process in the learning phase.

%X = Test or training set
%y = class labels
%v and gamma = vStar and gammaStar
function [correctness] = calculateCorrectness(X, y, v, gamma)
  
  correctPoints = 0;
  
  for i = 1:size(X)(1)
  
    if y(i) == +1 && (v'*X(i,:)') - gamma  >= 0
      correctPoints = correctPoints + 1;
    endif
    
    if y(i) == -1 && (v'*X(i,:)') - gamma  <= 0
      correctPoints = correctPoints + 1;
    endif
  
  end
  
  correctness = correctPoints / size(X)(1);
  
endfunction