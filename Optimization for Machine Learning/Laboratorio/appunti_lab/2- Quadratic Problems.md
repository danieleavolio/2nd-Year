# Lezione 2: Problemi Quadratici

## Definizione dle problema
Il problema è il seguente:

Il problema è quadratico perche:

- Ci sono variabili $x^2$
- Ci sono moltiplicazinoi tra variabili come $x_1x_2$


```octave 
%qp min_x  z = 6x1^2 +4x1x2 - 6x1x3 + 5x2^2 + 2x3^3 + 6x1 - 5x2 + 4x3

                2x1 + 4x2 -5x3   = 6
                3x1 -2x2 + 4x3  <= 8
                2x1 -3x2 +x3    >= 1
                          x3    <= 2
                x1              >= 0
```

$$
\min_{x}  z = 6x_1^2 +4x_1x_2 - 6x_1x_3 + 5x_2^2 + 2x_3^2 + 6x_1 - 5x_2 + 4x_3 \\

                2x_1 + 4x_2 -5x_3   = 6\\
                3x_1 -2x_2 + 4x_3  <= 8\\
                2x_1 -3x_2 +x_3    >= 1\\
                          x_3    <= 2\\
                x_1              >= 0\\
$$

Un problema quadratico ha solitamente forma:

$$
\frac{1}{2}x^T H_x + x^Tq \\
Ax = b\\
A_{lb} \leq A_in \leq A_{ub} \\
lb \leq x \leq ub
$$

## Analisi delle variabili

### $q$
Ora, considerando il nostro problema, cerchiamo di trovare la correlazione con le variabili. Partiamo da $q$.

**La parte lienare del problema:**
$$q^T = [6, -5, 4]$$

Infatti, se vediamo, quando non compare il quadrato nella funzione obiettivo 
abbiamo proprio i valori specificati sopra.

### $H$

Ora concentriamoci su $\tilde{H}$.

$$\tilde{H} = \begin{bmatrix}
    x1\cdot x1 & x1\cdot x2 & x1\cdot x3 \\
    x2\cdot x1 & x2\cdot x2 & x2\cdot x3 \\
    x3\cdot x1 & x3\cdot x2 & x3\cdot x3 \\
\end{bmatrix}$$

$$\tilde{H} = \begin{bmatrix}
    6 & 4 & -6 \\
    0 & 5 & 0 \\
    0 & 0 & 2 \\
\end{bmatrix}$$

Questa matrice, se vediamo al problema, c'è $\frac{1}{2}$. Quindi, per ottenere $H$ dobbiamo moltiplicare per 2.

$$ H = \tilde{H} \cdot 2 $$

$$ H = \begin{bmatrix}
    12 & 8 & -12 \\
    0 & 10 & 0 \\
    0 & 0 & 4 \\
\end{bmatrix}$$

Questa cosa, però, **non può essere fatta per problemi più complessi.** Dobbiamo trovare un altro modo.

#### $H$ con $\nabla$

Dal punto di vista teorico, la $H$ è la **Hessian** della funzione. Per avere questa Heissan, dobbiamo prima calcolare il gradiente.

Calcoliamo il gradiente della funzione:

$$ \nabla f = \begin{bmatrix}
        12x_1 + 4x_2  - 6x_3 + 6 \\ 
        4x_1 + 10x_2 - 5 \\
        -6x_1 + 4x_3 4 \\
    \end{bmatrix}
$$

Avendo il gradiente, possiamo calcolare ora la Heissan.

$$

H = \begin{bmatrix}
    12 & 4  &-6 \\
    4  & 10 &0 \\
    -6 & 0  &4 \\
\end{bmatrix}

$$

Cosa notiamo?
$$
\tilde{H} \cdot 2 = H_1 = \begin{bmatrix}
    12 & 8 & -12 \\
    0 & 10 & 0 \\
    0 & 0 & 4 \\
\end{bmatrix}
$$

$$ Heissan \ H_2 = \begin{bmatrix}
    12 & 4  &-6 \\
    4  & 10 &0 \\
    -6 & 0  &4 \\
\end{bmatrix}$$

- Non sono uguali
- La matrice Heissan è simmetrica
- Usando 1 o l'altra **ottengo lo stesso risultato**

Se passiamo al solver la matrice **non simmetrica** ci darà un warning. Questo perchè il solver, per risolvere il problema, usa un metodo che sfrutta la simmetria della matrice. Infatti, la matrice verrà convertita.

### $A$

Ora, passiamo ad $A$. Quante righe avremo? $1$. Abbiamo solamente **1 vincolo di uguaglianza**.

$$
A = \begin{bmatrix}
    2 & 4 & -5 \\
\end{bmatrix}
\\

b = \begin{bmatrix}
    6 \\
\end{bmatrix}
$$

E ora calcoliamo $A_{in}$ che sarebbero i vincoli di disuguaglianza.

