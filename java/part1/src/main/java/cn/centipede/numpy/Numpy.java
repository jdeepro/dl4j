package cn.centipede.numpy;

import java.lang.reflect.Array;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;


public class Numpy extends NumpyBase{
    public static final int ALL = 999_999_999;

    public static Object getArray(NDArray array) {
        Object real = getArrayData(array);
        int[] dim = array.getDimens();
        return ArrayHelper.struct(real, dim);
    }

    public static Object getArrayData(NDArray array) {
        Object data = array.data();
        int[] index = array.getDataIndex();

        if (data instanceof int[]) {
            return IntStream.of(index).map(i-> (int) Array.get(data, i)).toArray();
        } else {
            return IntStream.of(index).mapToDouble(i-> (double) Array.get(data, i)).toArray();
        }
    }

    public static NDArray zeros(int... dimens) {
        return zeros(dimens, double.class);
    }

    public static NDArray zeros(int[] dimens, Class<?> dtype) {
        int size = IntStream.of(dimens).reduce(1, (a, b) -> a * b);
        Object array = Array.newInstance(dtype, size);
        for (int i = 0; i < size; i++) {
            Array.set(array, i, 0);
        }
        return new NDArray(array, dimens);
    }

    public static NDArray ones(int... dimens) {
        return ones(dimens, double.class);
    }

    public static NDArray ones(int[] dimens, Class<?> dtype) {
        int size = IntStream.of(dimens).reduce(1, (a, b) -> a * b);
        Object array = Array.newInstance(dtype, size);
        for (int i = 0; i < size; i++) {
            Array.set(array, i, 1);
        }
        return new NDArray(array, dimens);
    }

    /**
     * arange(9) = [0,1,2,3,4,5,6,7,8]
     * @param top = 9
     * @param dtype int/double
     * @return
     */
    public static NDArray arange(int top, Class<?> dtype) {
        Object array = Array.newInstance(dtype, top);
        for (int i = 0; i < top; i++) {
            Array.set(array, i, i);
        }

        return new NDArray(array, top);
    }

    /**
     * TODO: Need to refactor so support more operators
     * @param src
     * @param data
     * @return
     */
    public static NDArray add(NDArray src, Object data) {
        Object srcData = src.data();
        Object array;

        if (data instanceof NDArray) {
            array = getArrayData((NDArray) data);
        } else if (data.getClass().isArray()) {
            array = ArrayHelper.flatten(data)[1];
        } else {
            array = data instanceof Integer?new int[]{(int) data}:new double[]{(double)data};
        }

        boolean isIntArray = srcData instanceof int[];
        boolean isIntData = array instanceof int[];
        int[] index = src.getDataIndex();
        int[] dimens = src.getDimens();

        Object ret;
        if (isIntArray && isIntData) {
            ret = doOp((int[])srcData, index, (int[])array, Integer::sum);
        } else if (isIntArray) {
            ret = doOp((int[])srcData, index, (double[])array, Double::sum);
        } else if (isIntData) {
            ret = doOp((double[])srcData, index, (int[])array, Double::sum);
        } else {
            ret = doOp((double[])srcData, index, (double[])array, Double::sum);
        }

        return new NDArray(ret, dimens);
    }

    /**
     * Now only support 1-2 dimens
     * @param a number/vector/matrix
     * @param b number/vector/matrix
     * @return NDArray
     */
    public static NDArray dot(NDArray a, NDArray b) {
        int[] aDim = a.getDimens();
        int[] bDim = b.getDimens();
        boolean isArray = false;

        if (bDim.length == 1) {
            isArray = true;
            bDim = new int[]{bDim[0], 1};
        }

        Object aData = a.data();
        Object bData = b.data();
        Object cData;

        if (aDim.length == 0 || aDim.length == 1) {
            cData = multiply(aData, bData);
            return new NDArray(cData, aDim);
        }

        cData = dot(aData, aDim, a.getDataIndex(), bData, bDim, b.getDataIndex());
        int[] cDim = isArray?new int[]{aDim[0]}:new int[]{aDim[0], bDim[1]};
        return new NDArray(cData, cDim);
    }

    public static NDArray dot(NDArray a, int b) {
        Object data = getArrayData(a);
        if (data instanceof int[]) {
            data = IntStream.of((int[])data).map(n->n*b).toArray();
        } else {
            data = DoubleStream.of((double[]) data).map(n->n*b).toArray();
        }
        return Numpy.array(data, a.getDimens());
    }

    public static NDArray dot(NDArray a, double b) {
        Object data = getArrayData(a);
        if (data instanceof int[]) {
            data = IntStream.of((int[])data).mapToDouble(n->n*b).toArray();
        } else {
            data = DoubleStream.of((double[]) data).map(n->n*b).toArray();
        }
        return Numpy.array(data, a.getDimens());
    }

    /**
     * src & dst share the same data buffer,
     * but own idata separately
     * @param idataSrc src data index
     * @param dimens dimens array
     * @return transpose data index
     */
    public static int[] transpose(int[] idataSrc, int[] dimens) {
        int[] index = new int[dimens.length];
        int[] idataRet = new int[idataSrc.length];
        int[][] dimensRet = new int[][]{dimens, ArrayHelper.reverse(dimens)};
        _transpose(idataSrc, idataRet, index, 0, 0, dimensRet);
        return idataRet;
    }


    public static void set(NDArray array, Object data, int... index) {
        array.set(data, index);
    }

    public static NDArray arange(int top) {
        return arange(top, int.class);
    }

    /**
     * [[2,3,4],[4,5,6]] -> flatten
     * -> obj{[2,3,4,4,5,6], int[] dimens}
     * @param dimens multi-dimens
     * @return NDArray
     */
    public static NDArray array(Object data, int... dimens) {
        if (dimens.length > 0) {
            return new NDArray(data, dimens);
        }

        boolean isArray = data.getClass().isArray();
        if (!isArray) {
            return new NDArray(data);
        }

        Object[] object = ArrayHelper.flatten(data);
        return new NDArray(object[1], (int[]) object[0]);
    }

    public static Matrix matrix(Object data) {
        NDArray array = array(data);
        return new Matrix(array);
    }
}