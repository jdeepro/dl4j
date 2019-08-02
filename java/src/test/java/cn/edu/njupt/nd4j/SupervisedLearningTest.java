package cn.edu.njupt.nd4j;

import org.junit.Test;
import static org.junit.Assert.*;

public class SupervisedLearningTest {
    @Test
    public void testSigmoid() {
        System.out.println("testSigmoid");
        assertEquals(SupervisedLearning.sigMoid(0), 0.5, 0.00001);
        assertEquals(SupervisedLearning.sigMoid(10), 0.9, 0.1);
    }
}
