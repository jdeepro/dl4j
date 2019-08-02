package cn.edu.njupt;

import org.nd4j.linalg.cpu.nativecpu.NDArray;

public class Array {
    public static void main(String[] args) {

        int[][] numbers = new int[2][];
        int[] N1 = {1,2,3};
        int[] N2 = {2,3,4};

        numbers[0] = N1;
        numbers[1] = N2;

        System.out.println(numbers[1].length);
    }
}
