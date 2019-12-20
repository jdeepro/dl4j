package cn.centipede.model.ccn;

import cn.centipede.numpy.NDArray;

public class Conv {

    public Conv(NDArray kshape, int stride, int pad) {
        init(kshape, stride, pad);
    }

    public Conv(NDArray kshape) {
        init(kshape, 1, 0);
    }

    private void init(NDArray kshape, int stride, int pad) {
    }

    public NDArray forward(NDArray x) {
        return null;
    }

    public NDArray backward(NDArray x, NDArray delta, double learning_rate) {
        return null;
    }

    static NDArray img2col(NDArray x, int ksize, int stride) {
        return null;
    }
}