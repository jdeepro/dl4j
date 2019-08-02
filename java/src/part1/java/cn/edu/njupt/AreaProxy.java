package cn.edu.njupt;

import java.io.*;
import java.lang.reflect.*;

class MyAutoCb implements AutoCloseable {
    public void print() {
        System.out.println("我要输出我自己");
    }
    @Override
    public void close() throws Exception {
        System.out.println("我被关了....");
    }
}

public class AreaProxy implements InvocationHandler {
    IArea object;
    AreaProxy(IArea o) {
        object = o;
    }

    public IArea getProxy() {
        return (IArea) Proxy.newProxyInstance(
                AreaProxy.class.getClassLoader(),
                Triangle.class.getSuperclass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("调用之前");
        Object obj = method.invoke(object,args);
        System.out.println("调用之后");
        return obj;
    }



    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("test.txt");

        try(MyAutoCb cb = new MyAutoCb();FileInputStream fis = new FileInputStream(file)) {
            fis.read();
            cb.print();
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在");
            throw e;
        } catch (IOException e) {
            System.out.println("读文件时遇到错误");
        } catch (Exception e) {
            System.out.println(e.getCause());
        } finally {
            System.out.println("无论如何，都到此善尾");
        }
    }

    private static void readFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read();
        fis.close();
    }
}