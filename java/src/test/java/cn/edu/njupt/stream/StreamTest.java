package cn.edu.njupt.stream;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;

public class StreamTest {
    @Test
    public void parallelSum() {
        int[] nums = {1,2,3,4,5,6,7,8,9,10};

        OptionalInt sum = IntStream.of(nums)
                .parallel()
                .reduce((a, b)->a+b);
        assertEquals(sum.getAsInt(), 55);
    }

    @Test
    public void randomStream() {
        System.out.println("randomStream output 6 random numbers");
        Random random = new Random(System.currentTimeMillis());
        IntStream stream = random.ints(1000, 9999);
        stream.limit(6).forEach(System.out::println);
    }
}
