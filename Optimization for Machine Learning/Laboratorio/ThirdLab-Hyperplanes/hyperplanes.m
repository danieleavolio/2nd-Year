
v = [2,5];

gamma = 6;


x1 = -5:0.1:5;


x2 = (gamma - v(1) * x1) / v(2);

hold on;

plot(x1, x2, 'k');

%Supporting hyperplanes
gammaPlus = gamma + 1;
gammaMinus = gamma - 1;

x2Plus = (gammaPlus - v(1) * x1) / v(2);
x2Minus = (gammaMinus - v(1) * x1) / v(2);

plot(x1, x2Plus, 'r');

plot(x1, x2Minus, 'b');
