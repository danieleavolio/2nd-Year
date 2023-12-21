# `Spark Cheatsheet`

Questo si basa sulla traccia `PDF` che ha svolto il prof `Mazzotta`


`Importante`: Prima di eseguire il codice, bisogna `cambiare` il `master` in base a dove si vuole eseguire il codice. Se si vuole eseguire il codice in `locale` bisogna impostare `local` altrimenti bisogna impostare `yarn`.

```java
	private static String master = "yarn";
    //private static String master = "local";
```

## `Problem 1`
![Problem 1](https://i.imgur.com/QJ6dkFz.png)

`Song dataset`

Dato il dataset dei brani riportato nella tabella 1, implementare le seguenti query:
- Trovare il `brano più popolare`
- Trovare il `numero totale di album venduti per l’autore1`
- `Per ogni album` calcolare la `media` del numero di stream, il `numero` di tracce e la `deviazione standard` del numero di stream


### `Soluzione per il problema 1`

Analizziamo le prime cose da fare quando si fa un esercizio di questo tipo:

```java
public static void main(String[] args) {
		problem1();
		problem2();
		problem3();
	}
```

Suddividere il problema in sotto problemi. In questo caso abbiamo 3 problemi da risolvere.

Iniziamo dal `problema 1`.

```java
SparkSession session = SparkSession.builder().master(Main.master).appName("songs stats").getOrCreate();
session.sparkContext().setLogLevel("error");

```
Bisogna sempre creare una `SparkSession` per poter lavorare con `Spark. 

#### `Leggere il dataset`

Ci sono diversi modi per leggere un dataset. In questo caso abbiamo un file `csv` che contiene i dati. 

```java

//In questo modo leggiamo il dataset e gli diamo uno schema manualmente
Dataset<Row> dataset = session.read().schema("title string, popularity integer, streams integer, author string, album string, year integer, sold_copies integer").csv("data/songs.csv");

//In questo modo leggiamo il dataset e gli diamo uno schema automaticamente (inferSchema)
Dataset<Row> dataset = session.read().option("inferSchema", "true").csv("data/songs.csv");

//In questo modo leggiamo il dataset e gli diamo uno schema automaticamente (inferSchema) e gli diciamo che la prima riga è l'header
Dataset<Row> dataset = session.read().option("header", "true").option("inferSchema","true").csv("data/songsHeader.csv");

//In questo modo gli diciamo che la prima riga è l'header
Dataset<Row> dataset = session.read().option("header", "true").csv("data/songsHeader.csv");

//Stampiamo lo schema del dataset
dataset.printSchema();
```

`Consiglio:` Suddividere le `queries` in `metodi` per avere un codice più pulito e più facile da leggere.

```java
query1(dataset);
query2(dataset);
query3(dataset);
```

#### `Query 1`: Trovare il brano più popolare

Ci sono diversi modi per risolvere questo problema. Vediamoli uno per uno.

`Reduce`:

```java
public static void metodo1(Dataset<Row>dataset){
    String mostFrequent = dataset.reduce(new ReduceFunction<Row>() {

        @Override
        public Row call(Row v1, Row v2) throws Exception {
            // TODO Auto-generated method stub
            int p1 = v1.getAs("popularity");
            int p2 = v2.getAs("popularity");
            return p1 >= p2 ? v1 : v2;
        }
    }).getAs("title");
    System.out.println("Most frequent song: " + mostFrequent);
}
```

Si utilizza la funzione `reduce` che prende in input una `ReduceFunction` che ha come parametri due oggetti di tipo `Row` e restituisce un oggetto di tipo `Row`. In questo caso la funzione restituisce il brano con la popolarità maggiore.


`Order By`:
```java
public static void metodo2(Dataset<Row>dataset){
    Dataset<String> limit = dataset.orderBy(dataset.col("popularity").desc())
    .limit(1).select("title").as(Encoders.STRING());

    limit.show();
}
```

Si sfrutta la funzione `orderBy` che ordina il dataset in base alla colonna `popularity` in modo decrescente. Poi si prende il primo elemento del dataset ordinato e si seleziona solo la colonna `title` e si converte il dataset in un dataset di stringhe. Infine si stampa il dataset.

`Select`

```java
public static void metodo3(Dataset<Row>dataset){
    Dataset<Row> selectMax = dataset.select(functions.max("popularity").as("maxPopularity"));

    Dataset<Row> mostPopularSongs = dataset.join(selectMax, dataset.col("popularity").equalTo(selectMax.col("maxPopularity")));

    mostPopularSongs.show();
}
```

Si sfrutta la funzione `select` che prende in input una `Column` e restituisce un dataset con una sola riga che contiene il valore massimo della colonna `popularity`. Poi si fa un `join` tra il dataset originale e il dataset che contiene il valore massimo della colonna `popularity` in modo da ottenere il brano più popolare.

#### `Query 2`: Trovare il numero totale di album venduti per l’`autore1`

```java

private static void query2(Dataset<Row> dataset) {
    Integer sum = dataset.filter(dataset.col("author").equalTo("author1"))
    .select("sold_copies").as(Encoders.INT())
            .reduce(new ReduceFunction<Integer>() {
                @Override
                public Integer call(Integer v1, Integer v2) throws Exception {
                    // TODO Auto-generated method stub
                    return v1 + v2;
                }
            });
    System.out.println("-------- Author 1 sold " + sum + " copies --------");
}
```

Si utilizza la funzione `filter` che prende in input una `Column` e restituisce un dataset che contiene solo le righe che soddisfano la condizione. Poi si seleziona la colonna `sold_copies` e si converte il dataset in un dataset di interi. Infine si utilizza la funzione `reduce` per sommare tutti i valori della colonna `sold_copies` e si stampa il risultato.

#### `Query 3`: Per ogni album calcolare la media del numero di stream, il numero di tracce e la deviazione standard del numero di stream

```java
private static void query3(Dataset<Row> dataset) {
    Dataset<Row> stats = dataset.groupBy("album").agg(functions.avg("streams"), functions.count("title"), functions.stddev("streams"));
    
    stats.show();
}
```

Si utilizza la funzione `groupBy` che raggruppa il dataset in base alla colonna `album`. Poi si utilizza la funzione `agg` che prende in input una lista di `Column` e restituisce un dataset con una riga per ogni gruppo e una colonna per ogni aggregazione. Infine si stampa il dataset.

## `Problem 2`


Dato il seguente file `csv` che rapresenta le informazioni sugli esami sostenuti dagli studenti, implementare le seguenti query:

- Per ogni anno, calcolare il `numero di tentativi di esame` effettuati dagli studenti di laurea triennale e magistrale
- Trasformare la colonna `score` in una colonna `result` che ha due possibili valori `passed` o `failed`
- Per ogni anno, calcolare sia il numero di tentativi di esame `superati` che `falliti` effettuati dagli studenti di laurea triennale e magistrale

### `Soluzione per il problema 2`

Dataset che abbiamo:

`Exam`:
```csv
student1,course1,14/12/2022,24
```

`Student`:
```csv
student1,2019,B
```

`Course`:
```csv
course1,bigdata,6
```


Aggiungiamo una cosa. Come fare in `Hive`.

```java

SparkSession sessionWithHive = null;
    if (Main.master.equals("local"))
        sessionWithHive = SparkSession.builder().master(Main.master)
        .enableHiveSupport()
                .config("spark.sql.warehouse.dir", "hive_local").appName("student app").getOrCreate();
                
    else
        sessionWithHive = SparkSession.builder().master(Main.master)
        .enableHiveSupport().appName("student app")
                .getOrCreate();
```
Se si usa `master = local` sara' tutto runnato in locale. Se si usa `master = yarn` sara' tutto runnato su `yarn`.

#### Creazione delle tabelle su Hive

```java
private static void createAndPopulateTables(SparkSession sessionWithHive) {
    // TODO Auto-generated method stub
    Dataset<Row> courses = sessionWithHive.read().schema("id_ string, name string, cfu integer")
            .csv("data/course.csv");
    Dataset<Row> students = sessionWithHive.read().schema("id_ string, academic_year integer, degree string")
            .csv("data/student.csv");
    Dataset<Row> exams = sessionWithHive.read()
            .schema("student_id string, course_id string, exam_day string, score integer").csv("data/exam.csv");
```

Si leggono i dataset e si da uno schema manualmente, in modo da poter creare le tabelle su `Hive`.

```java
courses.write().saveAsTable("course");
students.write().saveAsTable("student");
exams.write().saveAsTable("exam");
}
```
I metodi `write` e `saveAsTable` servono per salvare i dataset come tabelle su `Hive`.

`Salvare in formato diverso da parquqet`

Se si vuole salvare in un formato diveso da `parquet` bisogna utilizzare il metodo `format` e passare come parametro il formato che si vuole utilizzare.

```java
courses.write().format("csv").saveAsTable("course");
students.write().format("csv").saveAsTable("student");
exams.write().format("csv").saveAsTable("exam");
```

Una volta letto il `dataset` e salvate le `3 tabelle`:

- `course`
- `student`
- `exam`

Si può iniziare a risolvere i problemi.

Per risolvere i problemi, bisogna fare un `join` tra la tabella `exam` e la tabella `student` in modo da ottenere i dati che ci servono.

```java
Dataset<Row> exams = sessionWithHive.sql("select * from exam as e inner join student as s on e.student_id = s.id_");

```

#### `Query 1`: Per ogni anno, calcolare il numero di tentativi di esame effettuati dagli studenti di laurea triennale e magistrale

```java
private static void p2query1(Dataset<Row> exams) {
    Dataset<Attempt> attempts = exams.map(new MapFunction<Row, Attempt>() {

        @Override
        public Attempt call(Row value) throws Exception {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(value.getAs("exam_day"));
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            String degree = value.getAs("degree");
            return new Attempt(instance.get(Calendar.YEAR), degree);
        }

    }, Encoders.bean(Attempt.class));

    attempts.groupBy("year", "degree").count().show();
}
```

Ill metodo `map` viene utilizzato per trasformare un Dataset di `Row` in un Dataset di `Attempt`. La funzione passata al metodo `map` prende una `Row` come input, estrae i dati necessari, crea un nuovo oggetto `Attempt` e lo restituisce.

Ecco come funziona nel dettaglio:

1. La funzione `call` viene chiamata per ogni riga nel Dataset `exams`.
2. Per ogni riga, estrae il valore del campo `exam_day` e lo converte in un oggetto `Date`.
3. Crea un'istanza di `Calendar` e imposta il suo tempo sulla data dell'esame.
4. Estrae il valore del campo `degree`.
5. Crea un nuovo oggetto `Attempt` con l'anno dell'esame e il grado, e lo restituisce.

Infine, il nuovo Dataset di `Attempt` viene raggruppato per anno e grado, e viene calcolato il conteggio per ogni gruppo. Il risultato viene visualizzato con il metodo `show()`.

`Importante`: La classe `Attempt` e' un `Java Bean` che va creato manualmente e deve avere i `getter` e i `setter` per ogni attributo.

Esempio di `Attempt`
    
```java
package test;

public class Attempt {
	int year;
	String degree;
	public Attempt(int year, String degree) {
		super();
		this.year = year;
		this.degree = degree;
	}
	public Attempt() {
		super();
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
}
```

#### `Query 2`: Trasformare la colonna `score` in una colonna `result` che ha due possibili valori `passed` o `failed`

```java
private static Dataset<Exam> p2q2(Dataset<Row> exams) {
    Dataset<Exam> reshapedExams = exams.map(new MapFunction<Row, Exam>() {

        @Override
        public Exam call(Row value) throws Exception {
            // TODO Auto-generated method stub
            Student stud = new Student();
            stud.setDegree(value.getAs("degree"));
            stud.setYear(value.getAs("academic_year"));
            stud.setStudent_id(value.getAs("student_id"));
            int score = value.getAs("score");
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(value.getAs("exam_day"));
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            return new Exam(stud, value.getAs("course_id"), instance.get(Calendar.YEAR), score >= 18);
        }

    }, Encoders.bean(Exam.class));
    return reshapedExams;
}
```

Qui come prima chiama il metodo `map` e si costruisce lo studente in base al suo `Java Bean` che va creato. Poi si costruisce l'esame in base al suo `Java Bean` che va creato. Infine si restituisce il dataset di esami.

Il problema viene risolto nel Java Bean col costruttore di Exam, poiché il valore di `result` viene impostato in base al valore di `score`. Se `score` è maggiore o uguale a 18, `result` è impostato su `true`, altrimenti è impostato su `false`.


```java
public Exam(Student student, String course, int year, boolean result) {
		super();
		this.student = student;
		this.course = course;
		this.exam_year = year;
		this.result = result ? "passed" : "failed";
	}

//Resto del codice
```

#### `Query 3`: Per ogni anno, calcolare sia il numero di tentativi di esame superati che falliti effettuati dagli studenti di laurea triennale e magistrale

```java
private static void p2q3(Dataset<Exam> exams) {
    exams.groupBy("exam_year", "student.degree", "result").count().show();
}
```

Banalmente, qui si raggruppa per anno, grado e risultato e si calcola il conteggio per ogni gruppo. Il risultato viene visualizzato con il metodo `show()`.

## `Problem 3`

MapReduce - WordCount
Leggi il contenuto dei file txt e conta le occorrenze di ogni parola tranne ”a”, ”an” e ”the”.

Trova le parole che appaiono più di una volta e le occorrenze della parola `spark`

### `Soluzione per il problema 3`

#### `WordCount`

Puo' sembrare `complicato` ma non lo e'. Vediamo come si fa.

Praticamente, i passaggi da fare sono questi:

1. Usare una `FlatMap` e non una `Map` perche' vogliamo `splittare` le righe in parole.
   1. Per ogni riga, splittarla in parole.
   2. Per ogni parola, rimuovere la punteggiatura.
   3. Per ogni parola, convertirla in minuscolo.
   4. Per ogni parola, restituirla.
2. Usare una `Filter` per filtrare le parole che non ci interessano.
   1. Per ogni parola, filtrare quelle vuote.
      1. Per ogni parola, filtrare quelle che sono `the`, `a` o `an`.
      2. Per ogni parola, restituirla.
3. Usare una `GroupBy` per raggruppare le parole e contare le occorrenze.
   1. Per ogni parola, raggrupparla.
   2. Per ogni parola, contare le occorrenze.


```java
private static Dataset<Row> wordcount() {
    // TODO Auto-generated method stub
    SparkSession session = SparkSession.builder().master(Main.master).appName("read file app").getOrCreate();
    Dataset<Row> dataset = session.read().text("sample-text-file.txt");
    dataset.show(2);

    Dataset<Row> dataset2 = dataset.flatMap(new FlatMapFunction<Row, String>() {

        @Override
        public Iterator<String> call(Row t) throws Exception {
            // TODO Auto-generated method stub
            //Prende la singola riga
            String line = t.getAs("value");

            List<String> res = new ArrayList<String>();
            List<String> punctuation = Arrays.asList(".", ",", ":", ";", "?", "!");
            
            //Per ogni parola della riga
            for (String word : Arrays.asList(line.split(" "))) {
                String currentWord = word;
                
                // Se la parola contiene punteggiatura la rimuove
                for (String character : punctuation) {
                    currentWord = currentWord.replace(character, "");
                }
                // Lo aggiunge alla lista di parole
                res.add(currentWord.toLowerCase());
            }
            //CAZI SUOI
            return res.iterator();
        }
    }, Encoders.STRING())
            // Filtra le parole vuote e quelle che non sono rilevanti
            //Ritorna un dataset di String
            .filter(new FilterFunction<String>() {
                @Override
                public boolean call(String value) throws Exception {
                    // TODO Auto-generated method stub
                    return !value.isEmpty() && !value.equals("the") && !value.equals("a") && !value.equals("an");
                }
                //Conta le occeurenze di ogni parola
            }).groupBy("value").count();

    return dataset2;
}
```
