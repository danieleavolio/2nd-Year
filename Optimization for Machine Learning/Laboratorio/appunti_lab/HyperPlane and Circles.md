# Lezione 3
## Iperpiani

Ricordiamo la formula dell'iperpiano:

$$
H(v,\gamma) = \{x | v^T x = \gamma\} \\

H(v,\gamma) = \{x | [v1 v2] \begin{bmatrix}
    x1 \\ 
    x2
\end{bmatrix} = \gamma\} 
\\

H(v,\gamma) = \{x | v1 x1 + v2 x2 = \gamma\} \\

H(v,\gamma) = \{x | x2 = \frac{\gamma - v1 x1}{v2}\} \\
$$


Immaginiamo voler prendere un iperpiano con $v = [2,5]$ e $\gamma = 6$.

**Punto 1:** Fissiamo il valore delle ascisse $x_1$

```octave
x1 = -5:0.1:5;
```

In questo caso, $x_1$ è un intervallo che va da -5 a 5 con passo 0.1, e non è un solo scalare.

**Punto 2**: Calcoliamo i **valori** delle ordinate $x_2$
    
```octave
x2 = (gamma - v(1) * x1) / v(2);
```

**Punto 3:** Plottare l'iperpiano

```octave
plot(x1, x2, 'k');
```

**Punto 4:** Trovare gli **iperpiani di supporto**

L'equazione va trovata. Dobbiamo andare a cambiare i valori di $x_2$ andando a fare un cambiamento su gamma.

```octave
gammaPlus = gamma + 1;
gammaMinus = gamma - 1;

x2Plus = (gammaPlus - v(1) * x1) / v(2);
x2Minus = (gammaMinus - v(1) * x1) / v(2);

plot(x1, x2Plus, 'r');

plot(x1, x2Minus, 'b');
```

Ricorda di inserire `hold on` prima di fare il plot degli iperpiani di supporto, altrimenti si perde il plot dell'iperpiano precedente.

