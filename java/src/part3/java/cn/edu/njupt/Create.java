package cn.edu.njupt;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Create {

    public static void main(String[] args) {
        INDArray array = Nd4j.create(2,3);
        INDArray vec1 = Nd4j.create(new float[]{1,2,3});
        INDArray vec2 = Nd4j.create(new float[]{2,3,4});

        array.putRow(0, vec1);
        array.putRow(1, vec2);

        System.out.println(array);
    }
}
