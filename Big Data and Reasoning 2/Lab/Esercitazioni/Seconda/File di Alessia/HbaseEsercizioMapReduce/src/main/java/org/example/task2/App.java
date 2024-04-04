package org.example.task2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.example.TableSchema;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "master,slave2,slave4");

        try {
            Job job = Job.getInstance(conf, "Filtering and Population");
            job.setJarByClass(App.class);

            job.setMapperClass(CleanerMapper.class);
            TableMapReduceUtil.initTableReducerJob(TableSchema.SOURCERIDES, PopulateTableReducer.class, job);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            FileInputFormat.addInputPath(job, new Path("source"));
            job.setOutputFormatClass(TableOutputFormat.class);

            job.waitForCompletion(true);

        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
