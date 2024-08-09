function polynomial_kernel = polynomial_kernel(X_1, X_2, c=1, degree=2)
    polynomial_kernel = (X_1 * X_2' + c) .^ degree;
end
