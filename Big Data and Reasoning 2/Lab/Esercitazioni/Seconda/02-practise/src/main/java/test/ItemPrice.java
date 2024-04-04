package test;

import java.io.Serializable;

public class ItemPrice implements Serializable{
	
	private static final long serialVersionUID = 1L;
	Float price;
	Float freight_value;
	public ItemPrice(Float price, Float freight_value) {
		super();
		this.price = price;
		this.freight_value = freight_value;
	}
	
	public ItemPrice() {
		// TODO Auto-generated constructor stub
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getFreight_value() {
		return freight_value;
	}

	public void setFreight_value(Float freight_value) {
		this.freight_value = freight_value;
	}

	@Override
	public String toString() {
		return "Tmp [price=" + price + ", freight_value=" + freight_value + "]";
	}
	
}