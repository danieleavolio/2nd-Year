package com.example;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class OrderMapper extends Mapper<LongWritable, Text, Text, Data>{
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Data>.Context context)
            throws IOException, InterruptedException {

                Order order = new Order(value.toString());
                Data data = new Data();
                data.realObject = order;
                data.className = order.getClass().getName();

                context.write(new Text(order.getOrder_id()), data);

                
    }
}
