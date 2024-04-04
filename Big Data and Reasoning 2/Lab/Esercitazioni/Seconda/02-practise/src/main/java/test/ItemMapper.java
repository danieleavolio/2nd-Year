package test;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ItemMapper extends Mapper<LongWritable, Text, Text, Data> {

	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Data>.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		Item i = new Item(value.toString().split(";"));
		context.write(new Text(i.getOrder_id()),new Data(i,Item.class.getName()));
	}
}
