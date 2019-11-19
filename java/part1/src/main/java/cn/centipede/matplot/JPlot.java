package cn.centipede.matplot;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleFunction;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class JPlot {
    private Figure figure;

    public Figure figure() {
        figure = new Figure();
        return figure;
    }

    public void scatter(double[] x, double[] y, String caption) {
        Scatter scatter = new Scatter();
        scatter.plot(x, y, caption);
        figure.add_subplot(scatter);
    }

    public void plot(double[] x, DoubleFunction<Double> func, String name) {
        List<XYChart> charts = figure.getCharts();
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
        y[i] = func.apply(x[i]);
        }

        charts.get(0).addSeries(name, x, y);
        Map<String, XYSeries> map = charts.get(0).getSeriesMap();
        map.get(name).setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
        map.get(name).setMarker(SeriesMarkers.CIRCLE);
    }

    public void plot(double[] x, double[] y, String name) {
        Axes axes = new Axes();
        axes.plot(x, y, name);
        figure.add_subplot(axes);
    }

    public void show() {
        List<XYChart> charts = figure.getCharts();
        if (charts.size() > 1) {
            new SwingWrapper<XYChart>(charts).displayChartMatrix("JPlot");
        } else {
            new SwingWrapper<XYChart>(charts.get(0)).displayChart("JPlot");
        }
    }
}