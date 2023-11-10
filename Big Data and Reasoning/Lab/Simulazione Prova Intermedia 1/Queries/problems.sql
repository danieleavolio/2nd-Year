'For each customer, find the number of order with at least 2 items'

select o.id, count(o.id) as num
from customer as c, orders as o, items as od
where c.id = o.customer_id and o.id = od.id
group by c.id
having count(od.product_id) >= 2;

SELECT c.id, count(o.id) as num
from customer as c, orders as o, items as i1, items as i2
where o.id == i1.id and o.id == i2.id and not i1.item_id == i2.item_id
group by c.id;


' Find active customers for each year. A customer is active if it has at least three order
in a given year.'




'For each year and for each customer city, compute the total income for the company
(i.e. the sum of the total price of each order)'


'Find the three most frequent categories (possibly english) among e-commerce product'
'Find for each product the number of sold items and the total income'
'Find product category (possibly english) compute of sold items and the total income'