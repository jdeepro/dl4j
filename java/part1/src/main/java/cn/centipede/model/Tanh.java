package cn.centipede.model;

import cn.centipede.numpy.NDArray;

/**
 *  y = (e^x - e^-x)/(e^x + e^-x)
 *  dy/dx = 1 - y2 
 */
public class Tanh implements Activation{
	@Override
	public NDArray active(NDArray z) {
		return null;
	}

	@Override
	public NDArray deactive(NDArray z) {
		return null;
	}
}