# Progetto AGT

Abbiamo un `grafo`. Abbiamo 2 nodi `s` e `t` e nodi interni numerati $1,2,3,...,n$. Questi nodi sono gli agenti. `s` e `t` non fanno parte del gioco

Il grafo `indiretto`. Le connessioni tra i nodi definiscono un grafo indiretto standard.

## La domanda principale

Vogliamo trovare una `connessione` tra `s` e `t`. Con connessione si intende un `path` tra `s` e `t` che passi tra gli `agenti`.

Esempi di path:
- `s` $\rightarrow$ `1` $\rightarrow$ `2` $\rightarrow$ `t`

Non consideriamo i path che `non sono semplici`, quindi tutti i `path che hanno nodi ripetuti non sono accettati.`

## Le varie domande

### Domanda 1
Per ogni agente $i$ che viene usato, va divisa una cifra tipo $100\$$.

`Come li distribuisco in modo equo?` Quindi in base alla loro capacita' di contribuire alla connessione.

Voglio incentivare `collaborazione`. In pratica, **`Shapley Value`**

v(c) =

- 100$ se $c$ garantisce la connessione tra `s` e `t`
- 0$ altrimenti

`Noi:` Dobbiamo fornire una soluzione ad un grafo che ha un numero di nodi fissato. 
Il risultato va `commentato`. Se riesci, cerca di capire come si evolve questo shapley value in base alle specifiche topografie del grafo. (Preferirei evitare, grazie)

### Domanda 2
Assumiamo un setting differente.

Ogni agente puo' `decidere` se essere in un path oppure no. Praticamente, ogni agente e' `strategico`.

$i \in N$  e decide se $yes$ o $no$.

Gli agenti hanno questo modo di pensare:

Un agente $i$ `collabora` $\iff$ al massimo altri 2 agenti vicini a lui collaborano.

`Nota:` $s$ e $t$ hanno di base $yes$ come label.

La `domanda`: Esiste un `equilibrio di nash`? (puro)

`Quali sono` gli equilibri di nash puri e `quali sono le proprieta'` di essi.

`Noi:` Dobbiamo fornire una soluzione ad un grafo che ha un numero di nodi fissato.

### Domanda 3

Una variante di quello di prima.

Dobbiamo `modificare` il codice di prima sapendo che il grafo ha `tree width limitata`.

Dobbiamo sempre calcolare un `equilibrio di nash puro`.

Dobbiamo fare un algoritmo, si. Ma se abbiamo capito come funziona, possiamo
anche direttamente `spiegare` il funzionamento avendo questo vincolo della `tree width limitata`.

### Domanda 4

Ogni agente in questo gioco ha `un'utilita interna` per essere selezionato nel path.

L'utilita' interna viene calcolata in questo modo:

$u_i = i \times 10\$$

Queste sono `interne` e `non dichiarate` e gli `agenti possono cheattare`.

Ad esempio, un path che contiene tutti e' quello che ha utilita' massimizzata.

