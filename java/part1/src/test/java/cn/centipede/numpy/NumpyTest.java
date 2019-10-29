package cn.centipede.numpy;

import java.util.Arrays;

import org.junit.Test;
import junit.framework.TestCase;
import static cn.centipede.numpy.Numpy.ALL;

public class NumpyTest extends TestCase {
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

		b = Numpy.sub(b, 2);
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
	public void test_ndarray_dot() {
		NDArray a = Numpy.arange(12).reshape(3,4);
		NDArray b = Numpy.arange(16).reshape(4,4);
		NDArray c = Numpy.dot(a, b);

		assertEquals(
			"array([[56 , 62 , 68 , 74 ]\n"+
			"       [152, 174, 196, 218]\n"+
			"       [248, 286, 324, 362]])\n", c.toString());

		NDArray d = Numpy.arange(12).reshape(3,4);
		NDArray e = Numpy.dot(d, 2);
		assertEquals(e, Numpy.add(d, d));
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
}