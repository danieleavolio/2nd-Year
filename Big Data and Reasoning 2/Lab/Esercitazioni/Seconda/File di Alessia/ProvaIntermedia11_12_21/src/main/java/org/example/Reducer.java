package org.example;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Reducer extends TableReducer<Text, Text, NullWritable> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, org.apache.hadoop.mapreduce.Reducer<Text, Text, NullWritable, Mutation>.Context context) throws IOException, InterruptedException {
        // my reducer has to create the new table on HBase and write every new order in the table
        // the input of my reducer is the following one: <order_id, information_about_order>
        // for the same order_id, I expect the same customer which has ordered maybe different products and different amount of item for each product
        Map<String, Integer> productsQuantity = new HashMap<>();
        Map<String, Float> productsPrice = new HashMap<>();
        Map<String, String> productsCategory = new HashMap<>();

        String currentKey = "";
        String orderStatus = "";

        for(Text value: values) {
            String[] fields = value.toString().split(",");
            String orderTimestamp = fields[3];
            String customerId = fields[1];
            String productId = fields[4];
            String priceProduct = fields[7];
            String categoryProduct = fields[5];
            orderStatus = fields[2];

            if (currentKey.equals("")) {
                currentKey = customerId+"-"+orderTimestamp;
            }

            if (productsQuantity.containsKey(productId)) {
                Integer quantity = productsQuantity.get(productId);
                productsQuantity.put(productId, quantity+1);
            }
            else productsQuantity.put(productId, 1);
            if(!productsCategory.containsKey(productId)) {
                productsCategory.put(productId, categoryProduct);
            }
            if (!productsPrice.containsKey(productId)) {
                productsPrice.put(productId, Float.parseFloat(priceProduct));
            }
        }
        Put p = new Put((currentKey).getBytes());
        Float orderTotalAmount = 0.0f;
        for (String product : productsPrice.keySet()) {
            orderTotalAmount += Float.valueOf(productsQuantity.get(product)*Float.valueOf(productsPrice.get(product)));
            String valueOfQualifier = productsQuantity.get(product) + "-" + productsPrice.get(product) + "-" + productsCategory.get(product);
            p.addColumn(TableSchema.PRODUCTS_FAMILY.getBytes(), product.getBytes(), valueOfQualifier.getBytes());
        }
        p.addColumn(TableSchema.STATUS_FAMILY.getBytes(), TableSchema.AMOUNT_QUALIFIER.getBytes(), String.valueOf(orderTotalAmount).getBytes());
        p.addColumn(TableSchema.STATUS_FAMILY.getBytes(), TableSchema.STATUS_QUALIFIER.getBytes(), orderStatus.getBytes());
        context.write(null, p);
    }
}
