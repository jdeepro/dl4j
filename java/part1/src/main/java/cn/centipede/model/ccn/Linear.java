package cn.centipede.model.ccn;

import cn.centipede.numpy.NDArray;

public class Linear {

    public Linear(int inChannel, int outChannel) {
        init(inChannel, outChannel);
    }

    private void init(int inChannel, int outChannel) {
    }

    public NDArray forward(NDArray x) {
        return null;
    }

    public NDArray backward(NDArray x, NDArray delta, double learning_rate) {
        return null;
    }
}