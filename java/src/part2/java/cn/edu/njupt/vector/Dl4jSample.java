package cn.edu.njupt.vector;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Dl4jSample {
    public static void main(String[] args) {
        INDArray vec1 = Nd4j.create(new float[]{1,2,3});

        // vector add
        INDArray vec2 = Nd4j.create(new float[]{10,20,30});

        // [[11.0000, 22.0000, 33.0000]]
        System.out.printf("vec1+vec2=%s\n", vec1.add(vec2));

        // [[11.0000, 22.0000, 33.0000]]
        System.out.printf("vec1*vec2=%s\n", vec1.mul(vec2));

        // [[11.0000, 12.0000, 13.0000]]
        System.out.printf("vec3+10=%s\n", vec1.add(10));

        INDArray v1 = Nd4j.create(new float[]{1,2,3}).reshape(1,3);
        INDArray v2 = Nd4j.create(new float[]{10,20,30}).reshape(3,1);
        System.out.printf("v1v2=[%s]\n", v1.mmul(v2));

    }
}
