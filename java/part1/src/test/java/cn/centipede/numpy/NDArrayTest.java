package cn.centipede.numpy;

import junit.framework.TestCase;
import java.util.Arrays;
import org.junit.Test;

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
        assertTrue(a.toString(), Arrays.equals(a.dimens(), new int[]{2,6}));
    }

    @Test
    public void test_to_string() {
        int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
        NDArray a = new NDArray(dat, 3,4);
        assertEquals("(3, 4)", a.shape());
        a = a.reshape(2,6);
        assertEquals("(2, 6)", a.shape());
    }
}