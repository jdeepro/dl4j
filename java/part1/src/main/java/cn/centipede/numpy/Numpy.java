package cn.centipede.numpy;

import java.lang.reflect.Array;
import java.util.Arrays;
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
        } else if (data instanceof Integer) {
            return new int[]{(int)data};
        } else if (data instanceof Double) {
            return new double[]{(double)data};
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
     * data: NDArray, number, int[][][], double[][][]
     * genertic
     * convenrt to int[] or double[]
     */
    private static Object dat2Array(Object data) {
        Object array;

        if (data instanceof NDArray) {
            array = getArrayData((NDArray) data);
        } else if (data.getClass().isArray()) {
            array = ArrayHelper.flatten(data)[1];
        } else {
            array = data instanceof Integer?new int[]{(int) data}:new double[]{(double)data};
        }
       return array;
    }

    /**
     * support double & int
     * @param src
     * @param data
     * @return
     */
    public static NDArray add(NDArray src, Object dat) {
        Object datArray  = dat2Array(dat);
        Object srcData   = getArrayData(src);

        Object ret = doOp(srcData, datArray, Operator::add);
        return new NDArray(ret,  src.getDimens());
    }

    public static NDArray sub(NDArray src, Object dat) {
        Object datArray  = dat2Array(dat);
        Object srcData   = getArrayData(src);

        Object ret = doOp(srcData, datArray, Operator::subtract);
        return new NDArray(ret,  src.getDimens());
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
        Object aData = a.data();
        Object bData = b.data();

        /** a is number */
        if (aDim.length == 0) {
            if (aData instanceof Integer)return dot(b, (int)aData);
            else return dot(b, (double)aData);
        }

        /** b is number */
        if (bDim.length == 0) {
            if (bData instanceof Integer)return dot(a, (int)bData);
            else return dot(a, (double)bData);
        }

        /** a is array but not a vector */
        if (aDim.length == 1) {
            aDim = new int[]{1, aDim[0]};
        }

        int[] cDim;

        /** b is array but not a vector */
        if (bDim.length == 1) {
            bDim = new int[]{bDim[0], 1};
            cDim = new int[]{aDim[0]};
        } else {
            cDim = new int[]{aDim[0], bDim[1]};
        }

        /** normal dot operation */
        Object cData = dot(getArrayData(a), aDim, getArrayData(b), bDim);
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

    /**
     * Matrix is special NDArray
     */
    public static Matrix matrix(Object data) {
        NDArray array = array(data);
        return new Matrix(array);
    }

    public static boolean compare(NDArray src, NDArray dst) {
        if (!Arrays.equals(src.getDimens(), dst.getDimens())) {
            return false;
        }
        Object srcData = getArrayData(src);
        Object dstData = getArrayData(dst);
        if (!srcData.getClass().equals(dstData.getClass())) {
            return false;
        }

        if (srcData instanceof int[]) {
            return Arrays.equals((int[])srcData, (int[])dstData);
        } else {
            return Arrays.equals((double[])srcData, (double[])dstData);
        }
    }
}