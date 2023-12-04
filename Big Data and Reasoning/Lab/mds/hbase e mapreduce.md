# HBASE E MAPREDUCE CHEAT SHEET (Daniele)

## HBase

### Da csv in HDFS in HBase

Il funzionamento si basa su usare **mapreduce** per fare questa operazione.
Immaginiamo di avere un file csv con i seguenti dati:

```csv
id, name, surname, age, city, gender, job
1, John, Doe, 30, New York, M, Engineer
2, Jane, Doe, 25, New York, F, Engineer
3, Mark, Smith, 40, London, M, Manager
4, Mary, Smith, 35, London, F, Manager
```

#### Main e job mapreduce

Ora, dobbiamo creare un progetto **Java** che sfrutta **mapreduce** per caricare questi dati in HBase.
Il progetto deve avere una classe **main** che si occupa di lanciare il job mapreduce.
Il job mapreduce deve avere una classe **Mapper** e una classe **Reducer**.
Partiamo dal main.

```java
public static void main(String[] args) throws Exception {
    Configuration conf = HBaseConfiguration.create();
    Job job = Job.getInstance(conf, "csv to hbase");
    job.setJarByClass(Main.class);

    job.setMapperClass(CsvToHBaseMapper.class);
    job.setMapOutputKeyClass(ImmutableBytesWritable.class);
    job.setMapOutputValueClass(Put.class);

    job.setReducerClass(CsvToHBaseReducer.class);
    job.setOutputKeyClass(ImmutableBytesWritable.class);
    job.setOutputValueClass(Put.class);
    
    FileInputFormat.addInputPath(job, "Path nel hdfs");

    TableMapReduceUtil.initTableReducerJob("people", CsvToHBaseReducer.class, job);

    System.exit(job.waitForCompletion(true) ? 0 : 1);
}
```

In particolare, la riga `FileInputFormat.addInputPath(job, "Path nel hdfs");` indica il path nel hdfs del file csv.
La riga `TableMapReduceUtil.initTableReducerJob("people", CsvToHBaseReducer.class, job);` indica che il reducer deve caricare i dati nella tabella **people**.

#### Mapper
Ora, passiamo al mapper.

```java

public class CsvToHBaseMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // Dividi la riga del CSV in campi
        String[] fields = value.toString().split(",");

        // Crea una chiave e un valore Put per HBase
        ImmutableBytesWritable rowKey = new ImmutableBytesWritable(fields[0].getBytes());
        Put put = new Put(rowKey.get());

        // Aggiungi colonne a Put
        put.addColumn(Bytes.toBytes("columnFamily"), Bytes.toBytes("column"), Bytes.toBytes(fields[1]));

        // Scrivi la chiave e il valore al contesto
        context.write(rowKey, put);
    }
}
```

**Spiegazione**:
- La riga `String[] fields = value.toString().split(",");` divide la riga del csv in campi.
- La chiave è il primo campo del csv, mentre il valore è il resto dei campi.
- La riga `ImmutableBytesWritable rowKey = new ImmutableBytesWritable(fields[0].getBytes());` crea la chiave.
- La riga `Put put = new Put(rowKey.get());` crea il valore. In particolare, il valore è un oggetto **Put** che rappresenta una riga di HBase.
- La riga `put.addColumn(Bytes.toBytes("columnFamily"), Bytes.toBytes("column"), Bytes.toBytes(fields[1]));` aggiunge una colonna al valore. In particolare, la colonna è composta da:
    - **columnFamily**: il nome della famiglia di colonne. In questo caso, la famiglia di colonne è **columnFamily**.
    - **column**: il nome della colonna. In questo caso, la colonna è **column**.
    - **fields[1]**: il valore della colonna. In questo caso, il valore è il secondo campo del csv.


Alternativa se ci fossero state piu famiglie di colonne:


