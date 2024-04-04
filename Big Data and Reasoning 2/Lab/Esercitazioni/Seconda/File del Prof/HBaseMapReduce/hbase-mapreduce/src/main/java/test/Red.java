package test;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Red extends TableReducer<Text, Text, NullWritable>{

	@Override
	protected void reduce(Text arg0, Iterable<Text> arg1,
			Reducer<Text, Text, NullWritable, Mutation>.Context arg2) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		float sum=0;
		float total=0;
		
		while(arg1.iterator().hasNext()) {
			String[] data = arg1.iterator().next().toString().split(":");
			sum+= Integer.valueOf(data[0]);
			total+= Integer.valueOf(data[1]);
		}
		Put out = new Put("stats-row".getBytes());
		out.addColumn("empstats".getBytes(), arg0.getBytes(), Bytes.toBytes(sum/total));
		arg2.write(null, out);
	}
}
