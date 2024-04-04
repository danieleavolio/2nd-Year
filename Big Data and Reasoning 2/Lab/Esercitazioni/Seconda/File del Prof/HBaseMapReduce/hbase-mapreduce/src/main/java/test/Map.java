package test;

import java.io.IOException;
import java.util.NavigableMap;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map extends TableMapper<Text, Text> {

	@Override
	protected void map(ImmutableBytesWritable key, Result value,
			Mapper<ImmutableBytesWritable, Result, Text, Text>.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("Hello, I'm a mapper");
		NavigableMap<byte[], byte[]> familyMap = value.getFamilyMap("skills".getBytes());
		for(Entry<byte[], byte[]> e : familyMap.entrySet()) {
//			IntWritable score = new IntWritable(Integer.valueOf(Bytes.toString(e.getValue()))); 
			
			context.write(new Text(e.getKey()),new Text(String.valueOf(Bytes.toInt(e.getValue()))));	
		}
	}
}
