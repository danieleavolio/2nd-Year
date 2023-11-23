clear;

%max x min {3x1 - 7x2 +4x3 +x4, 2x1+6x2+5x3+8x4, x1-x2+x4}

% x1+  x2+  x3+ x4 <= 1
%7x1 +4x2 -5x3 +x4 = 3
%x1,        x3, x4 >= 0


%Anche qui, va linearizzato inserendo una nuova variabile

%max x,v    v
%

%           v >= 3x1 - 7x2 +4x3 +x4
%           v >= 2x1+6x2+5x3+8x4
%           v >= x1-x2+x4

%           x1+  x2+  x3+ x4 <= 1
%           7x1 +4x2 -5x3 +x4 = 3
%           x1,        x3, x4 >= 0


%Cambiamo i segni dei vincoli dove serve
%max x,v    v
%

%           v  - 3x1 + 7x2 - 4x3 -x4 <=0
%           v  - 2x1  -6x2 - 5x3-8x4 <=0
%           v  - x1    +x2       -x4 <=0

%           x1+  x2+  x3+ x4 <= 1
%           7x1 +4x2 -5x3 +x4 = 3
%           x1,        x3, x4 >= 0




%Vettore costi
c = [1; 0; 0; 0; 0];


%Matrice dei vincoli
A = [ 1 -3 7 -4 -1;
      1 -2 -6 -5 -8;
      1 -1 1 0 -1;
      0 1 1 1 1 ;
      0 7 4 -5 1];

%Constraint a destra
b = [0;0;0;1;3];

%Lowerbound
lb = [-inf 0 -inf 0 0];

%Upperbound
ub = [inf inf inf inf inf];

%Tipi di constraint
ctype = "UUUUS";

%Tipi di variabili
vartype = "CCCCC";

%Senso MAX -1
sense = -1;


%Risoluzione del problema
[xopt, fmax, status, extra] = glpk (c, A, b, lb, ub, ctype, vartype, sense);

%Scrivere soluzione su file

file = fopen('soluzione3.txt', 'w');

fprintf(file, "La soluzione ottima x = [%f %f %f] e il valore ottimo di f* = %f", xopt(1), xopt(2), xopt(3), fmax);

fclose(file);


