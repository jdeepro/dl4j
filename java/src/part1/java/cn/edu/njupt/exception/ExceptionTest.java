package cn.edu.njupt.exception;

public class ExceptionTest {
    static void throwRuntime() {
        throw new MyRunException("运行时遇到异常");
    }

    static void throwChecked() throws MyCheckedException {
        throw new MyCheckedException("我会在编译时检查");
    }

    static void throwError() {
        throw new MyError("运行时遇到错误");
    }

    static void testException1() throws MyCheckedException {
        throwChecked();
    }

    static void testException2() {
        try {
            throwChecked();
        } catch (MyCheckedException e) {
            e.printStackTrace();
        }
    }

    int num=1;
    public void testStack(){
        num++;
        this.testStack();
    }

    void thread2() {
        new Thread(()-> testStack()).start();
    }

    void thread1() {
        new Thread(()->{
            while (true) {
                System.out.println("thread1");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {

        ExceptionTest ex = new ExceptionTest();
        ex.thread1();
        ex.thread2();
    }
}
