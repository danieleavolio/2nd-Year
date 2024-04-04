package org.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import java.io.IOException;

public class App 
{
    public static void main( String[] args )
    {

        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "master,slave2,slave4");
        System.setProperty("HADOOP_USER_NAME", "ubuntu");

        try {
            // initializing the job
            Job j = Job.getInstance(conf, "Read from hdfs and write on HBase.");
            j.setJarByClass(App.class);
            // initializing the mapper and the reducer
            j.setMapperClass(MyMapper.class);
            TableMapReduceUtil.initTableReducerJob(TableSchema.ORDERS, Reducer.class, j);
            // specifying the output of the map-reduce job
            j.setMapOutputKeyClass(Text.class);
            j.setMapOutputValueClass(Text.class);
            // specifying the format of the input of the map-reduce job ( a file from which we want to read the hive table)
            FileInputFormat.addInputPath(j, new Path("/user/ubuntu/hive-storage/hbasehadoop.db/ordereditems"));
            //specifying the format of the output of the map-reduce job ( the table in HBase)
            j.setOutputFormatClass(TableOutputFormat.class);
            // starting the job
            j.waitForCompletion(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
