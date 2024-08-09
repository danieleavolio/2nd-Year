classdef dataset
    
    properties ( SetAccess = private, GetAccess = public )
        data;
        X;
        y;
        n;
        A;
        B;
    endproperties

    methods
        function dataset = dataset(X, y)
            # combine the features and the labels
            dataset.data = [X, y];
            dataset.X = X;
            dataset.y = y;
            dataset.n = size(X, 1);
            [dataset.A, dataset.B] = dataset.split();
        endfunction

        function shuffled = shuffle(ds)
            # shuffle the dataset
            shuffled_data = ds.data(randperm(ds.n), :);
            features = size(shuffled_data, 2);
            shuffled = dataset(shuffled_data(:, 1:features-1), shuffled_data(:, features));
        endfunction

        function [X, y] = get_data(dataset)
            # return the features and the labels
            X = dataset.X;
            y = dataset.y;
        endfunction

        function summary(dataset)
            disp(dataset);
            # print the number of rows and columns
            disp(sprintf('\n%srows: %d, columns: %d', inputname(1), size(dataset.data, 1), size(dataset.data, 2)));
            dataset.peek();
        endfunction

        function head(dataset, n=5)
            disp(dataset.data(1:n, :));
        endfunction

        function tail(dataset, n=5)
            disp(dataset.data(end-n+1:end, :));
        endfunction

        function peek(dataset, n=5)
            # show the first and the last n rows of the dataset,
            # printing '...' in between
            subset = vertcat(dataset.data(1:n, :), dataset.data(end-n+1:end, :));
            format = disp(subset);
            # split the format string into lines
            splitlines = @(str) regexp(str, '\n', 'split');
            lines = splitlines(format);
            # print the first n lines
            for i = 1:n
                disp(lines{i});
            endfor
            # print '...' in between
            disp('...');
            # print the last n lines
            for i = length(lines)-n+1:length(lines)
                disp(lines{i});
            endfor
        endfunction

        function [A, B] = split(ds, a=1, b=-1)
            A = ds.X(ds.y == a, :);
            B = ds.X(ds.y == b, :);
        endfunction

        function plot_3d(dataset)
            # function to plot a 3d dataset
            # the dataset must have 3 features and 1 label
            # the label must be either 1 or -1
            # the label is used to color the points
            # the features are used to plot the points
            # the function plots the points in a 3d space
            if size(dataset.data, 2) != 4
                error('The dataset must have 3 features and 1 label');
            endif
            
            # plot the points
            scatter3(dataset.A(:, 1), dataset.A(:, 2), dataset.A(:, 3), 'r', 'filled');
            hold on;
            scatter3(dataset.B(:, 1), dataset.B(:, 2), dataset.B(:, 3), 'b', 'filled');
            hold off;
        endfunction

        function plot_3d_predictions(dataset, y_hat)
            if size(dataset.data, 2) != 4
                error('The dataset must have 3 features and 1 label');
            endif

            A = dataset.X(y_hat == 1, :);
            B = dataset.X(y_hat == -1, :);

            # plot the points
            scatter3(A(:, 1), A(:, 2), A(:, 3), 'r', 'filled');
            hold on;
            scatter3(B(:, 1), B(:, 2), B(:, 3), 'b', 'filled');
            hold off;
        endfunction

        function to_csv(dataset, filename)
            csvwrite(filename, dataset.data);
        endfunction
    endmethods


    methods (Static = true)
        function dataset = from_file(path)
            # load the dataset
            load(path);
            dataset = dataset(X, y);
        endfunction
    endmethods

endclassdef
