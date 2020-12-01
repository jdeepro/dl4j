package cn.centipede.numpy;

import junit.framework.TestCase;
import java.util.Arrays;
import org.junit.Test;

import cn.centipede.numpy.Numpy.np;

public class NDArrayTest extends TestCase {
    @Test
    public void test_create() {
        int[] dat = {1,2,3,4,5,6,7,8};
        NDArray a = new NDArray(dat, 2,4);
        assertTrue(a.toString(), Arrays.equals((int[])a.data(), (int[])dat));
    }

    @Test
    public void test_transpose() {
        NDArray a = Numpy.arange(36).reshape(4,3,3);
        int[] expected = {0, 9, 18, 27};
        int[][] range = {{0}, {0}};
        assertEquals(Numpy.array(expected), a.T().get(range));
        System.out.println(a.T());

        a = Numpy.arange(12).reshape(4,3);
        int[] expected2 = {1, 4, 7};
        int[][] range2 = {{1}, {0,-1}}; // {{1}, {0, 3}}
        assertEquals(Numpy.array(expected2), a.T().get(range2));
    }

    @Test
    public void test_reshape() {
        int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
        NDArray a = new NDArray(dat, 3,4);
        a = a.reshape(2,6);
        assertTrue(a.toString(), Arrays.equals(a.shape(), new int[]{2,6}));
    }

    @Test
    public void test_to_string() {
        int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
        NDArray a = new NDArray(dat, 3,4);
        assertEquals("(3, 4)", a.dimens());
        a = a.reshape(2,6);
        assertEquals("(2, 6)", a.dimens());
    }

    @Test
    public void test_reciprocal() {
        int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
        NDArray a = new NDArray(dat, 3,4);
        a = a.reciprocal(2);
        assertEquals(a.get(0,1), np.array(1.0));
    }

    @Test
    public void test_compare() {
        int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
        NDArray a = new NDArray(dat, 3,4);
        a = a.lessThan(5);
        // a.get(0,1).dump();
        assertEquals(a.get(0,1), np.array(true));
    }

    @Test
    public void test_operator_set() {
        int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
        NDArray a = new NDArray(dat, 3,4);
        a.operator_set(2, new int[][]{{0,2}});

        int[] expected = {2,2,2,2,2,2,2,2};
        assertEquals(a.get(new int[][]{{0,2}}), np.array(expected, 2,4));
    }

    @Test
    public void test_operator_pow() {
        int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
        NDArray a = new NDArray(dat, 3,4);
        NDArray b = a.operator_power(2);
        NDArray actual = b.get(new int[][]{{1},{1}});
        assertEquals(actual, np.array(36));
    }

    @Test
    public void test_operator_multiply() {
        int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
        NDArray a = new NDArray(dat, 3,4);
        NDArray b=a.operator_multiply(2);
        b.dump();
    }

    @Test
    public void test_operator_subtract_after() {
        int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
        NDArray a = new NDArray(dat, 3,4);
        a=a.subtract_after(1);
        a.dump();
    }

    @Test
    public void test_operator_reciprocal() {
        int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
        NDArray a = new NDArray(dat, 3,4);
        a=a.operator_reciprocal(2);
        a.dump();
    }

    @Test
    public void test_rows_vs_get() {
        NDArray a = np.arange(24).reshape(2,2,2,3);
        a.row(0).dump();
        a.get(0).dump();
    }
}