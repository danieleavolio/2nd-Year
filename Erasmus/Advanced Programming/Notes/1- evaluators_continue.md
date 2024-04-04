# Evaluators

So, why is there an `infinite loop`?

```lisp
>> (+ 3 4)
7
>> (flet ((+ (x y) "I'm an addition"))
(+ 10 20))
"I'm an addition"
>> (+ 3 (flet ((+ (x y) (* x y))) (+ 4 5)))
23
>> (+ 1 2 3 4 5 6)
21
>> (let ((* 10))
(+ * *))
20
>> (let ((+ *)
(* +))
(+ (* 1 2) 3))
9
>> (flet ((+ (x y) (* x y))
    (* (x y) (+ x y)))
(+ (* 1 2) 3))
...infinite loop
```

Imagine having a global environment in which we have the symbols `+` and `*`. When we define the `+` function, we are shadowing the global `+` symbol. When we call `+` inside the `flet` form, we are calling the local `+` function. The same happens with the `*` symbol. When we call `*` inside the `let` form, we are calling the local `*` symbol. The problem arises when we call `+` inside the `flet` form. We are calling the local `+` function, which calls the local `*` function, which calls the local `+` function, and so on. 

So basically this is an infinite chain of calls between the `+` and `*` functions. This is why we have an `infinite loop`.

The problem is that we are doing an `extension of the global environment`.

So naming is an important part of the language. We need to be careful when we are defining new functions or variables.

## Primitive operations and environment extension

```lisp

(define initial-bindings
    (list (cons 'pi 3.14159)
                (cons 'e 2.71828)
                (cons 'square (make-function '(x) '((* x x))))
                (cons '+ (make-primitive +))
                (cons '* (make-primitive *))
                (cons '- (make-primitive -))
                (cons '/ (make-primitive /))
                (cons '= (make-primitive =))
                (cons '< (make-primitive <))
                (cons '> (make-primitive >))
                (cons '<= (make-primitive <=))
                (cons '>= (make-primitive >=))
                ...))

```
`The Problem`: The `eval` doesn't know how to evaluate the primitive operations.

```lisp
>> (= (- 1 2) (- 3 4))
#t
>> (>= 2 5)
#f
>> #t
error in "Unknown expression type -- EVAL": #t
```

Relational operations compute boolean values.

We need to extend the set of value in order to make it accept boolean values.

`Solution`

```lisp
(define (self-evaluating? exp)
    (or (number? exp) (string? exp) (boolean? exp)))
```

`Note:` In the past `if` didn't work with boolean values but with dynamic `goto` operations.  Until 1960 John McCarthy invented `conditional expressions`.

```lisp
(if (< x y)
    (+ x 5)
    (- y 5))
```

So we are going to abstract this structure.

```lisp

(define (if? exp)
    (and (pair? exp) (eq? (car exp) 'if)))

(define (if-condition exp)
    (cadr exp))

(define (if-consequent exp)
    (caddr exp))

(define (if-alternative exp)
    (cadddr exp))

(define (eval-if exp env)
    (if (true? (eval (if-condition exp) env))
            (eval (if-consequent exp) env)
            (eval (if-alternative exp) env)))
```

But now we have to talk on what does it means `true?` and `false?`.

A common error in `c` what this:

```c

if (a = b){
    ...
}
else {
    ...
}

```

The error is that the `assignment` would always go on and the conditional expression was based on the fact that `a` would have a `false` or `0` value.

To fix this there are sole solutions:
- Only use `boolean`, like in Java and Pascal
- Use only `1` `false` value and the others are `true` values.

```lisp

>> (flet ((abs (x)
         (if (< x 0)
             (- x)
             x)))
     (abs -5))
5

>> (flet ((fact (n)
         (if (= n 0)
             1
             (* n (fact (- n 1))))))
     (fact 3))
6

>> (flet ((fact (n)
         (if (= n 0)
             1
             (* n (fact (- n 1))))))
     (fact 50))
30414093201713378043612608166064768844377641568960512000000000000

```

`Question`: Why is the factorial working and not exploding the stack?

Because is relying on `primitive operations`.

`Question`: Why is recursion working without specifying anything to the evaluator?


It's something working with the `extension` of the `global environment`.

