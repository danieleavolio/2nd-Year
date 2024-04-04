# Continue from the previous example

```lisp
>> (fdef lie (truth)
(display "it is not true that ")
(display truth)
(newline))
(function (truth) ...)
>> (lie "1+1=2")
it is not true that 1+1=2
>> (lie "it is not true that 1+1=2")
it is not true that it is not true that 1+1=2
>> (fdef make-it-true (lie)
(lie lie))
(function (lie) (lie lie))
>> (make-it-true "1+1=3")
error in cadr: expected appropriate list structure, but got "1+1=3"
backtrace:
0 (cadr func)
1 (function-parameters func)
2 (eval input initial-environment)
3 (repl)
```

The problem here is that `lie` is a string, not a function. We are passing the function as a parameter.

Java solves it using `namespaces`

```java

public class foo {
    int foo;

    foo() {
        foo = 0;
    }

    foo(foo foo) {
        this.foo = foo.foo + 1;
    }

    void foo(foo foo) {
        System.err.println(foo.foo);
    }

    public static void main(String args[]) {
        foo foo = new foo();
        foo:foo.foo(new foo(foo));
    }
}
```

This program prints `1`. The `foo` in `foo:foo.foo` is the class name, and the `foo` in `foo(foo)` is the parameter name. The `foo` in `foo:foo.foo` is the class name, and the `foo` in `foo.foo` is the parameter name. The `foo` in `foo(foo)` is the parameter name, and the `foo` in `foo.foo` is the method name.

In `C` you have a single namespace for functions and variables, but different for `struct`.

To solve the problem in `lisp` we have to define a `function namespace`.

```lisp
(define (in-function-namespace name)
    (string->symbol
        (string-append
            "%function-"
            (symbol->string name))))

;(flet ((square (x) (* x x))
; (foo (a b c) (+ a (* b c))))
; ...)
(define (flet-names exp)
    (map car (cadr exp)))
(define (flet-names exp)
    (map in-function-namespace
        (map car (cadr exp))))
```

```lisp
;;fdef
;(fdef triple (a) (+ a a a))
(define (fdef-name exp)
    (cadr exp))
(define (fdef-name exp)
    (in-function-namespace (cadr exp)))

;;Function Call
;;(square radius)
(define (call-operator exp)
    (car exp))
(define (call-operator exp)
    (in-function-namespace (car exp)))
```

And then the primitive
```lisp
;;Initial Bindings
(define initial-bindings
    (list (cons 'pi 3.14159)
    ...
    (cons (in-function-namespace '+) (make-primitive +))
    (cons (in-function-namespace '*) (make-primitive *))
    (cons (in-function-namespace '-) (make-primitive -))
    (cons (in-function-namespace '/) (make-primitive /))
    (cons (in-function-namespace '=) (make-primitive =))
    (cons (in-function-namespace '<) (make-primitive <))
    (cons (in-function-namespace '>) (make-primitive >))
    (cons (in-function-namespace '<=) (make-primitive <=))
    (cons (in-function-namespace '>=) (make-primitive >=))
    ...
    (cons (in-function-namespace 'read) (make-primitive read))))
```


This is working!  But why do languages are going into the direction of having a single namespace?

EH, because there are problems of course. Let's see some puzzles.

```lisp
>> (def foo 1)
1
>> (fdef foo () foo)
(function () foo)
>> foo
1
>> (foo)
1
>> (let ((foo (+ (foo) 1)))
    (flet ((foo (foo) (+ foo 1)))
        (foo (+ foo 1))))
4
>> (+ foo
    (foo)
    (let ((foo 2))
        (foo))
    (flet ((foo () foo))
        (foo)))
1+1+2+1 = 5
```

But now the problem itslef.

```lisp
>> (fdef reflexive-operation (f x)
(f x x))
(function (f x) (f x x))
>> (reflexive-operation + 3)
error in "Unbound name -- EVAL-NAME": +
```

The problem is that the `+` is not in a `function position`, as we expect it to be. In Julia, we said we consider functions if they are in a syntaxt like:
```lisp
(f x)
```
`f` is considered a function. In Lisp2, if the functions is not in a function position `it's not considered in the function namespace`. 
What we use is the form `(function +)`.

