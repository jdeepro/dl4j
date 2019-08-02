package cn.edu.njupt.thread;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Pool {
    static void print(String info) {
        System.out.println(info);
    }

    static void test() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);

        pool.schedule(() -> print("Task1 delay 3 seconds"), 3, TimeUnit.SECONDS);
        pool.schedule(() -> print("Task2 delay 2 seconds"), 2, TimeUnit.SECONDS);
        pool.schedule(() -> print("Task3 delay 1 seconds"), 1, TimeUnit.SECONDS);
        pool.schedule(() -> print("Task4 delay 4 seconds"), 4, TimeUnit.SECONDS);
        pool.schedule(() -> print("Task5 delay 5 seconds"), 5, TimeUnit.SECONDS);
        pool.schedule(() -> print("Task6 delay 6 seconds"), 6, TimeUnit.SECONDS);
        pool.shutdown();
    }

    public static void main(String[] args) {
        test();
    }
}
