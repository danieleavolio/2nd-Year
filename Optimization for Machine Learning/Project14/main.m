clear;

%loading of the dataset
load dataset14MIL1.mat;

[numPoints, numCol] = size(X);
numVar = numCol + 1 + numPoints; %n + 1 + m + k

yj=[];
x0=[];

for i= 1:numPoints
  if(i<=32)
    yj(i)=1;
   elseif
     yj(i)=-1;
   end
end

vStar=[];
gammaStar=0;
accuracyF=0;
#procedura iterativa
change=true;
temp=[]
while(change)
  temp=yj;
  [vSegnato,gammaSegnato,accuracy] = SVM(X, yj,1,1);

  for i=1: 32

    if(dot(vSegnato,X(i,:))>=gammaSegnato)
      yj(i)= 1;
    else
      yj(i)= -1;
    end
  end
  start=1;
  finish=8
  s=0;
  for j=1:4
    for i=start:finish
      s+=(yj(i)+1)/2;
    end
    if(s==0)
      k=1;
      mx=0;
      for i=start:finish
        if(max(dot(vSegnato,X(i,:))-gammaSegnato)>mx)
          mx=max(dot(vSegnato,X(i,:))-gammaSegnato);
          k=i
        end
      end
      yj(k)=1;
     end
     start+=8
     finish+=8
   end
   c=0;
   for i=1:32
     if(yj(i)!=temp(i))
        c=1;
     end
   end
   if(c==0)
    vStar=vSegnato;
    gammaStar=gammaSegnato;
    change=false;
    accuracyF=accuracy;
   end

end
vStar
gammaStar
accuracyF
