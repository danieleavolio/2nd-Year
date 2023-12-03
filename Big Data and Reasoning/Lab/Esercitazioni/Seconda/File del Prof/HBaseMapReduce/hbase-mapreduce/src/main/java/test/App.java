package test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;


public class App {
	
	public static void main(String[] args) {
		try {
			
			System.setProperty("HADOOP_USER_NAME", "hadoop");

			Job j = Job.getInstance(App.getHbaseConf(), "read from hbase");
			j.setJarByClass(App.class);
			Scan s=new Scan();
			s.addFamily("skills".getBytes());
			TableMapReduceUtil.initTableMapperJob("employee", s, Map.class, Text.class, Text.class, j);
			j.setCombinerClass(Combiner.class);
			TableMapReduceUtil.initTableReducerJob("stats", Red.class, j);
			j.setOutputFormatClass(TableOutputFormat.class);
			j.waitForCompletion(false);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Configuration getHbaseConf() {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
		return conf;
	}

}
