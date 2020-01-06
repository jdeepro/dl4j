package cn.centipede.model.cnn;

import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;

public class Pool {
    NDArray feature_mask;

    public NDArray forward(NDArray x) {
        int[] xshape = x.dimens();
        int b = xshape[0], w = xshape[1], h = xshape[2], c = xshape[3];
        int feature_w = w / 2;
        NDArray feature = np.zeros(new int[]{b, feature_w, feature_w, c});

        // 记录最大池化时最大值的位置信息用于反向传播
        this.feature_mask = np.zeros(new int[]{b, w, h, c});

        for (int bi = 0; bi < b; bi++) {
            for (int ci = 0; ci < c; ci++) {
                for (int i = 0; i < feature_w; i++) {
                    for (int j = 0; j < feature_w; j++) {
                        NDArray dat = x.get(new int[][]{{bi},{i*2,i*2+2},{j*2,j*2+2}, {ci}});
                        feature.set(np.max(dat), bi, i, j, ci);
                        int index = np.argmax(dat);
                        this.feature_mask.set(1, bi, i*2+index/2, j*2+index%2, ci);
                    }
                }
            }
        }
        return feature;
    }

    public NDArray backward(NDArray delta) {
        return np.repeat(np.repeat(delta, 2, 1), 2, 2).multiply(this.feature_mask);
    }
}