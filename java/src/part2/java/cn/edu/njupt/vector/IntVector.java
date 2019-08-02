package cn.edu.njupt.vector;

import java.util.Arrays;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntVector {
    int[] array;

    public IntVector(int... array) {
        this.array = array;
    }

    public IntVector add(IntVector vector) {
        foreach(this.array, vector.array, (a,b)->a+b);
        return this;
    }

    public IntVector sub(IntVector vector) {
        foreach(this.array, vector.array, (a,b)->a-b);
        return this;
    }

    public int dot(IntVector vector) {
        return foreach(this.array, vector.array, (a,b)->a*b);
    }

    /**
     * A = <a1, a2, a3>，B = <b1, b2, b3>，C = <c1, c2, c3>
     *       | i  j  k  |
     * AXB = | a1 a2 a3 |
     *       | b1 b2 b3 |
     *
     * A = <a1, a2>，B = <b1, b2>
     * AXB = <a1b2 - a2b1>
     *
     * @param vector
     * @return
     */
    public IntVector cross(IntVector vector) {
        //result[0] = v1[1] * v2[2] - v1[2]*v2[1];
        //result[1] = v1[2] * v2[0] - v1[0]*v2[2];
        //result[2] = v1[0] * v2[1] - v1[1]*v2[0];
        int length = vector.array.length;
        int[] v1 = this.array;
        int[] v2 = vector.array;

        if (length == 2) {
            return new IntVector(v1[0]*v2[1]-v1[1]*v2[0]);
        }

        int[] result = IntStream.range(0, length)
                .map(i -> v1[(i+1)%length]*v2[(i+2)%length]-v1[(i+2)%length]*v2[(i+1)%length])
                .toArray();

        return new IntVector(result);

    }

    private static int foreach(int[] src, int[] dst, IntBinaryOperator op) {
        int sum = 0;
        for (int i = 0; i < src.length; i++) {
            src[i] = op.applyAsInt(src[i], dst[i]);
            sum += src[i];
        }
        return sum;
    }

    @Override
    public String toString() {
        return Arrays.stream(array)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof IntVector) {
            return Arrays.equals(array, ((IntVector)(IntVector) obj).array);
        }

        return false;
    }

    public static void main(String[] args) {
        IntVector a = new IntVector(1,2,3);
        IntVector b = new IntVector(10,20,30);

        /**需要java-oop插件
        IntVector c = a+b;
        System.out.println(c);
         ******************/
    }
}
