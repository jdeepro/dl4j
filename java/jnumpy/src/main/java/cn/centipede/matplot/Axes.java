package cn.centipede.matplot;

import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleFunction;

import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.MatlabTheme;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class Axes extends ChartBase {
    public Axes() {
        super();
        XYStyler style = getStyler();
        style.setXAxisTickMarkSpacingHint(20);
        style.setTheme(new MatlabTheme());
    }

    @Override
    public ChartBase plot(double[] x, double[] y, String name) {
        List<Double> xData = new LinkedList<Double>();
        List<Double> yData = new LinkedList<Double>();

        int size = x.length > y.length ? y.length : x.length;
        for (int i = 0; i < size; i++) {
            xData.add(x[i]);
            yData.add(y[i]);
        }

        XYSeries series = addSeries(name, xData, yData);
        series.setMarker(SeriesMarkers.NONE);
        return this;
    }

    public ChartBase plot(double[] x, DoubleFunction<Double> func, String name) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = (Double)func.apply(x[i]);
        }
        return plot(x, y, name);
    }
}