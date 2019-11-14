package cn.centipede.model.activation;

import cn.centipede.model.Activation;
import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy;

/**
 * y = 1/(1 + e^-x)  = (1 + e^-x)-1
 * y/dx = y(1-y)
 */
public class Sigmoid implements Activation{
	@Override
	public NDArray active(NDArray z) {
		return Numpy.exp(z.negative()).add(1).reciprocal();
	}

	@Override
	public NDArray deactive(NDArray z) {
		return z.multiply(z.negative().add(1));
	}
}