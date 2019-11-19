package cn.centipede.numpy;

import java.util.Arrays;

import org.junit.Test;

import cn.centipede.numpy.Numpy.np;
import junit.framework.TestCase;
import static cn.centipede.numpy.Numpy.ALL;

public class NumpyTest extends TestCase {

    @Test
    public void test_api_at() {
        NDArray a = Numpy.arange(12).reshape(3,4);
        int[] expected = {4,5,6,7};
        assertEquals(Numpy.array(expected), a.index(1));
        assertEquals(Numpy.array(6), a.index(1,2));
    }

    @Test
    public void test_api_atRange() {
        NDArray a = Numpy.arange(36).reshape(4,3,3);
        int[] expected = {8, 17};
        int[][] range = {{0,2}, {2}, {-1+3}}; // not support negative, but slice can
        assertEquals(Numpy.array(expected), a.slice(range));
    }

    @Test
    public void test_shape() {
        int[] dat = new int[]{2,4};
        NDArray a = Numpy.array(dat);
        assertEquals("(2,)", a.shape());
    }

    @Test
    public void test_concatenate() {
        NDArray a = Numpy.arange(6).reshape(3,2);
        NDArray b = Numpy.arange(4).reshape(2,2);

        int[][] c = {
            {0, 1},
            {2, 3},
            {4, 5},
            {0, 1}, 
            {2, 3}
        };
        assertEquals(Numpy.array(c), Numpy.concatenate(a, b));

        a = Numpy.arange(6).reshape(2,3);
        int[][] d = {
            {0, 1, 2, 0, 1},
            {3, 4, 5, 2, 3}
        };
        int axis = 1;
        assertEquals(Numpy.array(d), Numpy.concatenate(a, b, axis));
    }

    @Test
    public void test_api_sum() {
        NDArray a = Numpy.arange(6).reshape(3,2);
        assertEquals(15, Numpy.sumInt(a));

        a = Numpy.arange(9).reshape(3,3);
        assertEquals(36, Numpy.sumInt(a));
    }

    @Test
    public void test_slice() {
        NDArray a = Numpy.arange(12).reshape(3,4);
        int[][] range1 = {{1, 3}, {-1}};
        NDArray b = a.slice(range1);
        assertEquals("array([7, 11])\n", b.toString());

        int[][] range2 = {{ALL}, {-2}};
        b = a.slice(range2);
        assertEquals("array([2, 6, 10])\n", b.toString());

        int[][] range3 = {{-2, -1}, {-2}};
        b = a.slice(range3);
        assertEquals("array([6])\n", b.toString());

        int[][] range4 = {{-1}, {-2}};
        b = a.slice(range4);
        assertEquals("10", b.toString());
    }

    @Test
    public void test_broadcast() {
        NDArray a = Numpy.arange(12).reshape(3,4);
        NDArray b = Numpy.add(a, 2);

        assertEquals("array([[2 , 3 , 4 , 5 ]\n" +
                    "       [6 , 7 , 8 , 9 ]\n" +
                    "       [10, 11, 12, 13]])\n", b.toString());

        b = Numpy.subtract(b, 2);
        assertEquals("array([[0 , 1 , 2 , 3 ]\n" +
                    "       [4 , 5 , 6 , 7 ]\n" +
                    "       [8 , 9 , 10, 11]])\n", b.toString());

        int[] dat = {10, 20, 30, 40};
        NDArray c = Numpy.add(a, dat);

        assertEquals("array([[10, 21, 32, 43]\n" +
                    "       [14, 25, 36, 47]\n" +
                    "       [18, 29, 40, 51]])\n", c.toString());
    }

    @Test
    public void test_arange() {
        NDArray a = Numpy.arange(12).reshape(3,4);
        assertEquals("(3, 4)", a.shape());
    }

    @Test
    public void test_random() {
        NDArray a = Numpy.random.rand(4,4);
        System.out.println(a);
        assertEquals("(4, 4)", a.shape());
    }

    @Test
    public void test_ndarray_add() {
        NDArray a = Numpy.ones(3,4);
        NDArray b = Numpy.zeros(3,4);
        NDArray c = Numpy.add(a, b);
        System.out.println(c);
    }

    @Test
    public void test_ndarray_abs() {
        int[][] dat = {{1,-2,3},{-1,-3,4},{-4,-3,6}};
        NDArray a = Numpy.array(dat);

        int[][] expected = {{1,2,3},{1,3,4},{4,3,6}};
        assertEquals(Numpy.array(expected), Numpy.abs(a));
    }

    @Test
    public void test_ndarray_mean() {
        NDArray a = Numpy.arange(12).reshape(3, 4);
        assertEquals(5.5, Numpy.mean(a));
    }

    @Test
    public void test_ndarray_dot() {
        NDArray a = Numpy.arange(12).reshape(3,4);
        NDArray b = Numpy.arange(16).reshape(4,4);
        NDArray c = Numpy.dot(a, b);

        assertEquals("array([[56 , 62 , 68 , 74 ]\n"+
                    "       [152, 174, 196, 218]\n"+
                    "       [248, 286, 324, 362]])\n", c.toString());

        NDArray d = Numpy.arange(12).reshape(3,4);
        NDArray e = Numpy.dot(d, 2);
        assertEquals(e, Numpy.add(d, d));

        d = Numpy.arange(12).reshape(3,4);
        e = Numpy.array(2);
        assertEquals(Numpy.dot(d, 2), Numpy.dot(d, e));

        d = Numpy.arange(12).reshape(3, 4);
        e = Numpy.array(new int[]{1, 2, 3, 4});
        assertEquals(Numpy.array(new int[]{20, 60, 100}), Numpy.dot(d, e));
    }

    @Test
    public void test_ndarray_choice() {
        NDArray a = Numpy.arange(24).reshape(4,6);
        int[] choice = Numpy.random.choice(4, 4);
        System.out.println(a.rows(choice));
    }

    @Test
    public void test_ndarray_shuffle() {
        NDArray a = np.arange(24);
        NDArray b = a.clone();

        int[] input = (int[])np.getArray(a);
        int[] choice = np.random.shuffle(input);

        int[] notExpected = (int[])np.getArray(b);
        assertFalse(Arrays.equals(notExpected, choice));
    }

    @Test
    public void test_struct() {
        NDArray a = Numpy.arange(24).reshape(2,3,4);
        int[][][] array = (int[][][])Numpy.getArray(a);

        int[][][] real = new int[][][]{
            {{0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}}, 
            {{0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}}
        };

        assertEquals("3 dimens int[][][] array", Arrays.deepToString(array), Arrays.deepToString(real));
    }

    @Test
    public void test_read_csv() {
        NDArray boston = np.loadtxt(getClass().getResource("/boston.csv").getPath(), ",");
        double[][] dat = {{4.98, 24.0}, {9.14, 21.6}, {4.03, 34.7}};
        NDArray expected = np.array(dat);
        NDArray actual = boston.slice(new int[][]{{0,3}, {-2, ALL}});
        assertEquals(expected, actual);
    }
}