`Domanda:` Voglio un `payment scheme` per definire un path con social welfare massima (massimizzare la somma delle utilita' dichiarata) e voglio che loro siano `onesti` e che dicano la verita'.

Cercare di capire cosa otteniamo in questo caso.


# Come si presenta

- Si definisce in `python` e quindi bisogna avere gli script su:
  - Shapeley Value
  - Nash Equilibrium
  - Nash Equilibrium con Tree Width Limitata
  - Payment Scheme

*Avremo dei grafi specifici per testare gli script.*

- Creare un `Report` dove commentiamo i risultati ottenuti e le nostre considerazioni. Dove spieghiamo le proprieta' dei giochi e di mostrare le cose piu' importanti che ci sono nei problemi.

- Tempo di consegna: Se si consegna $7$ giorni prima, allora ci sara' poca discussione. Se si consegna $2$ giorni prima, ovviamente ci sara' piu da parlare.


# Ricevimento con Sebastiano

Come conviene strutturare il report.

- Presentazione problema
- Possibile soluzione
  - Se ci sono più soluzioni, descrivere entrambe
  - Analizzare le soluzioni dal punto di vista di complessità
- Risultati ottenuti

*Usare unità di misura in modo coerente*: Se usi *secondi* non usare *minuti* da altre parti.

## Analisi dei singoli punti 

### Domanda 1
Calcolare shapley value con gli agenti $1,2,...,n$ in base agli agenti che 
sono stati usati per la connessione per i nodi $s$ e $t$.

Dobbiamo considerare i giochi dove questo path da $s$ a $t$ si forma.

Probabilmente la cosa migliore da fare inizialmente è trovare tutti i `path` che vanno da $s$ a $t$. 

`Centrality Measures` possono essere importanti. Ad esempio, la `degree`. Oppure la `PageRank`.

Provare a rappresentare il grafo come path, per poi controllare quale di questi sono possibili. 

Alla fine vogliamo calcolare il `Shapley Value` per ogni nodo, perche' assumiamo che ogni nodo voglia collaborare.

- Calcoli tutti i path possibili e li enumeri
- Per ogni path, (Lo vediamo come una coalizione) calcoli gli shapley value per ogni nodo
  - Nota che c'è una simmetria se consideriamo i nodi nei path validi.
  - (1,2) e (2,1). Siccome il valore il valore dei dollari viene dato al nodo che arriva alla fine $t$, alla fine praticamente abbiamo un 50/50 per i nodi.
  - Abbiamo quindi già il valore delle permutazioni.
- Alla fine abbiamo tutte le possibili coalizioni. Sappiamo il valore di ogni coalizione. Usiamo la formula dello shapley value per calcolare il valore di ogni nodo.

Se riesci a trovare una struttura in questo problema, prova ad applicarla. `Prima` risolviamo però in modo banale, quindi con il metodo più semplice.

### Domanda 2
Ogni agente può decidere se essere in un path oppure no. L'agente entra nel path (oppure possiamo dire che `ENTRA IN UNA COALIZIONE`) se al massimo 2 dei suoi agenti vicini sono in quella coalizione.

Dobbiamo controlalre se il risultato ammette un equilibrio di nash puro.

Si sta parlando di enumerare gli equilibri, e sappiamo che gli unici nodi che decidono di non stare nel path sono quelli che almeno 3 vicini. Gli altri, stanno nel chilling.

Praticamente, dobbiamo capire se c'e' una situazione di equilibrio e stamparla a quanto pare.

Pare che un equilibrio sia: 
- [2,4,3] In, [1] Not In
- [1,2,3] In, [4] Not In

### Domanda 3
Assumiamo che G abbia treewidth limitata da qualche constraint. Rispondi alla domand 2 con questo vincolo.

Simile al problema del maximum independent set che abbiamo fatto con la tree decomposition.

- Calcolare tree decomposition del grafo
- Calcolare equilibrio locale per ogni bag
- Quando una bag si mergia con quelle piu grandi nell'albero bisogna risolvere i possibili conflitti


### Domanda 4
Dobbiamo comunque fare un path da `s` a `t` e ogni nodo `puntera'` dei soldi (`indice nodo` * 10) per essere in questo path. Noi abbiamo a disposizione il valore `dichiarato` del nodo, ma non sappiamo il valore `vero`.

Vogliamo quindi fare un'`asta` e poi calcolare un path che massimizzi il valore dichiarato e calcolare un `payment scheme` che sia `truthful`.

Consideriamo:

- [3,4] = 70
- [1,2] = 30
- [1,2,3,4] = 100

Vogliamo il path con il valore massimale. C'e' una struttura in questo problema che permette di farlo senza calcolare tutti i path possibili? Pare di si.

`Hint`: Posso pesare questo grafo? Perdo qualcosa? 
Forse diamo come peso dell'arco il valore del nodo destinazione e poi calcoliamo il path con dijkstra al contrario.