function [training_set, test_set] = k_folds(ds, fold, k)
    test_set_size = floor(ds.n / k);
    training_set_size = ds.n - test_set_size;

    test_set_start = (fold - 1) * test_set_size + 1;
    test_set_end = fold * test_set_size;
    train_i = 1;
    test_i = 1;
    for i = 1:ds.n
        if i >= test_set_start && i <= test_set_end
            X_test(test_i, :) = ds.X(i, :);
            y_test(test_i, :) = ds.y(i, :);
            test_i = test_i + 1;
        else
            X_training(train_i, :) = ds.X(i, :);
            y_training(train_i, :) = ds.y(i, :);
            train_i = train_i + 1;
        end
    end
    test_set = dataset(X_test, y_test);
    training_set = dataset(X_training, y_training);
end