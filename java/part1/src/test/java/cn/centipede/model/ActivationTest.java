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

		// double[][] dat_w = {{-0.16595599},{0.44064899}, {-0.99977125}};
		Numpy.random.seed(1);
		NDArray w = Numpy.random.rand(new int[]{3, 1}).multiply(2).subtract(1);
		// NDArray w = Numpy.array(dat_w);//.multiply(2).subtract(1);

		for (int i = 0; i < 10000; i++) {
			NDArray z = Numpy.dot(x, w).multiply(-1);
			NDArray o = Numpy.exp(z).add(1).reciprocal();
			NDArray e = y.subtract(o);
			NDArray slope = o.multiply(o.multiply(-1).add(1));

			NDArray delta = e.multiply(slope);
			
			w = w.add(Numpy.dot(x.T, delta));
		}

		double[][] dat = {{9.6729}, {-0.20784}, {-4.62963}};
		NDArray expected = Numpy.array(dat);
		System.out.println(w);
		assertTrue(expected.same(w, 0.1));
	}
}