```lisp
=> (eval n ((n . 2) (n . 3) (fact . λ) …))
<= 2
=> (eval 1 ((n . 2) (n . 3) (fact . λ) …))
<= 1
<= 1
=> (eval (if (= n 0) 1 (* n (fact (- n 1)))) ((n . 1) (n . 2) (n . 3) (fact . λ) …))
=> (eval (= n 0) ((n . 1) (n . 2) (n . 3) (fact . λ) …))
=> (eval n ((n . 1) (n . 2) (n . 3) (fact . λ) …))
<= 1
=> (eval 0 ((n . 1) (n . 2) (n . 3) (fact . λ) …))
<= 0
<= #f
=> (eval (* n (fact (- n 1))) ((n . 1) (n . 2) (n . 3) (fact . λ) …))
=> (eval n ((n . 1) (n . 2) (n . 3) (fact . λ) …))
<= 1
=> (eval (fact (- n 1)) ((n . 1) (n . 2) (n . 3) (fact . λ) …))
=> (eval (- n 1) ((n . 1) (n . 2) (n . 3) (fact . λ) …))
=> (eval n ((n . 1) (n . 2) (n . 3) (fact . λ) …))
<= 1
=> (eval 1 ((n . 1) (n . 2) (n . 3) (fact . λ) …))
<= 1
<= 0
=> (eval (if (= n 0) 1 (* n (fact (- n 1)))) ((n . 0) (n . 1) (n . 2) (n . 3) (fact . λ) …))
=> (eval (= n 0) ((n . 0) (n . 1) (n . 2) (n . 3) (fact . λ) …))
=> (eval n ((n . 0) (n . 1) (n . 2) (n . 3) (fact . λ) …))
<= 0
=> (eval 0 ((n . 0) (n . 1) (n . 2) (n . 3) (fact . λ) …))
<= 0
<= #t
=> (eval 1 ((n . 0) (n . 1) (n . 2) (n . 3) (fact . λ) …))
<= 1
<= …
<= 6
```

So, extending the global environment is going to give `recursion` for free.

## Def

We want to define the `def`.
`def` is a definition tha inject the name inside the environment.

```lisp
>> (def x (+ 1 2))
3
>> (+ x 2)
5
>> (fdef triple (a) (+ a a a))
    (function (a) (+ a a a))
>> (triple (+ x 3))
18
```

### The definition of def

```lisp
;(def x (+ 1 2))
(define (def? exp)
    (and (pair? exp) (eq? (car exp) 'def)))

(define (def-name exp)
    (cadr exp))

(define (def-init exp)
    (caddr exp))

(define (eval-def exp env)
    (let ((value (eval (def-init exp) env)))
        (define-name! (def-name exp) value env)
        value))

(define (define-name! name value env)
    (let ((binding (cons name value))
                (new-pair (cons (car env) (cdr env))))
        (set-car! env binding)
        (set-cdr! env new-pair)))
```

Professor said something like we in this case push the definition over to make space to insert the new one in the environment. Looks like a shifting to me?

### The definition of fdef

```lisp

;(fdef triple (a) (+ a a a))
(define (fdef? exp)
    (and (pair? exp) (eq? (car exp) 'fdef)))
(define (fdef-name exp)
    (cadr exp))
(define (fdef-parameters exp)
    (caddr exp))
(define (fdef-body exp)
    (cdddr exp))
(define (eval-fdef exp env)
    (let ((value
                 (make-function (fdef-parameters exp)
                                                (fdef-body exp))))
        (define-name! (fdef-name exp) value env)
        value))
```

### Change the evaluator

```lisp
(define (eval exp env)
    (cond ((self-evaluating? exp) exp)
                ((name? exp) (eval-name exp env))
                ((if? exp) (eval-if exp env))
                ((let? exp) (eval-let exp env))
                ((flet? exp) (eval-flet exp env))
                ((def? exp) (eval-def exp env))
                ((fdef? exp) (eval-fdef exp env))
                ((call? exp) (eval-call exp env))
                (else (error "Unknown expression type -- EVAL" exp))))
```

### Little exercise

```lisp
>> (def foo 1)
1
>> (+ foo (def foo 2))
3 ;;But it could have been 4
>> (def bar 1)
1
>> (+ (def bar 2) bar)
4 ;;But it could have been 3
```

The evaluation of a definition modifies the current environment. This modification is a side-effect. The evaluation order for side-effects varies across programming languages:

- Left-to-right in `Java` and `Common Lisp`.
- Right-to-left in `APL` and `J`.
- Unspecified in `Scheme`, `C`, `Pascal`, and many others.

An example in `c`

```c

//Imagine having an array big enough

int i = 1;
A[i++] = i;

```

We don't know which value and which index will be used. The `c` compiler is free to choose the order of evaluation.


### Example with left right order

```lisp
>> (def baz 3)
3
>> (+ (let ((x 0))
        (def baz 5))
    baz)
```

So we have defined `baz` as 3. Then we defined inside a local scope `baz` as 5. Then we are adding `baz` to the result of the `let` form. 

