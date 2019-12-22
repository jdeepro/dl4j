package cn.centipede.model.cnn;

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
public class CNN {
    public NDArray train() {return null;}
    public NDArray fit() {return null;}

    public static void main(String[] args) {
        NDArray[] mnist = MNIST.numpy(false); //train=false
        mnist[1].row(1).dump(); // lable

        NDArray test = mnist[0].reshape(10000, 28, 28, 1);
        //System.out.println(test.shape());
        //test.index(1).reshape(28,28).dump();

        NDArray img2col = Conv.img2col(test.index(1), 5, 1);
        System.out.println(img2col.shape());
    }
}