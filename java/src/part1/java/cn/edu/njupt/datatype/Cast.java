package cn.edu.njupt.datatype;

public class Cast {

    public static void byte2Int() {
        byte b = -1;
        int n1 = b;
        int n2 = b & 0xFF;
        System.out.println("byte2Int: n1="+n1); // 输出-1
        System.out.println("byte2Int: n2="+n2); // 输出255
    }

    public static void implicit() {
        byte b = 127; // 小于127的时候可看作byte类型
        int n1 = b; // 隐式转换
        long l1 = n1; // 隐式转换

        long l2 = 127l; // 显式为long

        float f1 = 12;

        System.out.println("implicit: f1="+f1); // 12.0
        System.out.println("implicit: l2="+l2); // 127
    }

    public static void cast() {
        // 小数默认为double类型，需要强制转换
        float f2 = (float) 12.3;
        float f3 = 12.3f; // 显式为float

        double d1 = 12d; // 显式为double
        double d2 = 12.3;

        long l3 = (long) d2; // 强制转换

        System.out.println("cast: d2="+d2); // 输出12.3
        System.out.println("cast: l3="+l3); // 输出12
    }

    public static void main(String[] args) {
        byte2Int();
        implicit();
        cast();
    }
}
