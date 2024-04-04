package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

//Global variable

import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static final String EOF = "-1";
    public static final String customers_path = "/e-commerce/customer_data/olist_customers_dataset.csv";
    public static final String products_path = "/e-commerce/product_data/olist_products_dataset.csv";
    public static final String orders_path = "/e-commerce/order_data/order_meta/olist_orders_dataset.csv";

    public static final String items_path = "/e-commerce/order_data/items/olist_order_items_dataset.csv";

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        // Loading hadoop configuration file in order to reach my cluster
        Configuration conf = new Configuration();
        conf.addResource("hdfs-site.xml");
        conf.addResource("core-site.xml");

        // Setup hadoop username
        System.setProperty("HADOOP_USER_NAME", "hadoop");

        while (true) {
            System.out.println("0: exit");
            System.out.println("1: read users");
            System.out.println("2: write hdfs/path");
            System.out.println("3: find");
            System.out.println("4: Create order");


            int choice = Integer.valueOf(s.nextLine());

            String path = "";
            //command 1 and 2 wait for a path before starting
            switch (choice) {
                case 0:
                    System.exit(10);
                    break;
                case 1:
                    read(s, conf, customers_path);
                    break;
                case 2:
                    write(s, conf, path);
                    break;
                case 3:
                    break;
                case 4:
                    createOrder(s, conf);
                    break;
                case 5:
                    break;
            }
        }
    }

    private static void createOrder(Scanner s, Configuration conf) {
        System.out.println("Creating order:");
        System.out.println("Insert order id");
        String id = s.nextLine();
        read(s, conf, customers_path);

        System.out.println("Insert customer id");

        String customer_id = s.nextLine();

        while (customer_id.isEmpty()){
            System.out.println("Insert a valid customer id");
            customer_id = s.nextLine();
        }

        // Show products
        System.out.println("Insert product id");
        read(s,conf, products_path);
        String product_id = s.nextLine();

        while (product_id.isEmpty()){
            System.out.println("Insert a valid product id");
            product_id = s.nextLine();
        }

        //Add a new line to the items file
        //|          items.order_id           | items.id  |         items.product_id          |          items.seller_id          | items.shipping_limit_date  | items.price  | items.freight_value  |

        String newItem = id + "," + "1" + "," + product_id + "," + "1" + "," + "2017-10-06 11:07:15" + "," + "100" + "," + "10";


        //Add a new line to the order file
        //|             orders.id             |        orders.customer_id         | orders.status  | orders.purchase_timestamp  |  orders.approved_at  | orders.delivered_carrier_date  | orders.delivered_customer_date  | orders.estimated_delivery_data  |

        String newOrder = id + "," + customer_id + "," + "delivered" + "," + "2017-10-02 10:56:33" + "," + "2017-10-02 11:07:15" + "," + "2017-10-06 17:30:08" + "," + "2017-10-10 21:25:13" + "," + "2017-10-18 00:00:00";

        //Open the file and append the new line for the item

        try{
            // Get the hdfs
            FileSystem hdfs = FileSystem.get(conf);

            // Get the file
            FSDataOutputStream stream = hdfs.append(new Path(items_path));

            // Wrap the stream into a buffered writer
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));

            // Write the new line
            writer.write(newItem + "\n");

            // Close the stream
            writer.flush();

            // Close the writer
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Open the file and append the new line for the order
        try {
            // Get the hdfs
            FileSystem hdfs = FileSystem.get(conf);

            // Get the file
            FSDataOutputStream stream = hdfs.append(new Path(orders_path));

            // Wrap the stream into a buffered writer
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));

            // Write the new line
            writer.write(newOrder + "\n");

            // Close the stream
            writer.flush();

            // Close the writer
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Order created");
        return;
    }




    private static void write(Scanner s, Configuration conf, String path) {
        //Objective: write data on file "path"
        /*
         *Objective: write data on file
         *	"path" is not a standard file -> Return a failure to the user
         *	"path" is a standard file:
         *		Override the file, or
         *		Append to the file
         *	"path" does not exists -> create it and write on it*/
        try {

            // Get HDFS instance
            FileSystem hdfs = FileSystem.get(conf);

            // initialize an outputstream
            FSDataOutputStream stream = null;

            if (hdfs.exists(new Path(path))) {
                FileStatus status = hdfs.getFileStatus(new Path(path));
                if (status.isDirectory())
                    System.out.println("unable to write on folder");
                else {
                    do {
                        System.out.println("File already exits. Choose among a (append), o (override), or no (do not write on this file)");
                        String ans = s.nextLine();
                        if (ans.equals("a")) {
                            // asking hdfs to open a stream on "path" for appending data
                            stream = hdfs.append(new Path(path));
                            break;
                        } else if (ans.equals("o")) {

                            // asking hdfs to delete "path"
                            boolean deleted = hdfs.delete(new Path(path), false);
                            if (deleted) {
                                // asking hdfs to create "path" and get an outputstream on it
                                stream = hdfs.create(new Path(path));
                                break;
                            } else System.out.println("Unable to override the selected file");
                        } else if (ans.equals("no")) return;
                    } while (true);
                }
            } else {
                // "path" does not exists so let create it
                // asking hdfs to create "path" and get an outputstream on it
                stream = hdfs.create(new Path(path));
            }
            if (stream != null) {
                String line = s.nextLine();

                //the obtained stream is wrapped into a java.io BufferedWriter
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
                while (!line.equals(EOF)) {
                    writer.write(line + "\n");
                    line = s.nextLine();
                }
                //close the stream to flush remaining data
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param s    is the scanner
     * @param conf is the configuration
     * @param path is the path to read
     */
    private static void read(Scanner s, Configuration conf, String path) {
        // TODO Auto-generated method stub
        int batch_size = 3;
        try {
            /*
             * Objective read line of "path" file in a lazy way (3 lines at time)
             * 		If "path" does not exists or it is a directory -> the method fails
             * 		*/

            // Get HDFS instance
            FileSystem hdfs = FileSystem.get(conf);

            // check "path" existence
            if (!hdfs.exists(new Path(path))) {
                System.out.println("No such file or directory");
                return;
            }

            // "path" exists so, let us check if it is a file or not
            FileStatus status = hdfs.getFileStatus(new Path(path));
            if (status.isFile()) {
                // Get an input stream on "path" with open method
                FSDataInputStream stream = hdfs.open(new Path(path));

                // stream is wrapped into a buffered writer
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                boolean end = false;
                boolean stop = false;
                do {
                    for (int i = 0; i < batch_size; i++) {
                        if (reader.ready()) {
                            System.out.println(reader.readLine());
                        } else end = true;
                    }
                    System.out.println("Print more to continue ...");
                    if (!s.nextLine().equals("more"))
                        stop = true;
                } while (!end && !stop);
                stream.close();
            } else {
                System.out.println("Unable to read a folder");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
