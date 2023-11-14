# Report for Assignment

## If HIVESERVER2 doesn't work
```bash
# This command change the permission of the scartch dir 
hadoop fs -chmod 1777 /user/hadoop/hive-tem-fold

# This command change the permission of the tmp dir
hadoop fs -chmod 1777 /tmp
```

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
    order_id string,
    id string,
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

## Problem 2

### Description

Compute the following queries:

1. For each customer, find the number of order with at least 2 items
1. Find active customers for each year. A customer is active if it has at least three order
   in a given year.
1. For each year and for each customer city, compute the total income for the company
   (i.e. the sum of the total price of each order)
1. Find the three most frequent categories (possibly english) among e-commerce product
1. Find for each product the number of sold items and the total income
1. Find product category (possibly english) compute of sold items and the total income

### Solution

#### 1. For each customer, find the number of order with at least 2 items

Works when it feels like it. Sometimes I have to delete `/tmp` on hdfs.
I don't even know.

```sql
select c.id, count(distinct o.id) as orders
from customer as c, orders as o, items as i1, items as i2
where c.id = o.customer_id and o.id = i1.order_id and o.id = i2.order_id and i1.id <> i2.id
group by c.id;
```

#### Find active customers for each year

poi vediamo...


## Problem 4

Create a Data Warehouse.

The data warehouse should be populated using data coming from:

- Products that have a category assigned
- Orders that have only products with categories assigned
- Orders thta have a valid date. i.e. ***delivered_carried_date*** and ***delivered_custom_date*** are not null or missing

### The fact table is **Order_Product**. 
- **customer_zip_code**: zip code of the customer which made the order
- **seller_zip_ccode**: zip code of the seller which sold the product in the order
- **product_id**: product identifier
- **purchase_day**: day of the purchase of the order
- **income**: Sum of the price of the items in the order
- **quantity**: Number of ordered products items
- **delivery_time**: Difference in days between **delivered_custom_date** and **delivered_carrier_date**

### Dimensions Tables:

**Product:**
- product_id
- product_category (In english)

**Date:** (Date in which at least one order has been made)
- purchase_day
- purchase_month
- purchase_year

**Location:**
- zip_code (For both customer and sellers)
- city (For both customer and sellers)
- state (For both customer and sellers)


## Solution

1. Create temporary tables for Orders and Products

Create the temporary table for the products having category not null.
```sql
create table temp_prods as 
select * 
from product
where category_name is not null or category_name <> '';
```

Create the temporary table having only orders with valid dates and orders with products with assigned category
```sql
create table temp_orders as
select o.*
from orders as o, items as i
where o.delivered_carrier_date <> "" and o.delivered_customer_date <> "" and o.id = i.order_id and i.product_id in (select id from temp_prods) ;
```

Ok, now time for the dimensions!!!

2. Create the dimension tables

Dimension Table for **Product**
```sql
create table product_dim as
select p.id, ct.translated_name as category
from temp_prods as p, category_translate as ct
where p.category_name = ct.name; 
```

Dimension Table for **Date** 

(Knowing a date is valid and selecting only the ones in which at least one order has been made)
```sql
create table date_dim as
select 
    distinct YEAR(temp_orders.purchase_timestamp) as date_year,
    MONTH(temp_orders.purchase_timestamp) as date_month,
    DAY(temp_orders.purchase_timestamp) as date_day
from temp_orders
ORDER BY YEAR(temp_orders.purchase_timestamp), MONTH(temp_orders.purchase_timestamp), DAY(temp_orders.purchase_timestamp);
```

Dimension Table for **Location**

```sql
create table location_dim as
select loc.zip_code, loc.city, loc.statee
from
(
select 
    distinct c.zip_code_prefix as zip_code,
    c.city as city,
    c.state as statee
from customer as c 
union
select 
    distinct s.zip_code_prefix as zip_code,
    s.city as city,
    s.state as statee
from seller as s
) as loc
group by loc.zip_code, loc.city, loc.statee;
```

3. Create the fact table

Manca da fare:
- group by per le *dimensioni*
- al posto di purchase_day fare la cast a day da purchase_date

```sql
create table Order_Product as 
    select 
        c.zip_code_prefix as customer_zip_code, 
        s.zip_code_prefix as seller_zip_code, 
        i.product_id as product_id,
        DAY(purchase_timestamp) as purchase_day,
        SUM(i.price) as income,
        COUNT(*) as quantity,
        DATEDIFF(tod.delivered_customer_date, tod.delivered_carrier_date) as delivery_time
    from items as i , temp_orders as tod, seller as s, customer as c
    where 
        i.order_id = tod.id and 
        i.seller_id = s.id and 
        tod.customer_id = c.id
    group by c.zip_code_prefix, 
             s.zip_code_prefix, 
             i.product_id, 
             tod.delivered_customer_date,
             tod.delivered_carrier_date,
             tod.purchase_timestamp;
```