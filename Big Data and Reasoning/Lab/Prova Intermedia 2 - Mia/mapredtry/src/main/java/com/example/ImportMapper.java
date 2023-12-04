package com.example;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ImportMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        boolean anyNull = false;
        String[] values = value.toString().split(",");
        String rowKey = values[0];
        String fareAmount = values[1];
        String pickupDatetime = values[2];
        String pickupLongitude = values[3];
        String pickupLatitude = values[4];
        String dropoffLongitude = values[5];
        String dropoffLatitude = values[6];
        String passengerCount = values[7];

        if (rowKey == null || rowKey.isEmpty()) {
            anyNull = true;
        }

        if (fareAmount == null || fareAmount.isEmpty()) {
            anyNull = true;
        }

        if (pickupDatetime == null || pickupDatetime.isEmpty()) {
            anyNull = true;
        }

        if (pickupLongitude == null || pickupLongitude.isEmpty()) {
            anyNull = true;
        }

        if (pickupLatitude == null || pickupLatitude.isEmpty()) {
            anyNull = true;
        }

        if (dropoffLongitude == null || dropoffLongitude.isEmpty()) {
            anyNull = true;
        }

        if (dropoffLatitude == null || dropoffLatitude.isEmpty()) {
            anyNull = true;
        }

        if (passengerCount == null || passengerCount.isEmpty()) {
            anyNull = true;
        }

        if (!anyNull) {

            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("fare_amount"), Bytes.toBytes(fareAmount));
            put.addColumn(Bytes.toBytes("pickup"), Bytes.toBytes("pickup_datetime"), Bytes.toBytes(pickupDatetime));
            put.addColumn(Bytes.toBytes("pickup"), Bytes.toBytes("pickup_longitude"), Bytes.toBytes(pickupLongitude));
            put.addColumn(Bytes.toBytes("pickup"), Bytes.toBytes("pickup_latitude"), Bytes.toBytes(pickupLatitude));
            put.addColumn(Bytes.toBytes("dropoff"), Bytes.toBytes("dropoff_longitude"),
                    Bytes.toBytes(dropoffLongitude));
            put.addColumn(Bytes.toBytes("dropoff"), Bytes.toBytes("dropoff_latitude"), Bytes.toBytes(dropoffLatitude));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("passenger_count"), Bytes.toBytes(passengerCount));

            context.write(new ImmutableBytesWritable(Bytes.toBytes(rowKey)), put);
        }
    }

}
