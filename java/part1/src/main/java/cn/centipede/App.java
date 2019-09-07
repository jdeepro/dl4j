package cn.centipede;

import cn.centipede.jnum.Range;
import cn.centipede.matplot.Axes;
import cn.centipede.matplot.Figure;
import cn.centipede.matplot.JPlot;

public class App {
	public static void showScatter() {
		JPlot plot = new JPlot();
		double []RM = {6.575, 6.421, 7.185, 6.998, 7.147, 6.43, 6.012, 6.172, 5.631};
    double []MDEV = {24, 21.6, 34.7, 33.4, 36.2, 28.7, 22.9, 27.1, 16.5};

		plot.figure();	
		plot.scatter(RM, MDEV, "Boston House Price");
		plot.show();
	}

	public static void showXY() {
		JPlot plot = new JPlot();

		Figure figure = plot.figure();
		Axes axes = figure.add_subplot(1, 1, 1);
		double[] x = Range.arange(0, 11, 0.1);
		axes.plot(x, x_->x_*x_, "y=x^2");
		plot.show();
	}

	public static void showSubPlot() {
		JPlot plot = new JPlot();
		double[] x = Range.arange(0, 10, 0.1);

		Figure figure = plot.figure();
		Axes axes = figure.add_subplot(2, 2, 1);
		axes.plot(x, v->v, "y=x");
	
		axes = figure.add_subplot(2, 2, 2);
		axes.plot(x, v->v*v, "y=x^2");

		axes = figure.add_subplot(2, 2, 3);
		axes.plot(x, v->v*v*v, "y=x^3");

		axes = figure.add_subplot(2, 2, 4);
		axes.plot(x, v->Math.sin(v), "y=sin(x)");

		plot.show();
	}

	public static void main(String[] args) {
		showSubPlot();
	}
}