package org.example;

import org.apache.hadoop.hbase.TableName;

public class TableSchema {
    public static final String ORDERS = "orders";
    public static final String PRODUCTS_FAMILY = "orderedProducts";
    public static final String STATUS_FAMILY = "orderInformation";
    public static final String STATUS_QUALIFIER = "orderStatus";
    public static final String AMOUNT_QUALIFIER = "orderFinalAmount";
}
