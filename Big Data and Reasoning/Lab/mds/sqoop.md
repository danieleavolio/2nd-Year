# SQOOP (SQL to Hadoop)

Lo usi per spostare dati da risorse esterne ad **Hadoop**. Viene considerato tool ETL. Per farlo, sfrutta **MapReduce**

## Overview Generale

**Sqoop Client:** Quello che configuriamo noi. 

Fetcha metadati dalla tabella del RDBMS, e genera un job MapReduce che viene eseguito da YARN.

I comandi principali sono:
- **import:** Importa dati da un RDBMS ad HDFS
- **export:** Exporta dati da HDFS ad un RDBMS

**Nota importante:** La clausola **where** avrà un particolare modo di essere formata per fare in modo che ogni mapper si occupi di una porzione di dati.

*Versione che usiamo:* [Sqoop 1.4.7](https://archive.apache.org/dist/sqoop/1.4.7/)

**Nota:** Sqoop si installa sulla macchina client, e **non sul cluster**.

**Librerie richieste**

- commons-lang-2.6:
Used internally by sqoop

- mysql-connector:
Used to communicate with mysql
server (this could be different depending on the RDBMS).
- hive-common:
Used to import data into hive

Queste librerie vanno aggiunte nella cartella lib di sqoop. 

Questi file li troviamo nella directory *lib* di **hive**. Possiamo copiarli da lì.

## Import

**Dal Client:**

```sql
hadoop@Lovaion:~$ mysql -u hive -p -h master
```

### Spiegazione di alcuni parametri:

```
--target-dir "nome_dir": Specifichiamo il nome della directory in cui l'output del mapreduce verrà salvato dopo che sqoop finisce.

--warehouse-dir "nome_dir": Indica la directory in cui verranno salvato più tabelle che vogliamo importare.

--columns: Indica le colonne che vogliamo importare.

--where: Indica la condizione che vogliamo usare per importare i dati.
```

### Import Incrementale

```
sqoop import --connect jdbc:mysql://master:/sqooptest --username hive -P --table cities --incremental append --check-column id --last-value 0
```

*Nota:* Non ci son target-dir. In questo caso sqoop fa l'append sulla cartella in cui abbiamo già importato i dati.

---
### Paritcular Condition

```bash
sqoop -import --connect jdbc:mysql://master:/sqooptest --username hive -P --query 'select id,name,surname, course_name,dep_name from student join (course join department on dep=dep_id) on course = course_id where $CONDITION' --split-by id --target-dir allStudents
```

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

**Nota:** Quando si fa un sqoop export, bisogna essere consapevoli che i dati che si importano all'interno del database devono essere coerenti con lo schema della tabella in cui verranno inseriti i dati.

