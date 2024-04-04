package org.example;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MyMapper extends Mapper<LongWritable, Text, Text, Text> {
    // my mapper read from input a file (in particular a table from hive which was saved as csv)
    // and return the couple <order_id, value_of_order> where value_of_order is the row of the file

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        // what I have in input is a string in the csv format
        String[] fields = value.toString().split(",");
        // I expect that the row is formed by 8 fields
        if(fields.length==8) {
            context.write(new Text(fields[0]), value);
        }

        /*
        If i wanted to skip rows with null values

        boolean nullValue=false;
        for(String v:fields) {
            if(v=="" || v.equals("nan")) {
                nullValue=true;
                break;
            }
        }
        if(!nullValue) {
            context.write(new Text(fields[0]), value);
        }
        */
    }
}
