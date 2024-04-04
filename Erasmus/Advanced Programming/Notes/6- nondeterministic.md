# Nondeterministic programs

Think about a program that can have a different value in more worlds.

```lisp
(define (a-pythagorean-triple n)
    (let ((i (an-integer-between 1 n)))
        (let ((j (an-integer-between i n)))
            (let ((k (an-integer-between j n)))
                (require (= (+ (* i i) (* j j)) (* k k)))
                (list i j k)))))
```

The program above generates a Pythagorean triple. A Pythagorean triple is a set of three positive integers, $i$, $j$, and $k$, such that $i^2 + j^2 = k^2$. The program generates a triple by choosing three integers between 1 and $n$ and checking if they form a Pythagorean triple. If they do, the program returns the triple; otherwise, it tries again.

The program is nondeterministic because it can return different values in different worlds. In one world, the program may return $(3, 4, 5)$, and in another world, it may return $(5, 12, 13)$. The program is nondeterministic because it can return different values in different worlds.

A solution:

```
(define (an-integer-between a b)
    (if (> a b)
    (fail)
    (amb a
        (an-integer-between (+ a 1) b))))
```

It returns nondeterministically the value of the expression.

```lisp
>> (amb 1 2)
1
>> (fail)
2
>> (fail)
no-more-choices
>> (+ 1 (amb 10 20))
11
>> (fail)
21
>> (cons (amb 1 2) (amb 3 4))
(1 . 3)
>> (fail)
(1 . 4)
>> (fail)
(2 . 3)
>> (fail)
(2 . 4)
>> (fail)
```

So when you call fail, you will get another value from the amb expression.
If you finish, you will get no-more-choices.

### How to implement it

```lisp
(define choices (list))

(define (add-choices! cs)
    (set! choices (append cs choices)))

(define (pop-choice!)
    (let ((choice (car choices)))
        (set! choices (cdr choices))
        choice))

>> (* 2 (+ 3 (call/cc (lambda (c)
    (add-choices! (list (lambda () (c 5))
                        (lambda () (c 6))))
            4))))

14
>> ((pop-choice!))
16
>> ((pop-choice!))
18
```

Of course, we use a macro for this.

![nondeterministic](https://i.imgur.com/RBZbksg.png)

#### Failing

```lisp
(define (fail)
    (if (null? choices)
        (abort-top-level 'no-more-choices)
        ((pop-choice!))))
```

Now the pythagorean triple program:

![pythagorean-triple](https://i.imgur.com/ru0qCG7.png)

### N-Queens

![n-queens](https://i.imgur.com/7XAPuU0.png)

Now this is with backtracking.
But what if we want to do it with a nondeterministic program?

```lisp
(define (queens n i j board)
    (if (= i n)
            board
            (let ((j (an-integer-between 0 (- n 1))))
                (require (not (attacked? i j board)))
                (queens n (+ i 1) 0 (cons (cons i j) board)))))

(define (solve-queens n)
    (queens n 0 0 (list)))
```

![stuff](https://i.imgur.com/jLohE9J.png)

#### puzzle

Adrian, Bob, Charles, David, and Edward live on different floors
of an apartment house that contains only five floors.
Adrian does not live on the top floor.
Bob does not live on the bottom floor.
Charles does not live on either the top or the bottom floor.
David lives on a higher floor than does Bob.
Edward does not live on a floor adjacent to Charles’s.
Charles does not live on a floor adjacent to Bob’s.
Where does everyone live?


```lisp
(define (apartment-puzzle)
  (let ((a (amb 1 2 3 4))
      (b (amb 2 3 4 5))
      (c (amb 2 3 4))
      (d (amb 1 2 3 4 5))
      (e (amb 1 2 3 4 5)))
    (require (distinct? (list a b c d e)))
    (require (> d b))
    (require (not (= (abs (- e c)) 1)))
    (require (not (= (abs (- c b)) 1)))
    (list (list 'adrian a) (list 'bob b) (list 'charles c)
        (list 'david d) (list 'edward e))))

(apartment-puzzle)
((adrian 3) (bob 2) (charles 4) (david 5) (edward 1))
(fail)
no-more-choices 
```

