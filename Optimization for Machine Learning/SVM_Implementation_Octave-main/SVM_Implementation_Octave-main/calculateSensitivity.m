%This function calculates the sensitivity, also called he true positive rate or 
%recall. It measures the proportion of positive points correctly identified.

%A = Set of points labelled +1
%v and gamma = vStar and gammaStar
function [sensitivity] = calculateSensitivity(A, v, gamma)
  
  correctPoints = 0;
  sensitivity = 0;
  if A > 0
    for i = 1:size(A)(1)
    
      if (v'*A(i,:)') - gamma - 1  >= 0
        correctPoints = correctPoints + 1;
      endif
    
    endfor

    sensitivity = correctPoints/size(A)(1);
  endif
  
endfunction