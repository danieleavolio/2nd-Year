# Simulazione per Intermediate Test con HBASE e MapReduce

The company bigdata2022 is building a new e-commerce website to sell its
products across the entire world. In particular the company needs to track
all the orders in order to monitor the monthly and annually income. For this
reason it has asked us to build a big data solution for their needs to move
the old database on the company cluster.

In particular the current schema
is the following:
- Table orders
    - order_id varchar(255) primary key,
    - customer_id varchar(255),
    - status varchar(255),
    - order_purchase_timestamp varchar(255);
- Table products
    - product_id varchar(255) primary key,
    - product_category_name varchar(255);
- Table items
    - order_id varchar(255),
    - order_item_id varchar(255),
    - product_id varchar(255),
    - price varchar(255);

The new schema that fit company needs is the following:
- Table orders
    - Timestamp of the order
    - User that makes the order (customer_id)
    - List of the ordered products; for each product we want to store id, category, price and the quantity that has been ordered.
    - Total amount of the order
    - Status (Delivered/Cancelled)

## Task 1
Import the ecommerce database into hdfs by using sqoop

### Solution
We will skip the Sqoop part, because we didn't do it yet. So, we will just copy the files from the local file system to HDFS.

```bash
hdfs dfs -mkdir -p /user/hive/warehouse/orders
hdfs dfs -mkdir -p /user/hive/warehouse/products
hdfs dfs -mkdir -p /user/hive/warehouse/items

hdfs dfs -put orders.csv /user/hive/warehouse/orders
hdfs dfs -put products.csv /user/hive/warehouse/products
hdfs dfs -put items.csv /user/hive/warehouse/items
```


## Task 2
Create external tables on the data imported at step 1 and join all the table into a new one named “ordereditems” in csv format

### Solution

What does it mean *join all the tables into one*? We will just create a view that will join the tables.

```sql
CREATE EXTERNAL TABLE orders (
    order_id STRING,
    customer_id STRING,
    status STRING,
    order_purchase_timestamp STRING
)
ROW FORMAT 'SERDE'
    'org.apache.hadoop.hive.serde2.OpenCSVSerde'
LOCATION '/user/hive/warehouse/orders';

CREATE EXTERNAL TABLE products (
    product_id STRING,
    product_category_name STRING
)
ROW FORMAT 'SERDE'
    'org.apache.hadoop.hive.serde2.OpenCSVSerde'
LOCATION '/user/hive/warehouse/products';

CREATE EXTERNAL TABLE items (
    order_id STRING,
    order_item_id STRING,
    product_id STRING,
    price STRING
)
ROW FORMAT 'SERDE'
    'org.apache.hadoop.hive.serde2.OpenCSVSerde'
LOCATION '/user/hive/warehouse/items';

```

To solve the problem, we will create a view that will join the tables. What is a **VIEW**? A view is a virtual table based on the result-set of an SQL statement. A view contains rows and columns, just like a real table. The fields in a view are fields from one or more real tables in the database. You can add SQL functions, WHERE, and JOIN statements to a view and present the data as if the data were coming from one single table.

```sql
CREATE VIEW ordereditems AS
SELECT
    o.order_purchase_timestamp,
    o.customer_id,
    i.order_id,
    i.order_item_id,
    i.product_id,
    p.product_category_name,
    i.price
FROM orders o
JOIN items i ON o.order_id = i.order_id
JOIN products p ON i.product_id = p.product_id;
```

Is not clear to me what we have to do now. Do we have to export the view to a CSV file? If so, we can do it with the following command:

```bash
hdfs dfs -get /user/hive/warehouse/ordereditems /tmp/ordereditems
```


## Task 3
Create a mapreduce job that read the folder of the hive table and stores data into hbase according to orders table description

Design of the hbase table is up to you.

### Solution

**POM.XML** to create the jar file:

```xml
<build>
    <plugins>
      <!-- any other plugins -->
      <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>single</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
          </descriptorRefs>
        </configuration>
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

We will create a table named `orders` with the following schema:

| Column Family | Column | Description |
|---------------|--------|-------------|
| order | order_id | The id of the order |
| order | customer_id | The id of the customer |
| order | status | The status of the order |
| order | order_purchase_timestamp | The timestamp of the order |
| items | product_id | The id of the product |
| items | product_category_name | The category of the product |
| items | price | The price of the product |

We will create the table with the following command:

```bash
hbase shell
```

```bash
create 'orders', 'order', 'items'
```

We will create a mapreduce job that will read the folder of the hive table and stores data into hbase according to orders table description.

```java

