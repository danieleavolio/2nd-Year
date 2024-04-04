package com.example;

public class MonthOrders {

    private String month;
    private String product_category_name;

    public MonthOrders(String month, String product_category_name) {
        this.month = month;
        this.product_category_name = product_category_name;
    }

    public MonthOrders() {
    }

    public String getMonth() {
        return month;
    }

    public String getProduct_category_name() {
        return product_category_name;
    }   

    public void setMonth(String month) {
        this.month = month;
    }

    public void setProduct_category_name(String product_category_name) {
        this.product_category_name = product_category_name;
    }       

    @Override
    public String toString() {
        return "MonthOrders{" +
                "month='" + month + '\'' +
                ", product_category_name='" + product_category_name + '\'' +
                '}';
    }

}
