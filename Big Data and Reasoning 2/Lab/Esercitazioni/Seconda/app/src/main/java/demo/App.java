package demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        createTable();
        Job job = getJob();
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static Configuration getConfiguration() {
        Configuration con = new Configuration();
        con.set("hbase.zookeeper.quorum", "master,slave1,slave2");
        return con;
    }

    // Create the table
    public static void createTable() {
        Configuration conf = getConfiguration();
        Connection connection = null;
        try {
            connection = ConnectionFactory.createConnection(conf);
            Admin admin = connection.getAdmin();
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf("orders"));
            tableDescriptor.addFamily(new HColumnDescriptor("order"));
            tableDescriptor.addFamily(new HColumnDescriptor("items"));
            admin.createTable(tableDescriptor);
            System.out.println("Table created");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Create the job
    public static Job getJob() throws IOException {
        Configuration conf = getConfiguration();
        Job job = Job.getInstance(conf, "orders");
        job.setJarByClass(Order.class);
        job.setMapperClass(OrdersMapper.class);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(Put.class);
        job.setOutputFormatClass(TableOutputFormat.class);
        job.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, "orders");
        FileInputFormat.addInputPath(job, new Path("/user/hive/warehouse/ordereditems"));
        return job;
    }

    public void analyzeMonths() throws IOException {
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

    public void customerOutcomeByCategory(String name, String surname) throws IOException {
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

    public void groupIncomeByYear() throws IOException {
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

    public float totalIncomeForMonth(int month) throws IOException {
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
}
