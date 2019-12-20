package cn.centipede.model.ccn;

import cn.centipede.model.data.MNIST;
import cn.centipede.numpy.NDArray;

/**
 * Yes, I want to implement CCN
 * oops, a little complex for me
 * :)
 * ----------------------------
 * input->hidden->output
 * ----------------------------
 * @author simbaba
 * @version 0.0
 */
public class CCN {
    public NDArray train() {return null;}
    public NDArray fit() {return null;}

    public static void main(String[] args) {
        NDArray[] mnist = MNIST.numpy(false);
        NDArray row = mnist[0].row(1);
        row.dump();
    }
}