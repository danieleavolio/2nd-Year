function K = polynomial_kernel_matrix(dataset, c=1, d=2)
    for i = 1:dataset.n
        for j = i:dataset.n
            K(i, j) = polynomial_kernel(dataset.X(i, :), dataset.X(j, :), c, d);
            K(j, i) = K(i, j);
        end
    end
end
