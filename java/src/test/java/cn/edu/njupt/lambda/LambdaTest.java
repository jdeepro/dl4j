package cn.edu.njupt.lambda;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class LambdaTest {
    @Test
    public void visitIntList() {
        int[] intList = {1,2,3,4,5};
        IntStream stream = IntStream.of(intList);
        int[] array = stream.map(n -> n * 2).toArray();

        assertArrayEquals(array, new int[]{2,4,6,8,10});
    }

    @Test
    public void visitIntegerList() {
        Integer[] intList = {1,2,3,4,5};
        IntStream stream = Arrays.stream(intList).mapToInt(n->n);
        int[] array = stream.map(n -> n * 2).toArray();

        assertArrayEquals(array, new int[]{2,4,6,8,10});
    }

    @Test
    public void map2Stream() {
        HashMap<Integer, String> n2ch = new HashMap<>();
        n2ch.put(1, "abcdefg");
        n2ch.put(2, "hijklmn");
        n2ch.put(3, "opqrst");

        Stream<Integer> keyStream = n2ch.keySet().stream();
        Object[] even = keyStream.filter(key -> key % 2 == 0).map(n2ch::get).toArray();

        assertArrayEquals(even, new String[]{"hijklmn"});
    }

    @Test
    public void batchProcess() {
        int[] intList = {1,2,3,4,5};
        IntStream stream = IntStream.of(intList);

        OptionalInt optSum = stream.reduce((a, b)->a+b);

        int sum = optSum.getAsInt();
        assertEquals(sum, 1+2+3+4+5);
    }

    @Test
    public void groupBySex() {
        class Student {
            private String name;
            private Integer sex;

            private Student(String name, Integer sex) {
                this.name = name;
                this.sex = sex;
            }
        }

        Student jim = new Student("jim", 1);
        Student jane = new Student("jane", 0);
        Student tom = new Student("tom", 1);
        Student sunny = new Student("sunny", 0);

        Student[] students = new Student[] {jim, jane, tom, sunny};

        Map<Integer, List<Student>> group = Stream.of(students).collect(
                        Collectors.groupingBy(s -> s.sex));

        List female = group.get(1);
        Student[] dst = new Student[]{jim, tom};
        assertArrayEquals(female.toArray(), dst);
    }
}