// Lets create a class that will represent the data that we will store in HBase

public class Order {
    private String order_id;
    private String customer_id;
    private String status;
    private String order_purchase_timestamp;
    private String product_id;
    private String product_category_name;
    private String price;

    public Order(String order_id, String customer_id, String status, String order_purchase_timestamp, String product_id, String product_category_name, String price) {
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.status = status;
        this.order_purchase_timestamp = order_purchase_timestamp;
        this.product_id = product_id;
        this.product_category_name = product_category_name;
        this.price = price;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_purchase_timestamp() {
        return order_purchase_timestamp;
    }

    public void setOrder_purchase_timestamp(String order_purchase_timestamp) {
        this.order_purchase_timestamp = order_purchase_timestamp;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_category_name() {
        return product_category_name;
    }

    public void setProduct_category_name(String product_category_name) {
        this.product_category_name = product_category_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id='" + order_id + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", status='" + status + '\'' +
                ", order_purchase_timestamp='" + order_purchase_timestamp + '\'' +
                ", product_id='" + product_id + '\'' +
                ", product_category_name='" + product_category_name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
```

```java

// What do we have to do?
// We have to read the data from the hive table and store it in HBase
// We will use the TableOutputFormat class to store the data in HBase

// Let's configure the job

public static Configuration getConfiguration(){
    Configuration con = new configuration();
    conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
    return conf
}

// Create the table
public static void createTable(){
    Configuration conf = getConfiguration();
    Connection connection = ConnectionFactory.createConnection(conf);
    Admin admin = connection.getAdmin();
    HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf("orders"));
    tableDescriptor.addFamily(new HColumnDescriptor("order"));
    tableDescriptor.addFamily(new HColumnDescriptor("items"));
    admin.createTable(tableDescriptor);
    System.out.println("Table created");
}

// Create the job
public static Job getJob() throws IOException {
    Configuration conf = getConfiguration();
    Job job = Job.getInstance(conf, "orders");
    job.setJarByClass(Orders.class);
    job.setMapperClass(OrdersMapper.class);
    job.setMapOutputKeyClass(ImmutableBytesWritable.class);
    job.setMapOutputValueClass(Put.class);
    job.setOutputFormatClass(TableOutputFormat.class);
    job.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, "orders");
    FileInputFormat.addInputPath(job, new Path("/user/hive/warehouse/ordereditems"));
    return job;
}

// Create the mapper
public static class OrdersMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] values = value.toString().split(",");
        Order order = new Order(values[0], values[1], values[2], values[3], values[4], values[5], values[6]);
        Put put = new Put(Bytes.toBytes(order.getOrder_id()));
        put.addColumn(Bytes.toBytes("order"), Bytes.toBytes("order_id"), Bytes.toBytes(order.getOrder_id()));
        put.addColumn(Bytes.toBytes("order"), Bytes.toBytes("customer_id"), Bytes.toBytes(order.getCustomer_id()));
        put.addColumn(Bytes.toBytes("order"), Bytes.toBytes("status"), Bytes.toBytes(order.getStatus()));
        put.addColumn(Bytes.toBytes("order"), Bytes.toBytes("order_purchase_timestamp"), Bytes.toBytes(order.getOrder_purchase_timestamp()));
        put.addColumn(Bytes.toBytes("items"), Bytes.toBytes("product_id"), Bytes.toBytes(order.getProduct_id()));
        put.addColumn(Bytes.toBytes("items"), Bytes.toBytes("product_category_name"), Bytes.toBytes(order.getProduct_category_name()));
        put.addColumn(Bytes.toBytes("items"), Bytes.toBytes("price"), Bytes.toBytes(order.getPrice()));
        context.write(new ImmutableBytesWritable(Bytes.toBytes(order.getOrder_id())), put);
    }
}

// Main
public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
    createTable();
    Job job = getJob();
    System.exit(job.waitForCompletion(true) ? 0 : 1);
}
```

```bash
hadoop jar orders.jar Orders
```


## Extra
Build a simple java application or mapreduce jobs that are able to perform
the following queries


### Select the total income for a precise month 

```
public float totalIncomeForMonth(int month);
```

#### Solution

```java

