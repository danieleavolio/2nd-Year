%Given a set to preditct, this function looks if the set is well predicted
%X = Dataset
%y = Class labels
%C = Weight
%draw (0,1) = If we want to draw the hyperplanes
function [performanceIndicators] = SVM_Prediction(X, y, draw, vStar, gammaStar, labelTitle = "")

[numPoints, numCol] = size(X);
numVar = numCol + 1 + numPoints; %n + 1 + m + k

setA = []; %Array for the set A points
setB = []; %Array for the set B points

for i = 1: numPoints
  
  if y(i) == +1 %When the class label is +1
    setA = [setA; 
            X(i,:)]; %append to the set A the point
  else %When the class label is +1
    setB = [setB; 
            X(i,:)]; %append to the set B the point
  end
  
end

if draw == 1
  drawPicture(setA, setB, vStar, gammaStar, X, labelTitle);
endif

%Performance indicators process

disp("TEST SET PERFORMANCE INDICATOR");
sensitivity = calculateSensitivity(setA, vStar, gammaStar)
specificity = calculateSpecificity(setB, vStar, gammaStar)
accuracy = calculateCorrectness(X,y,vStar,gammaStar)
precision = calculatePrecision(setA,setB,vStar,gammaStar)
fScore = 0;
if sensitivity > 0 || precision > 0
  fScore = 2 * (sensitivity * precision) / (sensitivity + precision)
endif
fScore

performanceIndicators = [sensitivity, specificity, accuracy, precision, fScore];

endfunction