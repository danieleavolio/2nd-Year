function y_hat = predict(X, alphas, b, sv_x, sv_y, kernel_costant=1, kernel_exponent=2)
    y_hat = zeros(size(X, 1), 1);
    for i = 1:size(X, 1)
        y_hat(i) = sign(sum(alphas .* sv_y .* polynomial_kernel(sv_x, X(i, :), kernel_costant, kernel_exponent)) + b);
        if y_hat(i) == 0
            y_hat(i) = +1;
        end
    end
end