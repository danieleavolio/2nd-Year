package com.example;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ItemMapper extends Mapper<LongWritable, Text, Text, Data> {
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Data>.Context context)
            throws IOException, InterruptedException {
        Item o = new Item(value.toString().split(";"));
        Data data = new Data();

        data.realObject = o;
        data.className = o.getClass().getName();

        context.write(new Text(o.getOrder_id()), data);


    }
}
