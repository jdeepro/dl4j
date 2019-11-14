package cn.centipede.model;

import cn.centipede.numpy.NDArray;

/**
 * Gradient Descent
 * BGD ：Batch Gradient Descent
 * SGD ：Stochastic Gradient Descent
 * MBGD：Mini-batch Gradient Descent
 * 
 * 
 */
public interface GradientDescent {

	NDArray gradientDescent(NDArray x, NDArray y, double alpha, int epochs, double epsilon);

	/**
	 * epochs = 10000, epsilon = 1e-4
	 * @param x
	 * @param y
	 * @param alpha
	 * @return weights
	 */
	default NDArray gradientDescent(NDArray x, NDArray y, double alpha) {
		return gradientDescent(x, y, alpha, 10000, 1e-4);
	}
}