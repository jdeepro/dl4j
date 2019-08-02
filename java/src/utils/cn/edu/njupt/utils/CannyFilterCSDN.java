package cn.edu.njupt.utils;

import com.sun.javafx.application.PlatformImpl;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

public class CannyFilterCSDN {
    private float gaussianKernelRadius = 2f;
    private int gaussianKernelWidth = 16;
    private float lowThreshold = 2.5f;
    private float highThreshold = 7.5f;

    private int width;
    private int height;
    private float[] data;
    private float[] magnitudes;

    /**
     * 传入被处理图片，返回边缘提取效果图
     * @param srcPath
     * @return
     */
    public WritableImage process(String srcPath) {
        Image image = new Image(srcPath, false);
        width = (int) image.getWidth();
        height = (int) image.getHeight();

        WritableImage src = new WritableImage(
                image.getPixelReader(),
                width,
                height);

        WritableImage outImage = new WritableImage(width, height);

        // 图像灰度化
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];

        getRGB(src, width, height, inPixels);
        toGray(inPixels);

        // 计算高斯卷积核
        float kernel[][] = new float[gaussianKernelWidth][gaussianKernelWidth];
        for (int x = 0; x < gaussianKernelWidth; x++) {
            for (int y = 0; y < gaussianKernelWidth; y++) {
                kernel[x][y] = gaussian(x, y, gaussianKernelRadius);
            }
        }

