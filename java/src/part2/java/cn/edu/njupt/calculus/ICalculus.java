package cn.edu.njupt.calculus;

enum Activation {
    ReLu, TanH, Sigmoid
}

public interface ICalculus {
    double function(double value);
    double derivative(double value);
}
