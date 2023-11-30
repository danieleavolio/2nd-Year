package demo;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class OrdersMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] values = value.toString().split(",");
        Order order = new Order(values[0], values[1], values[2], values[3], values[4], values[5], values[6]);
        Put put = new Put(Bytes.toBytes(order.getOrder_id()));
        put.addColumn(Bytes.toBytes("order"), Bytes.toBytes("order_id"), Bytes.toBytes(order.getOrder_id()));
        put.addColumn(Bytes.toBytes("order"), Bytes.toBytes("customer_id"), Bytes.toBytes(order.getCustomer_id()));
        put.addColumn(Bytes.toBytes("order"), Bytes.toBytes("status"), Bytes.toBytes(order.getStatus()));
        put.addColumn(Bytes.toBytes("order"), Bytes.toBytes("order_purchase_timestamp"), Bytes.toBytes(order.getOrder_purchase_timestamp()));
        put.addColumn(Bytes.toBytes("items"), Bytes.toBytes("product_id"), Bytes.toBytes(order.getProduct_id()));
        put.addColumn(Bytes.toBytes("items"), Bytes.toBytes("product_category_name"), Bytes.toBytes(order.getProduct_category_name()));
        put.addColumn(Bytes.toBytes("items"), Bytes.toBytes("price"), Bytes.toBytes(order.getPrice()));
        context.write(new ImmutableBytesWritable(Bytes.toBytes(order.getOrder_id())), put);
    }
}