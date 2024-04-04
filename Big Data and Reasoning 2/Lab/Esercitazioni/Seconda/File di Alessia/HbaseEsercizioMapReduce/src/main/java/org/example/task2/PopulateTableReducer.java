package org.example.task2;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.example.TableSchema;

import java.io.IOException;

public class PopulateTableReducer extends TableReducer<Text, Text, NullWritable> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, NullWritable, Mutation>.Context context) throws IOException, InterruptedException {
        // we have to add a new row which id is the key and the value is the values of our column families
        String[] fields = values.toString().split(",");
        Put newRowPut = new Put(key.getBytes());
        newRowPut.addColumn(TableSchema.RIDE_INFO.getBytes(), TableSchema.AMOUNT_QUALIFIER.getBytes(), fields[1].getBytes());
        newRowPut.addColumn(TableSchema.RIDE_INFO.getBytes(), TableSchema.PASSENGER_COUNT_QUALIFIER.getBytes(), fields[7].getBytes());
        newRowPut.addColumn(TableSchema.RIDE_PICKUP_INFO.getBytes(), TableSchema.PICKUP_DATETIME_QUALIFIER.getBytes(), fields[2].getBytes());
        newRowPut.addColumn(TableSchema.RIDE_PICKUP_INFO.getBytes(), TableSchema.PICKUP_LONGITUDE_QUALIFIER.getBytes(), fields[3].getBytes());
        newRowPut.addColumn(TableSchema.RIDE_PICKUP_INFO.getBytes(), TableSchema.PICKUP_LATITUDE_QUALIFIER.getBytes(), fields[4].getBytes());
        newRowPut.addColumn(TableSchema.RIDE_DROPOFF_INFO.getBytes(), TableSchema.DROPOFF_LONGITUDE_QUALIFIER.getBytes(), fields[5].getBytes());
        newRowPut.addColumn(TableSchema.RIDE_DROPOFF_INFO.getBytes(), TableSchema.DROPOFF_LATITUDE_QUALIFIER.getBytes(), fields[6].getBytes());
        context.write(null, newRowPut);
    }
}
