# `Sqoop Cheat Sheet`

**Cosa è `Sqoop`**: Sqoop è uno strumento che consente di trasferire dati tra `Hadoop`  e database relazionali strutturati come `MySQL`, Oracle, PostgreSQL, ecc.

Ad esempio, supponiamo di avere un grande database di vendite in un sistema MySQL e si desidera analizzare i dati utilizzando Hadoop. Invece di scrivere un codice personalizzato per estrarre i dati e importarli in Hadoop, si può utilizzare Sqoop per fare il lavoro. Sqoop estrarrà i dati dal database MySQL e li importerà in Hadoop (ad esempio, in `HDFS` o `Hive`).

Allo stesso modo, una volta completata l'analisi in Hadoop, si potrebbe voler esportare i risultati nel database `MySQL` per poterli utilizzare in altre applicazioni. Anche in questo caso, si può utilizzare `Sqoop` per esportare i dati da Hadoop al database `MySQL`.

In sintesi, Sqoop è uno strumento che `semplifica il trasferimento di dati tra Hadoop e i database relazionali strutturati.`

## `Overview Generale`

`Sqoop Client`: Quello che configuriamo noi. 

Fetcha metadati dalla tabella del RDBMS, e genera un job MapReduce che viene eseguito da YARN.

I comandi principali sono:
- `import`: Importa dati da un RDBMS ad HDFS
- `export`: Exporta dati da HDFS ad un RDBMS

**Nota importante:** La clausola **`where`** avrà un particolare modo di essere formata per fare in modo che ogni mapper si occupi di una porzione di dati.

