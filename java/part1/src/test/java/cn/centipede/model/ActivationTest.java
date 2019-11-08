package cn.centipede.model;

import org.junit.Test;

import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy;
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
		NDArray x =  Numpy.array(dat_x);

		int[][] dat_y = {{0,1,1,0}};
		NDArray y =  Numpy.array(dat_y).T;

		Numpy.random.seed(1);
		int[] w_dimens = {3, 1};
		NDArray w = Numpy.random.rand(w_dimens).multiply(2).subtract(1);
		Activation sigmoid = new Sigmoid();

		for (int i = 0; i < 10000; i++) {
			NDArray z = Numpy.dot(x, w);
			NDArray o = sigmoid.active(z);
			NDArray delta = y.subtract(o).multiply(sigmoid.deactive(o));

			w = w.add(Numpy.dot(x.T, delta));
		}

		double[][] dat = {{9.6729}, {-0.20784}, {-4.62963}};
		NDArray expected = Numpy.array(dat);

		assertTrue(expected.same(w, 0.1));
	}
}