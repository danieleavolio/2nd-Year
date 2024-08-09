function [correctPoints] = countCorrectPoints(X, y, v, gamma)
  
  correctPoints = 0;
  
  for i = 1:size(X)(1)
  
    if y(i) == +1 && (v'*X(i,:)') - gamma  >= 0
      correctPoints = correctPoints + 1;
    endif
    
    if y(i) == -1 && (v'*X(i,:)') - gamma  <= 0
      correctPoints = correctPoints + 1;
    endif
  
  end
  
endfunction