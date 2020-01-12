package cn.centipede.model;

import org.junit.Test;

import cn.centipede.model.data.MNIST;
import cn.centipede.numpy.NDArray;
import junit.framework.TestCase;

public class MnistTest extends TestCase {

    @Test
    public void test_load_mnist() {
        NDArray[] mnist = MNIST.numpy(false); // train=false
        assertEquals("2", mnist[1].row(1).toString()); // lable

        NDArray test = mnist[0].reshape(10000, 28, 28, 1).row(1);
        assertEquals("(28, 28, 1)", test.dimens());
    }

}