# Lezione 1

### Dichiarare variabili

```octave
num = 5
num2 = 2

sum = num+num2
power = sum^2
```

### Variabili particolaroi
    
```octave
inf_var = inf
nan_var = nan
```

### Casi Particolari

```octave

1/inf_var --> 0
5/0 --> inf
```

**Nota**: Per pulire le variabili --> **clear**

### Salvare dati in dataset

```octave

a = 5
b = 7

save data.mat
```

### Caricare dati da dataset

```octave

load data.mat
```

### Salvare 1 sola variabile

```octave

save data2.mat a
```

### Definizione di vettore

```octave
v = [1 2 3 4 5]

Result:
vector =

   1   2   3
```

Questo crea un vettore $R^{1x5}$

### Definizione di vettore verticale

```octave
v = [1; 2; 3; 4; 5]

Result:
vector2 =

   1
   2
   3

```
### Somma di vettori

```octave

v = [1; 2; 3; 4; 5]
v2 = [1; 2; 3; 4; 5]

v+v2

Result:
ans =

   2
   4
   6
   8
  10
```


#### Cosa succede se facciamo una moltiplicazione tra due vettori cosi?

```octave

v = [1 2 3]
v2 = [2 3 4]

v*v2

Result error: operator *: nonconformant arguments (op1 is 1x3, op2 is 1x3)
```

Per risolvere, si fa cosi:

```octave

v = [1 2 3]
v2 = [2 3 4]

v * v2'

ans = 20

----------------------
Oppure

dot(v,v2)

ans = 20
```

### Norm

```octave

v = [1 2 3]

norm(v)

ans = 3.7417
```

Questo ci da la norma 2 del vettore

### Matrici

```octave

m = [1 2 3; 4 5 6; 7 8 9]

Result:

m =

   1   2   3
   4   5   6
   7   8   9
```

**Per accedere alle posizioni:**

```octave
m(1,2)

    ans = 2

m(2,3)

    ans = 6
```

**Accedere ad una riga intera**
    
```octave
m(1,:)

ans =

    1   2   3
```

**Accedere ad una sola colonna**

```octave

m(:,1)

ans =

   1
   4
   7
```


**Stampare una sotto-matrice**

```octave

m(1:2,1:2)

ans =

   1   2
   4   5
```

**Moltiplicazione tra matrici**

```octave

m_1 = [1 2 3; 4 5 6];
m_2 = [0 1 2; 4 2 -1];

m_1 * m_2

ans = Errore

```
**Spiegazione**: Le dimensioni non sono compatibili. Il **numero di righe** della seconda matrice deve combaciare con il **numero di colonne** della prima matrice.


**Fix**: Usare la trasposta della seconda matrice.
```octave

m_1 * m_2'

ans =
    8    8   -1
   20   23    2
```

**Somma matrici**

```octave

m_1 = [1 2 3; 4 5 6];
m_2 = [0 1 2; 4 2 -1];

m_1 + m_2

ans =

   1   3   5
   8   7   5
```

**Moltiplicazione per scalare**

```octave

m_1 = [1 2 3; 4 5 6];
m_2 = [0 1 2; 4 2 -1];

m_1 * 2

ans =

    2    4    6
    8   10   12
```

### Inversa della matrice
    
```octave

m_1 = [2 1; 2 1]

inv(m_1)

ans = Warning: La matrice non è invertibile

Inf Inf
Inf Inf

```

Erano linearmente dipendenti, quindi non invertibili. Infatti, se controlliamo il **determinante**

```octave

det(m_1)

ans = 0
```

**Nota**: Se la matrice è quadrata, e il determinante è 0, allora la matrice non è invertibile.

Matrice Invertibile:

```octave

a = [2 4 5; 1 2 3; 0 1 2]

inv(a)

ans =
  -1   3  -2
   2  -4   1
  -1   2   0


a * inv(a)

  ans =

   1   0   0
   0   1   0
   0   0   1
```

**Inizializzazione delle matrici**

*Identità*
```octave

a = eye(3)

ans =

   1   0   0
   0   1   0
   0   0   1

Ci fornisce una matrice identità 3x3
```

*Zeros*

```octave

a = zeros(3,3)

ans =

   0   0   0
   0   0   0
   0   0   0
```

*Ones e Scalari generici*

```octave

a = ones(3,3)

ans =

   1   1   1
   1   1   1
   1   1   1
```

```octave

a = 5 * ones(3,3)

ans =

   5   5   5
   5   5   5
   5   5   5
```

**Minimi della matrice**

```octave

a = [1 2 3; -4 5 6; -7 8 9]

min(a)

ans =

  1   -4   -7
```

Banalmente, ci da il minimo di ogni **colonna**.

