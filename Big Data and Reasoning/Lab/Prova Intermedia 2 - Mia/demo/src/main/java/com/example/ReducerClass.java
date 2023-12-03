package com.example;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReducerClass extends TableReducer<Text, Text, NullWritable> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        for (Text value : values) {
            String[] tipologiaNumero = value.toString().split(" ");
            String tipologia = tipologiaNumero[0];
            String numero = tipologiaNumero[1];

            Put put = new Put(Bytes.toBytes(key.toString()));
            put.addColumn(Bytes.toBytes("tipologia"), null, Bytes.toBytes(tipologia));
            put.addColumn(Bytes.toBytes("numero"), null, Bytes.toBytes(numero));
            put.addColumn(Bytes.toBytes("nome"), null, Bytes.toBytes(key.toString()));

            context.write(NullWritable.get(), put);
        }
    }

}
