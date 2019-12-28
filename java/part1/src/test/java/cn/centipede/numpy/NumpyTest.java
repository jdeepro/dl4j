package cn.centipede.numpy;

import java.util.Arrays;

import org.junit.Test;

import cn.centipede.numpy.Numpy.np;
import junit.framework.TestCase;
import static cn.centipede.numpy.Numpy.ALL;

public class NumpyTest extends TestCase {

    @Test
    public void test_api_at() {
        NDArray a = np.arange(12).reshape(3,4);
        int[] expected = {4,5,6,7};
        assertEquals(np.array(expected), a.index(1));
        assertEquals(np.array(6), a.index(1,2));
    }

    @Test
    public void test_api_set() {
        NDArray a = np.arange(24).reshape(2, 3, 4);
        a.set(new int[]{100,200,300, 400}, 1, 1);
        assertEquals(np.array(new int[]{100,200,300, 400}), a.index(1,1));
    }

    @Test
    public void test_api_asInt() {
        NDArray a = np.arange(12).reshape(3, 4);
        NDArray b = a.index(0, 3);
        assertEquals(3, b.asInt());

        NDArray c = np.array(4);
        assertEquals(4, c.asInt());
    }

    @Test
    public void test_api_row() {
        NDArray a = np.array(new int[][]{{1,2,3}});
        assertEquals("(3,)", a.row(0).shape());

        a = np.arange(12).reshape(3,4);
        int[] expected = {4,5,6,7};
        assertEquals(np.array(expected), a.row(1));

        a = np.arange(12).reshape(3,4);
        NDArray b = a.clone();
        a.reshape(np.newaxis, ALL);
        assertEquals(b, a.row(0));
    }

    @Test
    public void test_api_slice() {
        NDArray a = np.arange(36).reshape(4,3,3);
        int[] expected = {8, 17};
        int[][] range = {{0,2}, {2}, {-1+3}}; // not support negative, but slice can
        assertEquals(np.array(expected), a.slice(range));
    }

    @Test
    public void test_shape() {
        int[] dat = new int[]{2,4};
        NDArray a = np.array(dat);
        assertEquals("(2,)", a.shape());
    }

    @Test
    public void test_api_maximum() {
        NDArray a = np.arange(12).reshape(3,4);
        assertEquals(np.array(new int[]{5,5,5,5}), np.maximum(a, 5).row(0));
    }

    @Test
    public void test_api_checkset() {
        NDArray a = np.arange(6).reshape(3,2);
        NDArray b = np.array(new int[][]{{3,43},{43,9},{1,8}});
        NDArray actual = np.checkset(a, b, (int n)->n<10, 2);
        assertEquals(np.array(new int[][]{{2,1},{2,2},{2,2}}), actual);
    }

    @Test
    public void test_api_repeat() {
        NDArray a = np.arange(6).reshape(3,2);
        np.repeat(a, 3).dump();

        a = np.arange(24).reshape(2,2,3,2);
        NDArray expected = np.repeat(a, new int[]{0,2}, 1).index(0,0);
        assertEquals(np.array(new int[][]{{6,7},{8,9},{10,11}}), expected);
    }

    @Test
    public void test_api_pad() {
        int[] pad = new int[]{1,2};
        NDArray a = np.array(new int[]{1,2,3,4});
        NDArray b = np.array(new int[]{0,1,2,3,4,0,0});

        pad = new int[]{1,2};
        a = np.array(new int[][]{{1,2,3,4}});
        b = np.array(new int[][]{{0,0,0,0,0,0,0},{0,1,2,3,4,0,0},{0,0,0,0,0,0,0},{0,0,0,0,0,0,0}});
        assertEquals(b, np.pad(a, pad));

        a = np.arange(16).reshape(2,2,2,2);
        assertEquals("(6, 6, 6, 6)", np.pad(a, new int[]{2}).shape());

        NDArray arr3D = np.array(new int[][][]{{{1, 1, 2, 2, 3, 4}, {1, 1, 2, 2, 3, 4}, {1, 1, 2, 2, 3, 4}},
        {{0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5}},
        {{1, 1, 2, 2, 3, 4}, {1, 1, 2, 2, 3, 4}, {1, 1, 2, 2, 3, 4}}});

        np.pad(arr3D, new int[][]{{0, 0}, {1, 1}, {2, 2}}).dump();;
    }

    @Test
    public void test_next_dimen() {
        int[] dimens = {2,4,3};
        int[] iter = new int[3];

        int index = 5; // 000,001,002,010,011,012,020
        while (index-->0) {
            np.next_dimen(dimens, iter);
        }
        String actual = Arrays.toString(iter);
        assertEquals("[0, 1, 2]", actual);
    }

    @Test
    public void test_api_swapaxes() {
        NDArray a = np.arange(24).reshape(1,2,3,4);
        NDArray actual = np.swapaxes(a, 1, 3);
        assertEquals(np.array(new int[]{0, 12}), actual.index(0,0,0));
    }

