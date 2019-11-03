package cn.centipede.numpy;

import org.junit.Test;
import junit.framework.TestCase;

public class DL4JTest extends TestCase {
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

		NDArray v = Numpy.array(new int[]{3, 4}).V();
		NDArray y = Numpy.dot(x.T, v.T);
		System.out.println(y);
	}
}