        // 高斯模糊 -灰度图像
        int krr = (int) gaussianKernelRadius;
        int index = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                double weightSum = 0.0;
                double redSum = 0;
                for (int subRow = -krr; subRow <= krr; subRow++) {
                    int nrow = row + subRow;
                    if (nrow >= height || nrow < 0) {
                        nrow = 0;
                    }
                    for (int subCol = -krr; subCol <= krr; subCol++) {
                        int ncol = col + subCol;
                        if (ncol >= width || ncol <= 0) {
                            ncol = 0;
                        }
                        int index2 = nrow * width + ncol;
                        int tr1 = (inPixels[index2] >> 16) & 0xff;
                        redSum += tr1 * kernel[subRow + krr][subCol + krr];
                        weightSum += kernel[subRow + krr][subCol + krr];
                    }
                }
                int gray = (int) (redSum / weightSum);
                outPixels[index] = gray;
            }
        }

        // 计算梯度-gradient, X放与Y方向
        data = new float[width * height];
        magnitudes = new float[width * height];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                // 计算X方向梯度
                float xg = (getPixel(outPixels, width, height, col, row + 1) -
                        getPixel(outPixels, width, height, col, row) +
                        getPixel(outPixels, width, height, col + 1, row + 1) -
                        getPixel(outPixels, width, height, col + 1, row)) / 2.0f;
                float yg = (getPixel(outPixels, width, height, col, row) -
                        getPixel(outPixels, width, height, col + 1, row) +
                        getPixel(outPixels, width, height, col, row + 1) -
                        getPixel(outPixels, width, height, col + 1, row + 1)) / 2.0f;

                // 计算振幅与角度
                data[index] = hypot(xg, yg);

                if (xg == 0) {
                    if (yg > 0) {
                        magnitudes[index] = 90;
                    }
                    if (yg < 0) {
                        magnitudes[index] = -90;
                    }
                } else if (yg == 0) {
                    magnitudes[index] = 0;
                } else {
                    magnitudes[index] = (float) ((Math.atan(yg / xg) * 180) / Math.PI);
                }
                // make it 0 ~ 180
                magnitudes[index] += 90;
            }
        }

        // 非最大信号压制算法 3x3
        Arrays.fill(magnitudes, 0);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                float angle = magnitudes[index];
                float m0 = data[index];
                magnitudes[index] = m0;

                if (angle >= 0 && angle < 22.5) {// angle 0
                    float m1 = getPixel(data, width, height, col - 1, row);
                    float m2 = getPixel(data, width, height, col + 1, row);
                    if (m0 < m1 || m0 < m2) {
                        magnitudes[index] = 0;
                    }
                } else if (angle >= 22.5 && angle < 67.5) {// angle +45
                    float m1 = getPixel(data, width, height, col + 1, row - 1);
                    float m2 = getPixel(data, width, height, col - 1, row + 1);
                    if (m0 < m1 || m0 < m2) {
                        magnitudes[index] = 0;
                    }
                } else if (angle >= 67.5 && angle < 112.5) {// angle 90
                    float m1 = getPixel(data, width, height, col, row + 1);
                    float m2 = getPixel(data, width, height, col, row - 1);
                    if (m0 < m1 || m0 < m2) {
                        magnitudes[index] = 0;
                    }
                } else if (angle >= 112.5 && angle < 157.5) { // angle 135 / -45
                    float m1 = getPixel(data, width, height, col - 1, row - 1);
                    float m2 = getPixel(data, width, height, col + 1, row + 1);
                    if (m0 < m1 || m0 < m2) {
                        magnitudes[index] = 0;
                    }
                } else if (angle >= 157.5) { // angle 0
                    float m1 = getPixel(data, width, height, col, row + 1);
                    float m2 = getPixel(data, width, height, col, row - 1);
                    if (m0 < m1 || m0 < m2) {
                        magnitudes[index] = 0;
                    }
                }
            }
        }

        // 寻找最大与最小值
        float min = 255;
        float max = 0;

        for (float magnitude : magnitudes) {
            if (magnitude == 0) continue;
            min = Math.min(min, magnitude);
            max = Math.max(max, magnitude);
        }
        System.out.println("Image Max Gradient = " + max + " Mix Gradient = " + min);

        // 通常比值为 TL : TH = 1 : 3， 根据两个阈值完成二值化边缘连接
        // 边缘连接-link edges
        Arrays.fill(data, 0);

        int offset = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (magnitudes[offset] >= highThreshold && data[offset] == 0) {
                    edgeLink(col, row, offset, lowThreshold);
                }
                offset++;
            }
        }

        // 二值化显示
        for (int i = 0; i < inPixels.length; i++) {
            int gray = clamp((int) data[i]);
            outPixels[i] = gray > 0 ? -1 : 0xff000000;
        }

        PixelWriter pixel = outImage.getPixelWriter();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixel.setArgb(j, i, outPixels[i * width + j]);
            }
        }
        return outImage;
    }

    private int clamp(int value) {
        return value > 255 ? 255 :
                (value < 0 ? 0 : value);
    }

    private void edgeLink(int x1, int y1, int index, float threshold) {
        int x0 = (x1 == 0) ? x1 : x1 - 1;
        int x2 = (x1 == width - 1) ? x1 : x1 + 1;
        int y0 = y1 == 0 ? y1 : y1 - 1;
        int y2 = y1 == height - 1 ? y1 : y1 + 1;

        data[index] = magnitudes[index];
        for (int x = x0; x <= x2; x++) {
            for (int y = y0; y <= y2; y++) {
                int i2 = x + y * width;
                if ((y != y1 || x != x1) && data[i2] == 0 && magnitudes[i2] >= threshold) {
                    edgeLink(x, y, i2, threshold);
                    return;
                }
            }
        }
    }

    private float getPixel(float[] input, int width, int height, int col, int row) {
        if (col < 0 || col >= width) {
            col = 0;
        }

        if (row < 0 || row >= height) {
            row = 0;
        }

        int index = row * width + col;
        return input[index];
    }

    private float hypot(float x, float y) {
        return (float) Math.hypot(x, y);
    }

    private int getPixel(int[] inPixels, int width, int height, int col, int row) {
        if (col < 0 || col >= width) {
            col = 0;
        }

        if (row < 0 || row >= height) {
            row = 0;
        }

        return inPixels[row * width + col];
    }

    private float gaussian(float x, float y, float sigma) {
        float xDistance = x * x;
        float yDistance = y * y;
        float sigma22 = 2 * sigma * sigma;
        float sigma22PI = (float) Math.PI * sigma22;
        return (float) Math.exp(-(xDistance + yDistance) / sigma22) / sigma22PI;
    }

    /**
     * read pixes of image
     * @param image
     * @param width
     * @param height
     * @param inPixel
     */
    private void getRGB(WritableImage image, int width, int height, int[] inPixel) {
        PixelReader reader = image.getPixelReader();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                inPixel[i * width + j] = reader.getArgb(j, i);
            }
        }
    }

    /**
     * convert color image pixels to gray
     * @param inPixels
     */
    private void toGray(int[] inPixels) {
        int index = 0;
        int a,r,g,b;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;

                a = (inPixels[index] >> 24) & 0xff;
                r = (inPixels[index] >> 16) & 0xff;
                g = (inPixels[index] >> 8) & 0xff;
                b = inPixels[index] & 0xff;

                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                inPixels[index] = (a << 24) | (gray << 16) | (gray << 8) | gray;
            }
        }
    }
}
