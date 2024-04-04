package wrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsWrapper {
	
	public static final String EOF="-1";
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		
		// Loading hadoop configuration file in order to reach my cluster
		Configuration conf = new Configuration();
		conf.addResource("hdfs-site.xml");
		conf.addResource("core-site.xml");
		
		// Setup hadoop username
		System.setProperty("HADOOP_USER_NAME", "hadoop");

		while(true) {
			System.out.println("0: exit");
			System.out.println("1: read hdfs/path");
			System.out.println("2: write hdfs/path");
			System.out.println("3: find");
			
			
			int choice = Integer.valueOf(s.nextLine());

			String path = "";
			//command 1 and 2 wait for a path before starting
			if(choice > 0 && choice < 3) path=s.nextLine();
			switch(choice) {
				case 0:
					System.exit(10);
					break;
				case 1:
					read(s,conf,path);
					break;
				case 2:
					write(s, conf, path);
					break;
				case 3:
					find(s,conf);
					break;
				case 4:
					break;
				case 5:
					break;
			}
		}
	}
	private static void find(Scanner s, Configuration conf) {
		
		System.out.println("Insert a key word to be contained in the name of the file");
		String keyword = s.nextLine();
		System.out.println("Insert max file size");
		long maxSize = Long.valueOf(s.nextLine());
		System.out.println("Insert parent folder");
		String path = s.nextLine();
		
		//Objective: find all files in "path" such that the name contains "keyword" and the length is less than "maxSize"
		try {
			//Getting hdfs instance
			FileSystem hdfs = FileSystem.get(conf);
			
			//MyFilter implements the check reported in the objective
			FileStatus[] listStatus = hdfs.listStatus(new Path(path), new MyFilter(hdfs, keyword, maxSize));
			for (FileStatus status : listStatus) {
				System.out.println(status.getPath().getName()+": "+status.getLen());
			}
		} catch (IOException e) {}
	}
	private static void write(Scanner s, Configuration conf, String path) {
		//Objective: write data on file "path"
		/*
		 *Objective: write data on file
		 *	"path" is not a standard file -> Return a failure to the user
		 *	"path" is a standard file:
		 *		Override the file, or
		 *		Append to the file
		 *	"path" does not exists -> create it and write on it*/
		try {
			
			// Get HDFS instance
			FileSystem hdfs = FileSystem.get(conf);

			// initialize an outputstream
			FSDataOutputStream stream = null;
			
			if(hdfs.exists(new Path(path))) {
				FileStatus status = hdfs.getFileStatus(new Path(path));
				if(status.isDirectory())
					System.out.println("unable to write on folder");
				else {
					do {
						System.out.println("File already exits. Choose among a (append), o (override), or no (do not write on this file)");
						String ans = s.nextLine();
						if(ans.equals("a")) {
							// asking hdfs to open a stream on "path" for appending data
							stream = hdfs.append(new Path(path));
							break;
						}else if(ans.equals("o")) {

							// asking hdfs to delete "path"
							boolean deleted = hdfs.delete(new Path(path), false);
							if(deleted) {
								// asking hdfs to create "path" and get an outputstream on it
								stream = hdfs.create(new Path(path));
								break;
							}
							else System.out.println("Unable to override the selected file");
						}else if(ans.equals("no")) return;
					}while(true);
				}
			}else {
				// "path" does not exists so let create it
				// asking hdfs to create "path" and get an outputstream on it
				stream = hdfs.create(new Path(path));
			}
			if(stream != null) {
				String line = s.nextLine();
				
				//the obtained stream is wrapped into a java.io BufferedWriter
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
				while(!line.equals(EOF)) {
					writer.write(line+"\n");
					line = s.nextLine();
				}
				//close the stream to flush remaining data
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static void read(Scanner s, Configuration conf, String path) {
		// TODO Auto-generated method stub
		int batch_size=3;
		try {
			/*
			 * Objective read line of "path" file in a lazy way (3 lines at time)
			 * 		If "path" does not exists or it is a directory -> the method fails
			 * 		*/
			
			// Get HDFS instance
			FileSystem hdfs = FileSystem.get(conf);

			// check "path" existence
			if(!hdfs.exists(new Path(path))) {
				System.out.println("No such file or directory");
				return;
			}
			
			// "path" exists so, let us check if it is a file or not
			FileStatus status = hdfs.getFileStatus(new Path(path));
			if(status.isFile()) {
				// Get an input stream on "path" with open method
				FSDataInputStream stream = hdfs.open(new Path(path));
				
				// stream is wrapped into a buffered writer
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				boolean end = false;
				boolean stop = false;
				do{
					for (int i = 0; i < batch_size; i++) {
						if(reader.ready()) {
							System.out.println(reader.readLine());
						}else end = true;
					}
					System.out.println("Print more to continue ...");
					if(!s.nextLine().equals("more"))
						stop=true;
				}while(!end && !stop);
				stream.close();
			}else {
				System.out.println("Unable to read a folder");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

}