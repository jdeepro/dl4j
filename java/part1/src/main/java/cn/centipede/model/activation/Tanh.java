package cn.centipede.model.activation;

import cn.centipede.model.Activation;
import cn.centipede.numpy.NDArray;

/**
 *  y = (e^x - e^-x)/(e^x + e^-x)
 *  dy/dx = 1 - y2 
 */
public class Tanh implements Activation{
    @Override
    public NDArray forward(NDArray z) {
        return null;
    }

    @Override
    public NDArray backward(NDArray z) {
        return null;
    }
}