package cn.centipede;

import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Range;
import cn.centipede.numpy.Numpy.np;
import cn.centipede.matplot.Axes;
import cn.centipede.matplot.Figure;
import cn.centipede.matplot.JPlot;
import cn.centipede.model.GradientDescent;
import cn.centipede.model.gradient.LinearImp;

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

    public static void showSgd() {
        GradientDescent gd = LinearImp::SGD;
        int N = 20;

        NDArray x = np.random.uniform(0, 5, N).reshape(N,1);
        NDArray ones = np.ones(new int[]{N, 1});

        NDArray y = x.multiply(3).add(np.random.uniform(0, 3, N).reshape(N,1));
        x = np.hstack(x, ones);

        NDArray ret = gd.fit(x, y, 0.01, 1000, 1e-3);
        System.out.println("#### test_linear_sgd: y = 2 * x + b ");
        System.out.println(ret);

        JPlot plot = new JPlot();
        double [][]X = (double[][])np.getArray(x.T());
        double [][]Y = (double[][])np.getArray(y.T());
        double [] W = (double[])np.getArray(ret);

        String caption = String.format("y=%f*x+%f", W[0], W[1]);

        plot.figure();
        plot.scatter(X[0], Y[0], "X-Y");
        plot.plot(X[0], a->W[0]*a+W[1], caption);

        plot.show();
    }

    public static void showNormalDistribution() {
        NDArray a = np.random.standard_normal(1000);
        JPlot plot = new JPlot();
        plot.figure();

        double[] x = new double[1000];
        for (int i = 0; i < 1000; i++) x[i]=i;

        double[] y = (double[])np.getArray(a);
        plot.scatter(x, y, "X-Y");
        plot.show();
    }

    public static void main(String[] args) {
        showSgd();
        //showSubPlot();
        //showNormalDistribution();
    }
}