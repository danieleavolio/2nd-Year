package org.example.task3;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.example.TableSchema;

import java.io.IOException;

public class AppTaskThree {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.set("hbase.zookeeper.quorum", "master,slave2,slave4");

        try {
            Job job = Job.getInstance(configuration, "Read from HBase and Write on HDFS");
            job.setJarByClass(AppTaskThree.class);

            TableMapReduceUtil.initTableMapperJob(TableSchema.SOURCERIDES.getBytes(), new Scan(), ReaderMapper.class, Text.class, Text.class, job);
            job.setReducerClass(WriterReducer.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            FileOutputFormat.setOutputPath(job, new Path("/user/ubuntu/cleandata"));
            job.setInputFormatClass(TableInputFormat.class);

            job.waitForCompletion(true);
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
