package com.example;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class OrderReducer extends TableReducer<Text, Data, NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<Data> values, Reducer<Text, Data, NullWritable, Mutation>.Context arg2)
            throws IOException, InterruptedException {
        // cosa ci facciamo qua dentros?

        // CHiave -> Order_id

        Put p = new Put(Bytes.toBytes(key.toString()));

        for (Data data : values) {
            if(data.className.equals(Order.class.getName())){
                // Se abbiamo un ordire
                // Dobbiamo aggiungere i dati dell'ordine alla put
                Order o = (Order) data.realObject;
                p.addColumn(Bytes.toBytes("info"), Bytes.toBytes("customer"), Bytes.toBytes(o.customer_id))    ;
                p.addColumn(Bytes.toBytes("info"), Bytes.toBytes("approved_at"), Bytes.toBytes(o.order_approved_at))    ;
            } else {
                // Se abbiamo un item
                // Dobbiamo aggiungere i dati dell'item alla put
                Item i = (Item) data.realObject;
                p.addColumn(Bytes.toBytes("item"), Bytes.toBytes(i.product_id+ "_"+i.seller_id+"_"+i.order_item_id), Bytes.toBytes(i.price));
                SerializationUtils.serialize(getClass());

                
            }

        }
    }
}
