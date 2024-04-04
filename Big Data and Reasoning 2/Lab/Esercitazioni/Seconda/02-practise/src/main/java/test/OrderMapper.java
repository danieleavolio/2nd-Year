package test;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class OrderMapper extends Mapper<LongWritable, Text, Text, Data> {

	// order_id;customer_id;order_status;order_purchase_timestamp;order_approved_at;order_delivered_carrier_date;order_delivered_customer_date;order_estimated_delivery_date
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Data>.Context context)
			throws IOException, InterruptedException {
		Order o = new Order(value.toString().split(";"));
		context.write(new Text(o.getOrder_id()),new Data(o,Order.class.getName()));
	}
}
