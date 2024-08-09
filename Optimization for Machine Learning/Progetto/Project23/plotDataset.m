function plotDataset(posPoints, negPoints, x0, t="Dataset")
    figure;
    hold on;
    scatter(posPoints(:, 1), posPoints(:, 2), 'b', 'o', 'filled');
    scatter(negPoints(:, 1), negPoints(:, 2), 'r', 'o', 'filled');
    scatter(x0(1), x0(2), 'm', 's', 'filled');
    xlabel('Feature 1');
    ylabel('Feature 2');
    title(t);
    legend('Positive points', 'Negative points', 'Positive barycenter');
    grid on;
    legend('Positive points', 'Negative points', 'Positive barycenter');

    if ~exist('output', 'dir')
        mkdir('output');
    end
    output_path = fullfile('output', strcat(t, '.png'));
    saveas(gcf, output_path);
    
end