![Iperpiano](https://i.imgur.com/RFBMxeW.png)

## Circonferenze

**Obiettivo:** Abbiamo una circonferenza centrata su $x_0 \ y_0$ con raggio $R$.

L'angolo $d \in [0, 2 \pi ]$ è l'angolo che va da 0 a 2 pi greco.

Per trovare dei punti all'inerno della circonferenza, dobbiamo trovare le ascisse e le ordinate dei punti.

Questo era l'esempio alla lavagna, ora su Octave a quanto pare stiamo facendo qualcos'altro.

**Punto 1:** Vogliamo disegnare una circonferenza $S(x_0, R)$.
    
```octave
%Centro
x0 = [0 3];

%Raggio 
R = 5
```

**Punto 2:** Vogliamo disegnare la circonferenza.

Per farlo, ci definiamo una funzione `drawCircumference` che prende in input il centro e il raggio e colore.

```octave

%Centro
x0 = [0 3];

%Raggio
R = 5

drawCircumference(x0,R,'k');

%In un altro file
function [] = drawCircumference(x0,R,color)
  theta = 0:2*pi/50:2*pi;
  xP = x0(1)+R*cos(theta);
  yP = x0(2) + R*sin(theta);
  plot(xP,yP,color);
 endfunction
```


**Domanda:** Come facciamo a disegnare anche le sfere di $S^+$ e $S^-$

**Risposta:** Dobbiamo cambiare il raggio. Ma in quale modo?

```octave

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
drawCircumference(x0,R+m,'r');

%circonferenza 3
drawCircumference(x0,R-m,'b');
```

![Circonferenze](https://i.imgur.com/23FMeae.png)



# Esercizio da fare

Given the dataset `toy.mat`: 

`Read` the dataset. 

`Draw` the points of the `two classes` (using two different colors). 

`Solve the linear model` that you have seen during the theoretical lecture to compute the separating hyperplane. 

Draw the `hyperplane` computed at step 3 together with the supporting hyperplanes `H+` and `H-`. 

Look at the values of the optimal solution that you have obtained. `Why did you obtain all those zeros`? What does they mean? 

**Soluzione step by step**

**Punto 1:** Leggere il dataset

```octave
load toy.mat
```

**Punto 2:** Disegnare i punti delle due classi

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

#Sto il minimo di tutte le righe per la prima colonna
xMin = min(X(:, 1));
#Sto il massimo di tutte le righe per la prima colonna
xMax = max(X(:, 1));

#Lo faccio anche per la Y
yMin = min(X(:, 2));
yMax = max(X(:, 2));

axis([xMin, xMax, yMin, yMax]);

hold on;
scatter(Plus(:, 1), Plus(:, 2), 'b', 'o', 'filled');

hold on;
scatter(Minus(:, 1), Minus(:, 2), 'r', 'o', 'filled');
```

![Punti](https://i.imgur.com/QTbELRj.png)

**Punto 3:** Ora dobbiamo risolvere il problema lineare che abbiamo visto durante le lezioni per calcoalre l'iperpiano di supporto.

![Problema lineare](https://i.imgur.com/GBKPnk5.png)

Possiamo usare la documentazione per trovare la funzione corretta. 
Usiamo comunque **glpk**.


### Spiegazione del prof

1. Calcoliamo il numero di punti e la dimensione

`[numPoints, dim] = size(X);`

2. Calcoliamo il numero di variabili 

`numVar = dim + 1 + numPoints;`

3. Dividiamo i punti in due classi

```
Positive = [];
Negative = [];

for i = 1 :numPoints
    if y(i) == 1
        Positive = [Positive; X(i, :)];
    else
        Negative = [Negative; X(i, :)];
    endif
end
```

4. Calcolare `m` e `k``

`
m = size(Positive, 1);
`

`
k = size(Negative, 1);
`


5. Inizio di progettazione del problema
    
```octave
%Vettore c

c = zeros(1,numVar);

for i = 1:numPoints
    if y(i) == 1
        c(dim+1+i) = 1/m;
    else
        c(dim+1+i) = 1/k;
    endif
end
```

**Motivazione di** `dim+1+i`: Alle prime posizioni ci sono i valori per gamma
e per il vettore v. Poi ci sono i valori per i punti.

6. Matrice A

```octave
A = zeros(numPoints, numVar);



for i=1:numPoints

    A(i, 1:dim) = X(i, :) * y(i); %per v^tai e vt bl
    A(i, dim+1) = -y(i); %per gamma
    A(i, dim+1+i) = 1; %per Xi e Psi

    %Possimao fare questa cosa
    ctype(i) = "L";
end
```

**Nota:** Se si usa un vettore non dichiarato, Octave lo crea automaticamente. Ok....

7. Vettore b

In questo caso abbiamo 1 constraint per ogni punto, quindi abbiamo `numPoints` righe.

```octave
b = ones(numPoints, 1);
```

8. Lower Bounds e Upper Bounds

Dai constraint, non abbiamo nessun `upper bound` sulle variabili. Li metteremo tutti ad infinito. Per il `lower bound` abbiamo tutto zero.  Manca solamente il `lower bound` per gamma e per v. Le prime tre saranno $-\infty$ 
```octave

ub = []; %inf
lb = zeros(numVar, 1); %0
lb(1:dim+1) = -inf; % -inf
```

9. Tipo di variabili

```octave
vartype = repmat("C", numVar, 1);
```

10. Tipo di vincoli

Abbiamo usato ctype(i) dentro la dichiarazione di $A$

11. Senso del problema

```octave
sense = 1;
```

12. Risoluzione del problema

```octave
[xopt, fopt, status, extra] = glpk(c, A, b, lb, ub, ctype, vartype, sense);
```

#### Continuo problema

13. 
Devo calcolare i valori di `v` e `gamma`.

```octave

v = xopt(1:dim);
gamma = xopt(dim+1);
```

14. Disegnare l'iperpiano

```octave


drawDataset(X, numPoints, y);

hold on;

drawHyperplane(X,v,gamma);

######################################################################
function retval = drawDataset (X, numPoints, y)

Plus = [];
Minus = [];

for i = 1: numPoints
    if y(i) == 1
        Plus = [Plus; X(i, :)];
    else
        Minus = [Minus; X(i, :)];
    endif
end

#Sto il minimo di tutte le righe per la prima colonna
xMin = min(X(:, 1));
#Sto il massimo di tutte le righe per la prima colonna
xMax = max(X(:, 1));

#Lo faccio anche per la Y
yMin = min(X(:, 2));
yMax = max(X(:, 2));

axis([xMin, xMax, yMin, yMax]);

hold on;
scatter(Plus(:, 1), Plus(:, 2), 'b', 'o', 'filled');

hold on;
scatter(Minus(:, 1), Minus(:, 2), 'r', 'o', 'filled');


endfunction

#######################################################

function retval = drawHyperplane (X, v, gamma)

  x1 = min(X(:,1)):0.1:max(X(:,1))


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
endfunction
```

15. Per quale motivo ci sono molti 0 nella soluzione?

Perché **non c'è errore**  nella separazione dei punti. Quindi, se andiamo a vedere la formula per le variabili come $\xi$ e $\psi$, vediamo che sono tutte 0.

$$

\xi_i = \max{0, 1 - v^T x_i} = 0 \\
\psi_i = \max{0, 1 + v^T x_i} = 0 \\

$$

Chiaramente, il massimo sarà sicuramente 0 poiché i valori sono 0.