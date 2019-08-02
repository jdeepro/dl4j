package cn.edu.njupt;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


public class Triangle extends Polygon {

    public float area() {
        log("Triangle", "我还没来得及写好");
        return 0;
    }


    public static void main(String[] args) {
        Polygon polygon = new Triangle();

        InvocationHandler invoker = (proxy, method, arg) -> {
            System.out.println("调用之前");
            Object obj = method.invoke(polygon, arg);
            System.out.println("调用之后");
            return obj;
        };

        IArea iArea = (IArea) Proxy.newProxyInstance(
                AreaProxy.class.getClassLoader(),
                Polygon.class.getInterfaces(),
                invoker);

        iArea.area();
    }
}