# HBase e Sqoop
Cose che spero possano tornare utili in futuro, ma io che ne so perdonami? 

## HBase

Esempi con le API per **Java**

```java
public static Configuration getConfiguration(){
    Configuration con = new configuration();
    conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
    return conf
}
```

Lo **zookeper quorum** permette di tenere le macchine sincronizzate.

### Creazione di una tabella


```java

public static void createTable(){
    Connection con = ConnectionFactory.createConnection(getConfiguration());
    
    // La classe Admin permette di creare tabelle
    Admin admin = con.getAdmin();

    // Controllare che una tabella esista prima di crearla
    // Anche questa è un'azione da fare con l'admin

    boolean tableExists = admin.tableExists(TableName.valueOf("table_name"));
    // TableName è una classe wrapper che si usa in questi casi

    if (tableExists){
        // Prima di eliminare una tabella bisogna disabilitarla [Obbligatorio]
            // La marchia come "non usabile"
        admin.disableTable(TableName.valueOf("table_name"));
        // Eliminazione
            // Puoi anche scegliere di non eliminare se vuoi lasciarle non
            // utilizzabili ma disponibil in futuro
        admin.deleteTable(TableName.valueOf("table_name"));
    }

    // Siamo sicuri che la tabella non esista.

    TableDescriptorBuilder tableDescBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf("table_name"));

    ColumnFamilyDescriptorBuilder userinfoBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("userinfo"));
    ColumnFamilyDescriptorBuilder contactinfoBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("contactinfo"));
    ColumnFamilyDescriptorBuilder addressBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("address"));

    tableDescBuilder.setColumnFamily(userinfoBuilder.build());
    tableDescBuilder.setColumnFamily(contactinfoBuilder.build());
    tableDescBuilder.setColumnFamily(addressBuilder.build());

    // Metodo per creare la tabella
    admin.createTable(tableDescBuilder.build());

}
```

### Caricare i dati

```java

public static void loadData(int numEmp){

    List<Put> emps = new ArrayList<Put>();
    for (int i = 0; i < numEmp; i++){
        String prefix = i > 8 ? (i > 98 ? "0" : "00") : "000";
        Employee e = new Employee();
        sout(e);
        Put putEmp = new Put((prefix+Employee.AUTO_INCREMENT).getBytes());
        putEmp.addColumn(Bytes.toBytes("userinfo"), Bytes.toBytes("name"), e.getNames().getBytes());
        putEmp.addColumn(Bytes.toBytes("userinfo"), Bytes.toBytes("surname"), e.getSurnames().getBytes());
        putEmp.addColumn(Bytes.toBytes("userinfo"), Bytes.toBytes("age"), Bytes.toBytes(e.getAge()));
        putEmp.addColumn(Bytes.toBytes("contactinfo"), Bytes.toBytes("email"), e.getEmail().getBytes());
        putEmp.addColumn(Bytes.toBytes("contactinfo"), Bytes.toBytes("phone"), e.getPhone().getBytes());
        putEmp.addColumn(Bytes.toBytes("address"), Bytes.toBytes("street"), e.getStreet().getBytes());
        putEmp.addColumn(Bytes.toBytes("address"), Bytes.toBytes("city"), e.getCity().getBytes());

        for (Entry<String, Integer> skill: e.getSkill().entrySet()){
            putEmp.addColumn(Bytes.toBytes("skills"), skill.getKey().getBytes(), Bytes.toBytes(skill.getValue()));
        }
        emps.add(putEmp);
    }

    try
    {    Connection con = ConnectionFactory.createConnection(getConfiguration());

        Table empTable = con.getTable(TableName.valueOf("table_name"));
        empTable.put(emps);
        empTable.close();
        con.close();
    }
    catch(IOException e)
    {
        e.printStackTrace();
    }

}
```

### Scan della tabella

