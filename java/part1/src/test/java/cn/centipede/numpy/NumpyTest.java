package cn.centipede.numpy;

import org.junit.Test;
import junit.framework.TestCase;

public class NumpyTest extends TestCase {
	@Test
	public void test_arange() {
		NDArray a = Numpy.arange(12).reshape(3,4);
		System.out.println(a);
	}
	
	@Test
	public void test_random() {
		NDArray a = Numpy.random.rand(3,4);
		System.out.println(a);
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
		System.out.println(c);
	}
}