package cn.centipede.matplot;

import java.util.List;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

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

	public void show() {
		List<XYChart> charts = figure.getCharts();
		if (charts.size() > 1) {
			new SwingWrapper<XYChart>(charts).displayChartMatrix("JPlot");
		} else {
			new SwingWrapper<XYChart>(charts.get(0)).displayChart("JPlot");
		}
	}
}