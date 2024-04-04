# Define a simple function for sqr
sqr(x) = x*x

# We can look at the code that was generated for the function.
# This is a reflective operation, and is not usually available in all languages.
@code_typed sqr(3)
# Output:
# CodeInfo(
# 1 ─ %1 = Base.mul_int(x, x)::Int64
# └──      return %1
# ) => Int64

# Let's see what happens when we call the function with a float.
sqr(3.0)
# Output:
# 9.0

# We can also look at the code that was generated for the function.
@code_typed sqr(3.0)

# CodeInfo(
# 1 ─ %1 = Base.mul_float(x, x)::Float64
# └──      return %1
# ) => Float64

# What happens when we call the function with a string?

sqr("hello")
# Output:
# "hellohello"

# We can also look at the code that was generated for the function.
@code_typed sqr("hello")

# CodeInfo(
# 1 ─ %1 = invoke Base._string(x::String, x::Vararg{String})::String
# └──      return %1
# ) => String

# Note: This is a macro-instruction. It's not exactly the machine code. This is an high level assembly.
# We have to go to another level to see the machine code.
# The exact code is @code_llvm

@code_llvm sqr(3)

# Output:
# ;  @ d:\Lovaion\University\2nd Year\Erasmus\Advanced Programming\Lab\Lab1\Example.jl:2 within `sqr`
# ; Function Attrs: uwtable
# define i64 @julia_sqr_1392(i64 signext %0) #0 {
# top:
# ; ┌ @ int.jl:88 within `*`
#    %1 = mul i64 %0, %0
# ; └
#   ret i64 %1
# }

@code_llvm sqr(3.0)

# Output:
# ;  @ d:\Lovaion\University\2nd Year\Erasmus\Advanced Programming\Lab\Lab1\Example.jl:2 within `sqr`
# ; Function Attrs: uwtable
# define double @julia_sqr_1409(double %0) #0 {
# top:
# ; ┌ @ float.jl:411 within `*`
#    %1 = fmul double %0, %0
# ; └
#   ret double %1
# }


@code_llvm sqr("hello")

# Oh boy...
# ;  @ d:\Lovaion\University\2nd Year\Erasmus\Advanced Programming\Lab\Lab1\Example.jl:2 within `sqr`
# ; Function Attrs: uwtable
# define nonnull {}* @julia_sqr_1403({}* noundef nonnull %0) #0 {
# top:
#   %1 = alloca [2 x {}*], align 8
#   %.sub = getelementptr inbounds [2 x {}*], [2 x {}*]* %1, i64 0, i64 0
# ; ┌ @ strings/basic.jl:260 within `*`
# ; │┌ @ strings/substring.jl:225 within `string`
#     store {}* %0, {}** %.sub, align 8
#     %2 = getelementptr inbounds [2 x {}*], [2 x {}*]* %1, i64 0, i64 1
#     store {}* %0, {}** %2, align 8
#     %3 = call nonnull {}* @j1__string_1405({}* inttoptr (i64 140709611612992 to {}*), {}** nonnull %.sub, i32 2)
# ; └└
#   ret {}* %3
# }

# As we can see, the code generated for the string is a bit more complex than the one for the numbers.
# But in general, there is a code generation different for each type of input.

# Usually, what hapepns is that functions are called from other functions.

foo(a) = sqr(a+1)+3


foo(3)

# Output:
# 19

@code_llvm foo(3)

# Output:
# ;  @ d:\Lovaion\University\2nd Year\Erasmus\Advanced Programming\Lab\Lab1\Example.jl:95 within `foo`
# ; Function Attrs: uwtable
# define i64 @julia_foo_1424(i64 signext %0) #0 {
# top:
# ; ┌ @ int.jl:87 within `+`
#    %1 = add i64 %0, 1
# ; └
# ; ┌ @ d:\Lovaion\University\2nd Year\Erasmus\Advanced Programming\Lab\Lab1\Example.jl:2 within `sqr`
# ; │┌ @ int.jl:88 within `*`
#     %2 = mul i64 %1, %1
# ; └└
# ; ┌ @ int.jl:87 within `+`
#    %3 = add i64 %2, 3
# ; └
#   ret i64 %3
# }

# What if we change the value of sqr to return always 42? That's the answer to everything, right?
sqr(x) = 42

# Now, every time we call the function, we will get 42.
sqr(3)

# 42

# But now if we call the function inside another function, it will invalidate the previous code and every time
# it will generate a new one.

foo(3)

# 45

@code_llvm foo(3)

# # Output:
# ;  @ d:\Lovaion\University\2nd Year\Erasmus\Advanced Programming\Lab\Lab1\Example.jl:95 within `foo`
# ; Function Attrs: uwtable
# define i64 @julia_foo_1426(i64 signext %0) #0 {
# top:
#   ret i64 45
# }

# Let's see the factorial definition

fact(n) = n == 0 ? 1 : n*fact(n-1)

fact(5)
# 120

# If we insert a very large number, we will get 0
fact(1000)
# 0

# This happens because the number is too large and the result is not representable in 64 bits.

# Yo, this is not like Python. They wanted to attract people from Python, so they made a syntax that is similar to Python.
function fact(n)
    if n == 0
        return 1
    else
        return n*fact(n-1)
    end
end

# Note: In Julia IF is only an expression, not a statement. It returns a value.
# In Julia you can even return without writing the "return" keyword.

function fact(n)
    if n == 0
        1
    else
        n*fact(n-1)
    end
end

#############################
# Scopes

# In Julia, the scope is defined by the block. The block is defined by the "let" keyword and the "end" keyword.

let x = 3
    x + 1
end + 1

# Output:
# 5

# Example of nested score

let a = 2
    let a = 1
        a + 3
    end + a
end

# Output:
# 6

# It's like this because the inner "a" is shadowing the outer "a" and then the inner "a" is added to the result of the inner block.
# Later then the outer "a" is added to the result of the outer block.
# So the result is 4 + 2 = 6

# Even using the previous scope to define the next scope, it works!

let a = 2
    let a = 1 + a
        a + 3
    end + a
end
# Output:
# 8

# In Python, something like this is not possible because Python is stupid.

################################
# Blocks

# Blocks: Sequence of instructions that are executed in order.

# In Julia, the block is defined by the "begin" keyword and the "end" keyword.

begin
    x = 3
    x + 1
end + 1

# Output:
# 5

# There is an alternative syntax
(println("foo"); println("bar"); 1+2;)

# Output:
# foo
# bar
# 3

#############################
# Pairs
1 => 2 # Pair

1 => 2 => 3 => 4 # Chain of pairs
# Output:
# 1 => (2 => (3 => 4))

##############
# Vectors

# Vectors are defined by the square brackets.

[1, 2, 3]

# Output:
# 3-element Vector{Int64}:
# 1
# 2
# 3

# This is a column vector

[1 2 3]

# Output:
# 1×3 Matrix{Int64}:

# 1 2 3

# This is a row vector

###############
# Tuples

# Tuples are defined by the parenthesis.

(1, 2, 3)

# Output:
# (1, 2, 3)

# Tuples are immutable. Once they are defined, they cannot be changed.

# Note: Tuple and Array
# If we insert a different type, in array it will convert all the elements to the same type.
# In the tuple, it will keep the types.

[1, 2, 3.0]

# Output:
# 3-element Vector{Float64}:
# 1.0
# 2.0
# 3.0


(1, 2, 3.0)

# Output:
# (1, 2, 3.0)

