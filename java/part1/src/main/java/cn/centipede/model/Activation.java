package cn.centipede.model;

import cn.centipede.numpy.NDArray;

interface Activation {
	NDArray active(NDArray z);
	NDArray deactive(NDArray z);
}