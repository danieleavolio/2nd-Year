# Separazione sferica
<!-- Aggiungi autore e data -->

- [Separazione sferica](#separazione-sferica)
- [Disegnare in 3D](#disegnare-in-3d)

Diciamo che facciamo il codice direttamente dhaaai

$$\min_{z, \xi, \Psi} z + C \sum_{i=1}^m \xi_i + C \sum_{l=1}^k \Psi_l \\
\begin{aligned}


                    Xsi_i >&= ||a_i - x_0||^2 - z, i = 1, ..., m
                    \\
                    Psi_i >&= z - ||b_l - x_0||^2, i = 1, ..., m \\
                    Xsi_i >&= 0, i = 1, ..., m \\
                    Psi_i >&= 0, i = 1, ..., k \\ 
                    z >&= 0 \\
\end{aligned}
$$

```octave

clear;

% Load data
load toy2.mat

% Print the data
X;
[numPoints, dim] = size(X);

numVar = 1 + numPoints;
Positive = [];
Negative = [];

for i = 1 :numPoints
    if y(i) == 1
        Positive = [Positive; X(i, :)];
    else
        Negative = [Negative; X(i, :)];
    endif
end

m = size(Positive, 1);

k = size(Negative, 1);

% Plot points

hold on;

scatter(Positive(:, 1), Positive(:, 2), 'r', 'filled');
scatter(Negative(:, 1), Negative(:, 2), 'b', 'filled');

waitforbuttonpress();

% Troviamo la media per il baricentro sui positivi
hold on;

% calculate using mean function
mediaX = mean(Positive(:, 1));
mediaY = mean(Positive(:, 2));

% Plot baricentro
scatter(mediaX, mediaY, 'g', 'filled');

waitforbuttonpress();

%Salviamo le medie in un vettore X0
x0 = [mediaX mediaY];

%Trasformiamo la formula in forma normale

%min {z, \Xsi, \Psi} z + C \sum_{i=1}^m \Xsi_i + C \sum_{l=1}^k \Psi_l


```
$$\min_{z, \xi, \Psi} z + C \sum_{i=1}^m \xi_i + C \sum_{l=1}^k \Psi_l \\
\begin{aligned}


                    \xi_i + z >&= ||a_i - x_0||^2, i &= 1, ..., m
                    \\
                    \Psi_i - z>&= - ||b_l - x_0||^2, i &= 1, ..., m \\
                    Xsi_i >&= 0, i &= 1, ..., m \\
                    Psi_i >&= 0, i &= 1, ..., k \\ 
                    z >&= 0 \\
\end{aligned}
$$
```
%Definiamo il vettore c
c = ones(numVar, 1);

% Cambiamo il valore di c dopo 1  a fine
for i=2:numVar
    c(i) = 1;
end

%Oppure definiamo C
%C = 10

%Moltiplichiamo tutto queli che ci serve con C
%c(2:numVar) *= C



%Definiamo la matrice A

A = zeros(numPoints, numVar);

for i=1:numPoints
    A(i, 1+i) = 1; %Per Xsi e Psi
    A(i, 1) = y(i);
end

%Definiamo il vettore b
b = zeros(numPoints, 1);

% Assegnazione dei valori
for i=1:numPoints
    b(i) = norm(X(i, :) - x0)^2 * y(i);
end

%Definiamo il vettore lb
lb = zeros(numVar, 1);

%Definiamo il vettore ub
ub = [];

%Definiamo il vettore ctype
ctype = repmat("L", 1, numPoints);

%Definiamo il vettore vartype
vartype = repmat("C", 1, numVar);

%Definiamo il vettore sense
sense = 1;

%Risoluzione del problema
[xopt, fopt, status, extra] = glpk(c, A, b, lb, ub, ctype, vartype, sense);

% Stampiamo una circonferenza partendo dal baricentro e con
%raggio pari
drawCircumference(x0, sqrt(fopt),'k');
waitforbuttonpress();
```


# Disegnare in 3D