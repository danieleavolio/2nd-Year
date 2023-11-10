# Report for Assignment

## Problem 1

### Description

Replicate the filesystem in the hadoop hdfs

### Solution

Replicate e-commerce file system tree on your hdfs and define hive tables on top of this data

```bash
hdfs dfs -mkdir /ecommerce /ecommerce/orders ...
```

load the data in the hdfs

```bash
hdfs dfs -put /path/from/master /path/to/hdfs
```

result:

```bash
hdfs dfs -ls -R /ecommerce | sort
```

```bash
drwxr-xr-x   - hadoop supergroup          0 2023-11-10 10:39 /ecommerce/customer
drwxr-xr-x   - hadoop supergroup          0 2023-11-10 10:45 /ecommerce/order/items
drwxr-xr-x   - hadoop supergroup          0 2023-11-10 10:47 /ecommerce/product
drwxr-xr-x   - hadoop supergroup          0 2023-11-10 11:04 /ecommerce/seller
drwxr-xr-x   - hadoop supergroup          0 2023-11-10 11:07 /ecommerce/meta/category
drwxr-xr-x   - hadoop supergroup          0 2023-11-10 11:07 /ecommerce/meta/geo
drwxr-xr-x   - hadoop supergroup          0 2023-11-10 11:08 /ecommerce/meta
drwxr-xr-x   - hadoop supergroup          0 2023-11-10 11:08 /ecommerce/meta/payments
drwxr-xr-x   - hadoop supergroup          0 2023-11-10 11:25 /ecommerce/order
drwxr-xr-x   - hadoop supergroup          0 2023-11-10 11:25 /ecommerce/order/order_meta
-rw-r--r--   1 hadoop supergroup   15007509 2023-11-10 10:39 /ecommerce/order/items/order_items_dataset.csv
-rw-r--r--   1 hadoop supergroup     164555 2023-11-10 10:39 /ecommerce/seller/sellers_dataset.csv
-rw-r--r--   1 hadoop supergroup   17406718 2023-11-10 10:39 /ecommerce/order/order_meta/orders_dataset.csv
-rw-r--r--   1 hadoop supergroup    2338123 2023-11-10 10:39 /ecommerce/product/products_dataset.csv
-rw-r--r--   1 hadoop supergroup       2560 2023-11-10 10:39 /ecommerce/meta/category/product_category_name_translation.csv
-rw-r--r--   1 hadoop supergroup    5647704 2023-11-10 11:05 /ecommerce/meta/payments/order_payments_dataset.csv
-rw-r--r--   1 hadoop supergroup   59273442 2023-11-10 10:39 /ecommerce/meta/geo/geolocation_dataset.csv
-rw-r--r--   1 hadoop supergroup    8586180 2023-11-10 10:39 /ecommerce/customer/customers_dataset.csv
```

### Hive Tables

Create the database

```sql
create database ecommerce;
use ecommerce;
```

for simplicity we will use external tables to exploit all the files we loaded in the hdfs

```sql
create external table customer(
    id string,
    unique_id string,
    zip_code_prefix string,
    city string,
    state string
) row format delimited fields terminated by ',' lines terminated by '\n' stored as textfile location '/ecommerce/customer';
```

```sql
create external table orders(
    id string,
    customer_id string,
    status string,
    purchase_timestamp string,
    approved_at string,
    delivered_carrier_date string,
    delivered_customer_date string,
    estimated_delivery_date string
) row format delimited fields terminated by ',' lines terminated by '\n' stored as textfile location '/ecommerce/order/order_meta';
```

```sql
create external table items(
    id string,
    item_id string,
    product_id string,
    seller_id string,
    shipping_limit_date string,
    price string,
    freight_value string
) row format delimited fields terminated by ',' lines terminated by '\n' stored as textfile location '/ecommerce/order/items';
```

```sql
create external table products(
    product_id string,
    product_category_name string,
    product_name_lenght string,
    product_description_lenght string,
    product_photos_qty string,
    product_weight_g string,
    product_length_cm string,
    product_height_cm string,
    product_width_cm string
) row format delimited fields terminated by ',' lines terminated by '\n' stored as textfile location '/ecommerce/product';
```

```sql
create external table sellers(
    id string,
    zip_code_prefix string,
    city string,
    state string
) row format delimited fields terminated by ',' lines terminated by '\n' stored as textfile location '/ecommerce/seller';
```

```sql
create external table category(
    name string,
    name_english string
) row format delimited fields terminated by ',' lines terminated by '\n' stored as textfile location '/ecommerce/meta/category';
```

```sql
create external table geo(
    zip_code_prefix string,
    lat string,
    lng string,
    city string,
    state string
) row format delimited fields terminated by ',' lines terminated by '\n' stored as textfile location '/ecommerce/meta/geo';
```

```sql
create external table payments(
    id string,
    order_id string,
    sequential string,
    type string,
    installments string,
    value string
) row format delimited fields terminated by ',' lines terminated by '\n' stored as textfile location '/ecommerce/meta/payments';
```

#### Schema

```bash
show tables;
```

```bash
+-----------+
| tab_name  |
+-----------+
| category  |
| customer  |
| geo       |
| items     |
| orders    |
| payments  |
| products  |
| sellers   |
+-----------+
8 rows selected (0.082 seconds)
```

each table has loaded the data from the hdfs, and to check this we can run a simple query

```sql
select count(*) from customer limit 10;
```

(output omitted)
