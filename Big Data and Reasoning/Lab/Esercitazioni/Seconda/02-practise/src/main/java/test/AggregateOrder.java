package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AggregateOrder extends TableReducer<Text, Data,NullWritable> {

	private final static byte[] infoFamily = Bytes.toBytes("info");
	private final static byte[] itemsFamily = Bytes.toBytes("items");
	
	private final static byte[] customerQualifier = Bytes.toBytes("customer");
	private final static byte[] statusQualifier = Bytes.toBytes("status");
	private final static byte[] deliveredCustomerQualifier = Bytes.toBytes("deliveredToCustomer");
	private final static byte[] deliveredCarrierQualifier = Bytes.toBytes("deliveredToCarrier");
	
	@Override
	protected void reduce(Text key, Iterable<Data> values, Reducer<Text, Data, NullWritable, Mutation>.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		Put order = new Put(Bytes.toBytes(key.toString()));
		for (Data current: values) {
			if(current.getClassName().equals(Order.class.getName())) {
				Order orderData = (Order) current.getObj();
				order.addColumn(infoFamily, customerQualifier, Bytes.toBytes(orderData.getCustomer_id()));
				order.addColumn(infoFamily, statusQualifier, Bytes.toBytes(orderData.getOrder_status()));
				order.addColumn(infoFamily, deliveredCarrierQualifier, Bytes.toBytes(orderData.getOrder_delivered_carrier_date()));
				order.addColumn(infoFamily, deliveredCustomerQualifier, Bytes.toBytes(orderData.getOrder_delivered_customer_date()));
			}
			else if(current.getClassName().equals(Item.class.getName())) {
				Item orderItem = (Item) current.getObj();
				
				String itemQualifier = orderItem.getProduct_id()+"_"+orderItem.getSeller_id()+"_"+orderItem.getOrder_item_id();
				ItemPrice price = new ItemPrice(Float.valueOf(orderItem.getPrice()), Float.valueOf(orderItem.getFreight_value()));
				
				order.addColumn(itemsFamily, Bytes.toBytes(itemQualifier), SerializationUtils.serialize(price));
			}
		}
		context.write(null, order);
		
	}
}
