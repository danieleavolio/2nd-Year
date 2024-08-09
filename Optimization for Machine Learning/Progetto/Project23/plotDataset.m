function plotDataset(posPoints, negPoints, x0, t="Dataset")
    % Plot points big
    figure;
    hold on;

    % Plot positive points in blue
    scatter(posPoints(:, 1), posPoints(:, 2), 'b', 'o', 'filled');

    % Plot negative points in red
    scatter(negPoints(:, 1), negPoints(:, 2), 'r', 'o', 'filled');

    % Plot the x0 with a black border
    scatter(x0(1), x0(2), 'm', 's', 'filled');

    % Set axis labels and title
    xlabel('Feature 1');
    ylabel('Feature 2');
    title(t);

    % Add legend
    legend('Positive points', 'Negative points', 'Positive barycenter');

    % Set grid
    grid on;

    % add legends
    legend('Positive points', 'Negative points', 'Positive barycenter');
end
