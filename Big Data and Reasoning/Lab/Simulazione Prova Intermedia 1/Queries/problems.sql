'For each customer, find the number of order with at least 2 items'

select
    c.id,
    count(distinct o.id) as orders
from
    customer as c,
    orders as o,
    items as i1,
    items as i2
where
    c.id = o.customer_id
    and o.id = i1.id
    and o.id = i2.id
    and i1.item_id <> i2.item_id
group by
    c.id;

' Find active customers for each year. A customer is active if it has at least three order
in a given year.'
SELECT
    c.id AS customer_id,
    YEAR(CAST(o.purchase_timestamp AS TIMESTAMP)) AS order_year,
    COUNT(DISTINCT o.id) AS num_orders
FROM
    customer c
    JOIN orders o ON c.id = o.customer_id
GROUP BY
    c.id,
    YEAR(CAST(o.purchase_timestamp AS TIMESTAMP))
HAVING
    COUNT(DISTINCT o.id) >= 3;

'For each year and for each customer city, compute the total income for the company
(i.e. the sum of the total price of each order)'
SELECT
    YEAR(CAST(o.purchase_timestamp AS TIMESTAMP)) AS order_year,
    c.city AS customer_city,
    SUM(CAST(i.price AS DECIMAL(10, 2))) AS total_income
FROM
    customer c
    JOIN orders o ON c.id = o.customer_id
    JOIN items i ON o.id = i.id
GROUP BY
    YEAR(CAST(o.purchase_timestamp AS TIMESTAMP)),
    c.city;

'Find the three most frequent categories (possibly english) among e-commerce product'
SELECT
    product_category_name,
    COUNT(*) AS category_count
FROM
    products
GROUP BY
    product_category_name
ORDER BY
    category_count DESC
LIMIT
    3;

'Find for each product the number of sold items and the total income'
SELECT
    i.product_id,
    COUNT(*) AS sold_items,
    SUM(CAST(i.price AS DECIMAL(10, 2))) AS total_income
FROM
    items i
GROUP BY
    i.product_id;

'Find product category (possibly english) compute of sold items and the total income'
SELECT
    p.product_category_name,
    COUNT(*) AS sold_items,
    SUM(CAST(i.price AS DECIMAL(10, 2))) AS total_income
FROM
    items i
    JOIN products p ON i.product_id = p.product_id
GROUP BY
    p.product_category_name;

select
    city
from
    orders,
    customer
where
    orders.id like 'e481f51cbdc54678b7cc49136f2d6af7'
    and orders.customer_id = customer.id;