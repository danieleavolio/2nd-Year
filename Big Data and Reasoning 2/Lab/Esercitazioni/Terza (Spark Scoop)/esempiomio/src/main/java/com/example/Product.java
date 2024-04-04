package com.example;

public class Product {

    private String product_id;
    private String category;
    private String price;
    private String quantity;
    

    public Product(String product_id, String category, String price, String quantity) {
        this.product_id = product_id;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public Product() {
    }

    public String getCategory() {
        return category;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }



    @Override
    public String toString() {
        return "Product{" +
                "product_id='" + product_id + '\'' +
                ", category='" + category + '\'' +
                ", price='" + price + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }


}
