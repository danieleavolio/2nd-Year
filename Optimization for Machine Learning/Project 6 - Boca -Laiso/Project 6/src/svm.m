function [alphas, b, sv_x, sv_y] = svm(dataset, C, costant=1, degree=2)
    % Compute the kernel matrix
    K = polynomial_kernel_matrix(dataset, costant, degree);
    % Compute the Hessian matrix
    H = (dataset.y * dataset.y') .* K;
    % Linear term
    q = -ones(dataset.n, 1);
    % Equality constraints
    A = dataset.y';
    b = 0;
    % Bounds
    lb = zeros(dataset.n, 1);
    ub = C * ones(dataset.n, 1);
    
    % Solve the quadratic program
    options = optimset('MaxIter', 50000);
    [alphas, obj, info, lambda] = qp([], H, q, A, b, lb, ub, options);

    alphas = alphas;
    sv_x = dataset.X;
    sv_y = dataset.y;
    % Compute the bias
    bias = 0;
    for i = 1:length(alphas)
        bias = bias + sv_y(i);
        for j = 1:length(alphas)
            bias = bias - alphas(j) * sv_y(j) * polynomial_kernel(sv_x(i, :), sv_x(j, :), costant, degree);
        end
    end
    b = bias / length(alphas);
end