Moreover, we want to treat `#\'foo` as an abbreviation for `(function foo)`. 

```lisp
;Evaluator
;;(function foo)
(define (function-reference? exp)
(and (pair? exp) (eq? (car exp) 'function)))
(define (function-reference-name exp)
(cadr exp))
(define (eval-function-reference exp env)
(eval (in-function-namespace (function-reference-name exp))
env))
(define (eval exp env)
(cond ((self-evaluating? exp) exp)
((name? exp)
(eval-name exp env))
((function-reference? exp)
(eval-function-reference exp env))
...
(else
(error "Unknown expression type -- EVAL" exp))))
```

```lisp
>> (fdef reflexive-operation (f x)
(f x x))
(function (f x) (f x x))
>> (reflexive-operation (function +) 3)
error in "Unbound name -- EVAL-NAME": %function-f
```

Now the problem is that everything in `function position` is threated as a function. In this case, `f` is not a function declared.

We fix this using `funcall`

```lisp
;;(funcall f ...)
(define (funcall? exp)
(and (pair? exp) (eq? (car exp) 'funcall)))
(define (funcall-operator exp)
(cadr exp))
(define (funcall-operands exp)
(cddr exp))
(define (eval-funcall exp env)
(let ((func (eval-name (funcall-operator exp) env))
(args (eval-exprs (funcall-operands exp) env)))
(if (primitive? func)
(apply-primitive-function func args)
(let ((extended-environment
(augment-environment (function-parameters func)
args
env)))
(eval (function-body func) extended-environment)))))

;;Evaluator
(define (eval exp env)
(cond ((self-evaluating? exp) exp)
((name? exp)
(eval-name exp env))
((function-reference? exp)
(eval-function-reference exp env))
((funcall? exp)
(eval-funcall exp env))
((if? exp)
(eval-if exp env))
((let? exp)
(eval-let exp env))
((flet? exp)
(eval-flet exp env))
((call? exp)
(eval-call exp env))
(else
(error "Unknown expression type -- EVAL" exp))))
```

This make the code way more complex, waaay more.

We have to find a way to work with a `single namespace`.

Drop function (and function-reference?,
function-reference-name, eval-function-reference).
Drop funcall (and funcall?, funcall-operator,
funcall-operands, eval-funcall).
Drop in-function-namespace.

Those are the semplification.

I don't fucking get it.

Boh now we are talking about another thing for functoins.


```lisp
>> (fdef next (i)
(if (< i 0)
(- i 1)
(+ i 1)))
(function (i) ...)
>> (fdef next (i)
    ((if (< i 0) - +) i 1))
(function (i) ...)
>> (next 3)
error in "Unbound name -- EVAL-NAME": (if (< i 0) - +)
```

This doesn't work because now it's expecting a function and we are passing an expression. To solve this, we don't have to eval the name of the function, but we can evaluate directly the expression:

```lisp
(define (eval-call exp env)
(let ((func (eval (call-operator exp) env))
(args (eval-exprs (call-operands exp) env)))
(if (primitive? func)
(apply-primitive-function func args)
(let ((extended-environment
(augment-environment (function-parameters func)
args
env)))
(eval (function-body func) extended-environment)))))
```

### Anon functions

```lisp
>> (fdef sum (f a b)
(if (> a b)
0
(+ (f a)
(sum f (+ a 1) b))))
(function (f a b) ...)
>> (fdef approx-pi (n)
(flet ((term (i) (/ (expt -1.0 i) (+ (* 2 i) 1))))
(* 4 (sum term 0 n))))
(function (n) ...)
>> (approx-pi 100)
3.151493401070991
```

Why do we need this `therm`? WHen we don't need the function we can use anonymous functions. BUt to do so, we have to make anon functions available for us.


