package com.example;

import java.util.ArrayList;
import java.util.List;

public class Orders {

    private String timestamp;
    private String customer_id;
    private List product;
    private String totalAmount;
    private String status;

    public Orders(String timestamp, String customer_id, List product, String totalAmount, String status) {
        this.timestamp = timestamp;
        this.customer_id = customer_id;
        this.product = product;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Orders() {
        this.timestamp = "";
        this.customer_id = "";
        this.product = new ArrayList();
        this.totalAmount = "";
        this.status = "";
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getCustomer_id() {
        return this.customer_id;
    }

    public List getProduct() {
        return this.product;
    }

    public String getTotalAmount() {
        return this.totalAmount;
    }

    public String getStatus() {
        return this.status;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }



    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public void setProduct(List product) {
        this.product = product;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return "Orders(timestamp=" + getTimestamp() + ", customer_id=" + getCustomer_id() + ", product=" + getProduct() + ", totalAmount=" + getTotalAmount() + ", status=" + getStatus() + ")";
    }

    


}
