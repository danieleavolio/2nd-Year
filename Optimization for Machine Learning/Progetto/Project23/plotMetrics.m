function plotMetrics(metrics,labels,t="Metrics")
    figure;
    bar(metrics);
    set(gca, 'xticklabel', labels);
    title(t);
    xlabel('Metric');
    ylabel('Value');
    % save the figure as png with t as name
    saveas(gcf, t+".png");
    
end