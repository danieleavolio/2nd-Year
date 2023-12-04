package com.example;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// Create a mapreduce application that reads the data from “sourcerides” and transform
// each row by replacing all the latitude and longitude fields for both family “pickup” and
// “dropoff” with the distance between the two coordinates. The result of the job must be
// stored into the folder “cleandata” into your hdfs

public class CleanMapper extends TableMapper<Text, Text> {
    private static final double AVERAGE_RADIUS_OF_EARTH = 6371;


    @Override
    protected void map(ImmutableBytesWritable key, Result value,
            Mapper<ImmutableBytesWritable, Result, Text, Text>.Context context)
            throws IOException, InterruptedException {

        String rowKey = Bytes.toString(value.getRow());
        String fareAmount = Bytes.toString(value.getValue(Bytes.toBytes("info"), Bytes.toBytes("fare_amount")));
        String pickupDatetime = Bytes.toString(value.getValue(Bytes.toBytes("pickup"), Bytes.toBytes("pickup_datetime")));
        String pickupLongitude = Bytes.toString(value.getValue(Bytes.toBytes("pickup"), Bytes.toBytes("pickup_longitude")));
        String pickupLatitude = Bytes.toString(value.getValue(Bytes.toBytes("pickup"), Bytes.toBytes("pickup_latitude")));
        String dropoffLongitude = Bytes.toString(value.getValue(Bytes.toBytes("dropoff"), Bytes.toBytes("dropoff_longitude")));
        String dropoffLatitude = Bytes.toString(value.getValue(Bytes.toBytes("dropoff"), Bytes.toBytes("dropoff_latitude")));
        String passengerCount = Bytes.toString(value.getValue(Bytes.toBytes("info"), Bytes.toBytes("passenger_count")));

        double pickupLat = Double.parseDouble(pickupLatitude);
        double pickupLng = Double.parseDouble(pickupLongitude);
        double dropoffLat = Double.parseDouble(dropoffLatitude);
        double dropoffLng = Double.parseDouble(dropoffLongitude);

        int distance = calculateDistance(pickupLat, pickupLng, dropoffLat, dropoffLng);

        String newPickupLat = String.valueOf(distance);
        String newPickupLng = String.valueOf(distance);
        String newDropoffLat = String.valueOf(distance);
        String newDropoffLng = String.valueOf(distance);

        String newFareAmount = fareAmount;
        String newPickupDatetime = pickupDatetime;
        String newPassengerCount = passengerCount;

        context.write(new Text(rowKey), new Text(newFareAmount + "," + newPickupDatetime + "," + newPickupLat + "," + newPickupLng + "," + newDropoffLat + "," + newDropoffLng + "," + newPassengerCount));

    }

    public int calculateDistance(double userLat, double userLng, double venueLat, double venueLng) {
        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(userLat))) *
                        (Math.cos(Math.toRadians(venueLat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH * c));
    }

}
