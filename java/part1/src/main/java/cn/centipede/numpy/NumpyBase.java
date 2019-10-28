package cn.centipede.numpy;


import cn.centipede.Config;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.IntStream;

interface ICalc {
    Object calc(Object a, Object b);
}

public class NumpyBase {
    private static int dataOffset(int[] dim, int[] index) {
        int len = dim.length;
        int s = 1, pos = 0;

        for (int i = len-1; i > 0; i--) {
            s *= dim[i];
            pos += s * index[i-1];
        }

        pos += index[len-1];
        return pos;
    }

    /**
     * transpose the index array
     * @param idataSrc src index array
     * @param idataDst dst index array
     * @param index data index
     * @param offset index offset
     * @param deep current dimens pos
     * @param dimens dimens array
     */
    static void _transpose(int[] idataSrc, int[] idataDst, int[] index, int offset, int deep, int[][] dimens) {
        if (deep == dimens[0].length) { // do transpose
            int p1 = dataOffset(dimens[0], index);
            int p2 = dataOffset(dimens[1], ArrayHelper.reverse(index));
            idataDst[p2] = idataSrc[p1];
            return;
        }

        for (int i = 0; i < dimens[0][deep]; i++) {
            index[offset] = i;
            _transpose(idataSrc, idataDst, index, offset+1, deep+1, dimens);
        }
    }

    public static Object doOp(Object src, Object dat, ICalc op) {
        boolean isSrcInt = src instanceof int[];
        boolean isDatInt = dat instanceof int[];

        if (isSrcInt && isDatInt) {
            return doOp((int[])src, (int[])dat, op);
        } else if (isSrcInt) {
            return doOp((double[])src, (int[])dat, op);
        } else if (isDatInt) {
            return doOp((double[])dat, (int[])src, op);
        } else {
            return doOp((double[])dat, (double[])src, op);
        }
    }

    public static double[] doOp(double[] src, int[] data, ICalc op) {
        int length = Array.getLength(data);
        double[] ret = new double[src.length];

        for (int i = 0; i < src.length; i++) {
            Double left = src[i];
            Integer right = data[i%length];
            ret[i] = (Double)op.calc(left, right);
        }
        return ret;
    }

    public static int[] doOp(int[] src, int[] data, ICalc op) {
        int length = Array.getLength(data);
        int[] ret = new int[src.length];

        for (int i = 0; i < src.length; i++) {
            Integer left = src[i];
            Integer right = data[i%length];
            ret[i] = (Integer)op.calc(left, right);
        }
        return ret;
    }

    public static double[] doOp(double[] src, double[] data, ICalc op) {
        int length = Array.getLength(data);
        double[] ret = new double[src.length];

        for (int i = 0; i < src.length; i++) {
            Double left = src[i];
            Double right = data[i%length];
            ret[i] = (Double)op.calc(left, right);
        }
        return ret;
    }

    public static Object dot(Object aData, int[] aDim, int[]aIData, Object bData, int[] bDim, int[] bIData) {
        Object result;
        if (aData instanceof double[] || bData instanceof double[]) {
            result = new double[aDim[0]*bDim[1]];
        } else {
            result = new int[aDim[0]*bDim[1]];
        }
        boolean isDouble =  result instanceof double[];

        for (int i = 0; i < aDim[0]; i++) {
            for (int j = 0; j < bDim[1]; j++) {
                for (int k = 0; k < aDim[1]; k++) {
                    Object o1 = Array.get(aData, aIData[i*aDim[1]+k]);
                    Object o2 = Array.get(bData, bIData[k*bDim[1]+j]);
                    if (isDouble) {
                        ((double[])result)[bDim[1]*i+j] += (double)multiply(o1, o2);
                    } else {
                        ((int[])result)[bDim[1]*i+j] += (int)multiply(o1, o2);
                    }
                }
            }
        }
        return result;
    }

    static Object multiply(Object a, Object b) {
        if (a.getClass().isArray()) {
            a = Array.get(a, 0);
            b = Array.get(b, 0);
        }
        if (a instanceof Integer && b instanceof Integer) {
            return (int)a*(int)b;
        } else {
            return (double)a*(double)b;
        }
    }

    /***
     * random inner class
     * similar to numpy.random
     * @author simbaba
     */
    public static class random {
        static Random random = new Random(System.currentTimeMillis());

        public static NDArray rand(int... dimens) {
            int size = IntStream.of(dimens).reduce(1, (a, b) -> a * b);
            double[] array = new double[size];
            for (int i = 0; i < array.length; i++) {
                double d = random.nextDouble();
                BigDecimal bd=new BigDecimal(d);
                double d1=bd.setScale(Config.ROUND_LIMIT, BigDecimal.ROUND_HALF_UP).doubleValue();
                array[i] = d1;
            }
            return new NDArray(array, dimens);
        }
    }
}

