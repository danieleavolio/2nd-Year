package test;

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
	
	public Order() {
		// TODO Auto-generated constructor stub
	}
	
	public Order(String [] fields) {
		this.order_id = fields[0];
		this.customer_id = fields[1];
		this.order_status = fields[2];
		this.order_purchase_timestamp = fields[3];
		this.order_approved_at = fields[4];
		this.order_delivered_carrier_date = fields[5];
		this.order_delivered_customer_date = fields[6];
		this.order_estimated_delivery_date = fields[7];
	}
	
	public Order(String order_id, String customer_id, String order_status, String order_purchase_timestamp,
			String order_approved_at, String order_delivered_carrier_date, String order_delivered_customer_date,
			String order_estimated_delivery_date) {
		this.order_id = order_id;
		this.customer_id = customer_id;
		this.order_status = order_status;
		this.order_purchase_timestamp = order_purchase_timestamp;
		this.order_approved_at = order_approved_at;
		this.order_delivered_carrier_date = order_delivered_carrier_date;
		this.order_delivered_customer_date = order_delivered_customer_date;
		this.order_estimated_delivery_date = order_estimated_delivery_date;
	}
	
	

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getOrder_status() {
		return order_status;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public String getOrder_purchase_timestamp() {
		return order_purchase_timestamp;
	}

	public void setOrder_purchase_timestamp(String order_purchase_timestamp) {
		this.order_purchase_timestamp = order_purchase_timestamp;
	}

	public String getOrder_approved_at() {
		return order_approved_at;
	}

	public void setOrder_approved_at(String order_approved_at) {
		this.order_approved_at = order_approved_at;
	}

	public String getOrder_delivered_carrier_date() {
		return order_delivered_carrier_date;
	}

	public void setOrder_delivered_carrier_date(String order_delivered_carrier_date) {
		this.order_delivered_carrier_date = order_delivered_carrier_date;
	}

	public String getOrder_delivered_customer_date() {
		return order_delivered_customer_date;
	}

	public void setOrder_delivered_customer_date(String order_delivered_customer_date) {
		this.order_delivered_customer_date = order_delivered_customer_date;
	}

	public String getOrder_estimated_delivery_date() {
		return order_estimated_delivery_date;
	}

	public void setOrder_estimated_delivery_date(String order_estimated_delivery_date) {
		this.order_estimated_delivery_date = order_estimated_delivery_date;
	}

	
	@Override
	public String toString() {
		return "Order [order_id=" + order_id + ", customer_id=" + customer_id + ", order_status=" + order_status
				+ ", order_purchase_timestamp=" + order_purchase_timestamp + ", order_approved_at=" + order_approved_at
				+ ", order_delivered_carrier_date=" + order_delivered_carrier_date + ", order_delivered_customer_date="
				+ order_delivered_customer_date + ", order_estimated_delivery_date=" + order_estimated_delivery_date
				+ "]";
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Writing order");
		out.writeUTF(order_id);
		out.writeUTF(customer_id);
		out.writeUTF(order_status);
		out.writeUTF(order_purchase_timestamp);
		out.writeUTF(order_approved_at);
		out.writeUTF(order_delivered_carrier_date);
		out.writeUTF(order_delivered_customer_date);
		out.writeUTF(order_estimated_delivery_date);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Reading order");
		this.order_id = in.readUTF();
		this.customer_id = in.readUTF();
		this.order_status = in.readUTF();
		this.order_purchase_timestamp = in.readUTF();
		this.order_approved_at = in.readUTF();
		this.order_delivered_carrier_date = in.readUTF();
		this.order_delivered_customer_date = in.readUTF();
		this.order_estimated_delivery_date = in.readUTF();
	}

	@Override
	public int compareTo(CustomObject o) {
		// TODO Auto-generated method stub
		if(o instanceof Order) {
			Order other = (Order) o;
			return this.order_id.compareTo(other.order_id);
		}
		throw new IllegalArgumentException("Error While Comparing Order: Class mistmatch");
	}

}
