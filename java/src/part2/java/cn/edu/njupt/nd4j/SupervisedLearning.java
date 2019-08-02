package cn.edu.njupt.nd4j;

public class SupervisedLearning {
    public static double sigMoid(double value) {
        double e_x = Math.pow(Math.E, -value);
        double result = 1 / (1 + e_x);
        return result;
    }

    public static double sigMoidDerivative(double value) {
        double A = sigMoid(value);
        double B = 1 - sigMoid(value);
        double result = A * B;
        return result;
    }
}
