package cn.centipede.model.activation;

import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;

public class Softmax {
    private NDArray delta;
    private NDArray softmax;

    public double cal_loss(NDArray predict, NDArray label) {
        int[] pdimens = predict.dimens();
        int batchsize = pdimens[0];

        predict(predict);

        delta = np.zeros(pdimens);
        double loss = 0;

        for (int i=0; i < batchsize; i++) {
            NDArray label_i = label.row(i);
            NDArray softmanx_i = this.softmax.row(i);
            delta.set(softmanx_i.subtract(label_i), i);
            loss -= np.sum(np.log(softmanx_i).multiply(label_i));
        }

        return loss /= batchsize;
    }

    public NDArray predict(NDArray predict) {
        int[] pdimens = predict.dimens();
        int batchsize = pdimens[0];

        softmax = np.zeros(predict.dimens());

        for (int  i=0; i < batchsize; i++) {
            NDArray predict_tmp = predict.row(i).subtract(np.max(predict.row(i)));
            predict_tmp = np.exp(predict_tmp);
            softmax.set(predict_tmp.divide(np.sum(predict_tmp)), i);
        }
        return softmax;
    }
}