    @Test
    public void test_api_flipud() {
        NDArray a = np.arange(12).reshape(3,4);
        assertEquals(np.array(new int[]{0,1,2,3}), np.flipud(a).row(2));
    }

    @Test
    public void test_api_rot90() {
        NDArray a = np.arange(6).reshape(6,1);
        np.rot90(a).dump();
        assertEquals(np.arange(6).reshape(1,6), np.rot90(a));

        int[][][] expect = {{{3,4,5}},{{0,1,2}}};
        a = np.arange(6).reshape(1,2,3);
        assertEquals(np.array(expect), np.rot90(a));

        a = np.arange(12).reshape(2,2,3);
        NDArray actual = np.rot90(a, 1, new int[]{0,2});
        int[][][] expected = {{{2,8},{5,11}},{{1,7},{4,10}},{{0,6},{3,9}}};
        assertEquals(np.array(expected), actual);
    }

    @Test
    public void test_api_concatenate() {
        NDArray a = np.array(new int[]{1,2,3});
        NDArray b = np.array(new int[]{2,3,4});
        NDArray e = np.array(new int[]{1, 2, 3, 2, 3, 4});
        assertEquals(e, np.concatenate(a, b, 0));

        a = np.array(new int[][]{{1,2,3}});
        b = np.array(new int[][]{{2,3,4}});
        e = np.array(new int[][]{{1, 2, 3}, {2, 3, 4}});
        assertEquals(e, np.concatenate(a, b, 0));

        // a = a[np.newaxis,:], b = b[np.newaxis,:]
        a = np.array(new int[][]{{1,2,3}});
        b = np.array(new int[][]{{2,3,4}});
        assertEquals(np.array(new int[][]{{1, 2, 3, 2, 3, 4}}), np.concatenate(a, b, 1));

        a = np.arange(6).reshape(3,2);
        b = np.arange(4).reshape(2,2);
        int[][] c = {{0, 1}, {2, 3}, {4, 5}, {0, 1}, {2, 3}};
        assertEquals(np.array(c), np.concatenate(a, b));

        a = np.arange(6).reshape(2,3);
        int[][] d = {{0, 1, 2, 0, 1}, {3, 4, 5, 2, 3}};
        int axis = 1;
        assertEquals(np.array(d), np.concatenate(a, b, axis));
    }

    @Test
    public void test_array_newaxis() {
        NDArray a = np.array(new int[]{3, 4, 5});
        NDArray b = np.array(new int[][]{{3, 4, 5}});
        assertEquals(b, a.reshape(np.newaxis, ALL));

        a = np.array(new int[]{3, 4, 5});
        b = np.array(new int[][]{{3}, {4}, {5}});
        assertEquals(b, a.reshape(ALL, np.newaxis));
    }

    @Test
    public void test_api_vstack() {
        NDArray a = np.arange(8).reshape(2,4);
        NDArray actual = np.vstack(a, np.array(new int[]{ 8,  9,  10, 11}));
        NDArray expect = np.arange(12).reshape(3,4);
        assertEquals(expect, actual);

        NDArray r = np.array(new double[]{0.2, 0.4, 0.6, 0.9, 0.3});
        NDArray g = np.array(new double[]{0.4, 0.1, 0.5, 0.7, 0.8});
        NDArray e = np.array(new double[]{
            0.2, 0.4, 0.6, 0.9, 0.3, 0.4, 0.1, 0.5, 0.7, 0.8});

        e = np.array(new double[][]{
            {0.2, 0.4, 0.6, 0.9, 0.3}, {0.4, 0.1, 0.5, 0.7, 0.8}});
        assertEquals(e, np.vstack(r, g));
    }

    @Test
    public void test_api_stack() {
        double[] R={0.2, 0.4, 0.6, 0.9, 0.3};
        double[] G={0.4, 0.1, 0.5, 0.7, 0.8};
        NDArray r = np.array(R);
        NDArray g = np.array(G);
        NDArray e = np.array(new double[]{
            0.2, 0.4, 0.6, 0.9, 0.3, 0.4, 0.1, 0.5, 0.7, 0.8});
        assertEquals(e, np.hstack(r, g));

        e = np.array(new double[][]{
            {0.2, 0.4}, {0.4, 0.1}, {0.6, 0.5}, {0.9, 0.7}, {0.3, 0.8}});
        r.reshape(ALL, np.newaxis);
        g.reshape(ALL, np.newaxis);
        assertEquals(e, np.hstack(r.T, g.T));

        double[][] D={{0.2, 0.4, 0.7}, {0.4, 0.1, 0.8}, {0.6, 0.5, 0.2}, {0.9, 0.7, 0.3}, {0.3, 0.8, 0.4}};
        double[][] E={{0.2, 0.4, 0.6, 0.9, 0.3}, {0.4, 0.1, 0.5, 0.7, 0.8}, {0.7, 0.8, 0.2, 0.3, 0.4}};

        r = np.array(R);
        g = np.array(G);
        NDArray b = np.array(new double[]{0.7, 0.8, 0.2, 0.3, 0.4});
        NDArray[] c = {r, g, b};
        assertEquals(np.array(E), np.stack(c, 0));

        r = np.array(R);
        g = np.array(G);
        b = np.array(new double[]{0.7, 0.8, 0.2, 0.3, 0.4});
        c = new NDArray[]{r, g, b};
        assertEquals(np.array(D), np.stack(c, 1));
    }

