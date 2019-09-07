package cn.centipede.matplot;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.XYChart;

public class Figure {
	private List<XYChart> charts;

	public Figure() {
		charts = new ArrayList<XYChart>(1);
	}

	public List<XYChart> getCharts() {
		return charts;
	}

	public void add_subplot(XYChart chart) {
		charts.add(chart);
	}

	public Axes add_subplot(int r, int c, int id) {
		if (charts == null) {
			charts = new ArrayList<XYChart>(r*c);
		}
		Axes axes = new Axes();
		charts.add(axes);
		return axes;
	}
}