package cn.edu.njupt.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Test {

    static void saveResult(String srcImg, String prefix, RenderedImage image) throws IOException {
        String[] paths = srcImg.split("/|\\.");
        String dst = paths[paths.length-2] + prefix + "_out.png";
        ImageIO.write(image, "png", new File(dst));
    }

    static void processByCSDN(String srcImg) throws IOException {
        URL realPath = Test.class.getClassLoader().getResource(srcImg);
        CannyFilterCSDN canny = new CannyFilterCSDN();
        WritableImage out = canny.process(realPath.toString());
        saveResult(srcImg, "_csdn_", SwingFXUtils.fromFXImage(out, null));
    }

    static int[] visitHor(BufferedImage data) {
        int w = data.getWidth();
        int h = data.getHeight();

        int[] cache = new int[w];

        for (int c=0; c<w; c++) {
            cache[c] = 0;
            for (int r=0; r<h; r++) {
                if (data.getRGB(c,r) == -1) {
                    cache[c]++;
                }
            }
        }

        int[] split = new int[10]; // 最大9个输入
        int index=0;

        for (int c=0; c<w; c++) {
            int lost = 0;

            while (c<w && cache[c]==0) {
                lost++;
                c++;
            }

            if (lost > 10) {
                split[index++] = c-lost/2; //中间切开
            }

            while (c<w && cache[c] != 0) {
                c++;
            }
        }

        int[] ret = Arrays.copyOf(split, index);
        for (int n:ret) {
            System.out.printf("n=%d", n);
        }
        return ret;
    }

    static int[][] visitVer(int[] hSplit, BufferedImage data) {
        int h = data.getHeight();
        int[][] ret = new int[hSplit.length][];

        for (int sp = 0; sp < hSplit.length-1; sp++) {
            int[] cache = new int[h];

            for (int r = 0; r < h; r++) {
                cache[r] = 0;
                for (int c = hSplit[sp]; c < hSplit[sp+1]; c++) {
                    if (data.getRGB(c, r) == -1) {
                        cache[r]++;
                    }
                }
            }

            int[] split = new int[3]; // 最大2个输入
            int index = 0;

            for (int r = 0; r < h; r++) {
                int lost = 0;

                while (r < h && cache[r] == 0) {
                    lost++;
                    r++;
                }

                if (r >=h ) {
                    break;
                }

                if (lost > 10) {
                    split[index++] = r - 10; //中间切开
                }

                while (r < h && cache[r] != 0) {
                    r++;
                }

                if (index >= 3) {
                    System.out.printf("index="+index);
                }

                split[index++] = r + 10;
            }

            split[index] = 0;

            ret[sp] = Arrays.copyOf(split, index);
            for (int n : ret[sp] ) {
                System.out.printf("n=%d", n);
            }
        }
        return ret;
    }

    static BufferedImage[] splitImage(BufferedImage data, BufferedImage src) {
        int[] hSplit = visitHor(data);
        int[][] split = visitVer(hSplit, data);

        BufferedImage[] subImages = new BufferedImage[hSplit.length-1];

        for (int i = 0; i < hSplit.length-1; i++) {
            subImages[i] = data.getSubimage(
                    hSplit[i],
                    split[i][0],
                hSplit[i+1]-hSplit[i],
                split[i][1]-split[i][0]);
        }

        return subImages;
    }

    static void swellPoint(BufferedImage dst, int x, int y) {
        final int R = 13;
        for (int i = 0; i < R; i++) {
            for (int j = 0; j < R; j++) {
                dst.setRGB(x-R/2+i, y-R/2+j, -1);
            }
        }
    }

    static void inflateEdge(BufferedImage dst, BufferedImage src) {
        int w = src.getWidth();
        int h =  src.getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int rgb = src.getRGB(i,j);
                dst.setRGB(i,j, src.getRGB(i,j));

                if (rgb == -1) {
                    swellPoint(dst, i, j);
                }

            }
        }

    }

    static void processByGitHub(String srcImg) throws IOException {
        String realPath = Test.class.getClassLoader().getResource(srcImg).getPath();
        File file = new File(realPath);
        BufferedImage photo = ImageIO.read(file);

        //create the detector
        CannyFilterGit detector = new CannyFilterGit();

        //adjust its parameters as desired
        detector.setLowThreshold(1.5f);
        detector.setHighThreshold(4.5f);

        //apply it to an image
        detector.setSourceImage(photo);
        detector.process();

//        BufferedImage edges = detector.getEdgesImage();
//        BufferedImage rgbImage = new BufferedImage(edges.getWidth(),edges.getHeight(), BufferedImage.TYPE_INT_RGB);
//
//        ColorConvertOp op = new ColorConvertOp(null);
//        op.filter(edges, rgbImage);
//        saveResult(srcImg, "_github_", rgbImage);

        BufferedImage edges = detector.getEdgesImage();
        BufferedImage[] subImages = splitImage(edges, photo);
        int h = edges.getHeight();

        for (int i = 0; i < subImages.length; i++) {
            //ImageIO.write(edges,"JPG",output);
            BufferedImage rgbImage = new BufferedImage(
                    subImages[i].getWidth(),
                    subImages[i].getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            ColorConvertOp op = new ColorConvertOp(null);

            //inflateEdge(rgbImage, subImages[i]);
            //invertColor(rgbImage);
            op.filter(subImages[i], rgbImage);
            saveResult(srcImg, "_github_"+i, rgbImage);
        }
    }

    private static void invertColor(BufferedImage rgbImage) {
        int w = rgbImage.getWidth();
        int h = rgbImage.getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = rgbImage.getRGB(i,j);
                if (color == -1) {
                    color = 0;
                } else {
                    color = -1;
                }
                rgbImage.setRGB(i, j, color);
            }
        }

    }

    static void cannyFilterCSDN() throws IOException {
        processByCSDN("Handwriting.png");
        processByCSDN("Handwriting2.png");
        processByCSDN("Lenna.png");
    }

    static void cannyFilterGitHub() throws IOException {
        //processByGitHub("Handwriting.png");
        processByGitHub("Handwriting2.png");
        //processByGitHub("Lenna.png");
    }

    public static void main(String[] args) throws IOException {
        //PlatformImpl.startup(()->{
        //    try {
        //        cannyFilterGitHub();
        //        //cannyFilterCSDN();
        //    } catch (IOException e) {
        //        e.printStackTrace();
        //    }
        //    System.exit(0);
        //});
        cannyFilterGitHub();
    }
}
