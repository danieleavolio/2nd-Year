package test;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Combiner extends Reducer<Text, Text, Text, Text>{

	@Override
	protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		int count=0;
		int sum=0;
		
		for (Iterator<Text> iterator = values.iterator(); iterator.hasNext();) {
			sum += Integer.valueOf(iterator.next().toString());
			count+=1;
		}
		context.write(key, new Text(sum+":"+count));
	}
}
