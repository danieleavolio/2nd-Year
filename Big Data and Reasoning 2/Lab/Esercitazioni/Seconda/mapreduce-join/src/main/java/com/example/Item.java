package com.example;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Item extends CustomObject {
    
    String order_id;
    String order_item_id;
    String product_id;
    String seller_id;
    String price;
    String freight_value;

    public Item(String order_id, String order_item_id, String product_id, String seller_id, String price, String freight_value) {
        this.order_id = order_id;
        this.order_item_id = order_item_id;
        this.product_id = product_id;
        this.seller_id = seller_id;
        this.price = price;
        this.freight_value = freight_value;
    }

    public Item(String string) {
    }

    public Item(String[] split) {
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOrder_item_id() {
        return order_item_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getPrice() {
        return price;
    }

    public String getFreight_value() {
        return freight_value;
    }
    
    @Override
    public String toString() {
        return "Item{" + "order_id=" + order_id + ", order_item_id=" + order_item_id + ", product_id=" + product_id + ", seller_id=" + seller_id + ", price=" + price + ", freight_value=" + freight_value + '}';
    }

    @Override
    public void readFields(DataInput arg0) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readFields'");
    }

    @Override
    public void write(DataOutput arg0) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }

    @Override
    public int compareTo(CustomObject o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compareTo'");
    }

    
}
