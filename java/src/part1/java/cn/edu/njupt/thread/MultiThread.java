package cn.edu.njupt.thread;

class Section_5_1 {
    void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void print(String info) {
        System.out.println(info);
    }


    synchronized void doLocked(String info) {
        try {
            print(info);
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void test() {
        Thread T1 = new Thread(() -> {
            print("T1: enter");
            doLocked("T1: process");
            print("T1: done");
        });
        Thread T2 = new Thread(() -> {
            print("T2: enter");
            doLocked("T1: process");
            print("T2: done");
        });
        Thread T3 = new Thread(() -> {
            print("T3: enter");
            doLocked("T3: process");
            print("T3: done");
        });
        Thread T4 = new Thread(() -> {
            synchronized (this) {
                sleep(3000);
                notify();
            }
            print("T4 exit!");
        });

        T1.start();
        T2.start();
        T3.start();
        T4.start();
    }
}

public class MultiThread {

    public static void main(String[] args) {
        new Section_5_1().test();
    }
}