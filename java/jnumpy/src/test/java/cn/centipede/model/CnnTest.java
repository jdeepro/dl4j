package cn.centipede.model;

import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import cn.centipede.model.cnn.CNN;
import cn.centipede.model.data.MNIST;
import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;
import junit.framework.TestCase;

public class CnnTest extends TestCase {

    @Test
    public void test_recongize_mnist() {
        NDArray[] mnist = MNIST.numpy(false); // train=false

        CNN cnn = new CNN();
        URL npzURL = CNN.class.getResource("/mnist.npz");
        cnn.loadNpz(npzURL);

        np.random.seed(System.currentTimeMillis());
        int rand = np.random.randint(10000);

        NDArray label = mnist[1].row(rand);
        NDArray X = mnist[0].reshape(10000, 28, 28, 1).get(rand);

        int actual = cnn.predict(X);

        X.reshape(28,28).dump();
        System.out.printf("rand=%d, label=%d, predict=%d\n", rand, label.asInt(), actual);
        assertEquals(label.asInt(), actual);
    }

}