public float totalIncomeForMonth(int month){
    Configuration conf = getConfiguration();
    Connection connection = ConnectionFactory.createConnection(conf);
    Table table = connection.getTable(TableName.valueOf("orders"));
    Scan scan = new Scan();
    scan.setFilter(new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(".*-0" + month + "-.*")));
    ResultScanner scanner = table.getScanner(scan);
    float total = 0;
    for (Result result : scanner) {
        for (Cell cell : result.listCells()) {
            String value = Bytes.toString(CellUtil.cloneValue(cell));
            if (cell.getQualifierArray().equals("price".getBytes())) {
                total += Float.parseFloat(value);
            }
        }
    }
    return total;
}
```



### For each year select the total income 
```
public void groupIncomeByYear();
```

#### Solution

```java

public void groupIncomeByYear(){
    Configuration conf = getConfiguration();
    Connection connection = ConnectionFactory.createConnection(conf);
    Table table = connection.getTable(TableName.valueOf("orders"));
    Scan scan = new Scan();
    ResultScanner scanner = table.getScanner(scan);
    Map<String, Float> map = new HashMap<>();
    for (Result result : scanner) {
        for (Cell cell : result.listCells()) {
            String value = Bytes.toString(CellUtil.cloneValue(cell));
            if (cell.getQualifierArray().equals("price".getBytes())) {
                String year = cell.getRowArray()[0] + "" + cell.getRowArray()[1] + "" + cell.getRowArray()[2] + "" + cell.getRowArray()[3];
                if (map.containsKey(year)) {
                    map.put(year, map.get(year) + Float.parseFloat(value));
                } else {
                    map.put(year, Float.parseFloat(value));
                }
            }
        }
    }
    for (Map.Entry<String, Float> entry : map.entrySet()) {
        System.out.println(entry.getKey() + " " + entry.getValue());
    }
}
```

### Select for a particular user the total amount spent for each category

```
public void customerOutcomeByCategory(String name, String
surname);
```

#### Solution

```java

public void customerOutcomeByCategory(String name, String surname){
    Configuration conf = getConfiguration();
    Connection connection = ConnectionFactory.createConnection(conf);
    Table table = connection.getTable(TableName.valueOf("orders"));
    Scan scan = new Scan();
    scan.setFilter(new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(".*" + name + ".*" + surname + ".*")));
    ResultScanner scanner = table.getScanner(scan);
    Map<String, Float> map = new HashMap<>();
    for (Result result : scanner) {
        for (Cell cell : result.listCells()) {
            String value = Bytes.toString(CellUtil.cloneValue(cell));
            if (cell.getQualifierArray().equals("price".getBytes())) {
                String category = Bytes.toString(CellUtil.cloneQualifier(cell));
                if (map.containsKey(category)) {
                    map.put(category, map.get(category) + Float.parseFloat(value));
                } else {
                    map.put(category, Float.parseFloat(value));
                }
            }
        }
    }
    for (Map.Entry<String, Float> entry : map.entrySet()) {
        System.out.println(entry.getKey() + " " + entry.getValue());
    }
}
```

### Select the count of orders and the most popular category for each month

```
public void analyzeMonths();
```

#### Solution

```java

public void analyzeMonths(){
    Configuration conf = getConfiguration();
    Connection connection = ConnectionFactory.createConnection(conf);
    Table table = connection.getTable(TableName.valueOf("orders"));
    Scan scan = new Scan();
    ResultScanner scanner = table.getScanner(scan);
    Map<String, Integer> map = new HashMap<>();
    Map<String, String> map2 = new HashMap<>();
    for (Result result : scanner) {
        for (Cell cell : result.listCells()) {
            String value = Bytes.toString(CellUtil.cloneValue(cell));
            if (cell.getQualifierArray().equals("product_category_name".getBytes())) {
                String month = cell.getRowArray()[5] + "" + cell.getRowArray()[6];
                if (map.containsKey(month)) {
                    map.put(month, map.get(month) + 1);
                } else {
                    map.put(month, 1);
                }
                if (map2.containsKey(month)) {
                    if (map2.get(month).equals(value)) {
                        map2.put(month, value);
                    }
                } else {
                    map2.put(month, value);
                }
            }
        }
    }
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
        System.out.println(entry.getKey() + " " + entry.getValue() + " " + map2.get(entry.getKey()));
    }
}
```



