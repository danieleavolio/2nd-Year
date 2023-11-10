'For each customer, find the number of order with at least 2 items'
SELECT
    c.id AS customer_id,
    COUNT(DISTINCT o.id) AS num_orders_with_at_least_two_items,
    COUNT(DISTINCT i.item_id)
FROM
    customer as c
    JOIN orders as o ON c.id = o.customer_id
    JOIN items as i ON o.id = i.id
GROUP BY
    c.id
HAVING
    COUNT(DISTINCT i.item_id) >= 2;

select o.id, count(o.id) as num
from customer as c, orders as o, items as od
where c.id = o.customer_id and o.id = od.id
group by c.id
having count(od.product_id) >= 2;

select c.id, count(distinct o.id) as orders
from customer as c, orders as o, items as i1, items as i2
where c.id = o.customer_id and o.id = i1.id and o.id = i2.id and i1.item_id <> i2.item_id
group by c.id;

' Find active customers for each year. A customer is active if it has at least three order
in a given year.' 

SELECT
    c.id AS customer_id,
    YEAR(CAST(o.purchase_timestamp AS TIMESTAMP)) AS order_year,
    COUNT(DISTINCT o.id) AS num_orders
FROM
    customer c
JOIN
    orders o ON c.id = o.customer_id
GROUP BY
    c.id, YEAR(CAST(o.purchase_timestamp AS TIMESTAMP))
HAVING
    COUNT(DISTINCT o.id) >= 3;


'For each year and for each customer city, compute the total income for the company
(i.e. the sum of the total price of each order)'

 'Find the three most frequent categories (possibly english) among e-commerce product' 
 
 'Find for each product the number of sold items and the total income' 
 
 'Find product category (possibly english) compute of sold items and the total income'
select
    city
from
    orders,
    customer
where
    orders.id like 'e481f51cbdc54678b7cc49136f2d6af7'
    and orders.customer_id = customer.id;