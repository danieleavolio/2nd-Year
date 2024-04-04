package com.example;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Main {

    public static String master = "local";
    public static Dataset<Row> ordered_items;
    public static Dataset<Row> orders;
    public static Dataset<Row> producs;

    // Problema: risolvere le seguent cose
    // 1. Importare i dati da raw-data nel filesystem
    // 2. Creare un nuovo dataset che contiene i dati importati nel punto
    // precedente e uniore i dati in un unico dataset chiamato ordereditems
    // 3. Creare un dataset che dataset che avra questo schema:
    // -timestamp of the order
    // -customer id
    // - Lista di prodotto ordinai. Per ogni prodotto vogliamo id, category, price e
    // quantita che e' stata ordinata
    // - Incasso totale dell'ordine
    // - Status (Delivered/cancelled)

    // 4. Fare le seguenti query:
    // 4.1. Prendere l'incasso totale per un mese preciso
    // 4.2. Per ogni anno selezionare l'income totale
    // 4.3. Per un determinato utente selezionare quanto speso per ogni categoria
    // 4.4 Contare gli ordini e la categoria piu popolare per ogni mese

    public static void main(String[] args) {
        System.out.println("Prova di Sparkingssss!");

        // Reading of the data from the file system
        String path = "raw-data";

        // Leggiamo i datozzi diddio
        readData(path);

        // Creare un nuovo dataset che contiene i dati importati nel punto
        // precedente e uniore i dati in un unico dataset chiamato ordereditems
        Dataset<Row> ordereditems;
        ordereditems = orders.join(ordered_items, orders.col("order_id").equalTo(ordered_items.col("order_id")))
                .join(producs, ordered_items.col("product_id").equalTo(producs.col("product_id")));

        ordereditems.printSchema();

        // Creare un dataset che dataset che avra lo schema descritto sopra
        Dataset<Orders> refinedOrders;

        // refinedOrders = buildOrders(ordereditems);

        // Metodo 2
        refinedOrders = buildOrder2(ordereditems);
        refinedOrders.show();

        // Query
        query1(refinedOrders);
        query2(refinedOrders);
        query3(ordereditems);
        query4(ordereditems);
    }

    private static void query4(Dataset<Row> ordereditems) {

        // Initialize Spark Session
        SparkSession spark = SparkSession.builder().master(master)
                .appName("Spark SQL Old Example").getOrCreate();


        // 4.4 Contare gli ordini e la categoria piu popolare per ogni mese

        Dataset<Row> monthOrders = ordereditems.map(new MapFunction<Row, MonthOrders>() {

            @Override
            public MonthOrders call(Row row) throws Exception {
                String timestamp = row.getAs("order_purchase_timestamp");
                String product_category_name = row.getAs("product_category_name");

                // Prendiamo il mese
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
                String month = new SimpleDateFormat("MM").format(date);

                MonthOrders monthOrders = new MonthOrders();
                monthOrders.setMonth(month);
                monthOrders.setProduct_category_name(product_category_name);

                return monthOrders;
            }
        }, Encoders.bean(MonthOrders.class)).groupBy("month", "product_category_name").count();
        monthOrders.createOrReplaceTempView("monthOrders");

        monthOrders.show();

        // +-----+---------------------+-----+
        // |month|product_category_name|count|
        // +-----+---------------------+-----+
        // |   12|              bebidas|    4|
        // |   07|                 null|   18|
        // |   03|   ferramentas_jardim|  142|

        // Per ogni mese vogliamo sapere la categoria piu popolare
        // Per ogni mese vogliamo sapere il numero di ordini

       
        // Trova il conteggio massimo per ogni mese
        Dataset<Row> maxCounts = spark.sql("SELECT month, max(count) as max_count FROM monthOrders GROUP BY month");

        // Crea una vista temporanea per i conteggi massimi
        maxCounts.createOrReplaceTempView("maxCounts");

        // Unisci i conteggi massimi con il DataFrame originale per ottenere il nome della categoria
        Dataset<Row> mostPopularCategory = spark.sql("SELECT m.month, mo.product_category_name, m.max_count " +
                                                    "FROM monthOrders mo " +
                                                    "JOIN maxCounts m ON mo.month = m.month AND mo.count = m.max_count");

        // Sort by month
        mostPopularCategory = mostPopularCategory.orderBy("month");
        
        mostPopularCategory.show();



        



       

    }



    private static void query3(Dataset<Row> ordereditems) {
        // 4.3. Per un determinato utente selezionare quanto speso per ogni categoria

        // group by user id e category e sum su total amount
        // Change price to double
        ordereditems = ordereditems.withColumn("price", ordereditems.col("price").cast("double"));
        
        ordereditems.select("customer_id", "product_category_name", "price").groupBy("customer_id", "product_category_name")
                .sum("price").show();

    }

    private static void query2(Dataset<Orders> refinedOrders) {
        // 4.2. Per ogni anno selezionare l'income totale

        refinedOrders.map(new MapFunction<Orders, YearIncome>() {

            @Override
            public YearIncome call(Orders order) throws Exception {
                String timestamp = order.getTimestamp();
                String totalAmount = order.getTotalAmount();

                // Prendiamo l'anno
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
                String year = new SimpleDateFormat("yyyy").format(date);

                YearIncome yearIncome = new YearIncome();
                yearIncome.setYear(year);
                yearIncome.setTotalAmount(Double.parseDouble(totalAmount));

                return yearIncome;
            }
        }, Encoders.bean(YearIncome.class)).groupBy("year").sum("totalAmount").show();
    }

    private static void query1(Dataset<Orders> refinedOrders) {
        // 4.1. Prendere l'incasso totale per un mese preciso
        refinedOrders.map(new MapFunction<Orders, MonthIncome>() {

            @Override
            public MonthIncome call(Orders order) throws Exception {
                String timestamp = order.getTimestamp();
                String totalAmount = order.getTotalAmount();

                // Prendiamo il mese
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
                String month = new SimpleDateFormat("MM").format(date);


                MonthIncome monthIncome = new MonthIncome();
                monthIncome.setMonth(month);
                monthIncome.setTotalAmount(Double.parseDouble(totalAmount));

                return monthIncome;
            }
        }, Encoders.bean(MonthIncome.class)).groupBy("month").sum("totalAmount").show();
    }

    private static Dataset<Orders> buildOrder2(Dataset<Row> ordereditems) {
        return ordereditems.map(new MapFunction<Row,Orders>(){
            @Override
            public Orders call(Row row) throws Exception {
                String timestamp = row.getAs("order_purchase_timestamp");
                String customer_id = row.getAs("customer_id");

                // Per ogni prodotto ci creaimo un oggetto Product
                // e poi lo mettiamo in una lista
                Product product = new Product();
                product.setProduct_id(row.getAs("product_id"));
                product.setCategory(row.getAs("product_category_name"));
                product.setPrice(row.getAs("price"));
                product.setQuantity("1");

                Orders order = new Orders();
                order.setTimestamp(timestamp);
                order.setCustomer_id(customer_id);
                List<Product> products = new ArrayList<Product>();
                products.add(product);

                order.setProduct(products);

                //Calcolo importo totale
                double total_amount = 0;

                total_amount = Double.parseDouble(row.getAs("price")) * Double.parseDouble(row.getAs("price"));

                order.setTotalAmount(String.valueOf(total_amount));
                order.setStatus(row.getAs("status"));
                return order;
            }
        }
        ,Encoders.bean(Orders.class));
    }

    private static Dataset<Orders> buildOrders(Dataset<Row> ordereditems) {
        // Conviene crearsi una classe che rappresenta l'ordine
        // e poi mappare i dati in questa classe
        // poi si crea il dataset
        Dataset<Orders> refinedOrders = ordereditems.map(new MapFunction<Row, Orders>() {

            @Override
            public Orders call(Row row) throws Exception {
                String timestamp = row.getAs("order_purchase_timestamp");
                String customer_id = row.getAs("customer_id");

                // For the product we want to store the id, category, price and quantity
                String product_id = row.getAs("product_id");
                String product_category_name = row.getAs("product_category_name");
                String price = row.getAs("price");
                // Per quantity possiamo fare un count con un groupby
                // Per ora mettiamo 1
                String quantity = "1";

                String product = product_id + " " + product_category_name + " " + price + " " + quantity;

                // Per total amount possiamo fare un sum con un groupby
                // Per ora mettiamo price
                String total_amount = price;
                String status = row.getAs("status");

                return null;
            }
        }, Encoders.bean(Orders.class));

        return refinedOrders;

    }

    private static void readData(String path) {

        // Initialize Spark Session
        SparkSession spark = SparkSession.builder().master(master)
                .appName("Spark SQL Old Example").getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");

        // Delete the wareouse if exists
        spark.sql("DROP DATABASE IF EXISTS `spark-warehouse` CASCADE");

        // Order: order_id, customer_id, status, order_purchase_timestamp
        // Product: product_id, product_category_name
        // Ordered_items: order_id, order_item_id, product_id, price
        ordered_items = spark.read().schema("order_id string, order_item_id string, product_id string, price string")
                .csv(path + "/ordered_items.csv");

        orders = spark.read()
                .schema("order_id string, customer_id string, status string, order_purchase_timestamp string")
                .csv(path + "/orders.csv");

        producs = spark.read().schema("product_id string, product_category_name string")
                .csv(path + "/product.csv");

        ordered_items.printSchema();
        orders.printSchema();
        producs.printSchema();
    }
}