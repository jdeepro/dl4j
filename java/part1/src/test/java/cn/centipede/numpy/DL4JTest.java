package cn.centipede.numpy;

import org.junit.Test;

import cn.centipede.model.GradientDescent;
import cn.centipede.model.gradient.LinearImp;
import cn.centipede.numpy.Numpy.np;
import junit.framework.TestCase;

public class DL4JTest extends TestCase {

    @Test
    public void test_dl4j_T() {
        NDArray a = Numpy.array(new int[]{3, 4});
        assertEquals("(2,)", a.shape());
        assertEquals("(2,)", a.T.shape());
    }

    @Test
    public void test_dl4j_operation() {
        double[][] expected = {{0.0, 1.0, 2.0}, {3.0, 4.0, 5.0}};

        NDArray a = Numpy.arange(6).reshape(2, 3);
        NDArray b = Numpy.multiply(a, 2);
        b = b.divide(2);
        assertEquals(Numpy.array(expected), b);

        double[][] expected2 = {{10.0, 11.0, 12.0}, {13.0, 14.0, 15.0}};
        assertEquals(Numpy.array(expected2), Numpy.array(expected).add(10));

        double[][] expected3 = {{10.0, 11.0, 12.0}, {103.0, 104.0, 105.0}};
        int[][] dat = {{10, 10, 10}, {100, 100, 100}};
        assertEquals(Numpy.array(expected3), Numpy.array(expected).add(dat));

        double[][] expected4 = {{10.0, 21.0, 32.0}, {13.0, 24.0, 35.0}};
        int[][] dat2 = {{10, 20, 30}};
        assertEquals(Numpy.array(expected4), Numpy.array(expected).add(dat2));
    }

    @Test
    public void test_dl4j_operation_polynomial() {
        double[][] input = {{0.0, 1.0, 2.0}, {3.0, 4.0, 5.0}};
        int[][] dat = {{10, 20, 30}};
        NDArray ret = Numpy.array(input).multiply(dat).divide(5);
        double[][] expected = {{0.0, 4.0, 12.0}, {6.0, 16.0, 30.0}};
        assertEquals(Numpy.array(expected), ret);
    }

    /**
     * y = 3x1+4x2
     */
    @Test
    public void test_dl4j_gen_data() {
        NDArray x1 = Numpy.linspace(0, 9, 10).V();
        NDArray x2 = Numpy.linspace(4, 13, 10).V();
        NDArray x = Numpy.concatenate(x1, x2);
        System.out.println(x.T);

        NDArray v = Numpy.array(new int[]{3, 4});
        NDArray y = Numpy.dot(x.T, v.T);
        double[] expected1 = {16.0, 23.0, 30.0, 37.0, 44.0, 51.0, 58.0, 65.0, 72.0, 79.0};
        assertEquals(Numpy.array(expected1), y);

        v = Numpy.array(new int[]{3, 4}).V();
        y = Numpy.dot(x.T, v.T);
        double[][] expected2 = {{16.0}, {23.0}, {30.0}, {37.0}, {44.0}, {51.0}, {58.0}, {65.0}, {72.0}, {79.0}};
        assertEquals(Numpy.array(expected2), y);
    }

    @Test
    public void test_sigmoid() {
        NDArray a = Numpy.arange(12).reshape(3,4);
        NDArray b = Numpy.exp(a);
        System.out.println(b);
    }

    /**
     * Linear Gradient
     * y = 2 * x1 + 3 * x2 + b
     */
    @Test
    public void test_linear_sgd() {
        GradientDescent gd = LinearImp::SGD;
        int N = 20;

        NDArray x1 = np.random.uniform(0, 5, N).reshape(N,1);
        NDArray x2 = np.random.uniform(0, 5, N).reshape(N,1);
        NDArray ones = np.ones(new int[]{N, 1});

        NDArray y = x1.multiply(2).add(x2.multiply(3)).add(np.random.uniform(0, 0.1, N).reshape(N,1));
        //NDArray y = x.multiply(3).reshape(N,1);
        NDArray x = np.hstack(x1, x2);
        x = np.hstack(x, ones);

        NDArray ret = gd.fit(x, y, 0.01, 100000, 1e-3);
        System.out.println("#### test_linear_sgd: y = 2 * x1 + 3 * x2 + b ");
        System.out.println(ret);
    }

    @Test
    public void test_linear_bgd() {
        GradientDescent gd = LinearImp::BGD;
        int N = 20;

        NDArray x1 = np.random.uniform(0, 5, N).reshape(N,1);
        NDArray x2 = np.random.uniform(0, 5, N).reshape(N,1);
        NDArray ones = np.ones(new int[]{N, 1});

        NDArray y = x1.multiply(2).add(x2.multiply(3)).add(np.random.uniform(0, 0.1, N).reshape(N,1));
        //NDArray y = x.multiply(3).reshape(N,1);
        NDArray x = np.hstack(x1, x2);
        x = np.hstack(x, ones);

        NDArray ret = gd.fit(x, y, 0.01, 10000, 1e-3);
        System.out.println("#### test_linear_bgd: y = 2 * x1 + 3 * x2 + b ");
        System.out.println(ret);
    }

    @Test
    public void test_linear_mbgd() {
        GradientDescent gd = LinearImp::MBGD;
        int N = 20;

        NDArray x1 = np.random.uniform(0, 5, N).reshape(N,1);
        NDArray x2 = np.random.uniform(0, 5, N).reshape(N,1);
        NDArray ones = np.ones(new int[]{N, 1});

        NDArray y = x1.multiply(2).add(x2.multiply(3)).add(np.random.uniform(0, 0.1, N).reshape(N,1));
        //NDArray y = x.multiply(3).reshape(N,1);
        NDArray x = np.hstack(x1, x2);
        x = np.hstack(x, ones);

        NDArray ret = gd.fit(x, y, 0.01, 10000, 1e-3);
        System.out.println("#### test_linear_mbgd: y = 2 * x1 + 3 * x2 + b ");
        System.out.println(ret);
    }
}