```lisp
;;(lambda (a b)
;; (+ a b))
(define (lambda? exp)
    (and (pair? exp)
        (eq? (car exp) 'lambda)))
(define (lambda-parameters exp)
    (cadr exp))
(define (lambda-body exp)
    (cddr exp))
(define (eval-lambda exp env)
    (make-function (lambda-parameters exp)
                    (lambda-body exp)))

;;Evaluator
(define (eval exp env)
    (cond ((self-evaluating? exp) exp)
        ((name? exp)
        (eval-name exp env))
        ((lambda? exp)
        (eval-lambda exp env))
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
        ...
        (else
        (error "Unknown expression type -- EVAL" exp))))
```

The evaluator is becoming huge. Having anonymous functions you can try to balance a lot of stuff.
- (fdef square (x) (* x x)) = (def square (lambda (x) (* x x)))
- Or, in more abstract terms: fdef = def + lambda
- The same equivalence occurs with flet = let + lambda.

The problems in Julia is that if you use this to setup the function with the anonymous function, you can later change it BUT you cannot change the type of it.
![asdsa](https://i.imgur.com/1AcE73b.png)

![asd2](https://i.imgur.com/xtFPsyv.png)

![asd3](https://i.imgur.com/WaGh1uG.png)

The last one makes clear why we don't need `let` if we have the anon functions.

`Second order sum example`

```lisp
*>> (fdef sum (f a b)
    (if (> a b)
    0
    (+ (f a)
        (sum f (+ a 1) b))))
(function (f a b) ...)

>> (fdef second-order-sum (a b c i0 i1)
    (sum (lambda (x)
        (+ (* a x x) (* b x) c))
        i0 i1))
(function (a b c i0 i1) ...)*

>> (second-order-sum 10 5 0 -1 +1)
0 ;;What???
```

The problem is that when you have same names in the function and in the parameters, the parameters are `shadowed` by the function. 

### Dynamic scope

- `def`: creates binding in the current env
- `function call`: creates a set of bindings for the parameters that extends the current env
- `end of the call`: the extended env is deleted
- `name` is search in the current env

### Downards funarg problem

THe problem is that passing a function with free variables as arguments causes the downards funarg problem. The function is called in an environment where the free varaibles might be shadowed by ohter variables of the calling context

`Solution`: functions must know the correct env to use when they are called.
The correct solution is to associate the downward function to the
scope where the function was created:
That scope is still active when the passed function is called…
…unless it is possible to store the function somewhere and call it
later

```lisp
>> (fdef compose (f g)
(lambda (x)
(f (g x))))
(function (f g) ...)
>> (fdef foo (x) (+ x 5))
(function (x) (+ x 5))
>> (fdef bar (x) (* x 3))
(function (x) (* x 3))
>> (def foobar (compose foo bar))
(function (x) (f (g x)))
>> (foobar 2)
error in "Unbound name -- EVAL-NAME": f
```

This brings to the `Upward funarg problem`: Returning a function with free variables as result causes the
upwards funarg problem…
… where the function is called in an environment where the free
variables are not longer bound (or are bound to different values)

Java solves this forbidding `non final` free variables in anonoymous inner classes.

```lisp
(define (make-function parameters body env)
    (cons 'function
                (cons parameters
                            (cons env
                                        body))))

(define (function? obj)
    (and (pair? obj)
             (eq? (car obj) 'function)))

(define (function-parameters func)
    (cadr func))

(define (function-body func)
    (make-begin (cdddr func)))

(define (function-environment func)
    (caddr func))

(define (eval-lambda exp env)
    (make-function (lambda-parameters exp)
                                 (lambda-body exp)
                                 env))

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

```lisp
>> (fdef sum (f a b)
    (if (> a b)
        0
        (+ (f a)
            (sum f (+ a 1) b))))
(function (f a b) ...)

>> (fdef second-order-sum (a b c i0 i1)
    (sum (lambda (x)
        (+ (* a x x) (* b x) c))
        i0 i1))
(function (a b c i0 i1) ...)

>> (second-order-sum 10 5 0 -1 +1)
20 ;;As expected
```

Now a stack is not enough. To remove an environment we have to make sure that there are no references to the variables in that env. Or a compiler does this or we rely on garbage collection.