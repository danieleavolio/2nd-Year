# Common Lisp Object System

![](https://i.imgur.com/yJMaP1M.png)

In Common LISP methods doesn't belong to the class. 

```commonlisp
(defgeneric add (x y))

(defmethod add ((x number) (y number))
    (x+y))

;test

(add 1 3)
4

(add '(1 2) '(3 4))
No methods applicable for generic function
```
### Multiple dispatch

We are trying to add CONS and CONS. We don't have any method matching this signature. We have to extend the generic function.

```lisp
(defmethod add ((x list) (y list))
    (mapcar # 'add x y))
```

Now it can work calling the function inside

```lisp

> (add '(1 2 3) '(4 5 6))
(5 7 9)
```

#### Factorial function as generic function

```lisp

(defgeneric fact (n))

(defmethod fact ((n interger)) ;no class for n > 0
    (* n (fact (1-n))))

(defmethod fact ((n (eql 0))) ; we can specialize on 0
    1) 

(fact 5)
120
```

#### Other functions def

We can define custom behavior

```lisp

(defmethod foobar ((x (eql fact 5)))
    1)
(defmethod foobar ((x t)) 
    0)

>foobar(3)
0

>foobar(5)
1

>foobar(120)
0
```

### Fibonacci and memoization

```lisp
(defgeneric fib (n))

(defmethod fib ((n (eql 0)))
    0)

(defmethod fib ((n (eql 1)))
    1)

(defmethod fib ((n number))
    (+ (fib (- n 1)) (fib (- n 2))))

> (time (fib 40))
; real time 22,612 msec
102334155
```

And now we need memoization to speedup the performance

```lisp
(let ((cached-results (make-hash-table)))
    (defmethod fib :around ((n number))
        (or (gethash n cached-results)
            (setf (gethash n cached-results)
                (call-next-method)))))
```

So this is just checking if we have the value of the fibonacci number inside an hash table. If so, we are not calling the function. Otherwise, we are.


sLIDE 116. Probably my pc will power off from here.

