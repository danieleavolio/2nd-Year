# Simplify evaluator

To make it more simple, we need to use macros.


```lisp
(mdef fdef (name parameters body)
`(def ,name (lambda ,parameters ,body)))
(mdef flet (binds body)
`(let ,(map (lambda (form)
`(,(car form)
(lambda ,@(cdr form))))
binds)
,body))
(mdef let (binds body)
`((lambda ,(map car binds)
,body)
,@(map cadr binds)))
```

![Simplify evaluator](https://i.imgur.com/rYbuH3M.png)

![Simplify evaluator](https://i.imgur.com/uG2Lzk4.png)


## Macros

`Definition:` Macro is a tagged function.

```lisp
(def mdef
(lambda (name parameters body)
"macro"
`(def ,name
(lambda ,parameters
"macro"
,body))))
```

We don't need anymore to identify macro definition into evaluator.

```lisp
(define (eval exp env)
(cond ((self-evaluating? exp)
exp)
((quote? exp)
(eval-quote exp env))
((quasiquote? exp)
(eval-quasiquote exp env))
((name? exp)
(eval-name exp env))
((lambda? exp)
(eval-lambda exp env))
((if? exp)
(eval-if exp env))
((def? exp)
(eval-def exp env))
((set? exp)
(eval-set exp env))
((begin? exp)
(eval-begin exp env))
((call? exp)
(eval-call exp env))
(else
(error "Unknown expression type -- EVAL" exp))))
```

```lisp
>> (mdef define (name form)
(if (pair? name)
`(def ,(car name)
(lambda ,(cdr name)
,form))
`(def ,name ,form)))
(function (name form) ...)
>> (define pi 3.14159)
3.14159
>> (define (fact n)
(if (= n 0)
1
(* n (fact (- n 1)))))
(function (n) ...)
>> (fact 10)
3628800
```

`Question:` Why the metacircular evaluator is good?

`Answer:` We can execute code on the language itself and understand the semantics of the language.


But Programs don't have the same capability. They can generate code for another program but we can't interpret that generated code.

We need an evaluator to do so.

2 Solutions:

1. Simple: If the language of the program is the same language of the evaluator, so we can input the evaluator to the evaluator.
2. Best: Provide the evaluator as a primitive operation. -> Reify the environment

## Reify the environment

```lisp
;;(current-environment)
(define (current-environment? exp)
(and (pair? exp)
(eq? (car exp) 'current-environment)))
(define (eval exp env)
(cond ((self-evaluating? exp) exp)
...
((current-environment? exp)
env)
((call? exp)
(eval-call exp env))
(else
(error "Unknown expression type -- EVAL" exp))))
(define initial-bindings
(list (cons '+ (make-primitive +))
...
(cons 'eval (make-primitive eval))))
```

```lisp
>> (eval (list '+ '1 '2) (current-environment))
3
>> (define my-env
(let ((x 1) (y 2))
(current-environment)))
...
>> (+ (eval 'x my-env) (eval 'y my-env))
3
>> (define (kons kar kdr)
(current-environment))
(function (kar kdr) ...)
>> (define (kar kons)
(eval 'kar kons))
(function (kons) ...)
>> (define (kdr kons)
(eval 'kdr kons))
(function (kons) ...)
>> (kar (kons 1 2))
1
>> (kdr (kons 1 2))
2
```

`NB:` We are missing some concepts of CLOS, common lisp object system. 
`defclass` is used to define a class and the `slots` are the fields of the class.


`defclass` and what we can do with it:

```lisp
>> (mdef defclass (name slots)
`(fdef ,name ,slots
(current-environment)))
(function (name slots) ...)
>> (fdef slot-value (instance slot)
(eval slot instance))
(function (instance slot) ...)
>> (defclass person
(name age sex))
(function (name age sex) ...)
>> (def john (person "John Adams" 42 "male"))
...
>> (def sally (person "Sally Field" 64 "female"))
...
>> (slot-value john 'age)
42
>> (slot-value sally 'name)
"Sally Field"
```

As the code suggest, we can use `defclass` to define a class and `slot-value` to access the fields of the class.


`function with different behavior in different environment`

It's based on message sending.


```lisp
>> (mdef send (obj message)
`(eval ',message ,obj))
(function (obj message) ...)
>> (send john (fdef fact (n)
(if (= n 0)
1
(* n (fact (- n 1))))))
(function (n) ...)
>> (send sally (fdef fact (what)
(begin
(display "It's a fact that ")
(display what)
(newline))))
(function (what) ...)
>> (send john (fact 10))
3628800
>> (send sally (fact "Lisp is great!"))
It's a fact that Lisp is great!
```

## Continuations

`Definition:` A continuation is a function that represents the future of the computation.

We want to expand our evaluator to express:
- error
- yield
- throw
- suspend


`Example`:

```lisp
>> (define (mystery a b c d e)
(+ a (* b (/ (- c d) e))))
...
>> (mystery 5 4 3 2 1)
9
>> (mystery 4 3 2 1 0)
ERROR ;;and the evaluator stops!!!!
```

The evaluator exploded because of the division by zero.
Is this problem solvable?

```lisp
(define (mystery a b c d e)
(+ a (* b (safe-/ (- c d) e))))
(define (safe-/ x y)
(if (= y 0)
(error "Can't divide " (list x y))
(/ x y)))
(define (error msg args)
???)
```

The problem here is that there are a lot of `eval` calls that are waiting. How do we handle this?

It is generally difficult to handle exceptional situations in the
callee.
Each callee can be called from many different places.
The callee rarely knows how to handle exceptional situations in a
way that pleases all callers.
Non-local transfers of control allow the callee to signal that an
exceptional situation ocurred…
… and the control is transfered to the most recent handler
established in the call chain.


`Continuations`: DUring a function call the caller wait for the callee to finish the continuation. After the return, the called proceeds with the computation. This is literally the `continuation` of the call. It's what is remaining to be done after the call. Usually it's in a `stack`.
In high level languages, the continuation can be expressed as a function.


`Using let`:

```lisp
(define (mystery a b c d e)
(+ a (* b (/ (- c d) e))))
(define (mystery a b c d e)
(let ((r0 (- c d)))
(+ a (* b (/ r0 e)))))
(define (mystery a b c d e)
(let ((r0 (- c d)))
(let ((r1 (/ r0 e)))
(+ a (* b r1)))))
(define (mystery a b c d e)
(let ((r0 (- c d)))
(let ((r1 (/ r0 e)))
(let ((r2 (* b r1)))
(+ a r2)))))
(define (mystery a b c d e)
(let ((r0 (- c d)))
(let ((r1 (/ r0 e)))
(let ((r2 (* b r1)))
(let ((r3 (+ a r2)))
r3)))))
```

Using lambda
```lisp
(define (mystery a b c d e)
(+ a (* b (/ (- c d) e))))
(define (mystery a b c d e)
((λ (r0) (+ a (* b (/ r0 e))))
(- c d)))
(define (mystery a b c d e)
((λ (r0) ((λ (r1) (+ a (* b r1)))
(/ r0 e)))
(- c d)))
(define (mystery a b c d e)
((λ (r0) ((λ (r1) ((λ (r2) (+ a r2))
(* b r1)))
(/ r0 e)))
(- c d)))
(define (mystery a b c d e)
((λ (r0) ((λ (r1) ((λ (r2) ((λ (r3) r3)
(+ a r2)))
(* b r1)))
(/ r0 e)))
(- c d)))
```

```lisp
Functions Accepting Explicit Continuations
(define (+c x y cont)
(cont (+ x y)))
(define (-c x y cont)
(cont (- x y)))
(define (*c x y cont)
(cont (* x y)))
(define (/c x y cont)
(cont (/ x y)))
```

```lisp
(define (mystery a b c d e)
(-c c d
(λ (r0) (/c r0 e
(λ (r1) ((λ (r2) ((λ (r3) r3)
(+ a r2)))
(* b r1)))))))
(define (mystery a b c d e)
(-c c d
(λ (r0) (/c r0 e
(λ (r1) (*c b r1
(λ (r2) ((λ (r3) r3)
(+ a r2)))))))))
(define (mystery a b c d e)
(-c c d
(λ (r0) (/c r0 e
(λ (r1) (*c b r1
(λ (r2) (+c a r2
(λ (r3) r3)))))))))
```

Each function now:
- Has a explicit continuation
- No return value

`Possible way to define error`

```lisp
(define (mystery a b c d e)
(-c c d
(λ (r0) (/c r0 e
(λ (r1) (*c b r1
(λ (r2) (+c a r2
(λ (r3) r3)))))))))
(define (/c x y cont)
(if (= y 0)
(error "Can't divide " (list x y))
(cont (/ x y))))
(define (error msg args)
(display msg)
(display args))
```

And now:
```lisp
>> (mystery 5 4 3 2 1)
9
>> (mystery 4 3 2 1 0)
Can't divide (1 0)
``` 

```lisp
>> (+ 1 (mystery 5 4 3 2 1))
10
>> (+ 1 (mystery 4 3 2 1 0))
ERROR ;;and the evaluator stops!!!!
```

What is going on?

- Primitive operations are converted to use explicit continuations
- mistery function `is not` converted to use explicit continuations
- If we see, the /c calls error but then this is tried to be added to 1. This is not possible.

![Continuations](https://i.imgur.com/8CW4n99.png)

The control is transferred to the continuation with the final result.

`Question`: Difference between direct style and continuation passing style?

`Answer`:
- Direct style: Implicit continuation 
```lisp
(foo 2 (bar 3 4))
```
- Continuation passing style: Explicit continuation
```lisp
(bar-c 3 4
    (lambda (r) (foo-c 2 r)))
```