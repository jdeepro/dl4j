package cn.centipede.model;

import cn.centipede.numpy.NDArray;

public interface Activation {
	NDArray active(NDArray z);
	NDArray deactive(NDArray z);
}