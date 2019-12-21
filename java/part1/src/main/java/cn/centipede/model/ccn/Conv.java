package cn.centipede.model.ccn;

import java.util.ArrayList;

import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;

public class Conv {
    private NDArray x;
    private int pad = 0;
    private int stride = 0;
    private NDArray k;
    private NDArray b;
    private NDArray k_gradient;
    private NDArray b_gradient;
    private ArrayList<NDArray> image_col;

    public Conv(int[] kshape, int stride, int pad) {
        init(kshape, stride, pad);
    }

    public Conv(int[] kshape) {
        init(kshape, 1, 0);
    }

    private void init(int[] kshape, int stride, int pad) {
        int width = kshape[0], height = kshape[1];
        int in_channel = kshape[2], out_channel = kshape[3];
        this.stride = stride;
        this.pad = pad;

        double scale = Math.sqrt(3*in_channel*width*height/out_channel);
        this.k = np.random.standard_normal(kshape).divide(scale);
        this.b = np.random.standard_normal(out_channel).divide(scale);
        this.k_gradient = np.zeros(kshape);
        this.b_gradient = np.zeros(out_channel);

        this.image_col = new ArrayList<>();
    }

    public NDArray forward(NDArray x) {
        this.x = x;
        if (this.pad != 0) {
            // this.x = np.pad(self.x, ((0,0),(self.pad,self.pad),(self.pad,self.pad),(0,0)), 'constant')
        }

        int[] xshape = x.dimens();
        int bx = xshape[0];
        int wx = xshape[1];

        int[] kshape = k.dimens();
        int wk = kshape[0];
        int nk = kshape[3];

        int feature_w = (wx - wk) / this.stride + 1;
        NDArray feature = np.zeros(bx, feature_w, feature_w, nk);

        NDArray kernel = this.k.reshape(-1, nk);
        for (int i = 0; i < bx; i++) {
            NDArray image_col = img2col(this.x.row(i), wk, this.stride);
            NDArray ifeature = (np.dot(image_col, kernel).add(this.b)).reshape(feature_w,feature_w,nk);
            feature.set(ifeature, i);
            this.image_col.add(image_col);
        }
        return feature;
    }

    public NDArray backward(NDArray delta, double learning_rate) {
        int[] xshape = this.x.dimens(); // batch,14,14,inchannel
        int bx = xshape[0], wx = xshape[1], hx = xshape[2], cx = xshape[3];
        int[] kshape = this.k.dimens(); // 5,5,inChannel,outChannel
        int wk = kshape[0], hk = kshape[1], ck = kshape[2], nk = kshape[3];
        int[] dshape = delta.dimens();  // batch,10,10,outChannel
        int bd = dshape[0], wd = dshape[1], hd = dshape[2], cd = dshape[3];

        // self.k_gradient,self.b_gradient
        NDArray delta_col = delta.reshape(bd, -1, cd);
        for (int i = 0; i < bx; i++) {
            this.k_gradient.add(np.dot(this.image_col.get(i).T, delta_col.row(i)).reshape(this.k.dimens()));
        }

        this.k_gradient.divide(bx);
        //this.b_gradient.add(np.sum(delta_col, axis=(0, 1)));
        this.b_gradient.divide(bx);

        // delta_backward
        NDArray delta_backward = np.zeros(this.x.dimens());
        //NDArray k_180 = np.rot90(this.k, 2, (0,1));
        //NDArray k_180 = k_180.swapaxes(2, 3);
        //NDArray k_180_col = k_180.reshape(-1, ck);

        NDArray pad_delta;
        if (hd-hk+1 != hx) {
            pad = (hx-hd+hk-1) / 2;
            //pad_delta = np.pad(delta, ((0,0),(pad,pad),(pad,pad),(0,0)), 'constant')
        } else {
            pad_delta = delta;
        }

        for (int i = 0; i < bx; i++) {
            //pad_delta_col = img2col(pad_delta[i], wk, self.stride);
            //delta_backward[i] = np.dot(pad_delta_col, k_180_col).reshape(wx,hx,ck);
        }

        this.k.subtract(this.k_gradient.multiply(learning_rate));
        this.b.subtract(this.b_gradient.multiply(learning_rate));
        return delta_backward;
    }

    static NDArray img2col(NDArray x, int ksize, int stride) {
        int[] shape = x.dimens();
        int wx = shape[0], cx = shape[2];
        int feature_w = (wx - ksize) / stride + 1;

        NDArray image_col = np.zeros(feature_w*feature_w, ksize*ksize*cx);
        int num = 0;

        for (int i = 0; i < feature_w; i++) {
            for (int j = 0; j < feature_w; j++) {
                int[][] range = {{i*stride,i*stride+ksize}, {j*stride,j*stride+ksize}};
                NDArray slice = x.slice(range).reshape(-1);
                np.set(image_col, slice, num++);
            }
        }
        return image_col;
    }
}