package cn.centipede.model;

import cn.centipede.numpy.NDArray;

/**
 * y = 1/(1 + e^-x)  = (1 + e^-x)-1
 * y/dx = y(1-y)
 */
public class Sigmoid implements Activation{
	@Override
	public NDArray active(NDArray z) {
		return null;
	}
	
	@Override
	public NDArray deactive(NDArray z, NDArray delta) {
		return null;
	}
}