The result is 8. 

Note that `x` is not getting used. So why are we adding it? Let's create an `empty scope`

```lisp
>> (+ (let ()
        (def baz 6))
    baz)
12 ;;Huuh? Shouldn't it be 9?
```

So, if we have an `empty scope` is like it doesn't exist. So, when we do something inside this empty scope is making changes directly on the `global scope`.

`Question`: How to fix this?

`Answer:`
### Updating the Environment with Frames

In our current implementation, the environment is represented as a continuous sequence of bindings without any separation between different scopes. To introduce scope separation, we need to partition the environment into frames.

Each time we extend the environment (e.g., with let, flet, or function calls), we create a new frame. Each frame contains an association list for the bindings within that scope.

This change requires us to redefine the following processes:

- `eval-name`: The process of finding bindings needs to be updated to consider the frames and their association lists.
- `augment-environment`: The process of extending the environment needs to create a new frame for the new bindings.
- `define-name!`: The process of updating the environment needs to store the bindings in the appropriate frame.

By introducing frames, we can achieve proper scoping and handle nested scopes correctly.

But to do so, we need to `change` the function that `searches` in the `environment`..


```lisp
(define (augment-environment names values env)
    (cons (map cons names values) env))

(define (eval-name name env)
    (define (lookup-in-frame frame)
        (cond
            ((null? frame)
             (eval-name name (cdr env)))
            ((eq? name (caar frame))
             (cdar frame))
            (else
             (lookup-in-frame (cdr frame)))))
    (if (null? env)
            (error "Unbound name -- EVAL-NAME" name)
            (lookup-in-frame (car env))))

(define (define-name! name value env)
    (let ((binding (cons name value)))
        (set-car! env (cons binding (car env)))))
```

And now it should work well:

```lisp
>> (def baz 3)
3
>> (+ (let ((x 0))
(def baz 5))
baz)
8
>> (+ (let ()
(def baz 6))
baz)
9 ;;Good, it's fine now!
```

#### Another puzzle
```lisp
>> (def counter 0)
0
>> (fdef incr ()
(def counter (+ counter 1)))
    (function () (def counter (+ counter 1)))
>> (incr)
1
>> (incr)
1 ;;What?
```

If you folllow the scope:

- Global scope 
  - -> counter = 0
  - -> incr = lambda
This will create another box that points to the scope you are extending:

Now we evaluate the body of the function. To evaluate the body we need to evaluate the definition. So we search for `(+ counter 1)`.
It finds `counter` in the `global scope` and it adds `1`.

So we have something like:

- Global scope 
  - -> counter = 0
  - -> incr = lambda
- Another scope"
  - -> counter = 1
Now, this scope is not reachable because it exists only in the `function` scope. So we are always going to lose this scope. 
Calling the function again is going to create a new scope and the `counter` is going to be `1` again, because it can't find another `(+ counter 1)`

And so on and so forth.


`In Python`

Is kind of the same, but it defines initially the definition, so it creates a new scope with the `counter` with an undefined value. Then it tries to add `1` to `counter` and it returns a error because you can't add 1 to `None`.

To fix this you should clarify the definition with the `global` keyword.

## Naming scope

So, the stuff is that the `naming` scope.

A scope protects names: all internally defined names are invisible to the outside.
But sometimes we want to change a name defined in an outer scope.
This requires the concept of assignment. Instead of defining a new binding, an assignment updates an existing binding.
Concrete syntax: (set! name expression)

```lisp
;; (set! counter (+ counter 1))
(define (set? exp)
    (and (pair? exp) (eq? (car exp) 'set!)))

(define (assignment-name exp)
    (cadr exp))

(define (assignment-expression exp)
    (caddr exp))

(define (eval-set exp env)
    (let ((value (eval (assignment-expression exp) env)))
        (update-name! (assignment-name exp) value env)
        value))
```

`Evaluating a name`

```lisp
(define (eval-name name env)
(define (lookup-in-frame frame)
(cond ((null? frame)
(eval-name name (cdr env)))
((eq? name (caar frame))
(cdar frame))
(else
(lookup-in-frame (cdr frame)))))
(if (null? env)
(error "Unbound name -- EVAL-NAME" name)
(lookup-in-frame (car env))))
```

`Updating a name`

```lisp
(define (update-name! name value env)
(define (update-in-frame frame)
(cond ((null? frame)
(update-name! name value (cdr env)))
((eq? name (caar frame))
(set-cdr! (car frame) value))
(else
(update-in-frame (cdr frame)))))
(if (null? env)
(error "Unbound name -- EVAL-SET" name)
(update-in-frame (car env))))
```

