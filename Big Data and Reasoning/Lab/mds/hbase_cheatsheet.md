# Reference

## Java API

### Set the POM

[POM](#pom)

### Exporting a JAR

To export a JAR, we need to use the `maven-assembly-plugin` plugin. We need to add the plugin to the `pom.xml` file. Then, we need to run the command. The JAR will be created in the `target` folder. In the root directory of the project (where the POM is located), run the following command:

```bash
mnv clean package
```

### Running a JAR

To run a JAR, we need to use the `hadoop jar` command. We need to pass the JAR and the main class. We can also pass arguments to the main class. In the root directory of the project (where the POM is located), run the following command:

```bash
scp hbase-1.0-SNAPSHOT-jar-with-dependencies.jar hadoop@master:~/jars/hbase.jar
hadoop jar jars/hbase.jar
```

### Core Concepts

#### Creating a Table

To create a table, you need to create a `TableDescriptor` and add `ColumnFamilyDescriptor` to it. Then, you can create the table using the `Admin` object.

A `TableDescriptor` is created using the `TableDescriptorBuilder` class setting the table name using the `TableName` class. A `ColumnFamilyDescriptor` is created using the `ColumnFamilyDescriptorBuilder` class setting the column family name using the `Bytes` class.

To add a column family to the table you need to use the setColumnFamily method of the `TableDescriptorBuilder` class passing as parameter the `ColumnFamilyDescriptor` object.

[Implementation](#create-a-table)

#### Deleting a Table

To delete a table, you need to disable it first. Then, you can delete it using the `Admin` object.

[Implementation](#delete-a-table)

#### Creating a Row

To create a new Row we create a `Put` object passing the row key to the constructor. Then, we add the column family and the column to the `Put` object using the `addColumn` method. Finally, we add the `Put` object to the table by using the `put` method of the `Table` object. To get the `Table` object we use the `Connection` object.

The `addColumn` method takes as parameters the column family, the column and the value. The column family and the column are `byte[]` objects.

[Implementation](#create-a-row)

#### Deleting a Row

To delete a row, we create a `Delete` object passing the row key to the constructor. We can choose to delete:

1. [Specific Row](#delete-row)
2. [Specific Column Family](#delete-column-family)
3. [Specific Column](#delete-column)

##### Deleting a specific row

To delete a specific row, we create a `Delete` object passing the row key to the constructor. Then, we add the `Delete` object to the table by using the `delete` method of the `Table` object.

##### Deleting a specific column family

To delete a specific column family, we do the same as deleting a specific row but we add the column family to the `Delete` object using the `addFamily` method.

##### Deleting a specific column

To delete a specific column, we do the same as deleting a specific row but we add the column family and the column to the `Delete` object using the `addColumn` method.

#### Modifying a Table

We can modify a table by adding a column family or by removing a column family. To modify a table, we need to disable it first. Then, we can modify it using the `Admin` object. Finally, we need to enable the table.

[Adding to a Table](#adding-to-a-table)
[Removing from a Table](#removing-from-a-table)

#### Scanning a Table

To scan a table, we need to create a `Scan` object. Then, we can scan the table using the `getScanner` method of the `Table` object passing the `Scan` object as parameter. We can add families and columns to the `Scan` object using the `addFamily` and `addColumn` methods. Finally, we can iterate over the `ResultScanner` object to get the results. Since all the data in HBase is stored as `byte[]`, we need to convert it to the desired type in case we want to use it.

[Implementation](#scanning)

#### Filtering

We can filter the results of a scan by using the `Filter` class. We can create a `Filter` based on different criteria. Then, we can add the filter to the `Scan` object using the `setFilter` method.
After that we can scan the table as usual.

##### PrefixFilter

We can filter the results by a specific prefix. To do so, we need to create a `PrefixFilter` object passing the prefix to the constructor. The prefix is applied to the row key.

```java
Scan s = new Scan();
// selects all the rows that start with 000
Filter filter = new PrefixFilter(Bytes.toBytes("000"));
s.setFilter(filter);
```

##### RowFilter

We can filter the results by a specific row key. To do so, we need to create a `RowFilter` object passing the comparison operator and the row key to the constructor.

```java
Scan s = new Scan();
// selects the row with the key 129
Filter filter = new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("129")));
s.setFilter(filter);
```

Using the `SubstringComparator` class we can select all the rows that contain a specific string.

```java
// selects all the rows that contain 12
Filter filter = new RowFilter(CompareOperator.EQUAL, new SubstringComparator("12"));
```

##### SingleColumnValueFilter

We can filter the results by a specific column value. To do so, we need to create a `SingleColumnValueFilter` object passing the column family, the column, the comparison operator and the value to the constructor.

```java
Scan s = new Scan();
// selects all the rows that have the age greater or equal to 13
Filter filter = new SingleColumnValueFilter(Bytes.toBytes("info"), Bytes.toBytes("age"), CompareOperator.GREATER_OR_EQUAL, Bytes.toBytes("13"));
f.setFilterIfMissing(true); // if the column is missing, the row is not selected
s.setFilter(filter);
```

##### FilterList

We can combine multiple filters by using the `FilterList` class. We can create a `FilterList` object passing the `Operator` and a list of `Filter` objects to the constructor. The `Operator` can be `MUST_PASS_ALL` or `MUST_PASS_ONE`. Then, we can add the `FilterList` object to the `Scan` object.

###### MUST_PASS_ALL

```java
Scan s = new Scan();
FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
// select all the rows with age greater or equal to 13 AND less or equal to 15
filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("info"), Bytes.toBytes("age"), CompareOperator.GREATER_OR_EQUAL, Bytes.toBytes("13")));
filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("info"), Bytes.toBytes("age"), CompareOperator.LESS_OR_EQUAL, Bytes.toBytes("15")));
s.setFilter(filterList);
```

###### MUST_PASS_ONE

```java
Scan s = new Scan();
FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
// select all the rows with age greater OR equal to 13 or gender equal to M
filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("info"), Bytes.toBytes("age"), CompareOperator.GREATER_OR_EQUAL, Bytes.toBytes("13")));
filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("info"), Bytes.toBytes("age"), CompareOperator.EQUAL, Bytes.toBytes("M")));
s.setFilter(filterList);
```

### Examples

#### Create a Configuration

```java
public static Connection getConnection() throws Exception {
    Configuration conf = new Configuration();
    conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
    return ConnectionFactory.createConnection(conf);
}
```

#### Connect to HBase

```java
Connection connection = getConnection();
Admin admin = connection.getAdmin();
admin.close();
connection.close();
```

#### Create a Table

[BACK](#creating-a-table)

```java
// create the builder
TableName tableName = TableName.valueOf(table);
TableDescriptorBuilder table_builder = TableDescriptorBuilder.newBuilder(tableName);

// create the column family by building it
ColumnFamilyDescriptor column_family = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(column_family)).build();
table_builder.setColumnFamily(column_family); // we add it to the table

// create the table by building it
TableDescriptor table = table_builder.build();
admin.createTable(table);
```

#### Delete a Table

[BACK](#deleting-a-table)

```java
TableName tableName = TableName.valueOf(table);
if (admin.tableExists(tableName)) {
    admin.disableTable(tableName);
    admin.deleteTable(tableName);
}
```

#### Create a Row

[BACK](#creating-a-row)

```java
Put put_obj = new Put("row_key".getBytes());
ColumnFamilyDescriptor cf = ColumnFamilyDescriptorBuilder.newBuilder("cf".getBytes()).build();
put_obj.addColumn(cf, "column_name".getBytes(), "value".getBytes()); // can add as many as you want

// get the table
Table table = con.getTable(Table.valueOf("table-name"));
table.put(put_obj); // can also be a list of puts
```

#### Delete Row

[BACK](#deleting-a-row)

```java
Delete delete_obj = new Delete("row-key".getBytes());
Table table = con.getTable(Table.valueOf("table-name"));
table.delete(delete_obj);
```

#### Delete Column Family

[BACK](#deleting-a-row)

```java
Delete familyDelete = new Delete("row-key".getBytes());
familyDelete.addFamily("column-family".getBytes());
Table table = con.getTable(Table.valueOf("table_name"));
table.delete(familyDelete);
```

#### Delete Column

[BACK](#deleting-a-row)

```java
Delete columnDelete = new Delete("row-key".getBytes());
columnDelete.addColumn("column-family".getBytes(), "column-name".getBytes());
Table table = con.getTable(Table.valueOf("table-name"));
table.delete(columnDelete);
```

#### Adding to a Table

[BACK](#modifying-a-table)

```java
con.getAdmin().disableTable(TableName.valueOf("table-name"));
ColumnFamilyDescriptor column = ColumnFamilyDescriptorBuilder.newBuilder("column-family".getBytes()).build();
con.getAdmin().addColumnFamily(TableName.valueOf("table-name"), column);
con.getAdmin().enableTable(TableName.valueOf("table-name"));
```

#### Removing from a Table

[BACK](#modifying-a-table)

```java
con.getAdmin().disableTable(TableName.valueOf("table-name"));
con.getAdmin().deleteColumnFamily(TableName.valueOf("table-name"), "column-family".getBytes());
con.getAdmin().enableTable(TableName.valueOf("table-name"));
```

#### Scanning

[BACK](#scanning-a-table)

```java
Scan scan = new Scan();
scan.addFamily("column-family".getBytes());
// scan.addColumn("column-family".getBytes(), "column-name".getBytes());

Table table = con.getTable(TableName.valueOf("table-name"));
ResultScanner scanner = table.getScanner(scan);

for (Result result : scanner) {
    System.out.println("Key: " +Bytes.toString(result.getRow()));
    System.out.println("\tColumn 1: "+ Bytes.toString(result.getValue("column-family".getBytes(), "column-name1".getBytes())));
    System.out.println("\tColumn 2: "+ Bytes.toString(result.getValue("column-family".getBytes(), "column-name2".getBytes())));
    System.out.println("\tColumn 3: "+ Bytes.toString(result.getValue("column-family".getBytes(), "column-name3".getBytes())));

    // We can also get a map containing all the columns in a column family for the specific row key
    for (Entry<byte[], byte[]> entry : result.getFamilyMap("column-family".getBytes()).entrySet()) {
        System.out.println("\tColumn: " + Bytes.toString(entry.getKey()) + " -> " + Bytes.toString(entry.getValue()));
    }
}

scanner.close();
table.close();
```

#### POM

[BACK](#set-the-pom)

> #### :warning: Change the \<mainClass> tag to your main class

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                    <configuration>
                        <archive>
                            <manifest>
                                <mainClass>
                                    -----------> CHANGE THIS <-----------
                                </mainClass>
                            </manifest>
                        </archive>
                        <descriptorRefs>
                            <descriptorRef>jar-with-dependencies</descriptorRef>
                        </descriptorRefs>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>

<dependencies>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-common</artifactId>
        <version>2.5.6</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-client</artifactId>
        <version>2.5.6</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-mapreduce</artifactId>
        <version>2.5.6</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-common</artifactId>
        <version>3.3.6</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-mapreduce-client-core</artifactId>
        <version>3.3.6</version>
    </dependency>
</dependencies>
```
