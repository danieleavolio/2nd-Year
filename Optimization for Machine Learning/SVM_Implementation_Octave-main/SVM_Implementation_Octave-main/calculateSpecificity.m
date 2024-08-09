%This function calculates the specificity, also called the true negative rate. 
%It measures the proportion of negative points correctly identified.

%B = Set of points labelled -1
%v and gamma = vStar and gammaStar
function [specificity] = calculateSpecificity(B, v, gamma)
  
  correctPoints = 0;
  specificity = 0;
  if (B > 0)
    for i = 1:size(B)(1)
    
      if (v'*B(i,:)') - gamma + 1  <= 0
        correctPoints = correctPoints + 1;
      endif
    
    endfor

    specificity = correctPoints/size(B)(1);
  endif
  
endfunction