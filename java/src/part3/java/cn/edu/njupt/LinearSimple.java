package cn.edu.njupt;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.List;
import java.util.Map;

public class LinearSimple {
    public static void main(String[] args) {

        OutputLayer outputLayer = new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .activation(Activation.IDENTITY)
                .nIn(1)
                .nOut(1).build();

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.ZERO)
                .updater(new Sgd(0.01))
                .list()
                .layer(0, outputLayer)
                .pretrain(false)
                .backprop(true).build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        double [] input = {
            0.2, 0.4, 0.6, 1.0, 1.4, 1.6, 2.0, 2.3
        };

        double [] output = {
            0.3, 0.6, 1.0, 1.2, 1.2, 1.7, 1.5, 1.8
        };

        //y=-0.3036t+27.125
//        double [] input = {
//                0,1,2,3,4,5,6,7
//        };
//
//        double [] output = {
//               27.0,26.8,26.5,26.3,26.1,25.7,25.3,24.8
//        };

        INDArray inputNDArray = Nd4j.create(input, new int[]{input.length,1});
        INDArray outPut = Nd4j.create(output, new int[]{output.length, 1});

        for( int i=0; i<5000; i++ ) {
            net.fit(inputNDArray, outPut);
        }

        Map<String, INDArray> params = net.paramTable();
        params.forEach((key, value) -> System.out.println("key:" + key +", value = " + value));
    }
}
