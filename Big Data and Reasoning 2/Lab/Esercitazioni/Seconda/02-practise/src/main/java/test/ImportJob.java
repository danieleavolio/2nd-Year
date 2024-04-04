package test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class ImportJob {
	
	
	public static void main(String[] args){
		
		Configuration configuration = new Configuration();

		Job j = null;
		try {
			j = Job.getInstance(configuration,"ecommerce import");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			System.err.println("Job.getInstace() failed");
			System.exit(180);
		}
		j.setJarByClass(ImportJob.class);
		
		MultipleInputs.addInputPath(j, new Path(args[0]), TextInputFormat.class, OrderMapper.class);
		MultipleInputs.addInputPath(j, new Path(args[1]), TextInputFormat.class, ItemMapper.class);
		
		j.setMapOutputKeyClass(Text.class);
		j.setMapOutputValueClass(Data.class);
		
		try {
			TableMapReduceUtil.initTableReducerJob("orders", AggregateOrder.class, j);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    TextOutputFormat.setOutputPath(j, new Path("out"));
		
		try {
			j.waitForCompletion(true);
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			System.err.println("Job execution failed");
			System.exit(180);
		}
		
	}

}
