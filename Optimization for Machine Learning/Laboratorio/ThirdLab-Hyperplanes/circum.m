%Centro
x0 = [0 3];

%Raggio
R = 5

drawCircumference(x0,R,'k');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%Circonferenze con circonferenza di supporto%%%%%%%%



%Centro
x0 = [0 3];

%Raggio
R = 5

%Margine
m = 1;

%circonferenza 1
drawCircumference(x0,R,'k');

hold on;

%circonferenza 2
drawCircumference(x0,R+m,'b');

%circonferenza 3
drawCircumference(x0,R-m,'r');

legend("S", "S+", "S-", "Location","northwestoutside");
