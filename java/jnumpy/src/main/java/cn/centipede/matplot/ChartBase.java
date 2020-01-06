package cn.centipede.matplot;

import org.knowm.xchart.XYChart;

public abstract class ChartBase extends XYChart{
    protected int markerSize = 4;

    public ChartBase() {
        super(400, 300);
    }

    public ChartBase(int w, int h) {
        super(w, h);
    }

    public ChartBase plot(double[] x, double[] y) {
        return plot(x, y, "X-Y");
    }

    public abstract ChartBase plot(double[] x, double[] y, String name);
}