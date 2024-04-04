package com.example;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class Import {

    public static void main(String[] args) {
        try {
            Job j = Job.getInstance(new Configuration());

            j.setJarByClass(Import.class);

            // dio  mapreduce
            MultipleInputs.addInputPath(j, new Path(args[0]), TextInputFormat.class, OrderMapper.class);
            MultipleInputs.addInputPath(j, new Path(args[1]), TextInputFormat.class, ItemMapper.class);

            // Key: OrderID
            TableMapReduceUtil.initTableReducerJob("", OrderReducer.class, j);




        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
