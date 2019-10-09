package cn.centipede.numpy;

/**
 * same as numpy
 */
public class JNum<T> {
	/**
	 * now only support 2d
	 */
	public static NDArray array(T[] dat) {
		return new NDArray(dat);
	}

	/**
	 * now only support 2d
	 * @param dimens
	 * @return
	 */
	public static NDArray ones(int... dimens) {
		
	}

	/**
	 * now only support 2d
	 */
	public static NDArray zeros(int... dimens) {

	}

	// public static NDArray linspace();
	// public static NDArray arange();
	
	// public static NDArray sin();
	// public static NDArray cos();
	// public static NDArray sqrt();
	// public static NDArray exp();

	// public static NDArray less();
	// public static NDArray greater();

	// public static boolean all();
	// public static boolean any();
	
	// public static int argmax(); // -> TODO: unravel_index
	// public static int argmin();

	static class random {
		public static NDArray rand();
		public static NDArray normal();
	}
}

/**
 * vector, matrix
 */
class NDArray<T> {
	private T[] data;
	private NDArray<T> T;

	public NDArray<T> reshape();
	public NDArray<T> transpose();

	public T sum();
	public T mean();

	public NDArray min();
	public NDArray max();

	public NDArray slice(int start, int stop, int step);
	public NDArray slice(int start, int stop);
	public NDArray slice(int start);
}