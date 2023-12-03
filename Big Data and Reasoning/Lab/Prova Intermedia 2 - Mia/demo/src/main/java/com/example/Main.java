package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;

public class Main {
    public static Configuration getConfiguration() {
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
        return conf;
    }

    public static void createTable(String tablename) {

        try {
            Connection conn = ConnectionFactory.createConnection(getConfiguration());
            Admin admin = conn.getAdmin();

            // Check that the table exists
            boolean tableExists = admin.tableExists(TableName.valueOf(tablename));

            if (tableExists) {
                System.out.println("Table " + tablename + " already exists");
            } else {
                System.out.println("Table " + tablename + " does not exist");
            }

            // Create the table if it does not exist
            if (!tableExists) {
                System.out.println("Creating table " + tablename);

                TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder
                        .newBuilder(TableName.valueOf(tablename));

                // Add column families
                ColumnFamilyDescriptorBuilder tipologiaFamily = ColumnFamilyDescriptorBuilder
                        .newBuilder("tipologia".getBytes());
                ColumnFamilyDescriptorBuilder numeroFamily = ColumnFamilyDescriptorBuilder
                        .newBuilder("numero".getBytes());
                ColumnFamilyDescriptorBuilder nomeFamily = ColumnFamilyDescriptorBuilder.newBuilder("nome".getBytes());

                tableDescriptorBuilder.setColumnFamily(tipologiaFamily.build());
                tableDescriptorBuilder.setColumnFamily(numeroFamily.build());
                tableDescriptorBuilder.setColumnFamily(nomeFamily.build());

                admin.createTable(tableDescriptorBuilder.build());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void loadData(String tablename) {
        // TODO Auto-generated method stub
        List<Documento> documenti = new Documento().generaDocumentiRandom(30);

        List<Put> toLoad = new ArrayList<Put>();
        for (Documento documento : documenti) {
            Put putDocument = new Put(Bytes.toBytes(documento.getNumero()));
            putDocument.addColumn(Bytes.toBytes("tipologia"), Bytes.toBytes("tipologia"),
                    Bytes.toBytes(documento.getTipo()));
            putDocument.addColumn(Bytes.toBytes("numero"), Bytes.toBytes("numero"),
                    Bytes.toBytes(documento.getNumero()));
            putDocument.addColumn(Bytes.toBytes("nome"), Bytes.toBytes("nome"), Bytes.toBytes(documento.getNome()));

            toLoad.add(putDocument);
        }

        try {
            Connection conn = ConnectionFactory.createConnection(getConfiguration());
            Admin admin = conn.getAdmin();

            Table table = conn.getTable(TableName.valueOf(tablename));
            table.put(toLoad);
            table.close();
            conn.close();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static void deleteData(Integer id) {
        // TODO Auto-generated method stub
        try {
            Connection conn = ConnectionFactory.createConnection(getConfiguration());
            Admin admin = conn.getAdmin();

            Table table = conn.getTable(TableName.valueOf("documenti"));
            Delete delete = new Delete(Bytes.toBytes(id));

            table.delete(delete);

            table.close();
            conn.close();
        } catch (Exception e) {
        }
    }

    public static void showTable(String tablename) {
        // TODO Auto-generated method stub
        try {
            Connection conn = ConnectionFactory.createConnection(getConfiguration());
            Admin admin = conn.getAdmin();

            Table table = conn.getTable(TableName.valueOf(tablename));

            Scan scan = new Scan();
            scan.addFamily(Bytes.toBytes("tipologia"));
            scan.addFamily(Bytes.toBytes("numero"));
            scan.addFamily(Bytes.toBytes("nome"));

            printDocuments(scan);
            table.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printDocuments(Scan scan) {
        try {
            Connection conn = ConnectionFactory.createConnection(getConfiguration());

            Table table = conn.getTable(TableName.valueOf("documenti"));

            ResultScanner scanner = table.getScanner(scan);

            for (Result result : scanner) {
                System.out.println("Document: " + Bytes.toString(result.getRow()) +
                        " Tipologia: "
                        + Bytes.toString(result.getValue(Bytes.toBytes("tipologia"), Bytes.toBytes("tipologia"))) +
                        " Numero: " + Bytes.toString(result.getValue(Bytes.toBytes("numero"), Bytes.toBytes("numero")))
                        +
                        " Nome: " + Bytes.toString(result.getValue(Bytes.toBytes("nome"), Bytes.toBytes("nome"))));
            }

            scanner.close();
            table.close();
            conn.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void main(String[] args) {
        // Voglio fare un esempio per hbase

        try {
            Configuration conf = getConfiguration();

            int azione = 0;

            if (azione == 1) {

                System.out.println(conf.get("hbase.zookeeper.quorum"));

                System.out.println("Creating table documenti");
                createTable("documenti");

                System.out.println("Loading data into table documenti");
                loadData("documenti");
                System.out.println("Loaded data into table documenti");

                System.out.println("Showing table documenti");
                showTable("documenti");

                System.out.println("Deleting row with id 1");
                deleteData(1);

                System.out.println("Showing table documenti");
                showTable("documenti");

                System.out.println("Ending program");
            }

            else{
                // Configurazione per MapReduce
                conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
                System.setProperty("HADOOP_USER_NAME", "hadoop");

                Job job = Job.getInstance(conf, "HBase MapReduce");
                job.setJarByClass(Main.class);

                // Configurazione Mapper
                TableMapReduceUtil.initTableMapperJob("documenti", new Scan(), Mapper.class, Text.class, Text.class,
                        job);     
                
                // Configurazione Reducer
                TableMapReduceUtil.initTableReducerJob("documenti", ReducerClass.class, job);

                job.setNumReduceTasks(1);

                System.exit(job.waitForCompletion(true) ? 0 : 1);
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}