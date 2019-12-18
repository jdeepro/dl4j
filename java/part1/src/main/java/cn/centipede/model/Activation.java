package cn.centipede.model;

import cn.centipede.numpy.NDArray;

public interface Activation {
    NDArray forward(NDArray z);
    NDArray backward(NDArray z);
}