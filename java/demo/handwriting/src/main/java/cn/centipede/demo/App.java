package cn.centipede.demo;

public class App {
    public static void main(String[] args) {
        new Thread(()->new Handwriting()).start();
    }
}
