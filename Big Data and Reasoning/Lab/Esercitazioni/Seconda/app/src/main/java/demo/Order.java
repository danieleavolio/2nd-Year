package demo;

public class Order {
    private String order_id;
    private String customer_id;
    private String status;
    private String order_purchase_timestamp;
    private String product_id;
    private String product_category_name;
    private String price;

    public Order(String order_id, String customer_id, String status, String order_purchase_timestamp, String product_id, String product_category_name, String price) {
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.status = status;
        this.order_purchase_timestamp = order_purchase_timestamp;
        this.product_id = product_id;
        this.product_category_name = product_category_name;
        this.price = price;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_purchase_timestamp() {
        return order_purchase_timestamp;
    }

    public void setOrder_purchase_timestamp(String order_purchase_timestamp) {
        this.order_purchase_timestamp = order_purchase_timestamp;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_category_name() {
        return product_category_name;
    }

    public void setProduct_category_name(String product_category_name) {
        this.product_category_name = product_category_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id='" + order_id + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", status='" + status + '\'' +
                ", order_purchase_timestamp='" + order_purchase_timestamp + '\'' +
                ", product_id='" + product_id + '\'' +
                ", product_category_name='" + product_category_name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
