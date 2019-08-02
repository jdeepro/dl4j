package cn.edu.njupt.vector;

import java.util.function.IntBinaryOperator;

public class IntNDArray {
    int[] array;
    int[] shape;

    public IntNDArray(int... array) {
        this.array = array;
        this.shape = new int[]{1, array.length};
    }

    public IntNDArray reshape(int[] shape) {
        this.shape = shape;
        return this;
    }

    public IntNDArray add(IntNDArray ndArray) {
        foreach(this.array, ndArray.array, (a,b)->a+b);
        return this;
    }

    public IntNDArray mul(IntNDArray ndArray) {
        final int ROWS = this.shape[0];
        final int COLS = ndArray.shape[1];

        int[] shape = new int[]{ROWS, COLS};
        int[] data = new int[ROWS*COLS];

        for(int i=0; i<ROWS; i++) {
            for (int j=0; j<COLS; j++) {
                for (int k=0; k<ndArray.shape[0]; k++) {
                    int a_i_k = this.array[i*this.shape[1]+k];
                    int b_k_j = ndArray.array[k*ndArray.shape[1]+j];
                    data[i*COLS+j] += a_i_k * b_k_j;
                }
            }
        }
        return new IntNDArray(data).reshape(shape);
    }

    private static void foreach(int[] src, int[] dst, IntBinaryOperator op) {
        int sum = 0;
        for (int i = 0; i < src.length; i++) {
            src[i] = op.applyAsInt(src[i], dst[i]);
            sum += src[i];
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int r=0; r<shape[0]; r++) {
            sb.append(array[r*shape[1]]);
            for (int c=1; c<shape[1];c++) {
                sb.append(',').append(array[r*shape[1]+c]);
            }
            sb.append('\n');
        }
        return sb.append(']').toString();
    }

    public static void main(String[] args) {
        IntNDArray m1 = new IntNDArray(1,2,3,4,5,6).reshape(new int[]{2, 3});
        IntNDArray m2 = new IntNDArray(2,3,4,5,6,7).reshape(new int[]{3, 2});

        System.out.printf("%s\n%s\nm1 x m2 =\n%s", m1, m2, m1.mul(m2));

        /**需要java-oop插件
         IntVector c = a+b;
         System.out.println(c);
         ******************/
    }
}