```java

public class CsvToHBaseMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // Dividi la riga del CSV in campi
        String[] fields = value.toString().split(",");

        // Crea una chiave e un valore Put per HBase
        ImmutableBytesWritable rowKey = new ImmutableBytesWritable(fields[0].getBytes());
        Put put = new Put(rowKey.get());

        // Aggiungi colonne a Put
        put.addColumn(Bytes.toBytes("columnFamily1"), Bytes.toBytes("column1"), Bytes.toBytes(fields[1]));
        put.addColumn(Bytes.toBytes("columnFamily2"), Bytes.toBytes("column2"), Bytes.toBytes(fields[2]));
        put.addColumn(Bytes.toBytes("columnFamily3"), Bytes.toBytes("column3"), Bytes.toBytes(fields[3]));
        put.addColumn(Bytes.toBytes("columnFamily4"), Bytes.toBytes("column4"), Bytes.toBytes(fields[4]));
        put.addColumn(Bytes.toBytes("columnFamily5"), Bytes.toBytes("column5"), Bytes.toBytes(fields[5]));
        put.addColumn(Bytes.toBytes("columnFamily6"), Bytes.toBytes("column6"), Bytes.toBytes(fields[6]));

        // Scrivi la chiave e il valore al contesto
        context.write(rowKey, put);
    }
}
```



#### Reducer

```java
public class CsvToHBaseReducer extends TableReducer<ImmutableBytesWritable, Put, ImmutableBytesWritable> {

    @Override
    protected void reduce(ImmutableBytesWritable key, Iterable<Put> values, Context context) throws IOException, InterruptedException {
        // Scrivi i valori in HBase
        for (Put put : values) {
            context.write(key, put);
        }
    }
}
```

**Spiegazione**:
Il reducer, semplicemente, scrive i valori in HBase prendendo la chiave dal mapper.
Cio' che e' dentro key e put:
- **key**: la chiave del mapper. In particolare, la chiave è il primo campo del csv.
- **put**: il valore del mapper. In particolare, il valore è un oggetto **Put** che rappresenta una riga di HBase.


### Da HBase in HDFS

Il metodo migliore per fare questo è usare **mapreduce**, ancora.

#### Main e job mapreduce

```java
public static void main(String[] args) throws Exception {
    Configuration conf = HBaseConfiguration.create();
    Job job = Job.getInstance(conf, "hbase to csv");
    job.setJarByClass(Main.class);

    job.setMapperClass(HBaseToCsvMapper.class);
    job.setMapOutputKeyClass(ImmutableBytesWritable.class);
    job.setMapOutputValueClass(Result.class);

    job.setReducerClass(HBaseToCsvReducer.class);
    job.setOutputKeyClass(ImmutableBytesWritable.class);
    job.setOutputValueClass(Text.class);

    FileOutputFormat.setOutputPath(job, new Path("Path nel hdfs"));

    TableMapReduceUtil.initTableMapperJob("people", new Scan(), HBaseToCsvMapper.class, ImmutableBytesWritable.class, Result.class, job);

    System.exit(job.waitForCompletion(true) ? 0 : 1);
}
```

In particolare, la riga `FileOutputFormat.setOutputPath(job, new Path("Path nel hdfs"));` indica il path nel hdfs dove salvare il file csv.

#### Mapper

```java

public class HBaseToCsvMapper extends TableMapper<ImmutableBytesWritable, Result> {

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        // Crea una chiave e un valore per il contesto
        ImmutableBytesWritable rowKey = new ImmutableBytesWritable(key.get());
        Text text = new Text();

        // Scrivi la chiave e il valore al contesto
        context.write(rowKey, text);
    }
}
```

**Spiegazione**:
- La riga `ImmutableBytesWritable rowKey = new ImmutableBytesWritable(key.get());` crea la chiave.
- La riga `Text text = new Text();` crea il valore. In particolare, il valore è un oggetto **Text** che rappresenta una riga di un file csv.
- La riga `context.write(rowKey, text);` scrive la chiave e il valore al contesto.

#### Reducer

```java
public class HBaseToCsvReducer extends Reducer<ImmutableBytesWritable, Text, ImmutableBytesWritable, Text> {

    @Override
    protected void reduce(ImmutableBytesWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Scrivi i valori in HDFS
        for (Text text : values) {
            context.write(key, text);
        }
    }
}
```

**Spiegazione**:
Il reducer, semplicemente, scrive i valori in HDFS prendendo la chiave dal mapper.
Cio' che e' dentro key e text:
- **key**: la chiave del mapper. In particolare, la chiave è il primo campo del csv.
- **text**: il valore del mapper. In particolare, il valore è un oggetto **Text** che rappresenta una riga di un file csv.



## MapReduce

Ipotizziamo sempre di avere:


