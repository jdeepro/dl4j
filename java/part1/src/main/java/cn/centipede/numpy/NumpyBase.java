package cn.centipede.numpy;

import cn.centipede.Config;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;


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
    static void doTranspose(int[] idataSrc, int[] idataDst, int[] index, int offset, int deep, int[][] dimens) {
        if (deep == dimens[0].length) { // do transpose
            int p1 = dataOffset(dimens[0], index);
            int p2 = dataOffset(dimens[1], ArrayHelper.reverse(index));
            idataDst[p2] = idataSrc[p1];
            return;
        }

        for (int i = 0; i < dimens[0][deep]; i++) {
            index[offset] = i;
            doTranspose(idataSrc, idataDst, index, offset+1, deep+1, dimens);
        }
    }

    public static Object doOp(Object src, Object dat, ICalc op) {
        boolean isSrcInt = src instanceof int[];
        boolean isDatInt = dat instanceof int[];

        if (isSrcInt && isDatInt) {
            return doOp((int[])src, (int[])dat, op);
        } else if (isSrcInt) {
            double[] srcNew = IntStream.of((int[])src).asDoubleStream().toArray();
            return doOp(srcNew, (double[])dat, op);
        } else if (isDatInt) {
            double[] datNew = IntStream.of((int[])dat).asDoubleStream().toArray();
            return doOp((double[])src, datNew, op);
        } else {
            return doOp((double[])src, (double[])dat, op);
        }
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

    public static Object doOp(Object src, ICalcEx op) {
        boolean isSrcInt = src instanceof int[];

        if (isSrcInt) {
            return IntStream.of((int[])src).mapToDouble(a->(double)op.calc(a)).toArray();
        } else {
            return DoubleStream.of((double[])src).map(a->(double)op.calc(a)).toArray();
        }
    }

    static Object dotDouble(double[] aData, int[] aDim, double[] bData, int[] bDim) {
        double[] result = new double[aDim[0]*bDim[1]];
        for (int i = 0; i < aDim[0]; i++) {
            for (int j = 0; j < bDim[1]; j++) {
                for (int k = 0; k < aDim[1]; k++) {
                    result[bDim[1]*i+j] += aData[i*aDim[1]+k] * bData[k*bDim[1]+j];
                }
            }
        }
        return result;
    }

    static Object dotInt(int[] aData, int[] aDim, int[] bData, int[] bDim) {
        int[] result = new int[aDim[0]*bDim[1]];
        for (int i = 0; i < aDim[0]; i++) {
            for (int j = 0; j < bDim[1]; j++) {
                for (int k = 0; k < aDim[1]; k++) {
                    result[bDim[1]*i+j] += aData[i*aDim[1]+k] * bData[k*bDim[1]+j];
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
        return Operator.multiply(a, b);
    }

    /***
     * random inner class
     * similar to numpy.random
     * @author simbaba
     */
    public static class random {
        static Random random = new Random(System.currentTimeMillis());

        /** set random seed, we can reproduce the output */
        public static void seed(long r) {
            random.setSeed(r);
        }

        public static NDArray uniform(double start, double end, int...dimens) {
            int size = IntStream.of(dimens).reduce(1, (a, b) -> a * b);
            DoubleStream ds = random.doubles(start, end);
            double[] array = ds.limit(size).toArray();
            return new NDArray(array, dimens);
        }

        /**
         * @param top
         * @return [0, top)
         */
        public static int randint(int top) {
            return random.nextInt(top);
        }

        /**
         * random ints between [0, top)
         * @param top
         * @param size
         * @return randome choices
         */
        public static int[] choice(int top, int size) {
            return random.ints(0, top).limit(size).toArray();
        }

        /**
         * shuffle number list in place
         * @param numbers
         */
        public static int[] shuffle(int[] numbers) {
            for (int i = numbers.length; i > 0; i--) {
                int r = random.nextInt(i);
                int temp = numbers[i - 1];
                numbers[i - 1] = numbers[r];
                numbers[r] = temp;
            }
            return numbers;
        }

        /** generate NDArray according dimens */
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

