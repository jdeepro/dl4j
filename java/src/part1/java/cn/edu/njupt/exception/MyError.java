package cn.edu.njupt.exception;

public class MyError extends Error {
    MyError(String msg) {
        super(msg);
    }
}