$$ 

A_{in} = \begin{bmatrix}
    3 & -2 & 4 \\
    2 & -3 & 1 \\
\end{bmatrix}
$$

Ora, calcoliamo $A_{lb}$ e $A_{ub}$.

Abbiamo un vettore che ha un numero di righe pari al numero di righe
di $A_{in}$.

$$

A_{lb} = \begin{bmatrix}
    -\infty \\
    1 \\
\end{bmatrix}

A_{ub} = \begin{bmatrix}
    8 \\
    \infty \\
\end{bmatrix} 

$$

### $b$

Quando compare $-\infty$ o $\infty$ vuol dire che non abbiamo valore di $lowerbound$ o $upperbound$ per quel vincolo.

Passiamo a lowerbound e upperbound per le varaibili.

Iniziamo con $b_{lb}$. Il numero di componenti viene dato dal **numero di variabili**. In questo caso $3$

$$

b_{lb} = \begin{bmatrix}
    0 \\
    -\infty \\
    -\infty \\
\end{bmatrix}

\\

b_{ub} = \begin{bmatrix}
    \infty \\
    \infty \\
    2 \\
\end{bmatrix}
$$

Abbiamo tutti gli elementi ora per risolvere il problema con $Octave$

## Octave code

```octave


# Quadratic problem

# Si può specificare anche un x0 da dove partire. Non lo faremo.
#Dove c'è [] vuol dire vuoto
#[x, obj] = qp ([], H, q, A, b, lb, ub, A_lb, A_in, A_ub)


#H


H = [12 4 -6;
     4 10 0;
     -6 0 4;];

q = [6 -5 4];

A = [2 4 -5];

b = [6];

Ain = [ 3 -2 4;
        2 -3 1];
Alb = [-inf; 1];

Aub = [8; inf];

blb = [0 -inf -inf];

bub = [inf inf 2];

[x, obj] = qp ([], H, q, A, b, lb, ub, A_lb, A_in, A_ub);

printf("x = %f\n", x);

```

## Problema 2

Traccia:

$$

\min_x z = 3x_1^2  + 5x_2^2     + 3x_3^2 +          6x_1x_2 + 4x_1x_3    + 2x_2x_3
-          x1      + 10x_2      -3x_3
\\
8x_1 - 6x_2 + 4x_3 \leq 7\\
11x1 +5x2 +4x3 = 2\\
6x1 + 7x2 + 9x3 \geq 1\\
-2 \leq x2 \leq 7
$$

Ora facciamo il codice octave:

```octave
clear;


H = 2 * [3 6 4;
         0 5 2;
         0 0 3];

q = [-1 10 -3];

A = [ 11 5 4];

b = [2];

A_in = [8 -6 4;
       6 7 9];

A_lb = [-inf; 1];

A_ub = [7; inf];

lb = [-inf; -2; -inf];

ub = [inf; 7; inf];

[x, obj] = qp ([], H, q, A, b, lb, ub, A_lb, A_in, A_ub)

#Open file in write
fid = fopen("output.txt", "w");

#Write the result
fprintf(fid, "Found solution x = %f %f %f with objective %f\n", x, obj);
```

TL-DR: Se hai $x1x2$ e non hai $x2x1$, allora in $H$ $x2x1$ lo metti $= 0$

# Plotting su Octave

Plottare -> Mostrare i punti

```octave

clear;

load toy.mat; #Caricare i dati da un dataset 

#Calcolare la dimensione della matrice
[numPoints, dim] = sixe(X); #numPoints + sono le righe, dim sono le colonne
```

Vogliamo dividere i punti in **positivi** e **negativi**. Per farlo, dobbiamo creare due vettori vuoti.

```octave

Plus = [];
Minus = [];

for i = 1: numPoints
    if y(i) == 1
        Plus = [Plus; X(i, :)];
    else
        Minus = [Minus; X(i, :)];
    endif
end

```

Mi serve anche conoscere il **range** delle $X$ e $Y$.

```octave
#Sto il minimo di tutte le righe per la prima colonna
xMin = min(X(:, 1));
#Sto il massimo di tutte le righe per la prima colonna
xMax = max(X(:, 1));

#Lo faccio anche per la Y
yMin = min(X(:, 2));
yMax = max(X(:, 2));
```

Usiamo **axis** per costruire l'asse. Gli passiamo un vettore con i valori di $x$ e $y$.

```octave
axis([xMin, xMax, yMin, yMax]);
```

**Keyword:** hold on
Scrivendo hold on si eviterà che il grafico precedente venga cancellato.

```octave
hold on;
```

E ora si plotta!

```octave

scatter(Plus(:, 1), Plus(:, 2), 'r', 'o', 'filled');
scatter(Minus(:, 1), Minus(:, 2), 'b', 'x', 'filled');

```