```java
public static void scanEmployee(){
    Sacn s = new Scan();
    s.addFamily(Bytes.toBytes("userinfo"));
    s.addColumn(Bytes.toBytes("contactinfo"));
    s.addColumn(Bytes.toBytes("address"));

    printEmployee(s);
}

public static void printEmployee(Scan s){
    try{
        Connection con = ConnectionFactory.createConnection(getConfiguration());
        Table empTable = con.getTable(TableName.valueOf("table_name"));
        // ResultScanner è un iteratore che permette di scorrere i risultati
        ResultScanner scanner = empTable.getScanner(s);

        for (Result r : scanner){
            Syste.out.printl("Employee"+ Bytes.toString(r.getRow()) + ": "+ Bytes.toString(r.getValue(Bytes.toBytes("userinfo"), Bytes.toBytes("name"))));

            for (Entry<byte[], byte[]> pair: r.getFamilyMap(Bytes.toBytes("skills").entrySet())){
                System.err.printl("\t" + Bytes.toString(pair.getKey()) + ": " + Bytes.toInt(pair.getValue()));
            }
        }

        scanner.close();

        empTable.close();
        con.close();
    }
    catch(IOException e){
            e.printStackTrace();
    }
}
```

### Eliminare dati da una tabella

```java

public static void deleteWEmployee(){
    try{
        System.setProperty("HADOOP_USER_NAME", "hadoop");
        Connection con = ConnectionFactory.createConnection(getConfiguration());

        Table empTable = con.getTable(TableName.valueOf("table_name"));
        // Con Delete si lavora a livello di RIGA, ma non a livello totale di tabella.
        Delete del = new Delete("0001".getBytes());
        empTable.delete(del);
        // Elimina tutte le colonne per 0001

        scanEmployee();

        // Rimuovere famiglia contactInfo
        Delete familyDelete = new Delete("0002".getBytes());
        familyDelete.addFamily(Bytes.toBytes("contactinfo"));
        empTable.delete(familyDelete);
        // Queto praticamente elimina la famiglia per 0002
        scanEmployee();

        // Rimuovere un qualifier in una data famiglia
        Delete qualifierDelete = new Delete("0003".getBytes());
        qualifierDelete.addColumn(Bytes.toBytes("userinfo"), Bytes.toBytes("surname"));
        empTable.delete(qualifierDelete);
        // Questo elimina una colonna dalla fmaiglia userinfo per 0003
        scanEmployee();

        empTable.close();

        // Se si usa con.getAdmin() si lavora a livello TABELLA
        con.getAdmin().disableTable(TableName.valueOf("table_name"));

        con.getAdmin().deleteColumnFamily(TableName.valueOf("table_name"), Bytes.toBytes("skills"));
        // Viene droppata da tutte le righe della tabella

        // Aggiunge una column family
        con.getAdmin().addColumnFamily(TableName.valueOf("table_name"), ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("cacong")).build());

        // Riattiva la tabella
        con.getAdmin().enableTable(TableName.valueOf("table_name"));

        con.close();



    }
    catch(IOException e){
        e.printStackTrace();
    }
}

```

### Filtrare i dati

```java

public static void scanEmployeeTo9(){
    Scan s = new Scan();
    s.addFamily(Bytes.toBytes("userinfo"));
    s.addColumn(Bytes.toBytes("contactinfo"));
    s.addColumn(Bytes.toBytes("address"));

    // Filtrare i dati
    s.setFilter(new PrefixFilter(Bytes.toBytes("000")));

    printEmployee(s);
}

```

```java

public static void scanEmployeeWith1(){
    Scan s = new Scan();
    s.addFamily(Bytes.toBytes("userinfo"));
    s.addColumn(Bytes.toBytes("contactinfo"));
    s.addColumn(Bytes.toBytes("address"));

    // Filtrare i dati
    s.setFilter(new RowFilter(CompareOperator.EQUAL, new SubstringComparator("1")));

    printEmployee(s);
}
    
```
    
```java

public static void scanEmployeeLangExpert(String lang){
    Scan s = new Scan();
    s.addFamily(Bytes.toBytes("userinfo"));
    s.addColumn(Bytes.toBytes("contactinfo"));
    s.addColumn(Bytes.toBytes("address"));

    // Filtrare i dati
    SingleColumnValueFilter f =  new SingleColumnValueFilter(Bytes.toBytes("skills"), Bytes.toBytes(lang), CompareOperator.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(5)));

    // Se non trova la colonna, la riga viene saltata
    f.setFilterIfMissing(true);
    s.setFilter(f);

    printEmployee(s);
}

```