`Evaluator`

```lisp
(define (eval exp env)
(cond ((self-evaluating? exp) exp)
((name? exp)
(eval-name exp env))
((if? exp)
(eval-if exp env))
((let? exp)
(eval-let exp env))
((flet? exp)
(eval-flet exp env))
((def? exp)
(eval-def exp env))
((set? exp)
(eval-set exp env))
((fdef? exp)
(eval-fdef exp env))
((call? exp)
(eval-call exp env))
(else
(error "Unknown expression type -- EVAL" exp))))
```

### Input / Output
Input and output are sources of side effects. They must be implemented and evaluated.

```lisp
(define initial-bindings
    (list (cons 'pi 3.14159)
        ...
        (cons '<= (make-primitive <=))
        (cons '>= (make-primitive >=))
        (cons 'display (make-primitive display))
        (cons 'newline (make-primitive newline))
        (cons 'read (make-primitive read))))
```

`A TEST`

```lisp
(fdef print-value (text value)
    (evaluate-all-and-return-the-last-one
        (display text)
        (display " = ")
        (display value)
        (newline)
        value))
(fdef evaluate-all-and-return-the-last-one (v1 v2 v3 v4 v5)
    v5)
>> (print-value "fact(5)" (fact 5))
= 120 fact(5) ;; Huh?
```

The problem is the `order of evaluation`. 

`Note:` The sequantial operator in `c` is the semi-colon `,`. It evaluates the first expression and then the second one

We need a specific control structure to ensure left-to-right
evaluation.
Following Scheme tradition, we will use begin

### Sequential ordering

```lisp
(define (begin? exp)
    (and (pair? exp) (eq? (car exp) 'begin)))

(define (begin-expressions exp)
    (cdr exp))

(define (eval-begin exp env)
    (define (eval-sequence expressions env)
        (if (null? (cdr expressions))
                (eval (car expressions) env)
                (begin
                    (eval (car expressions) env)
                    (eval-sequence (cdr expressions) env))))
    (eval-sequence (begin-expressions exp) env))
```

`Evaluator`

```lisp
(define (eval exp env)
    (cond ((self-evaluating? exp) exp)
                ((name? exp)
                 (eval-name exp env))
                ((if? exp)
                 (eval-if exp env))
                ...
                ((def? exp)
                 (eval-def exp env))
                ((set? exp)
                 (eval-set exp env))
                ((fdef? exp)
                 (eval-fdef exp env))
                ((begin? exp)
                 (eval-begin exp env))
                ((call? exp)
                 (eval-call exp env))
                (else
                 (error "Unknown expression type -- EVAL" exp))))
```

```lisp
(fdef print-value (text value)
(begin
(display text)
(display " = ")
(display value)
(newline)
value))
>> (print-value "fact(5)" (fact 5))
fact(5) = 120
120
>> (print-value "35^3" (* 35 35 35))
35^3 = 42875
42875
```


`Implicit begin`
It is usual to consider that every function body, every let body,
and every flet body has an implicit begin form.

To implement the implicit begin, we have to modify the selectors
that return the body of a let, the body of a flet and the body
of a function.

```lisp
;; (let (...)
;; ... ... ...)
(define (let-body exp)
    (make-begin (cddr exp)))
;; (flet (...)
;; ... ... ...)
(define (flet-body exp)
    (make-begin (cddr exp)))
;; (function (...)
;; ... ... ...)
(define (function-body func)
    (make-begin (cddr func)))
(define (make-begin expressions)
    (cons 'begin expressions))
```

### High order function

Just providing the environment something I didn't understand, it make us able to define `high order functions`.

```lisp
>> (fdef reflexive-addition (x)
(+ x x))
(function (x) (+ x x))
>> (reflexive-addition 3)
6
>> (fdef reflexive-product (x)
(* x x))
(function (x) (* x x))
>> (reflexive-product 3)
9
>> (fdef reflexive-operation (f x)
(f x x))
(function (f x) (f x x))
>> (reflexive-operation + 3)
6
>> (reflexive-operation * 3)
9
>> (reflexive-operation / 3)
1
>> (reflexive-operation - 3)
0
```


A higher-order function is a function that accepts other functions as arguments or computes functions as results. Many modern programming languages support higher-order functions and provide libraries with higher-order functions, such as `qsort` in C and `map` in Scheme.

In terms of higher-order functions, our evaluator has the same expressive power as the C programming language. We can pass functions as arguments and apply functions contained in parameters. However, we `cannot create anonymous functions` at this time. Additionally, our evaluator has other limitations that C avoids by prohibiting internal function definitions (which we will discuss later).

