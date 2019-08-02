package cn.edu.njupt.vector;

import org.junit.Test;
import static org.junit.Assert.*;

public class IntVectorTest {

    /**需要java-oop插件
    @Test
    public void add_operator_override() {
        IntVector a = new IntVector(1,2,3);
        IntVector b = new IntVector(10,20,30);

        IntVector c = a + b; // 看上去运算符重载了
        int[] expected = new int[]{11, 22, 33};

        assertArrayEquals(expected, c.array);
        assertEquals(new IntVector(expected), c);
    }
     ******************/

    @Test
    public void add() {
        IntVector a = new IntVector(1,2,3);
        IntVector b = new IntVector(10,20,30);

        IntVector c = a.add(b);
        int[] expected = new int[]{11, 22, 33};

        assertArrayEquals(expected, c.array);
        assertEquals(new IntVector(expected), c);
    }

    @Test
    public void sub() {
        IntVector a = new IntVector(1,2,3);
        IntVector b = new IntVector(10,20,30);

        assertArrayEquals(new int[]{-9, -18, -27}, a.sub(b).array);
    }

    @Test
    public void dot() {
        IntVector a = new IntVector(1,2,3);
        IntVector b = new IntVector(10,20,30);

        assertEquals(1*10+2*20+3*30, a.dot(b));
    }

    @Test
    public void cross() {
        IntVector vect1 = new IntVector(3, 4, 5);
        IntVector vect2 = new IntVector(4, 3, 5);
        IntVector product = vect1.cross(vect2);

        assertArrayEquals(new int[]{5, 5, -7}, product.array);

        vect1 = new IntVector(3, 4);
        vect2 = new IntVector(4, 3);
        product = vect1.cross(vect2);

        assertArrayEquals(new int[]{-7}, product.array);
    }
}