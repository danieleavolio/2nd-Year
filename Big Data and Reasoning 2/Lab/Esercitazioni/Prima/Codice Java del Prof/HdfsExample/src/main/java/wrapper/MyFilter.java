package wrapper;

import java.io.IOException;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class MyFilter implements PathFilter{
	
	FileSystem hdfs;
	String keyword;
	long maxSize;
	
	public MyFilter(FileSystem hdfs, String keyword, long maxSize) {
		super();
		this.hdfs = hdfs;
		this.keyword = keyword;
		this.maxSize = maxSize;
	}

	@Override
	public boolean accept(Path path) {
		if(!path.getName().contains(keyword)) return false;
		try {
			FileStatus status = hdfs.getFileStatus(path);
			if(status.isFile() && status.getLen() < maxSize) {
				return true;
			}
		} catch (IOException e) {
			System.err.println("Error filtering "+path.getName());
		}
		return false;
	}
	
	
}