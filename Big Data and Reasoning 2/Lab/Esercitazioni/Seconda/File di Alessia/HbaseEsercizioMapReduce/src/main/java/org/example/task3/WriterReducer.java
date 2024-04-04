package org.example.task3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WriterReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        context.write(new Text(), values.iterator().next());
    }
}

// Task 4: Define an external hive table for the cleaned data and find the most frequent passenger count value.
// create external table cleaned_data(row_timestamp string, fare_amount, string, pickup_datetime string, distance string, passenger_count string) row format delimited fields terminated by ',' location '/user/ubuntu/cleandata';
// select count(*) as most_frequent_counter, passenger_count from cleaned_data group by passenger_count order by most_frequent_counter DESC limit 1;
