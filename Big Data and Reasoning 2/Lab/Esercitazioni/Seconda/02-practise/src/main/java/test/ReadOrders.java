package test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;

public class ReadOrders {

	public static void main(String[] args) {
		
		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
		
		try {
			Connection connection = ConnectionFactory.createConnection(conf);

			Scan s = getSingleProductScanner();
			ResultScanner resultScanner = connection.getTable(TableName.valueOf("orders")).getScanner(s);
			printOrders(resultScanner);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Scan getSingleProductScanner() {
		// TODO Auto-generated method stub
		Scan scan = new Scan();
		ColumnPrefixFilter filter = new ColumnPrefixFilter(Bytes.toBytes("0b0172eb0fd18479d29c3bc122c058c2"));
//		QualifierFilter filter = new QualifierFilter(CompareOperator.EQUAL, new SubstringComparator("5656537e588803a555b8eb41f07a944b"));
//		PrefixFilter filter = new PrefixFilter(Bytes.toBytes("00526a9d4ebde463baee25f386963ddc"));
		scan.setFilter(filter);
		return scan;
	}

	public static void printOrders(ResultScanner resultScanner) {
		for(Result res : resultScanner) {
			System.out.println("   Order: "+Bytes.toString(res.getRow()));
			for (Entry<byte[], byte[]> pair : res.getFamilyMap(Bytes.toBytes("info")).entrySet()) {
				System.out.println("      "+Bytes.toString(pair.getKey())+":"+Bytes.toString(pair.getValue()));	
			}
			for (Entry<byte[], byte[]> pair : res.getFamilyMap(Bytes.toBytes("items")).entrySet()) {
				String[] data = Bytes.toString(pair.getKey()).split("_");
				System.out.println("      Item: "+data[2]);
				System.out.println("         Product: "+data[0]);
				System.out.println("         Seller: "+data[1]);
				
				ItemPrice price = SerializationUtils.deserialize(pair.getValue());
				System.out.println("         Price: "+price.getPrice());
				System.out.println("         Freight Value: "+price.getFreight_value());
				
			}
			
		}
	}
}
