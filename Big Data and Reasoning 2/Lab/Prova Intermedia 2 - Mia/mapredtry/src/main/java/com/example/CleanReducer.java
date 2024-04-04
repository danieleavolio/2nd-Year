package com.example;

import java.io.IOException;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CleanReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text arg0, Iterable<Text> arg1, Reducer<Text, Text, Text, Text>.Context arg2)
            throws IOException, InterruptedException {
        
        for (Text text : arg1) {
            arg2.write(arg0, text);
        }
    }
}
