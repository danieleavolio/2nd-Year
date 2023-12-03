package org.example;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;

/*
In the setup method the bloom fileter file is deserialized and loaded into the bloom filter.In the map method, the
departmentName is extracted from each input record and tested against the Bloom filter. If the word is a member, the
entire record is output to the file system.Ideally to load the bloom filter hot words we should be using
DistributedCache a hadoop utility that ensures that a file in HDFS is present on the local file system of each task that
requires that file for simplicity purpose i am loading it from my local file system. As we have trained the bloom filter
with PUBLIC LIBRARY department the output of the map reduce program will have only employee data relevant to PUBLIC
LIBRARY department.

Bloom filters can assist expensive operations by eliminating unnecessary ones. For example a Bloom filter can be
previously trained with IDs of all users that have a salary of more than x and use the Bloom filter to do an initial
test before querying the database to retrieve more information about each employee. By eliminating unnecessary queries,
we can speed up processing time.

 */
public class DepartmentBloomFilterMapper extends Mapper<Object, Text, Text, NullWritable> {
    private BloomFilter filter = new BloomFilter();
    protected void setup(Context context) throws IOException, InterruptedException {
        DataInputStream dataInputStream = new DataInputStream(
                new FileInputStream(context.getConfiguration().get("bloom_filter_file_location")));
        filter.readFields(dataInputStream);
        dataInputStream.close();
    }
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String data = value.toString();
        String[] field = data.split(",", -1);
        String department = null;
        if (null != field && field.length == 9 && field[3].length() > 0) {
            department = field[3];
            if (filter.membershipTest(new Key(department.getBytes()))) {
                context.write(value, NullWritable.get());
            }
        }
    }
}