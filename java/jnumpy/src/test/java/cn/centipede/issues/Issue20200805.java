package cn.centipede.issues;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;


public class Issue20200805 {
  @Test
  public void test_zeros_3x4_plus_ones_3x1() {
    NDArray zeros = np.zeros(3, 4);
    NDArray ones = np.ones(3, 1);
    NDArray result = zeros.add(ones);
    assertTrue(result.equals(np.ones(3, 4)));
    result.equals(np.ones(3, 4));
  }

}