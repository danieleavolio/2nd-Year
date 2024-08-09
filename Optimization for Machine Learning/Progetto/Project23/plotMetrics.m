function plotMetrics(metrics,labels,t="Metrics", color="blue")
    figure;
    h = bar(metrics);

    % Set colors of the bars
    set(h, 'FaceColor', color);
    % set labels 45 degrees rotated
    set(gca, 'xticklabel', labels);
    title(t);
    xlabel('Metric');
    ylabel('Value');
    xtickangle(45);
    % save the figure as png
    filename = strcat(t, '.png');
    
    if ~exist('output', 'dir')
        mkdir('output');
    end
    output_path = fullfile('output', filename);
    saveas(gcf, output_path);
    
end