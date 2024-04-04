package test;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Item extends CustomObject{
	
	String order_id;
	String order_item_id;
	String product_id;
	String seller_id;
	String price;
	String freight_value;
	
	public Item() {
		// TODO Auto-generated constructor stub
	}
	
	public Item(String [] fields) {
		super();
		this.order_id = fields[0];
		this.order_item_id = fields[1];
		this.product_id = fields[2];
		this.seller_id = fields[3];
		this.price = fields[4];
		this.freight_value = fields[5];
	}
	
	public Item(String order_id, String order_item_id, String product_id, String seller_id, String price,
			String freight_value) {
		super();
		this.order_id = order_id;
		this.order_item_id = order_item_id;
		this.product_id = product_id;
		this.seller_id = seller_id;
		this.price = price;
		this.freight_value = freight_value;
	}
	
	
	@Override
	public String toString() {
		return "Item [order_id=" + order_id + ", order_item_id=" + order_item_id + ", product_id=" + product_id
				+ ", seller_id=" + seller_id + ", price=" + price + ", freight_value=" + freight_value + "]";
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getOrder_item_id() {
		return order_item_id;
	}

	public void setOrder_item_id(String order_item_id) {
		this.order_item_id = order_item_id;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getFreight_value() {
		return freight_value;
	}

	public void setFreight_value(String freight_value) {
		this.freight_value = freight_value;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeUTF(this.order_id);
		out.writeUTF(this.order_item_id);
		out.writeUTF(this.product_id);
		out.writeUTF(this.seller_id);
		out.writeUTF(this.price);
		out.writeUTF(this.freight_value);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		this.order_id = in.readUTF();
		this.order_item_id = in.readUTF();
		this.product_id = in.readUTF();
		this.seller_id = in.readUTF();
		this.price = in.readUTF();
		this.freight_value = in.readUTF();
		
	}

	@Override
	public int compareTo(CustomObject o) {
		// TODO Auto-generated method stub
		if(o instanceof Item) {
			Item other = (Item) o;
			if(this.order_id.equals(other.order_id))
				if(this.order_item_id.equals(other.order_item_id))
					return 0;
				else return order_item_id.compareTo(other.order_item_id);
			else return this.order_id.compareTo(other.order_id);
		}
		throw new IllegalArgumentException("Error While Comparing Order: Class mistmatch");
	}

}
