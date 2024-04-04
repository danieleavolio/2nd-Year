package com.example;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class Data implements WritableComparable<Data> {

    String className;
    CustomObject realObject;

    
    @Override
    public int compareTo(Data o) {
        return this.realObject.compareTo(o.realObject);
    }

    @Override
    public void readFields(DataInput arg0) throws IOException {
        this.className = arg0.readUTF();
        try {
            realObject = (CustomObject) Class.forName(className).newInstance();
            realObject.readFields(arg0);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void write(DataOutput arg0) throws IOException {
        arg0.writeUTF(className);
        if(realObject!=null)
            realObject.write(arg0);
    }
   
}
