package cn.centipede.model.gradient;

import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;

public class LinearImp {

	public static double loss(NDArray x, NDArray y, NDArray w) {
		NDArray diff = x.dot(w).subtract(y);
		double cost = np.dot(diff.T, diff).asDouble();
		return cost/(x.getDimens()[0]*2);
	}

	public static NDArray BGD(NDArray x, NDArray y, double alpha, int epochs, double epsilon) {
		int m = x.getDimens()[0];
		int n = x.getDimens()[1];
		NDArray w = np.ones(n);

		for (int i = 0; i < epochs; i++) {
			NDArray diff = np.dot(x, w).subtract(y);
			NDArray gradient = (np.dot(x.T, diff)).divide(m);
			w = w.subtract(gradient.multiply(alpha));
		}
		return w;
	}

	public static NDArray SGD(NDArray x, NDArray y, double alpha, int epochs, double epsilon) {
		int m = x.getDimens()[0];
		int n = x.getDimens()[1];
		NDArray w = np.ones(new int[]{n, 1});

		for (int i = 0; i < epochs; i++) {
			int idex = np.random.randint(m);
			NDArray rand = x.at(idex).V();
			NDArray diff = np.dot(rand, w).subtract(y.at(idex));
			NDArray gradient = np.dot(rand.T, diff);
			w = w.subtract(gradient.multiply(alpha));
		}
		return w;
	}

	public static NDArray MBGD(NDArray x, NDArray y, double alpha, int epochs, double epsilon) {
		int m = x.getDimens()[0];
		int n = x.getDimens()[1];
		NDArray w = np.ones(n);
		int batch_size = 5;

		for (int i = 0; i < epochs; i++) {
			int[] idex = np.random.choice(m, batch_size);
			NDArray rand = x.index(idex);
			NDArray diff = np.dot(rand, w).subtract(y.index(idex));
			NDArray gradient = np.dot(rand.T, diff).divide(batch_size);
			w = w.subtract(gradient.multiply(alpha));
		}
		return w;
	}
}