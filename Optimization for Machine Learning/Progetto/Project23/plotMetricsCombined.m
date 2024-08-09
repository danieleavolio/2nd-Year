% Plot con barre a colori diversi
function plotMetricsCombined(metrics_combined, labels, t="Metrics")
    figure;
    h = bar(metrics_combined, 'grouped'); % Usa 'grouped' per barre affiancate
    % Personalizza i colori delle barre
    set(h(1), 'FaceColor', 'blue'); % Colore delle barre per Training Metrics
    set(h(2), 'FaceColor', 'red'); % Colore delle barre per Testing Metric
    set(gca, 'xticklabel', labels);
    title(t);
    xlabel('Metric');
    ylabel('Value');
    legend({'Training Metrics', 'Testing Metrics'}, 'Location', 'Best');
    % save the figure as png
    saveas(gcf, 'metrics_combined.png');
    

end
