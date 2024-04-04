package com.example;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

// Row: a timestamp the uniquely identifies the taxi ride
// Fare_amount: is the cost of the taxi ride
// Pickup_datetime: a timestamp in which the ride starts
// Pickup_longitude: the starting longitude coordinate of the ride
// Pickup_latitude: the starting latitude coordinate of the ride
// Dropoff_longitude: the final longitude coordinate of the ride
// Dropoff_latitude: the final latitude coordinate of the ride
// Passenger_count: the number of passengers of the ride

public class Main {

    public static Configuration getConfiguration(){
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
        return conf;
    }
    public static void main(String[] args) {
        System.out.println("Map reduce jobs to import");
        Configuration conf = getConfiguration();

        cleanJob(conf);
    }

    public static void importJob(Configuration conf){

        try {
            Job job = Job.getInstance(conf, "Import to HBASE");
            job.setJarByClass(Main.class);
            job.setMapperClass(ImportMapper.class);            
            job.setMapOutputKeyClass(ImmutableBytesWritable.class);
            job.setMapOutputValueClass(Put.class);
            FileInputFormat.addInputPath(job, new Path("/user/hadoop/source/random_sampleFazio.csv"));

            TableMapReduceUtil.initTableReducerJob("sourcerider", ImportReducer.class, job);

            try {
                job.waitForCompletion(true);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void cleanJob(Configuration conf){
        try {
            Job cleanJob = Job.getInstance(conf, "Clean from HBASE");
            

            System.out.println("Map reduce jobs to clean");


            cleanJob.setJarByClass(Main.class);
            // We must read from HBASE from the sourcerider table and write into the hdfs
            cleanJob.setMapperClass(CleanMapper.class);
            cleanJob.setMapOutputKeyClass(Text.class);
            cleanJob.setMapOutputValueClass(Result.class);

            cleanJob.setReducerClass(CleanReducer.class);
            cleanJob.setOutputKeyClass(Text.class);
            cleanJob.setOutputValueClass(Text.class);

            // Checck if the folder exists and delete it
            FileSystem fs = FileSystem.get(conf);
            if(fs.exists(new Path("/user/hadoop/cleandata"))){
                fs.delete(new Path("/user/hadoop/cleandata"), true);
            }
            
            FileOutputFormat.setOutputPath(cleanJob, new Path("/user/hadoop/cleandata"));
            
            TableMapReduceUtil.initTableMapperJob("sourcerider", new Scan(), CleanMapper.class, Text.class, Text.class, cleanJob);

            try {
                cleanJob.waitForCompletion(true);

                System.out.println("Ending the JOB!!");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

    }


}