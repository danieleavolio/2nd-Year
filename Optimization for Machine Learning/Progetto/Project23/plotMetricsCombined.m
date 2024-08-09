function plotMetricsCombined(metrics_combined, labels, t="Metrics", color1="blue", color2="red")
    figure;
    h = bar(metrics_combined, 'grouped');
    % Colori custom
    set(h(1), 'FaceColor', color1); 
    set(h(2), 'FaceColor', color2); 
    set(gca, 'xticklabel', labels);
    title(t);
    xlabel('Metric');
    ylabel('Value');
    legend({'Training Metrics', 'Testing Metrics'}, 'Location', 'Best');
    filename = strcat(t, '.png');
    if ~exist('output', 'dir')
        mkdir('output');
    end
    output_path = fullfile('output', filename);
    saveas(gcf, output_path);
end
