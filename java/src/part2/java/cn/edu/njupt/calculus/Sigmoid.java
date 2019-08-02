package cn.edu.njupt.calculus;

public class Sigmoid implements ICalculus{
    @Override
    public double function(double value) {
        double ey = Math.pow(Math.E, -value);
        double result = 1 / (1 + ey);
        return result;
    }

    @Override
    public double derivative(double value) {
        return 0;
    }
}
