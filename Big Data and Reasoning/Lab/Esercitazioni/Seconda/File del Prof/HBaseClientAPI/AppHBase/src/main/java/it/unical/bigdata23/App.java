package it.unical.bigdata23;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.ByteArrayComparable;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;

public class App {

	public static Configuration getConfiguration() {
		Configuration conf = new Configuration();		
		conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
		return conf;
	}
	public static void deleteEmployee() {
		
		try {
			System.setProperty("HADOOP_USER_NAME", "hadoop");

			System.out.println("LOG:	Connecting ...");
			Connection con = ConnectionFactory.createConnection(getConfiguration());
			
			System.out.println("LOG:	Connected");
			Table empTable = con.getTable(Constants.EMPLOYEE_TABLE_NAME);
			Delete del = new Delete("0001".getBytes());
			System.out.println("LOG:	Removing rowkey 0001");
			empTable.delete(del);
			System.out.println("LOG:	Removed");
			scanEmployee();
			
			System.out.println("LOG:	Removing family lives from rowkey 0002");
			Delete familyDelete = new Delete("0002".getBytes());
			familyDelete.addFamily(Constants.LIVES_BYTE);
			empTable.delete(familyDelete);
			System.out.println("LOG:	Removed");
			scanEmployee();
			
			System.out.println("LOG:	Removing column lives:street from rowkey 0003");
			Delete columnDelete = new Delete("0003".getBytes());
			columnDelete.addColumn(Constants.LIVES_BYTE, Constants.STREET_BYTE);
			empTable.delete(columnDelete);
			System.out.println("LOG:	Removed");
			
			scanEmployee();
			empTable.close();
			
			con.getAdmin().disableTable(Constants.EMPLOYEE_TABLE_NAME);
			System.out.println("LOG:	Removing family lives from employee table");
			con.getAdmin().deleteColumnFamily(Constants.EMPLOYEE_TABLE_NAME, Constants.LIVES_BYTE);
			System.out.println("LOG:	Removed");
			System.out.println("LOG:	Adding family born to employee table");
			con.getAdmin().addColumnFamily(Constants.EMPLOYEE_TABLE_NAME, ColumnFamilyDescriptorBuilder.newBuilder("born".getBytes()).build());
			System.out.println("LOG:	Added");
			con.getAdmin().enableTable(Constants.EMPLOYEE_TABLE_NAME);
			
			
			con.close();
			
			System.out.println("LOG:	Bye");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	public static void scanEmployee() {
		
		Scan s = new Scan();
		s.addFamily(Constants.USERINFO_BYTE);
		s.addFamily(Constants.LIVES_BYTE);
		s.addFamily(Constants.SKILLS_BYTE);
		
		printEmployee(s);
		
	}
	public static void printEmployee(Scan s) {
		try {
			System.out.println("LOG:	Connecting ...");
			Connection con = ConnectionFactory.createConnection(getConfiguration());
			System.out.println("LOG:	Connected");
			Table empTable = con.getTable(Constants.EMPLOYEE_TABLE_NAME);
			ResultScanner scanner = empTable.getScanner(s);
			System.out.println("LOG:	Scan Result");
			
			for(Result emp : scanner) {
				System.out.print("Employee"+Bytes.toString(emp.getRow())+": "+Bytes.toString(emp.getValue(Constants.USERINFO_BYTE, Constants.NAME_BYTE))+", "+Bytes.toString(emp.getValue(Constants.USERINFO_BYTE, Constants.SURNAME_BYTE)));
				System.out.println(" Lives: "+Bytes.toString(emp.getValue(Constants.LIVES_BYTE, Constants.CITY_BYTE))+", "+Bytes.toString(emp.getValue(Constants.LIVES_BYTE, Constants.STREET_BYTE)));
				System.out.println(" Skilles:");
				for (Entry<byte[], byte[]> pair : emp.getFamilyMap(Constants.SKILLS_BYTE).entrySet()) {
					System.err.println("      "+Bytes.toString(pair.getKey())+":"+Bytes.toInt(pair.getValue()));
				}
			}
			
			scanner.close();
			
			empTable.close();
			con.close();
			System.out.println("LOG:	Bye");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void scanEmployeeTo9() {
		
		Scan s = new Scan();
		s.addFamily(Constants.USERINFO_BYTE);
		s.addFamily(Constants.LIVES_BYTE);
		s.addFamily(Constants.SKILLS_BYTE);
		
		s.setFilter(new PrefixFilter(Bytes.toBytes("000")));
		
		printEmployee(s);
	
		
	}
	public static void scanEmployeeWith1() {
		
		Scan s = new Scan();
		s.addFamily(Constants.USERINFO_BYTE);
		s.addFamily(Constants.LIVES_BYTE);
		s.addFamily(Constants.SKILLS_BYTE);
		
		s.setFilter(new RowFilter(CompareOperator.EQUAL,new SubstringComparator("1")));
		
		printEmployee(s);
	
		
	}
	public static void scanEmployeeLangExpert(String lang) {
		
		Scan s = new Scan();
		s.addFamily(Constants.USERINFO_BYTE);
		s.addFamily(Constants.LIVES_BYTE);
		s.addFamily(Constants.SKILLS_BYTE);
		
		SingleColumnValueFilter f = new SingleColumnValueFilter(Constants.SKILLS_BYTE, Bytes.toBytes(lang), CompareOperator.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(8)));
		f.setFilterIfMissing(true);
		s.setFilter(f);
		
		printEmployee(s);
		
	}
	
	public static void scanEmployeeExpert(List<String> list) {
		
		Scan s = new Scan();
		s.addFamily(Constants.USERINFO_BYTE);
		s.addFamily(Constants.LIVES_BYTE);
		s.addFamily(Constants.SKILLS_BYTE);
		
		List<Filter> filters = new ArrayList<Filter>();
		for(String lang: list) {
			SingleColumnValueFilter f = new SingleColumnValueFilter(Constants.SKILLS_BYTE, Bytes.toBytes(lang), CompareOperator.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(8)));
			f.setFilterIfMissing(true);
			filters.add(f);
		}
		FilterList allPassed = new FilterList(FilterList.Operator.MUST_PASS_ALL,filters);
		s.setFilter(allPassed);
		
		printEmployee(s);
	
		
	}
	public static void scanEmployeeLocalExpert(List<String> langs) {
		
		Scan s = new Scan();
		s.addFamily(Constants.USERINFO_BYTE);
		s.addFamily(Constants.LIVES_BYTE);
		s.addFamily(Constants.SKILLS_BYTE);
		
		List<Filter> filters = new ArrayList<Filter>();
		for(String lang: langs) {
			SingleColumnValueFilter f = new SingleColumnValueFilter(Constants.SKILLS_BYTE, Bytes.toBytes(lang), CompareOperator.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(8)));
			f.setFilterIfMissing(true);
			filters.add(f);
		}
		FilterList allPassed = new FilterList(FilterList.Operator.MUST_PASS_ONE,filters);
		s.setFilter(allPassed);
		
		printEmployee(s);
		
	}
	
	public static void loadData(int numEmp) {

		System.out.println("LOG:	Acquiring Employee Data ...");
		List<Put> emps = new ArrayList<Put>();
		for(int i=0;i<numEmp;i++) {
			String prefix = i>8 ? (i > 98 ? "0": "00") :"000";
			Employee e = new Employee();
			System.out.println(e);
			Put putEmp = new Put((prefix+Employee.AUTO_INCREMENT).getBytes());
			putEmp.addColumn(Constants.USERINFO_BYTE, Constants.NAME_BYTE, e.getName().getBytes());
			putEmp.addColumn(Constants.USERINFO_BYTE, Constants.SURNAME_BYTE, e.getSurname().getBytes());
			putEmp.addColumn(Constants.LIVES_BYTE, Constants.CITY_BYTE, e.getCity().getBytes());
			putEmp.addColumn(Constants.LIVES_BYTE, Constants.STREET_BYTE, e.getStreet().getBytes());
			for(Entry<String, Integer> skill:e.getSkills().entrySet()) {
				putEmp.addColumn(Constants.SKILLS_BYTE, skill.getKey().getBytes(), Bytes.toBytes(skill.getValue()));
			}
			emps.add(putEmp);
			
		}
		System.out.println("LOG:	Acquired");
		try {
			System.out.println("LOG:	Connecting ...");
			Connection con = ConnectionFactory.createConnection(getConfiguration());
			System.out.println("LOG:	Connected");
			System.out.println("LOG:	Storing Employee ...");
			Table empTable = con.getTable(Constants.EMPLOYEE_TABLE_NAME);
			empTable.put(emps);
			empTable.close();
			con.close();
			System.out.println("LOG:	Employee Stored");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public static void createTable() {
		
		try {
			System.out.println("LOG:	Connecting ...");
			Connection con = ConnectionFactory.createConnection(getConfiguration());
			System.out.println("LOG:	Connected");
			Admin admin = con.getAdmin();
			System.out.println("LOG:	Checking Table exists");
			boolean tableExists = admin.tableExists(Constants.EMPLOYEE_TABLE_NAME);
			System.out.println("LOG:	Checked");
			if(tableExists) {
				System.out.println("LOG:	Deleting Old Table ...");
				admin.disableTable(Constants.EMPLOYEE_TABLE_NAME);
				admin.deleteTable(Constants.EMPLOYEE_TABLE_NAME);
				System.out.println("LOG:	Old Table Deleted");
			}
			
			TableDescriptorBuilder tableDescBuilder = TableDescriptorBuilder.newBuilder(Constants.EMPLOYEE_TABLE_NAME);
			
			ColumnFamilyDescriptorBuilder userinfoBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Constants.USERINFO_BYTE);
			ColumnFamilyDescriptorBuilder livesBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Constants.LIVES_BYTE);
			ColumnFamilyDescriptorBuilder skillsBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Constants.SKILLS_BYTE);
			
			tableDescBuilder.setColumnFamily(userinfoBuilder.build());
			tableDescBuilder.setColumnFamily(livesBuilder.build());
			tableDescBuilder.setColumnFamily(skillsBuilder.build());
			
			System.out.println("LOG:	Creating Employee Table ...");
			admin.createTable(tableDescBuilder.build());
			System.out.println("LOG:	Employee Table Created");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException {
		createTable();
		loadData(10);
		scanEmployee();
		deleteEmployee();
		scanEmployee();
		System.out.println("-------------------------------------------------");
		scanEmployeeTo9();
		System.out.println("-------------------------------------------------");
		scanEmployeeWith1();
		System.out.println("-------------------------------------------------");
		scanEmployeeLangExpert("Python");
		System.out.println("-------------------------------------------------");
		scanEmployeeLangExpert("C++");
		System.out.println("-------------------------------------------------");
		scanEmployeeExpert(Arrays.asList("Python","SQL"));
		System.out.println("-------------------------------------------------");
		scanEmployeeLocalExpert(Arrays.asList("Python","C++"));
		
//		Connection con = ConnectionFactory.createConnection(getConfiguration());
//		Scan s = new Scan();
//		Table t = con.getTable(TableName.valueOf(Constants.STATS));
//		ResultScanner scanner = t.getScanner(s);
//		for (Result result : scanner) {
//			for (Entry<byte[], byte[]> qualifier_data : result.getFamilyMap(Constants.EMPSTATS_BYTE).entrySet()) {
//				String key = Bytes.toString(qualifier_data.getKey());
//				float val = Bytes.toFloat(qualifier_data.getValue());
//				System.out.println(key+" "+val);
//			}
//		}
	}
}
