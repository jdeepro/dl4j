package cn.edu.njupt.lambda;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.OptionalInt;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

interface Ixy {
    int add(int x, int y);
    default int sub(int x, int y) {return x-y;}
    default int mul(int x, int y) {return x*y;}
}

public class Lambda {
    static void summary() {
        // 1. 无参数, 返回值为7
        IntSupplier fn = () -> 7;

        // 2. 接收一个参数(数字类型),返回它的2倍
        IntFunction<String> fx = x -> (2 * x) + "$";

        // 3. 接受2个参数(数字),并返回他们的乘积
        IntBinaryOperator f_xy_mul = (x, y) -> x * y;

        // 4. 接收2个int型整数,返回他们的和
        IntBinaryOperator f_xy_add = (int x, int y) -> x + y;

        // 5. 接受一个 string 对象,并在控制台打印,不返回任何值(看起来像是返回void)
        Consumer<String> print = (String s) -> System.out.print(s);

        int n = fn.getAsInt();
        String d = fx.apply(3);
        int mul = f_xy_mul.applyAsInt(2, 3);
        int add = f_xy_add.applyAsInt(2, 4);
        print.accept(String.format("%d, %s, %d, %d", n , d, mul, add));
    }

    static void myLambda() {
        Ixy xy = (a, b)-> a+b;
        int sum = xy.add(1,2);
        int sub = xy.sub(1,2);
        System.out.printf("sum=%d, sub=%d", sum, sub);
    }

    static void myLambdaEx() {
        Ixy xy = Math::addExact;
        int sum = xy.add(1,2);
        int sub = xy.sub(1,2);

        Consumer<String> print = System.out::printf;
        print.accept(String.format("sum=%d, sub=%d", sum, sub));
    }

    static void consumerSample() {
        int[] ids = {0,1,2,3,4,5,6,7,8,9};

        Consumer<String> print = System.out::print;
        Consumer<String> println = System.out::println;
        IntPredicate isOdd = (n) -> n%2==1;

        IntConsumer c1 = (n)-> {
            if (isOdd.test(n))print.accept("单数");
            else print.accept("双数");
        };

        IntConsumer c2 = (n)-> {
            if (isOdd.test(n))println.accept("->"+n);
            else println.accept("=>"+n);
        };

        IntStream.of(ids).forEach(c1.andThen(c2));
    }

    static void fileSample() throws IOException {
        Path path = Paths.get("/Users/simbaba",
                "Desktop",
                "test.txt");
        Files.lines(path).forEach(System.out::println);
    }

    static void minAndMax() {
        Integer[] ids = {11, 2, 6, 4, 7, 10, 5};
        OptionalInt min = Stream.of(ids).mapToInt(id->id).min();
        OptionalInt max = Stream.of(ids).mapToInt(id->id).max();

        min.ifPresent(System.out::println);
        max.ifPresent(System.out::println);
    }

    static void mathSample() {
        DoubleUnaryOperator op1 = (d)->2*d*d/9;
        DoubleUnaryOperator op2 = (d)->(d+1)/2;
        DoubleUnaryOperator op3 = (d)->-d+8;

        double d = 4.5d;
        if (d > 5) {
            d = op3.applyAsDouble(d);
        } else if (d > 3) {
            d = op2.applyAsDouble(d);
        } else {
            d= op1.applyAsDouble(d);
        }
        System.out.println("d = " + d);
    }

    public static void main(String[] args) {
        mathSample();
    }
}