```csv
id, name, surname, age, city, gender, job
1, John, Doe, 30, New York, M, Engineer
2, Jane, Doe, 25, New York, F, Engineer
3, Mark, Smith, 40, London, M, Manager
4, Mary, Smith, 35, London, F, Manager
...
999999, John, Doe, 30, New York, M, Engineer
```

### Raggruppamento

Vogliamo raggruppare i dati in base ad una certa colonna. Ci serve un mapper che emette coppie chiave-valore, dove la chiave è il valore della colonna che si vuole raggruppare, mentre il valore è la riga.

Il reducer prende raccoglie le righe per ogni chiave.

#### Mapper

```java

// Mapper
public class GroupByCityMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] fields = value.toString().split(",");
        String city = fields[4]; // assuming city is the 5th field
        context.write(new Text(city), value);
    }
}
```

#### Reducer

```java
// Reducer
public class GroupByCityReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            context.write(key, value);
        }
    }
}
```
#### Main

```java
public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "group by city");
    job.setJarByClass(Main.class);

    job.setMapperClass(GroupByCityMapper.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);

    job.setReducerClass(GroupByCityReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job, "Path nel hdfs");
    FileOutputFormat.setOutputPath(job, new Path("Path nel hdfs"));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
}
```

### Join

Immaginiamo di avere un altro file csv con i seguenti dati:

```csv
id, marital_status, weight, salary
1, married, 80, 1000
2, single, 60, 2000
3, married, 70, 3000
4, single, 50, 4000
...
999999, married, 80, 1000
```

Vogliamo fare un join tra i due file in base alla colonna **id**.

Per eseguire un'operazione di **join** su due file CSV utilizzando **MapReduce**, avrai bisogno di un **Mapper** che emetta coppie **chiave-valore** dove la chiave è il campo su cui vuoi unire e il valore è l'intera riga con un'etichetta per indicare da quale file proviene. Il Reducer poi raccoglie tutte le righe per ogni chiave e le unisce.

#### Mapper

```java
public class JoinMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] fields = value.toString().split(",");
        String id = fields[0]; // assuming id is the 1st field
        String fileTag = ((FileSplit) context.getInputSplit()).getPath().getName();
        context.write(new Text(id), new Text(fileTag + "," + value.toString()));
    }
}
```

**Spiegazione**:
- La riga `String id = fields[0];` crea la chiave. In particolare, la chiave è il primo campo del csv.
- La riga `String fileTag = ((FileSplit) context.getInputSplit()).getPath().getName();` crea il valore. In particolare, il valore è un oggetto **Text** che rappresenta una riga di un file csv. Inoltre, il valore è composto da:
    - **fileTag**: l'etichetta del file. In particolare, l'etichetta del file è il nome del file.
    - **value.toString()**: la riga del csv.

#### Reducer

```java
// Reducer
public class JoinReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String personInfo = "";
        String otherInfo = "";
        for (Text value : values) {
            String[] taggedValue = value.toString().split(",", 2);
            if (taggedValue[0].equals("file1.csv")) {
                personInfo = taggedValue[1];
            } else if (taggedValue[0].equals("file2.csv")) {
                otherInfo = taggedValue[1];
            }
        }
        context.write(key, new Text(personInfo + "," + otherInfo));
    }
}
```

**Spiegazione**:
- Si scorrono tutti i valori, che singolarmente sono composti da:
    - **fileTag**: l'etichetta del file. In particolare, l'etichetta del file è il nome del file.
    - **value.toString()**: la riga del csv.
- Se l'etichetta del file è **file1.csv**, allora la riga `personInfo = taggedValue[1];` salva la riga del csv in **personInfo**.
- Se l'etichetta del file è **file2.csv**, allora la riga `otherInfo = taggedValue[1];` salva la riga del csv in **otherInfo**.
- La riga `context.write(key, new Text(personInfo + "," + otherInfo));` scrive la chiave e il valore al contesto. In particolare, il valore è composto da:
    - **personInfo**: la riga del csv del file **file1.csv**.
    - **otherInfo**: la riga del csv del file **file2.csv**.

#### Main

```java
public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "join");
    job.setJarByClass(Main.class);

    job.setMapperClass(JoinMapper.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);

    job.setReducerClass(JoinReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job, "Path nel hdfs");
    FileInputFormat.addInputPath(job, "Path nel hdfs");
    FileOutputFormat.setOutputPath(job, new Path("Path nel hdfs"));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
}
```



