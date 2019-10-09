package cn.centipede.numpy;

import junit.framework.TestCase;
import java.util.Arrays;
import org.junit.Test;

public class NDArrayTest extends TestCase {
	@Test
	public void test_create() {
			int[] dat = {1,2,3,4,5,6,7,8};
			NDArray a = new NDArray(dat, 2,4);
			assertTrue(Arrays.equals((int[])a.data(), (int[])dat));
	}

	@Test
	public void test_reshape() {
			int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
			NDArray a = new NDArray(dat, 3,4);
			a = a.reshape(2,6);
			assertTrue(Arrays.equals(a.getDimens(), new int[]{2,6}));
	}

	@Test
	public void test_to_string() {
		int[] dat = {1,2,3,4,5,6,7,8,9,10,11,12};
		NDArray a = new NDArray(dat, 3,4);
		a = a.reshape(2,6);
		System.out.println(a);
	}
}