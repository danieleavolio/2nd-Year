package com.example;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.mapreduce.Reducer;

public class ImportReducer extends TableReducer<ImmutableBytesWritable, Put, ImmutableBytesWritable> {

    @Override
    protected void reduce(ImmutableBytesWritable arg0, Iterable<Put> arg1,
            Reducer<ImmutableBytesWritable, Put, ImmutableBytesWritable, Mutation>.Context arg2)
            throws IOException, InterruptedException {

        for (Put put : arg1) {
            arg2.write(arg0, put);
        }

    }
}