*Versione che usiamo:* [Sqoop 1.4.7](https://archive.apache.org/dist/sqoop/1.4.7/)

**Nota:** Sqoop si installa sulla `macchina client`, e <span style="color:red">non sul cluster</span>

**Librerie richieste**

- `commons-lang`-2.6: Used internally by sqoop
- `mysql-connector`: Used to communicate with mysql server (this could be different depending on the RDBMS).
- `hive-common`:
Used to import data into hive

Queste librerie vanno aggiunte nella cartella lib di sqoop. 

Questi file li troviamo nella directory *lib* di **hive**. Possiamo copiarli da lì.

## `Import`

**Dal Client:**

```sql
hadoop@Lovaion:~$ mysql -u hive -p -h master
```

Questo comando ci permette di entrare nel database mysql che abbiamo creato in precedenza con `utenza` hive e `password` hive. `-h` indica l'host.



### Spiegazione di alcuni parametri:

`--target-dir` "nome_dir": Specifichiamo il nome della directory in cui l'output del mapreduce verrà salvato dopo che sqoop finisce.

`--warehouse-dir` "nome_dir": Indica la directory in cui verranno salvato più tabelle che vogliamo importare.

`--columns`: Indica le colonne che vogliamo importare.

`--where`: Indica la condizione che vogliamo usare per importare i dati.

### Import Incrementale

```
sqoop import --connect jdbc:mysql://master:/sqooptest --username hive -P --table cities --incremental append --check-column id --last-value 0
```

sqoop import: Questo avvia il processo di importazione da un database relazionale a Hadoop.

- `--connect jdbc:mysql://master:/sqooptest:` Questo specifica l'URL di connessione JDBC per il database sorgente. In questo caso, si tratta di un database MySQL situato nell'host denominato "master" e il nome del database è "sqooptest".

- `--username hive -P:` Questo è il nome utente per la connessione al database. L'opzione -P ti chiede la password, che è un'alternativa più sicura all'inclusione della tua password direttamente nel comando.

- `--table cities: `Questo specifica la tabella ("cities") da cui verranno importati i dati.

- `--incremental append: `Questa opzione viene utilizzata per le importazioni incrementali, che sono importazioni eseguite più volte per recuperare un sottoinsieme di righe da una tabella. La modalità append significa che verranno importate solo le nuove righe (quelle con un valore id superiore a quello dell'importazione precedente).

- `--check-column id:` Questa è la colonna che Sqoop utilizza per identificare le nuove righe per l'importazione incrementale. In questo caso, è la colonna "id".

- `--last-value 0:` Questo è il valore massimo della colonna di controllo dall'importazione precedente. Per la prima importazione, questo valore è tipicamente 0. Per le importazioni successive, Sqoop importerà le righe con un valore id superiore a questo "ultimo valore".

In sintesi, questo comando importa nuove righe dalla tabella "cities" nel database "sqooptest" situato nell'host "master", utilizzando una modalità di importazione incrementale che aggiunge nuove righe basandosi sulla colonna "id".


*Nota:* Non ci son target-dir. In questo caso sqoop fa l'append sulla cartella in cui abbiamo già importato i dati.

---
### Paritcular Condition

```bash
sqoop -import --connect jdbc:mysql://master:/sqooptest --username hive -P --query 'select id,name,surname, course_name,dep_name from student join (course join department on dep=dep_id) on course = course_id where $CONDITION' --split-by id --target-dir allStudents
```
La `$CONDITION` nel comando Sqoop è un segnaposto che Sqoop sostituisce automaticamente durante l'esecuzione della query. È obbligatorio quando si utilizza l'opzione `--query` in Sqoop.

Quando Sqoop esegue un'importazione, divide il carico di lavoro in più attività parallele per migliorare l'efficienza. Per fare ciò, ha bisogno di sapere come suddividere i dati. L'opzione `--split-by` indica la colonna da utilizzare per dividere i dati, ma Sqoop ha anche bisogno di sapere quali righe assegnare a ciascuna attività.

Ecco dove entra in gioco `$CONDITION`. Sqoop sostituisce `$CONDITION` con condizioni che limitano le righe a un sottoinsieme specifico per ciascuna attività. Ad esempio, se stai dividendo per una colonna di ID e hai 4 attività, Sqoop potrebbe sostituire `$CONDITION` con `id >= 1 AND id <= 250` per la prima attività, `id > 250 AND id <= 500` per la seconda, e così via.

In sintesi, `$CONDITION` è un segnaposto che Sqoop utilizza per dividere i dati in sottoinsiemi gestibili per l'importazione parallela.

Quel $CONDITION viene sostituito da sqoop. **E' obbligatorio**

```bash
sqoop -import --connect jdbc:mysql://master:/sqooptest --username hive -P --query 'select id,name,surname, course_name,dep_name from student join (course join department on dep=dep_id) on course = course_id where $CONDITION and course_name ="ComputerScience ' --split-by id --target-dir allStudentsCS
```

Se servono più condizioni, basta aggiungerle con **AND**.


### Importare più tabelle

```bash
sqoop import-all-tables --connect jdbc:mysql://master:/sqooptest --username hive -P --warehouse-dir distributed_sqoop_test --exclude-tables cities
```

Questo fa partire diversi sqoop jobs, uno per ogni tabella.

## Export

Per fare export, dobbiamo prima di tutto specificare la tabella in cui vogliamo inserire i dati.

```bash

hdfs dfs -mkdir t_fold
hdfs dfs -put data t_fold

create table table_(id int primary key, name varchar(255));

sqoop export --connect jdbc:mysql://master:/sqooptest --username hive -P --table table_ --export-dir t_fold
```

Ecco una spiegazione dettagliata:

- `hdfs dfs -mkdir t_fold`: Questo comando crea una nuova directory chiamata "t_fold" nel file system distribuito di Hadoop (HDFS).

- `hdfs dfs -put data t_fold`: Questo comando carica il file o la directory locale chiamata "data" nella directory "t_fold" in HDFS.

- `create table table_(id int primary key, name varchar(255));`: Questo è un comando SQL per creare una nuova tabella chiamata "table_" con due colonne: "id" di tipo intero, che è anche la chiave primaria, e "name" di tipo varchar(255).

- `sqoop export --connect jdbc:mysql://master:/sqooptest --username hive -P --table table_ --export-dir t_fold`: Questo è un comando Sqoop che esporta i dati da Hadoop a un database relazionale. In particolare, esporta i dati dalla directory "t_fold" in HDFS alla tabella "table_" nel database MySQL "sqooptest" situato nell'host "master". L'opzione `-P` chiede la password per l'utente "hive".

In sintesi, questi comandi creano una directory in HDFS, caricano i dati in essa, creano una tabella in un database MySQL e poi esportano i dati da HDFS a questa tabella utilizzando Sqoop.

**Nota:** Quando si fa un sqoop export, bisogna essere consapevoli che i dati che si importano all'interno del database devono essere coerenti con lo schema della tabella in cui verranno inseriti i dati.


## `Alcunie esempi del Prof`

```sql
CREATE DATABASE sqooptest
CREATE TABLE `cities` (
  `id` INTEGER UNSIGNED NOT NULL,
  `country` VARCHAR(50),
  `city` VARCHAR(150),
  PRIMARY KEY (`id`)
);

INSERT INTO `cities`(`id`, `country`, `city`) VALUES (1, "USA", "Palo Alto");
INSERT INTO `cities`(`id`, `country`, `city`) VALUES (2, "Czech Republic", "Brno");
INSERT INTO `cities`(`id`, `country`, `city`) VALUES (3, "USA", "Sunnyvale");
```
Qui si è creato un database con una tabella `cities` che ha 3 colonne: `id`, `country`, `city`.


```bash
sqoop import --connect jdbc:mysql://master/sqooptest --username hive -P --table cities
```

In questo caso i `dati` verranno salvati in una cartella chiamata `cities` all'interno della cartella in cui siamo.

---
```bash
sqoop import --connect jdbc:mysql://master/sqooptest --username hive -P --table cities --target-dir target_cities
```

In questo caso i dati verranno salvati in una cartella chiamata `target_cities` all'interno della cartella in cui siamo.

---
```bash
sqoop import --connect jdbc:mysql://master/sqooptest --username hive -P --table cities --warehouse-dir sqoop_warehouse
```


- `--target-dir`: Questa opzione consente di specificare la directory esatta in cui i dati importati saranno salvati. Se la directory specificata esiste già e contiene file, Sqoop terminerà con un errore. Se la directory non esiste, Sqoop la creerà.

- `--warehouse-dir`: Questa opzione consente di specificare una directory "magazzino" in cui Sqoop creerà una nuova sottodirectory per ogni tabella importata. Il nome della sottodirectory sarà il nome della tabella da cui i dati sono stati importati. Se la directory del magazzino non esiste, Sqoop la creerà.

In sintesi, `--target-dir` è utilizzato quando si desidera un controllo preciso sulla directory di destinazione, mentre `--warehouse-dir` è più conveniente quando si importano molte tabelle e si desidera che ciascuna abbia la sua directory.

---
```bash
sqoop import --connect jdbc:mysql://master/sqooptest --username hive -P --table cities --columns id,city --target-dir citiesNoCountry
```

In questo caso, si importano solo le colonne `id` e `city` della tabella `cities`.

---
```bash
sqoop import --connect jdbc:mysql://master/sqooptest --username hive -P --table cities --where 'country="USA"' --target-dir USA_cities
```

In questo caso, si importano solo le righe in cui il valore della colonna `country` è `USA`.

---

```bash
insert into cities values (4,"Italy","Milan"),(5,"Italy","Rome");


sqoop import --connect jdbc:mysql://master/sqooptest --username hive -P --table cities --incremental append --check-column id --last-value 3
```

`--last-value 3`: Questo è il valore massimo della colonna di controllo dall'importazione precedente. Per la prima importazione, questo valore è tipicamente 0. Per le importazioni successive, Sqoop importerà le righe con un valore id superiore a questo "ultimo valore". 


```bash

create table sparseTable (id int primary key, c1 varchar(255), c2 int);
insert into sparseTable values (1,"v1",NULL),(2,NULL,1),(3,"v1",1),(4,NULL,NULL);


sqoop-import --connect jdbc:mysql://master/sqooptest --username hive -P --table sparseTable --target-dir emptyString --null-string ''
sqoop-import --connect jdbc:mysql://master/sqooptest --username hive -P --table sparseTable --target-dir nullvalues --null-string '' --null-non-string '-1'

```
Ecco cosa fanno questi comandi:

1. Le prime due righe sono comandi SQL che creano una tabella chiamata `sparseTable` e inseriscono alcuni valori al suo interno. Alcuni di questi valori sono `NULL`.
2. La terza riga è un comando Sqoop import. Importa i dati da un database MySQL situato in `jdbc:mysql://master/sqooptest` in Hadoop. Il nome utente per il database è `hive`, e `-P` richiede la password. La tabella che viene importata è `sparseTable`, e i dati vengono memorizzati in una directory Hadoop chiamata `emptyString`. L'opzione `--null-string ''` specifica che i valori null nelle colonne di stringa della tabella dovrebbero essere rappresentati come stringhe vuote in Hadoop.
3. La quarta riga è simile alla terza, ma memorizza i dati in una directory Hadoop diversa chiamata `nullvalues`. Include anche l'opzione `--null-non-string '-1'`, che specifica che i valori null nelle colonne non stringa della tabella dovrebbero essere rappresentati come `-1` in Hadoop.
   
```bash

create table department(dep_id int primary key, dep_name varchar(255))
create table course(course_id int primary key, course_name varchar(255),dep int, foreign key (dep) references department(dep_id))
create table student(id int primary key, name varchar(255), surname varchar(255), course int, foreign key (course) references course(course_id));
insert into department values (1,"DEMACS");
insert into course values (1,"Computer Science",1),(2,"Mathematics", 1);
insert into student values (1,"n1", "s1",1),(2,"n2", "s2",2),(3,"n1", "s2",1),(4,"n2", "s1",2);
```

Qui crea 3 tabelle: `department`, `course`, `student` e le riempie con dei dati.

```bash
sqoop-import --connect jdbc:mysql://master/sqooptest --username hive -P --query 
	'select id,name,surname,course_name,dep_name from student join (course join department on dep=dep_id) on course = course_id where $CONDITIONS' 
	--split-by id --target-dir allStudents

sqoop-import --connect jdbc:mysql://master/sqooptest --username hive -P --query 
	'select id,name,surname,course_name,dep_name from student join (course join department on dep=dep_id) on course = course_id 
	where $CONDITIONS and course_name="Computer Science"' 
	--split-by id --target-dir allStudentsCS

sqoop import-all-tables jdbc:mysql://master/sqooptest --username hive -P --warehouse-dir distributed_sqoop_test --exclude-table department 
```
1. Il primo comando importa dati da una query SQL che unisce le tabelle `student`, `course` e `department`. La query seleziona gli studenti e le informazioni relative ai loro corsi e dipartimenti. L'opzione `--split-by id` indica che i dati dovrebbero essere suddivisi in base all'ID dello studente durante l'importazione. I dati importati vengono salvati in una directory Hadoop chiamata `allStudents`.

2. Il secondo comando è simile al primo, ma include un ulteriore filtro nella query SQL: seleziona solo gli studenti che frequentano il corso di "Computer Science". I dati importati vengono salvati in una directory Hadoop chiamata `allStudentsCS`.

3. Il terzo comando importa tutte le tabelle dal database MySQL in una directory Hadoop chiamata `distributed_sqoop_test`, escludendo la tabella `department`.

```bash

-Dsqoop.export.records.per.statement 
-Dsqoop.export.statements.per.transaction

```

Parametri per fare export. Il primo indica il numero di righe che vengono inserite in una singola istruzione SQL. Il secondo indica il numero di istruzioni SQL che vengono eseguite in una singola transazione.

```bash

Data2
	1,c1
	2,c2
	3,c3

hdfs dfs -put data2 t_fold
sqoop-export --connect jdbc:mysql://master/sqooptest --username hive -P --table table_ --export-dir t_fold --update-key id  

sqoop-export --connect jdbc:mysql://master/sqooptest --username hive -P --table table_ --export-dir t_fold --update-key id --update-mode allowinsert
```

1. `hdfs dfs -put data2 t_fold`: Questo comando viene utilizzato per inserire il file `data2` dal file system locale nella directory Hadoop Distributed File System (HDFS) denominata `t_fold`.	

2. `sqoop-export --connect jdbc:mysql://master/sqooptest --username hive -P --table table_ --export-dir t_fold --update-key id`: Questo comando esporta i dati dalla directory Hadoop `t_fold` in una tabella MySQL denominata `table_`. L'opzione `--update-key id` specifica che la colonna `id` dovrebbe essere utilizzata come chiave per determinare quali righe nella tabella MySQL dovrebbero essere aggiornate con i dati da Hadoop.

3. `sqoop-export --connect jdbc:mysql://master/sqooptest --username hive -P --table table_ --export-dir t_fold --update-key id --update-mode allowinsert`: Questo comando è simile al secondo, ma include l'opzione `--update-mode allowinsert`. Ciò significa che se una riga con un determinato `id` non esiste nella tabella MySQL, Sqoop inserirà una nuova riga con quell'`id` e i dati corrispondenti da Hadoop.

```bash
sqoop-import --connect jdbc:mysql://master/sqooptest --username hive -P --table cities --hive-import 
```

`--hive-import:` Questa opzione dice a Sqoop di importare i dati direttamente in Hive. Sqoop creerà automaticamente una tabella Hive con la stessa struttura della tabella MySQL e caricherà i dati in questa tabella. Se la tabella Hive esiste già, Sqoop vi aggiungerà i dati.


```bash
sqoop-import --connect jdbc:mysql://master/sqooptest --username hive -P --table cities --hive-import --hive-table citystring --map-column-hive id=string
```

- `--hive-import:` Questa opzione dice a Sqoop di importare i dati direttamente in Hive. Sqoop creerà automaticamente una tabella Hive con la stessa struttura della tabella MySQL e caricherà i dati in questa tabella. Se la tabella Hive esiste già, Sqoop vi aggiungerà i dati.
- `--hive-table citystring:` Questa opzione specifica il nome della tabella Hive in cui i dati verranno importati. Se questa opzione non viene fornita, Sqoop utilizzerà lo stesso nome della tabella MySQL.
- `--map-column-hive id=string:` Questa opzione specifica che la colonna `id` dovrebbe essere mappata alla colonna `string` nella tabella Hive. In questo caso, la colonna `id` è di tipo intero nella tabella MySQL, ma Sqoop la mappa a una colonna di tipo stringa nella tabella Hive.



```bash
sqoop-import --connect jdbc:mysql://master/sqooptest --username hive -P --table cities --hive-import --hive-table citypart --hive-partition-key part --hive-partition-value 'all'
```

`--hive-partition-key part:` Questa opzione specifica che i dati dovrebbero essere partizionati in base alla colonna part nella tabella Hive. La partizione è un modo per dividere una tabella in parti correlate in base ai valori di determinate colonne. Ciò può rendere più efficiente l'interrogazione dei dati.

`--hive-partition-value 'all':` Questa opzione specifica il valore per la partizione. In questo caso, tutti i dati verranno importati in una partizione denominata 'all'. Se si eseguisse questo comando più volte con valori di partizione diversi, ogni esecuzione creerebbe una nuova partizione nella tabella Hive.

Quindi, in questo caso, il comando importerà i dati dalla tabella cities nel database MySQL in una tabella Hive denominata citypart. I dati verranno partizionati in base alla colonna part, e tutti i dati da questa particolare operazione di importazione verranno inseriti in una partizione denominata 'all'.


```bash
sqoop-import --connect jdbc:mysql://master/sqooptest --username hive -P --table cities --where 'country="USA"' --hive-import --hive-table citypart --hive-partition-key part --hive-partition-value 'USA'
```

Quindi, in questo caso, il comando importerà i dati dalla tabella cities nel database MySQL in una tabella Hive denominata citypart. I dati verranno partizionati in base alla colonna part, e tutti i dati da questa particolare operazione di importazione verranno inseriti in una partizione denominata 'USA'.

