package test;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

public class Data implements WritableComparable<Data>,Writable{
	
	CustomObject obj;
	String className;

	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	public Data() {
		// TODO Auto-generated constructor stub
	}
	public Data(CustomObject obj,String className) {
		this.obj = obj;
		this.className = className;
	}

	public CustomObject getObj() {
		return obj;
	}

	public void setObj(CustomObject obj) {
		this.obj = obj;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return obj.toString();
	}
	@Override
	public int compareTo(Data o) {
		// TODO Auto-generated method stub
		return obj.compareTo(o.obj);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		if(obj == null) init();
		out.writeUTF(className);
		obj.write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		className=in.readUTF();
		System.out.println("Reading "+className);
		init();
		obj.readFields(in);
	}
	
	private void init() {
		// TODO Auto-generated method stub
//		
		try {
			obj = (CustomObject) Class.forName(className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