    @Test
    public void test_api_sum() {
        NDArray a = np.arange(6).reshape(3,2);
        assertEquals(15, np.sumInt(a));

        a = np.arange(9).reshape(3,3);
        assertEquals(36, np.sumInt(a));

        a = np.arange(12).reshape(2,3,2);
        int[][] expect = {{1, 5, 9}, {13, 17, 21}};
        assertEquals(np.array(expect), np.sum(a, 2));

        assertEquals(np.array(new int[]{15, 51}), np.sum(a, new int[]{1,2}));
    }

    @Test
    public void test_api_max() {
        NDArray a = np.arange(6).reshape(2,3);
        assertEquals(np.array(new int[]{3,4,5}), np.max(a, 0));
        assertEquals(np.array(new int[]{2,5}), np.max(a, 1));

        assertEquals(np.array(new int[]{1,1,1}), np.argmax(a, 0));
        assertEquals(np.array(new int[]{2,2}), np.argmax(a, 1));

        a = np.random.rand(2,3);
        np.max(a, 0).dump();
    }

    @Test
    public void test_slice() {
        NDArray a = np.arange(12).reshape(3,4);
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
        NDArray a = np.arange(12).reshape(3,4);
        NDArray b = np.add(a, 2);

        assertEquals("array([[2 , 3 , 4 , 5 ]\n" +
                    "       [6 , 7 , 8 , 9 ]\n" +
                    "       [10, 11, 12, 13]])\n", b.toString());

        b = np.subtract(b, 2);
        assertEquals("array([[0 , 1 , 2 , 3 ]\n" +
                    "       [4 , 5 , 6 , 7 ]\n" +
                    "       [8 , 9 , 10, 11]])\n", b.toString());

        int[] dat = {10, 20, 30, 40};
        NDArray c = np.add(a, dat);

        assertEquals("array([[10, 21, 32, 43]\n" +
                    "       [14, 25, 36, 47]\n" +
                    "       [18, 29, 40, 51]])\n", c.toString());
    }

    @Test
    public void test_arange() {
        NDArray a = np.arange(12).reshape(3,4);
        assertEquals("(3, 4)", a.shape());
    }

    @Test
    public void test_random() {
        NDArray a = np.random.rand(4,4);
        System.out.println(a);
        assertEquals("(4, 4)", a.shape());
    }

    @Test
    public void test_ndarray_add() {
        NDArray a = np.ones(3,4);
        NDArray b = np.zeros(3,4);
        NDArray c = np.add(a, b);
        System.out.println(c);
    }

    @Test
    public void test_ndarray_abs() {
        int[][] dat = {{1,-2,3},{-1,-3,4},{-4,-3,6}};
        NDArray a = np.array(dat);

        int[][] expected = {{1,2,3},{1,3,4},{4,3,6}};
        assertEquals(np.array(expected), np.abs(a));
    }

    @Test
    public void test_ndarray_mean() {
        NDArray a = np.arange(12).reshape(3, 4);
        assertEquals(5.5, np.mean(a));
    }

    @Test
    public void test_ndarray_dot() {
        NDArray a = np.arange(12).reshape(3,4);
        NDArray b = np.arange(16).reshape(4,4);
        NDArray c = np.dot(a, b);

        assertEquals("array([[56 , 62 , 68 , 74 ]\n"+
                    "       [152, 174, 196, 218]\n"+
                    "       [248, 286, 324, 362]])\n", c.toString());

        NDArray d = np.arange(12).reshape(3,4);
        NDArray e = np.dot(d, 2);
        assertEquals(e, np.add(d, d));

        d = np.arange(12).reshape(3,4);
        e = np.array(2);
        assertEquals(np.dot(d, 2), np.dot(d, e));

        d = np.arange(12).reshape(3, 4);
        e = np.array(new int[]{1, 2, 3, 4});
        assertEquals(np.array(new int[]{20, 60, 100}), np.dot(d, e));
    }

    @Test
    public void test_ndarray_choice() {
        NDArray a = np.arange(24).reshape(4,6);
        int[] choice = np.random.choice(4, 4);
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
        NDArray a = np.arange(24).reshape(2,3,4);
        int[][][] array = (int[][][])np.getArray(a);

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