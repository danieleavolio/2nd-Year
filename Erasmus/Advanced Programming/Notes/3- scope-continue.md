# Scope continue

Let's make an example.

```lisp

(define (foo x)
    (lambda (y) (+ x y)))
```

What happens is that we create an object `lambda`. It captures the env where it has been created.
In julia it's like:

```julia
>foo(x) = (y) -> x + y

>bar = foo(1)
``` 

Not we have on the stack something like:

| -        |
| -------- |
| 1 `->` x |
| -        |

And we have an object `lambda` that points to the `1` in the stack.

But then if we do 
```julia
>bar(2)
```
we will get an error.

`N.B:` 
- Define a function: It's when we give a name 
- Create a function: It's when we actually create the object `lambda` and store it in the env.

### Example for flet

```lisp
>> (flet ((fact (n)
           (if (= n 0)
               1
               (* n (fact (- n 1))))))
     (fact 3))
error in "Unbound name -- EVAL-NAME": fact
```

![flet](https://i.imgur.com/TuIpBwC.png)

The problem is that FACT is not defined in the global env. It's defined in the scope of the `flet` function. 

![flet](https://i.imgur.com/I9FzvnU.png)

You create the scope first, then the function and then you change the scope to the new one.


This is the `Common Lisp` solution
```lisp
>> (let ((fact #f))
    (set! fact (lambda (n)
        (if (= n 0)
            1
            (* n (fact (- n 1))))))
(fact 3))
6
```

The `Scheme` solution:
```lisp
>> (let ()
    (fdef fact (n)
        (if (= n 0)
            1
            (* n (fact (- n 1)))))
(fact 3))
6
```

# Defining data structures

## Pair

We want to create pairs in our language. To do so, let's define `kons`


```lisp

>> (fdef kons (a,b)
    (lambda (f)
        (f a b)))

>> (define my-pair (kons 1 2))
```

![kons](https://i.imgur.com/TleYRQQ.png)

Now to define `kar` and `kdr`

```lisp
>> (fdef kar (k)
(k (lambda (x y) x)))
(function (k) ...)
>> (fdef kdr (k)
(k (lambda (x y) y)))
(function (k) ...)
>> (def number-pair (kons 1 2))
(function (f) ...)
>> (kar number-pair)
1
>>
```

The drawign I did was kind of bad tbh.

![](https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F23d1c330-5be8-4224-b149-5f2108be9029%2Fa778cca2-c251-4d91-a365-418c3fb4af2f%2F1000175778.jpg?table=block&id=3d5fc73e-7f93-43ee-80d0-2cb1831cdd41&spaceId=23d1c330-5be8-4224-b149-5f2108be9029&width=2000&userId=c663dadd-c53f-47a8-a0f0-706e5d9f9100&cache=v2)

This is professor drawing.

## Use already defined structures

Since we are building on top of lisp, we can add to the primitive 
stuff that is already defined.

```lisp
(define initial-bindings
(list (cons 'pi 3.14159)
...
(cons '> (make-primitive >))
(cons '<= (make-primitive <=))
(cons '>= (make-primitive >=))
(cons 'display (make-primitive display))
(cons 'newline (make-primitive newline))
(cons 'read (make-primitive read))
(cons 'list (make-primitive list))
(cons 'cons (make-primitive cons))
(cons 'car (make-primitive car))
(cons 'cdr (make-primitive cdr))
(cons 'pair? (make-primitive pair?))
(cons 'null? (make-primitive null?))))
```

## Quoting

Since lisp works with symbol, we need to define the `quote` operator to avoid evaluating the symbol.

```lisp
;;(quote foo)
(define (quote? exp)
    (and (pair? exp)
             (eq? (car exp) 'quote)))

(define (quoted-form exp)
    (cadr exp))

(define (eval-quote exp env)
    (quoted-form exp))

(define (eval exp env)
    (cond ((self-evaluating? exp) exp)
                ((quote? exp)
                 (eval-quote exp env))
                ((name? exp)
                 (eval-name exp env))
                ...
                (else
                 (error "Unknown expression type -- EVAL" exp))))
```

```lisp
>> (quote 1)
1
>> (quote John)
John
>> (list (quote believes)
(quote John)
(list (quote loves) (quote Mary) (quote Peter)))
(believes John (loves Mary Peter))
>> (list 'believes
'John
(list 'loves 'Mary 'Peter))
(believes John (loves Mary Peter))
>> '(believes John (loves Mary Peter))
(believes John (loves Mary Peter))
>> (list pi 'pi)
(3.14159 pi)
>> (list 'fdef 'square (list 'x) (list '* 'x 'x))
(fdef square (x) (* x x))
```


## Macros

If we want to define some operators using `short circuit` we can use macros.

A short circuit is when we have an `or` and the first condition is true, we don't need to evaluate the second one. It's good to use those stuff when we have some expression that can break the program if evaluated. 

```lisp
>> (fdef and (b0 b1)
(if b0
b1
#f))
(function (b0 b1) ...)
>> (fdef quotient-or-false (a b)
(and (not (= b 0))
(/ a b)))
(function (a b) ...)
>> (quotient-or-false 6 2)
3
>> (quotient-or-false 6 0)
error in /: undefined for 0
```

This is an example of error because we are not usign short circuit evaluation.

![sc](https://i.imgur.com/Fl2ThWe.png)

`Question:` Differenze between function call and macro call

- `Function call`: evaluate all the arguments and then compute and return the result
- `Macro call`: Does not evaluate the arguments and computes a result that is then evaluated

```lisp
>> (fdef f-foo (x)
(display x)
(newline)
x)
(function (x) ...)
>> (mdef m-foo (x)
(display x)
(newline)
x)
(function (x) ...)
>> (f-foo (+ 1 2))
3
3
>> (m-foo (+ 1 2))
(+ 1 2)
3
```

Look what happens when you call a function and a macro

```lisp
>> (fdef f-bar ()
(list '+
'1
'2))
(function () ...)
>> (mdef m-bar ()
(list '+
'1
'2))
(function () ...)
>> (f-bar)
(+ 1 2)
>> (m-bar)
3
```

When you print a function, it returns the (+ 1 2). when you print a macro, it returns the evaluated result.

```lisp
;;(mdef quux (x) (list '+ x '1))
(define (mdef? exp)
(and (pair? exp)
(eq? (car exp) 'mdef)))
(define (eval-mdef exp env)
(eval `(def ,(cadr exp)
(lambda ,(caddr exp) "macro" ,@(cdddr exp)))
env))
```

And we have to extend our `eval` to chec kfor the macro


```lisp
(define (eval exp env)
    (cond ((self-evaluating? exp) exp)
                ...
                ((fdef? exp)
                 (eval-fdef exp env))
                ((mdef? exp)
                 (eval-mdef exp env))
                ((begin? exp)
                 (eval-begin exp env))
                ((call? exp)
                 (eval-call exp env))
                (else
                 (error "Unknown expression type -- EVAL" exp))))
```


Now, what we used to do is:

```lisp
(define (eval-call exp env)
    (let ((func (eval (call-operator exp) env))
                (args (eval-exprs (call-operands exp) env)))
        (if (primitive? func)
                (apply-primitive-function func args)
                (let ((extended-environment
                             (augment-environment (function-parameters func)
                                                                        args
                                                                        (function-environment func))))
                    (eval (function-body func) extended-environment)))))
```

This `CANNOT` be applied to `macros` because we need a `second evaluation step`.

Let's start by separating those stuff.

```lisp
(define (eval-call exp env)
    (let ((func (eval (call-operator exp) env)))
        (apply-function func (eval-exprs (call-operands exp) env))))

(define (apply-function func args)
    (if (primitive? func)
            (apply-primitive-function func args)
            (let ((extended-environment
                         (augment-environment (function-parameters func)
                                                                    args
                                                                    (function-environment func))))
                (eval (function-body func) extended-environment))))
```

If the function we have here is a macro, we make the call with the argument, but not the evaluation.

```lisp
(define (eval-call exp env)
    (let ((func (eval (call-operator exp) env)))
        (if (macro? func)
                (let ((expansion (apply-function func (call-operands exp))))
                    (eval expansion env))
                (apply-function func (eval-exprs (call-operands exp) env)))))

(define (macro? obj)
    (and (function? obj)
             (equal? (cadr (function-body obj)) "macro")))
```
In macros, the arguments are not evaluated immediately. Instead, macros build another expression that is returned and then evaluated in place of the macro call.

![](https://i.imgur.com/DPrWj8S.gif)

### Problems with macros

Let's take the `or` operator as an example.
We know that with short circuit evaluation, if the first condition is true, we don't need to evaluate the second one.

```lisp
(mdef or (b0 b1)
    (list 'if
                b0
                b0
                b1))
(function (b0 b1) ...)

(fdef in-interval (val inf sup)
    (or (and (< val inf) inf)
            (or (and (> val sup) sup) val)))
(function (val inf sup) ...)

(in-interval 1 3 7)
>> 3

(in-interval 9 3 7)
>> 7

(in-interval 4 3 7)
>> 4
```

```lisp
>> (fdef quotient-or-zero (a b)
(or (and (not (= b 0))
(/ a b))
0))
(function (a b) ...)
>> (quotient-or-zero 6 2)
3
>> (quotient-or-zero 6 0)
0
```
Looks like it works!

```lisp
>> (fdef quotient-or-zero (a b)
(or (begin
(display "Division:")
(and (not (= b 0))
(/ a b)))
0))
(function (a b) ...)
>> (quotient-or-zero 6 0)
```

I expected to see `Division: 0`.

```lisp
Division: 0
```

```lisp
>> (quotient-or-zero 6 2)
Division:Division:3 ;;What?
```

What is happening? The `problem` is in the definition of the `or`.
Everytime you repeat a parameter, you are `repeting the fragment of code`.

![](https://i.imgur.com/24Lli57.png)

What can we do to fix this? Let's see another example

```lisp

(or (foo)
    (bar))
```
We expect to see only 1 `evaluation`. What happens will do this:
```lisp
(if (foo)
    (foo)
    (bar))
```

Instead of generating this code, we need something more like this:

```lisp

(let ((r (foo)))
    (if r
        r
        (bar)))
```

Another `problem` is this one.

```lisp
(mdef mistery(e1 e2)
    (list '+ e2 e1))    
```

Note that we changed the order of the arguments.

Now, if we use this macro, we will have a problem.

```lisp
(mistery (foo) (bar))
```
What we expect is that `foo` is going to be called first and `bar` second. But what happens is that `bar` is called first and `foo` second.
So you are `confusing` the reader of the code.

`N.B:` All those problems have a solution and people have been working on it for a long time.

![](https://i.imgur.com/T9DnBNr.png)

And now if we run the code:
```lisp

>> (fdef quotient-or-zero (a b)
    (or (begin
            (display "Division:")
            (and (not (= b 0)) (/ a b)))
         0))
    (function (a b) ...)

>> (quotient-or-zero 6 0)
Division:0

>> (quotient-or-zero 6 2)
Division:3 ;;Now it's OK

>> (in-interval 1 3 7)
3

>> (in-interval 9 3 7)
error in >: expected real, but got #f, as argument 1

```
Aaaand it explodes.

![](https://i.imgur.com/NG3tsJ2.png)

This is `name collision`. We are using `val` in the `in-interval` and `val` in the `or` macro.

To solve this, we need to use `gensym` to generate a new name for the variable.

`Question`: What is `gensym`?
`Answer:` It's a function that generates a new name for a variable THAT is going to be unique.

```lisp
(define initial-bindings
(list ...
(cons 'eq? (make-primitive eq?))
(cons 'gensym (make-primitive gensym))))
```

![](https://i.imgur.com/lBhj5RC.png)

`N.B:` Gensym solve problem for names binding, but doesn't solve the problem for the expansion with free variables.

`Solution`:In a lexically scoped language, functions capture the surrounding  environment.

![](https://i.imgur.com/ZasTmxm.png)

```lisp
(fdef or-function (b0 f1)
(if b0
b0
(f1)))
```
**Hygiene**:
- The macro call expands into a function call that protects the “expanded” code from being evaluated in the scope of the macro call.
- The parameters are wrapped in lambdas that protect the macro arguments from being evaluated in any scope created by the macro expansion.
- Hygienic macros in Scheme operate differently but with the same
net result.

## Templates

In the slides there is the implementation for the backquote I think.
Page 770