```java

public static void scanEmployeeExpert(List<String> list){
    Scan s = new Scan();
    s.addFamily(Bytes.toBytes("userinfo"));
    s.addColumn(Bytes.toBytes("contactinfo"));
    s.addColumn(Bytes.toBytes("address"));

    // Lista di filtri da applicare
    List<Filter> filters = new ArrayList<Filter>();

    for (String lang:list){
        SingleColumnValueFilter f =  new SingleColumnValueFilter(Bytes.toBytes("skills"), Bytes.toBytes(lang), CompareOperator.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(5)));
        f.setFilterIfMissing(true);
        filters.add(f);       
    }


    // FilterList prende un opratore logico e una lista di filtri
    FilterList allPassed = new FilterList(FilterList.Operator.MUST_PASS_ALL, filters);

    s.setFilter(allPassed);

    printEmployee(s);
}
```

### Codice Map e Reduce e HBase

```java
public class App {

    public static void main(String[] args){
        try{

            System.setProperty("HADOOP_USER_NAME", "hadoop");

            Job j = Job.getInstance(App.getHBaseConf(), "read from hbase");
            j.setJarByClass(App.class);

            Scan s = new Scan();
            s.addFamily(Bytes.toBytes("skills"));

            // Nome skill --> Confidence Level
            // Perché non integer? Dopo vediam.
            TableMapReduceUtil.initTableMapperJob("table_name", s, Mapper.class, Text.class, Text.class, j);

            j.setCombinerClass(Combiner.class);
            
            TableMapReduceUtil.initTableReducerJob("table_name", Reducer.class, j);

            j.setNumReduceTasks(1);

            j.waitForCompletion(true);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static Configuration getHBaseConf(){
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
        return conf;
    }
}


public class Mapper extends TableMapper<Text, Text>{
    @Override
    protected void map(ImmutableBytesWritable key, Result value, Mapper<ImmutableBytesWritable, Result, Text, Text>.Context contest) throws IOException, InterruptedException{
        System.out.println("Maper here");

        NavigableMap<byte[], byte[]> skills = value.getFamilyMap(Bytes.toBytes("skills"));
        for (Entry<byte[], byte[]> skill: skills.entrySet()){
            contest.write(new Text(e.getKey()), new Text(String.valueOf(Bytes.toInt(e.getValue()))));
        }           
    }
}

public class Combiner extends Reducer<Text, Text, Text, Text>{
    @Override
    protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException{
        System.out.println("Combiner here");

        int sum = 0;
        int count = 0;

        for (Iterator<Text> iterator = values.iterator(); iterator.hasNext();){
            sum += Integer.parseInt(iterator.next().toString());
            count++;
        }

        context.write(key, new Text(sum+":"+count));
    }
}

public class Reducer extends TableReducer<Text, Text, NullWritable>{
    @Override
    protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, NullWritable, Mutation>.Context context) throws IOException, InterruptedException{
        System.out.println("Reducer here");

        int sum = 0;
        int count = 0;

        while(conext.iterator().hasNext()){
            String[] value = context.iterator().next().toString().split(":");
            sum += Integer.parseInt(value[0]);
            count += Integer.parseInt(value[1]);
        }

        Put out = new Put("Stats-row".getBytes());
        out.addColumn(Bytes.toBytes("stats"), key.getBytes(), Bytes.toBytes(sum/count));
        context.write(null, out);
    }
}
```

### Come si esegue?
    
```bash
hadoop jar hbase.jar App
```

### Come si costruisce il jar con le dipendenze? (Fix per hadoop classpath)

```xml

<plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <executions>
        <phase> package </phase>
        <goals>
            <goal> single </goal>
        </goals>
    </executions>
    <configuration>
        <archive>
            <manifest>
                <mainClass> com.example.App </mainClass>
            </manifest>
        </archive>
        <descriptorRefs>
            <descriptorRef> jar-with-dependencies </descriptorRef>
        </descriptorRefs>
    </configuration>
</plugin>
```

Porta per $HBase:16010$

```bash
hadoop jar hbase.jar mainclass.App
```

### Interrogare il database

```bash

hbase shell

# Si aspetta

# Scan della tabella
scan 'table_name'

# Scan di una riga
get 'table_name', 'row_name'

# Scan di una colonna
get 'table_name', 'row_name', 'family:column'

# Scan di una famiglia
get 'table_name', 'row_name', 'family'

# Scan di una colonna con un timestamp
get 'table_name', 'row_name', {COLUMN => 'family:column', TIMESTAMP => timestamp}

```
