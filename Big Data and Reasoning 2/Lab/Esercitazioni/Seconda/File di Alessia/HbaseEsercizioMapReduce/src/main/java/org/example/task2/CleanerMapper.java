package org.example.task2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;

public class CleanerMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        // Remember that the map function is called for each row of the file
        String[] fields = value.toString().split(",");
        if(fields.length == 8) {
            boolean missingValue = false;
            for (String field: fields) {
                if (field.equals("") || field.equals("nan")) {
                    missingValue = true;
                    break;
                }
            }
            if (!missingValue)
                context.write(new Text(fields[0]), value);
        }
    }
}