Minimo dei minimi della matrice
    
```octave

a = [1 2 3; -4 5 6; -7 8 9]

min(min(a))

ans =

  -7
```

Per il massimo, è la stessa cosa.


<!-- Centra il testo -->
<div align="center" style="color:red">
<h2> Fine introduzione</h2>
</div>

# Programmazione effettiva

## Funzioni

Per creare un nuovo file con una nuova funzione: 

```octave

edit nome_funzione.m
```


```octave

function retval = sum_and_diff (input1, input2)

sum = input1 + input2

diff = input1 - input2

retval = [sum diff]

endfunction

<!-- Nel file principale -->

sum_and_diff(5,2)

ans =

   7   3

```

### Scrivere su file

```octave

file = fopen('file.txt', 'w')

result = sum_and_diff(a,b)

fprintf(file, "The sum is equal to %i, the difference is equal to %i", result(1), result(2))

fclose(file)
 
```

# Problem Solving

## Linear Program
### Problema 1

```octave
min z = 2x1 + 3x2 -4x3
        4x1 + 5x2 -6x3  <= 5
        6x1 + 5x2 + 4x3 >= 9
        x1  - x2  + 3x3  = 6
        x1,        , x3 >= 0

%[xmin, fmin, status, extra] = ...
%glpk (c, A, b, lb, ub, ctype, vartype, sense); 

%Vettore costi
c = [2;3;-4];

%Matrice dei vincoli
A = [ 4 5 -6;
      6 5 4;
      1 -1 3];

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
%Oppure
vartype = repmat("C", 1, 3);
%Dove C = Continuo, B = Binario, I = Intero.
%In questo caso, siccome non abbiamo specificaot niente nel problema, le variabili sono continue.

%Senso del problema
sense = 1;
%Dove 1 = Minimizzazione, -1 = Massimizzazione

%Risoluzione del problema
[xopt, fmin, status, extra] = glpk (c, A, b, lb, ub, ctype, vartype, sense); 

%Scrivere soluzione su file

file = fopen('soluzione.txt', 'w')

fprintf(file, "La soluzione ottima x = [%f %f %f] e il valore ottimo di f* = %f", xopt(1), xopt(2), xopt(3), fmin)

fclose(file)

```

### Problema 2

```octave

%min x max {3x1 -4x2 -7x3, - 9x1 -2x2 + 4x3}
%           
%           2x1 + 5x2 - x3  = 4
%           6x1 + 6x2 - x3 >= 2
%           4x1 + 5x2 +2x3 <= 10
%                  x2      >= 0
```

Il problema però non può essere risolto come prima, perché è un problema
**non lineare**. Bisogna costruire il problema in modo che sia lineare.

```octave

%min x,v     v
%
%            v >= 3x1 -4x2 -7x3
%            v >= -9x1 -2x2 + 4x3
%
%            2x1 + 5x2 - x3  = 4
%            6x1 + 6x2 - x3 >= 2
%            4x1 + 5x2 +2x3 <= 10
%                   x2      >= 0
```

Adesso il problema che abbiamo è un problema lineare e può essere risolto
usando **grpk**.

Però, come specifichiamo che v è una variabile? 

```octave

%Vettore costi
c = [1];

%Matrice dei vincoli
A = [3 -4 -7;
     -9 -2 4;
     2 5 -1;
     6 6 -1;
     4 5 2;
     0 1 0];

%Constraint a destra
b = [0;0;4;2;10;0]

%Lower Bound (Non deve essere un column array)
lb = [0 -inf 0]

%Upper Bound (Non deve essere un column array)
ub = [inf inf inf]

%Vettori per i tipi di vincoli
ctype = ["U", "U", "S", "U", "L", "U"];

%Vettori per i tipi di variabili
vartype = ["C", "C", "C"];

%Senso del problema
sense = 1;

%Risoluzione del problema
[xopt, fmin, status, extra] = glpk (c, A, b, lb, ub, ctype, vartype, sense);

%Scrivere soluzione su file

file = fopen('soluzione2.txt', 'w')

fprintf(file, "La soluzione ottima x = [%f %f %f] e il valore ottimo di f* = %f", xopt(1), xopt(2), xopt(3), fmin)

fclose(file)
```

**La soluzione ottima x = [-7.078431 1.196078 0.562092] e il valore ottimo di f* = -7.078431**


### Problema 3

```octave
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
```
**Nota:** Siccome il problema era di max min, abbiamo invertito i segni dei vincoli dove compare $v$.

```octave
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

%Constraint a **destra**
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
```

**La soluzione ottima x = [4.333333 1.166667 -1.666667] e il valore ottimo di f* = 4.333333**
