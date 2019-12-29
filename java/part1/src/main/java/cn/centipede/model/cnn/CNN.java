package cn.centipede.model.cnn;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import cn.centipede.model.data.MNIST;
import cn.centipede.npz.NpzFile;
import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;

/**
 * Yes, I want to implement CCN oops, a little complex for me :)
 * ---------------------------- input->hidden->output
 * ----------------------------
 * 
 * @author simbaba
 * @version 0.0
 */
public class CNN {
    private Conv conv1 = new Conv(new int[]{5, 5, 1, 6});
    private Conv conv2 = new Conv(new int[]{5, 5, 6, 16});
    private Relu relu1 = new Relu();
    private Relu relu2 = new Relu();

    private Pool pool1 = new Pool();
    private Pool pool2 = new Pool();
    private Linear nn  = new Linear(256, 10);
    private Softmax softmax = new Softmax();

    public NDArray train() {
        return null;
    }

    public void loadNpz() throws URISyntaxException {
        URL npzURL = CNN.class.getResource("/mnist.npz");
        NpzFile npz = new NpzFile(Paths.get(npzURL.toURI()));
        conv1.k = npz.get("k1");
        conv1.b = npz.get("b1");
        conv2.k = npz.get("k2");
        conv2.b = npz.get("b2");
        nn.W = npz.get("w3");
        nn.b = npz.get("b3");
    }

    public int predict(NDArray X) {
        X = X.reshape(np.newaxis, np.ALL);

        NDArray predict = conv1.forward(X);
        predict = relu1.forward(predict);
        predict = pool1.forward(predict);
        predict = conv2.forward(predict);
        predict = relu2.forward(predict);
        predict = pool2.forward(predict);
        predict = predict.reshape(1, -1);
        predict = nn.forward(predict);

        predict = softmax.predict(predict);
        return np.argmax(predict);
    }

    public void eval() {
        NDArray[] mnist = MNIST.numpy(false); // train=false
        mnist[1].row(1).dump(); // lable

        NDArray test = mnist[0].reshape(10000, 28, 28, 1);
        // System.out.println(test.shape());
        // test.index(1).reshape(28,28).dump();

        NDArray img2col = Conv.img2col(test.index(1), 5, 1);
        System.out.println(img2col.shape());
    }

}