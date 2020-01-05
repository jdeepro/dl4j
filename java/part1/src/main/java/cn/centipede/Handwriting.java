package cn.centipede;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cn.centipede.model.cnn.CNN;
import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.geom.AffineTransform;

class Handwriting {
    private Graphics graphics;
    private BufferedImage image;
    private int lastX = 0;
    private int lastY = 0;
    private JFrame frame = new JFrame("手写识别Demo");
    private Thread hClearThread;

    private JPanel panel = new JPanel() {
        private static final long serialVersionUID = 1L;

        @Override
        public void paint(Graphics g) {
            g.drawImage(image, 0, 0, null);
        }
    };

    private MouseAdapter mouse = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();

            if (hClearThread != null) {
                hClearThread.interrupt();
                hClearThread = null;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (hClearThread == null) {
                hClearThread = new Thread(()->{try{Thread.sleep(5 * 1000);recognize();}catch (InterruptedException e1) {}});
                hClearThread.start();
            }
        }
    };

    private MouseMotionAdapter mouseMotion = new MouseMotionAdapter() {
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            graphics.drawLine(lastX, lastY, x, y);
            lastX = x;
            lastY = y;
            panel.repaint();
        }
    };

    public Handwriting() {
        image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        graphics = image.getGraphics();

        frame.setSize(300, 300);
        frame.setForeground(Color.BLUE);

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.addMouseMotionListener(mouseMotion);
        panel.addMouseListener(mouse);

        panel.setPreferredSize(new Dimension(300, 300));
        ((Graphics2D) graphics).setStroke(new BasicStroke(16.0f));

        graphics.clearRect(0, 0, 300, 300);
        graphics.setColor(new Color(255, 0, 0));

        frame.pack();
        frame.setVisible(true);
    }

	private BufferedImage resizeBufferedImage(BufferedImage source, boolean flag) {
		int type = source.getType();
        BufferedImage target = null;
        int targetW = 28;
        int targetH = 28;

		double sx = (double) targetW / source.getWidth();
        double sy = (double) targetH / source.getHeight();

		if (flag && sx > sy) {
			sx = sy;
			targetW = (int) (sx * source.getWidth());
		} else if(flag && sx <= sy){
			sy = sx;
			targetH = (int) (sy * source.getHeight());
		}

		target = new BufferedImage(targetW, targetH, type);

		Graphics2D g = target.createGraphics();
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}

    private void recognize() {
        BufferedImage hw = resizeBufferedImage(image, false);
        graphics.clearRect(0, 0, 300, 300);
        panel.repaint();

        int[] dat = new int[28*28];
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                int clr = hw.getRGB(i, j);
                int red   = (clr & 0x00ff0000) >> 17;
                // int green = (clr & 0x0000ff00) >> 8;
                // int blue  =  clr & 0x000000ff;
                if (red > 0) dat[i+j*28] = red+random.nextInt(16);//(red+green+blue)/3;
            }
        }

        NDArray a = np.array(dat, 28, 28);
        a.dump();

        CNN cnn = new CNN();
        cnn.loadNpz();
        int actual = cnn.predict(a.reshape(28,28,1));
        System.out.println("You write:" + actual);
    }

    public static void main(String[] args) {
        new Thread(()->new Handwriting()).start();
    }

}
