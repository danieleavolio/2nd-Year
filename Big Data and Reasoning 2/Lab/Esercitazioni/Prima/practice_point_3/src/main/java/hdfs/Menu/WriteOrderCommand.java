package hdfs.Menu;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.SocketHandler;

public class WriteOrderCommand extends Hdfs implements Command{


    public WriteOrderCommand(FileSystem hdfs) throws Exception {
        super(hdfs);
    }

    private void insertOrder() throws Exception {

        ReadFileCommand read_customer = new ReadFileCommand(hdfs, "/e-commerce/customer_data/olist_customers_dataset.csv");
        ReadFileCommand read_products = new ReadFileCommand(hdfs, "/e-commerce/product_data/olist_products_dataset.csv");
        ReadFileCommand read_sellers = new ReadFileCommand(hdfs, "/e-commerce/seller_data/olist_sellers_dataset.csv");

        System.out.println("Insert an order_id");
        String order_id = scanner.nextLine();
        System.out.println("To insert a order please first choose a customer");
        System.out.println("Here the customer source");
        read_customer.read();
        System.out.println("Insert a customer id");
        String customer_id = scanner.nextLine();
        System.out.println("Then the products id");
        System.out.println("Here the product source");
        read_products.read();
        System.out.println("insert products id separated by comma");
        String products_string = scanner.nextLine();
        String[] products = products_string.split(",");

        if(products.length == 0) throw  new Exception("No products entered !");

        String path_order_meta = "/e-commerce/order_data/order_meta/olist_orders_dataset.csv" ;
        FSDataOutputStream outputStream = null ;
        try{
            // Asking to hdfs to append on file: path_order_meta
            outputStream = hdfs.append(new Path(path_order_meta));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            String line = String.join("," , new String[]{order_id,customer_id,"NULL",
                    "NULL","NULL","NULL","NULL","NULL",}) ;
            writer.write(line + "\n");
            outputStream.close();

            // Asking to hdfs to append on file: path_items
            String path_items = "/e-commerce/order_data/items/olist_order_items_dataset.csv" ;
            outputStream = hdfs.append(new Path(path_items));
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            for (String product_id: products) {
                line = String.join("," , new String[]{order_id,order_id + "-" + product_id,product_id,
                        "NULL","NULL","NULL","NULL","NULL",});
                writer.write(line + "\n");
            }
        }finally {
            if(outputStream != null)  outputStream.close();
        }

        System.out.println("Order inserted ! :)");
    }

    @Override
    public int execute() throws Exception {
        insertOrder();
        return Command.CONTINUE;
    }

    @Override
    public String getString() {
        return "insert an order";
    }
}
