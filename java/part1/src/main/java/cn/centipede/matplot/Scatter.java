package cn.centipede.matplot;

import java.util.LinkedList;
import java.util.List;

import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.MatlabTheme;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.Styler.LegendPosition;

public class Scatter extends ChartBase{
	public Scatter() {
		this(400, 300);
	}

	public Scatter(int width, int height) {
		super(width, height);
		setXAxisTitle("X");
		setYAxisTitle("Y");
		setStyle();
	}

	public void setStyle() {
		XYStyler styler = getStyler();
		styler.setMarkerSize(markerSize);
		styler.setTheme(new MatlabTheme());

		styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter)
			.setChartTitleVisible(false)
			.setLegendPosition(LegendPosition.InsideSW);
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
		addSeries(name, xData, yData);
		return this;
	}
}