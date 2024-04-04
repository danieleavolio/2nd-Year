package org.example.task3;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.example.TableSchema;

import java.io.IOException;

public class ReaderMapper extends TableMapper<Text, Text> {
    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Mapper<ImmutableBytesWritable, Result, Text, Text>.Context context) throws IOException, InterruptedException {
        // The key in the parameter is not of our interest, we have to deal with the value object (Result)
        // Always remembering that we are dealing with one result per time
        String rowId = Bytes.toString(value.getRow());
        String fairAmount = Bytes.toString(value.getValue(TableSchema.RIDE_INFO.getBytes(), TableSchema.AMOUNT_QUALIFIER.getBytes()));
        String countPassengers = Bytes.toString(value.getValue(TableSchema.RIDE_INFO.getBytes(), TableSchema.PASSENGER_COUNT_QUALIFIER.getBytes()));
        String pickUpLongitude = Bytes.toString(value.getValue(TableSchema.RIDE_PICKUP_INFO.getBytes(), TableSchema.PICKUP_LONGITUDE_QUALIFIER.getBytes()));
        String pickUpLatitude = Bytes.toString(value.getValue(TableSchema.RIDE_PICKUP_INFO.getBytes(), TableSchema.PICKUP_LATITUDE_QUALIFIER.getBytes()));
        String pickUpDateTime = Bytes.toString(value.getValue(TableSchema.RIDE_PICKUP_INFO.getBytes(), TableSchema.PICKUP_DATETIME_QUALIFIER.getBytes()));
        String dropOffLongitude = Bytes.toString(value.getValue(TableSchema.RIDE_DROPOFF_INFO.getBytes(), TableSchema.DROPOFF_LONGITUDE_QUALIFIER.getBytes()));
        String dropOffLatitude = Bytes.toString(value.getValue(TableSchema.RIDE_DROPOFF_INFO.getBytes(), TableSchema.DROPOFF_LATITUDE_QUALIFIER.getBytes()));

        String distanceBetweenCoordinates = String.valueOf(calculateDistance(Double.parseDouble(pickUpLatitude), Double.parseDouble(pickUpLongitude), Double.parseDouble(dropOffLatitude), Double.parseDouble(dropOffLongitude)));
        context.write(new Text(rowId), new Text(rowId+","+fairAmount+","+pickUpDateTime+","+distanceBetweenCoordinates+","+countPassengers));
    }

    private int calculateDistance(double userLat, double userLng, double venueLat, double venueLng) {
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
