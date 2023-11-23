clear;



%min x max {3x1 -4x2 -7x3, - 9x1 -2x2 + 4x3}
%
%           2x1 + 5x2 - x3  = 4
%           6x1 + 6x2 - x3 >= 2
%           4x1 + 5x2 +2x3 <= 10
%                  x2      >= 0


%Il problema però non può essere risolto come prima, perché è un problema
%**non lineare**. Bisogna costruire il problema in modo che sia lineare.


%min x,v     v
%
%            v >= 3x1 -4x2 -7x3
%            v >= -9x1 -2x2 + 4x3
%
%            2x1 + 5x2 - x3  = 4
%            6x1 + 6x2 - x3 >= 2
%            4x1 + 5x2 +2x3 <= 10
%                   x2      >= 0



%Vettore costi
c = [1; 0; 0; 0];

%Matrice dei vincoli
A = [1 -3 4 7;
     1 9 2 -4;
     0 2 5 -1;
     0 6 6 -1;
     0 4 5 2];

%Constraint a destra
b = [0;0;4;2;10]

%Lower Bound (Non deve essere un column array)
lb = [-inf -inf  0 -inf]

%Upper Bound (Non deve essere un column array)
ub = [inf inf inf inf]

%Vettori per i tipi di vincoli
ctype = ["L", "L", "S", "L", "U"];

%Vettori per i tipi di variabili
vartype = ["C", "C", "C", "C"];

%Senso del problema
sense = 1;

%Risoluzione del problema
[xopt, fmin, status, extra] = glpk (c, A, b, lb, ub, ctype, vartype, sense);

%Scrivere soluzione su file

file = fopen('soluzione2.txt', 'w');

fprintf(file, "La soluzione ottima x = [%f %f %f] e il valore ottimo di f* = %f", xopt(1), xopt(2), xopt(3), fmin);

fclose(file);
