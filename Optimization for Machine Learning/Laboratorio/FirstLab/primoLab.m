clear

%min z = 2x1 + 3x2 -4x3
%        4x1 + 5x2 -6x3  <= 5
%        6x1 + 5x2 + 4x3 >= 9
%        x1  - x2  + 3x3  = 6
%        x1,        , x3 >= 0

%[xmin, fmin, status, extra] = ...
%glpk (c, A, b, lb, ub, ctype, vartype, sense);


c = [2;3;-4];

A = [4 5 -6;6 5 4; 1 -1 3]

%Constraint a destra
b = [5;9;6]


%Lower Bound (Non deve essere un column array)
%Si usa -inf per indicare che non c'è un lower bound
lb = [0 -inf 0]

%Upper Bound (Non deve essere un column array)

ub = [inf inf inf]

%<!-- Oppure, se sono forte -->
ub = inf(1,3);

%Vettori per i tipi di vincoli
ctype = ["U", "L", "S"];
%Oppure
ctype = "ULS";
%Dove U = Upper Bound, L = Lower Bound, S = Equality

%Vettori per i tipi di variabili
vartype = ["C", "C", "C"];
%Oppure
vartype = "CCC";
%Dove C = Continuo, B = Binario, I = Intero.
%In questo caso, siccome non abbiamo specificaot niente nel problema,
%le variabili sono continue.



%Senso del problema
sense = 1;
%Dove 1 = Minimizzazione, -1 = Massimizzazione

%Risoluzione del problema
[xopt, fmin, status, extra] = glpk (c, A, b, lb, ub, ctype, vartype, sense)

%Scrivere soluzione su file

file = fopen('soluzione.txt', 'w')

fprintf(file, "La soluzione ottima x = [%f %f %f] e il valore ottimo di f* = %f", xopt(1), xopt(2), xopt(3), fmin)

fclose(file)
