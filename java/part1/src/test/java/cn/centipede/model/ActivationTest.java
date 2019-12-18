package cn.centipede.model;

import org.junit.Test;

import cn.centipede.model.activation.Sigmoid;
import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;
import junit.framework.TestCase;

public class ActivationTest extends TestCase {

    /**
     * z=x1*w1+x2*w2+x3*w3
     * 
     *  x ====>  y
     *  ---------------
     *  0 0 1    0
     *  1 1 1    1
     *  1 0 1    1
     *  0 1 1    0
     *  --------------
     */
    @Test
    public void test_sigmoid() {
        int[][] dat_x = {{0,0,1}, {1,1,1}, {1,0,1}, {0,1,1}};
        NDArray x =  np.array(dat_x);

        int[][] dat_y = {{0,1,1,0}};
        NDArray y =  np.array(dat_y).T;

        np.random.seed(1);
        int[] w_dimens = {3, 1};
        NDArray w = np.random.rand(w_dimens).multiply(2).subtract(1);
        Activation sigmoid = new Sigmoid();

        for (int i = 0; i < 10000; i++) {
            NDArray z = np.dot(x, w);
            NDArray o = sigmoid.forward(z);
            NDArray delta = y.subtract(o).multiply(sigmoid.backward(o));

            w = w.add(np.dot(x.T, delta));
        }

        double[][] dat = {{9.6729}, {-0.20784}, {-4.62963}};
        NDArray expected = np.array(dat);

        assertTrue(expected.same(w, 0.1));
    }

    /**
     * https://www.bilibili.com/video/av37947862?p=38
     * [IN]   [Y]
     * 1 0 0  -1
     * 1 0 1   1
     * 1 1 0   1
     * 1 1 1  -1
     */
    @Test
    public void test_xor() {
        int[][] dat_X = {{1,0,0},{1,0,1},{1,1,0},{1,1,1}};
        NDArray X = np.array(dat_X);

        int[] dat_Y = {0,1,1,0};
        NDArray Y = np.array(dat_Y).V();

        NDArray V = np.random.rand(new int[]{3,4}).multiply(2).subtract(1);
        NDArray W = np.random.rand(new int[]{4,1}).multiply(2).subtract(1);

        double learn_rate = 0.11;
        Activation sigmoid = new Sigmoid();
        NDArray L1 = null, L2 = null;

        for (int i = 0; i < 20000; i++) {
            L1 = sigmoid.forward(np.dot(X, V));
            L2 = sigmoid.forward(np.dot(L1, W));

            NDArray L2_delta = Y.T.subtract(L2).multiply(sigmoid.backward(L2));
            NDArray L1_delta = L2_delta.dot(W.T).multiply(sigmoid.backward(L1));

            NDArray W_change = L1.T.dot(L2_delta).multiply(learn_rate);
            NDArray V_change = X.T.dot(L1_delta).multiply(learn_rate);

            W = W.add(W_change);
            V = V.add(V_change);

            // Error: np.mean(np.abs(Y.T-L2)) -> decrese to 0
            // System.out.println(np.mean(np.abs(Y.T.subtract(L2))));
        }

        double[] expected = {0,1,1,0};
        assertTrue(L2.same(np.array(expected).V().T, 0.1));
    }
}