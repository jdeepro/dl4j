package cn.edu.njupt.calculus;

public class Factory {


    public static ICalculus activation(String name) {
        Activation activation = Activation.valueOf(name);
        return null;
    }

    public static void main(String[] args) {
        Activation activation = Activation.Sigmoid;
        System.out.println(activation);
    }    
}
