# Notes for lecture of 05/03/2024

Today we talk on how to represent `code` with Julia.

## Important things

How to visualize what is going on inside Julia.

### dump

The `dump` function is used to visualize the structure of a variable.
```julia
julia> dump([1,2,3])
Array{Int64}((3,)) [1, 2, 3]
```

Remember, if we print `pi` in Julia, it will replace it with the value of `pi`.

```julia
julia> pi
π = 3.1415926535897...
```

But what if we want an array with the symbol `pi`?
```julia
julia> [pi]
1-element Array{Float64,1}:
 3.141592653589793
```

Ok, not working! We need the `quote`

```julia
julia> [:pi]
1-element Array{Symbol,1}:
 :pi
```

If we call dump on this array:
```julia
julia> dump([:pi,pi])
Array{Any}((2,))
  1: Symbol pi
  2: Irrational{:π} π
```
Look that `pi` is represented as symbol

Same, if we want a `quote of an operation`.
```julia
julia> [:(1+2)]
1-element Array{Expr,1}:
 :(1 + 2)
```

And if we call `dump` here we can see the structure of the `Expr`:
```julia
julia> dump(:(1+2))
Expr
  head: Symbol call
  args: Array{Any}((3,))
    1: Symbol +
    2: Int64 1
    3: Int64 2
```

In practice, it's like someone created the `structure` **Expr** made it with:
```c
struct Expr {
   head
   args
}       
```
If we wrap this inside some parenthesis, we know it's a struct of `Expr`. So we can ask for the parameters.

```julia
julia> (:(1+2)).head
:call
```

But we know it internalle everything works like in LISP, so we can write something like:

```julia
julia> +(1,2)
3
julia> +(1,2,3,5,6)
17
```

Remember that in Julia the operations have different importances

```julia

julia> (:(1+2*3)).args
3-element Vector{Any}:
  :+
 1
  :(2 * 3)
```

and in fact we see that multiplication is done first.

Knowing the structure of the `Expr` class we can instantiate it manually.

Note this: `The constructro` is expecting the first as the `head`, but then EVERYTHING AFTER is added to an array. So, we `DON'T` have to put the array inside the `Expr` constructor, but we can put the elements of the array as the arguments of the constructor.

```julia

julia> Expr(:call, :+, 1, 2)
:(1 + 2)
```

But is wrong to:

```julia
julia> Expr(:call, [:+,1,2])
```

We can even concatenate the constructors
    
```julia
julia> Expr(:call, :+, 1, Expr(:call, :*, 2, 3))
:(1 + 2 * 3)
```

So this mean that we can `run` the code inside this in language that support `meta-programming` like Julia.

The presence of `eval` function make it possible.

```julia

julia> eval(:(1+2))
3
```

But `eval` is not going to solve the project. Let's see an example 
where there can be problems.

```julia

julia> let a = 1, b = 2
              eval(Expr(:call, :+, :a, :b))
         end
ERROR: UndefVarError: `a` not defined
```

It will throw an `error`, because `eval` only evaluates in the global scope. So, we need to use `eval` in the global scope.

`Question`: Why does the `eval` only evaluates in the global scope?

`Answer:` When working with local scope the compiler don't need the names, because he only needs the `offset` of the variables. So, the names are not saved. For the global scope is different because those are needed to be saved.
So, `eval` doens't know where the values are stored.

## Alternatives to eval

The concept of `macros`. But before going here, let's see another example of `eval`.


### Boolean definition

Let's define some boolean operations.

```julia
julia> not(b) = b ? false : true
not (generic function with 1 method)

julia> not(true)
false

julia> not(false)
true
```

Let's define the `and`

```julia
julia> and(a,b) = a ? b : false
and (generic function with 1 method)

julia> and(true,true)
true

julia> and(true,false)
false
```
### Safe division

I'm scared of division by zero. So, let's define a safe division.

```julia
julia> safe_div(a,b) = and(not(b==0),a/b)
safe_div (generic function with 1 method)

julia> safe_div(1,0)
false

julia> safe_div(1,2)
0.5
```

Professor was `expecting an error`, but why? Let's analyze.

`safe_div` is a function that takes two arguments. It calls the `and` function that binds the value. But since it evaluate the 
values of `a` and `b` before calling `and`, it would throw an error. But looks like that in `Julia` is not the case, because `division by 0` in Julia returns a value, instead of exploding. Otherwise, we would have an error, because it would try to evaluate `a/b` before calling `and`.

Let's see with `sqr`

```julia
julia> safe_sqr(x) = and(x>=0,sqrt(x))
sqr (generic function with 1 method)

julia> safe_sqr(4)
2.0

julia> safe_sqr(-4)
ERRORS ERRORS ERRORS
```

This happens because it would try to evaluate `sqrt(-4)` before calling `and`. This is the typical behavior of `short-circuit` evaluation. We need to `control` the `flow of execution` of the code.

### AND with Short-circuit
We can use `eval` to control the flow of execution.

```julia
and(a,b) = eval(a) ? eval(b) : false

x = 4

and(:(x>=0),:(sqrt(x)))
value

x = -4
and(:(x>=0),:(sqrt(x)))
false
```

But now, what if we define the function.

```julia
safe_sqr(x) = and(:(x>=0),:(sqrt(x)))
```
If we try to call the function with an `x`, the `eval` inside the `and` has no idea of the `x` value. Because it's just checking it in the global scope. 

```julia
julia> x = -4

julia> safe_sqr(10)
false
```
It's not taking `10` but `-4`.

### Macros

Function that takes `expressions` as arguments and returns `expressions` as result.

```julia

macro and(a,b)
    Expr(:if, a,b, false)
end
```

`Question:` Difference between macro and functions?

- If you define something as a macro, macro is not going to evaluate the arguments. Aka, you don't need the quotes.
  - and(:(x>=0),:(sqrt(x)))
  - and(x>=0,sqrt(x))
- When you define the `macro`, it adds the symbol `@` before the name of the macro.

`@macroexpand` is a function that takes a macro and returns the expression that the macro would return. 
You can use this to debug the macro.

For example, in our code there is a bug. Since Julia can have problems of `collisions` of names, we need to tell Julia to not 
overwrite the variables name inside the macro.

In this case, it was referring to `x` to the `global` scope. We need to tell Julia to not overwrite the `x` inside the macro.

We can use the `esc` function to tell julia to not read name identifiers inside the macro.

```julia

macro and(a,b)
    esc(Expr(:if, a,b, false))
end
```

Not, the macro looks very ugly. We can use `backquote` to make it look better.

```julia
macro and(a,b)
    esc(:($a ? $b : false))
end
```
