package cn.centipede.model.gradient;

import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;

public class LinearImp {

	public static double loss(NDArray x, NDArray y, NDArray w) {
		NDArray diff = x.dot(w).subtract(y);
		double cost = np.dot(diff.T, diff).asDouble();
		return -cost/(x.getDimens()[0]*2);
	}

	public static NDArray BGD(NDArray x, NDArray y, double alpha, int epochs, double epsilon) {
		int m = x.getDimens()[0];
		int n = x.getDimens()[1];
		NDArray w = np.ones(n);

		for (int i = 0; i < epochs; i++) {
			NDArray diff = np.dot(x, w).subtract(y);
			NDArray gradient = (np.dot(x.T, diff)).divide(m);
			w = w.subtract(gradient.multiply(alpha));
			if (i < 40)System.out.println(loss(x, y, w));
		}
		return w;
	}

	public static NDArray SGD(NDArray x, NDArray y, double alpha, int epochs, double epsilon) {
		// TODO Auto-generated method stub
		return null;
	}

	public static NDArray MBGD(NDArray x, NDArray y, double alpha, int epochs, double epsilon) {
		// TODO Auto-generated method stub
		return null;
	}
}