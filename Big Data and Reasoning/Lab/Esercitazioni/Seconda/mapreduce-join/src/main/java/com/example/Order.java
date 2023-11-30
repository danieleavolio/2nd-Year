package com.example;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Order extends CustomObject{
        
        String order_id;
        String customer_id;
        String order_status;
        String order_purchase_timestamp;
        String order_approved_at;
        String order_delivered_carrier_date;
        String order_delivered_customer_date;
        String order_estimated_delivery_date;
    
        public Order(String order_id, String customer_id, String order_status, String order_purchase_timestamp, String order_approved_at, String order_delivered_carrier_date, String order_delivered_customer_date, String order_estimated_delivery_date) {
            this.order_id = order_id;
            this.customer_id = customer_id;
            this.order_status = order_status;
            this.order_purchase_timestamp = order_purchase_timestamp;
            this.order_approved_at = order_approved_at;
            this.order_delivered_carrier_date = order_delivered_carrier_date;
            this.order_delivered_customer_date = order_delivered_customer_date;
            this.order_estimated_delivery_date = order_estimated_delivery_date;
        }
    
        public Order(String string) {
            this.order_id = string;
        }

        public String getOrder_id() {
            return order_id;
        }
    
        public String getCustomer_id() {
            return customer_id;
        }
    
        public String getOrder_status() {
            return order_status;
        }
    
        public String getOrder_purchase_timestamp() {
            return order_purchase_timestamp;
        }
    
        public String getOrder_approved_at() {
            return order_approved_at;
        }
    
        public String getOrder_delivered_carrier_date() {
            return order_delivered_carrier_date;
        }
    
        public String getOrder_delivered_customer_date() {
            return order_delivered_customer_date;
        }
    
        public String getOrder_estimated_delivery_date() {
            return order_estimated_delivery_date;
        }
        
        @Override
        public String toString() {
            return "Order{" + "order_id=" + order_id + ", customer_id=" + customer_id + ", order_status=" + order_status + ", order_purchase_timestamp=" + order_purchase_timestamp + ", order_approved_at=" + order_approved_at + ", order_delivered_carrier_date=" + order_delivered_carrier_date + ", order_delivered_customer_date=" + order_delivered_customer_date + ", order_estimated_delivery_date=" + order_estimated_delivery_date + '}';
        }

        @Override
        public void readFields(DataInput arg0) throws IOException {

            // TODO Auto-generated method stub
            // Usa readUTF
            order_id = arg0.readUTF();
            customer_id = arg0.readUTF();
            order_status = arg0.readUTF();
            order_purchase_timestamp = arg0.readUTF();
            order_approved_at = arg0.readUTF();
            order_delivered_carrier_date = arg0.readUTF();
            order_delivered_customer_date = arg0.readUTF();
            order_estimated_delivery_date = arg0.readUTF();
        }

        @Override
        public void write(DataOutput arg0) throws IOException {
            // TODO Auto-generated method stub
            // Usa writeUTF 
            
            arg0.writeUTF(order_id);
            arg0.writeUTF(customer_id);
            arg0.writeUTF(order_status);
            arg0.writeUTF(order_purchase_timestamp);
            arg0.writeUTF(order_approved_at);
            arg0.writeUTF(order_delivered_carrier_date);
            arg0.writeUTF(order_delivered_customer_date);
            arg0.writeUTF(order_estimated_delivery_date);

        }

        @Override
        public int compareTo(CustomObject o) {
            // TODO Auto-generated method stub

            Order other = (Order) o;
            return this.order_id.compareTo(other.order_id);

        }
        
}
