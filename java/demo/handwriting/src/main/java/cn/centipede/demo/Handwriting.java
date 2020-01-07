package cn.centipede.demo;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import cn.centipede.model.cnn.CNN;
import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;

import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.awt.geom.AffineTransform;
import java.awt.BorderLayout;


class Handwriting {
    private static final int WIDTH  = 280;
    private static final int HEIGHT = 280;
    private static final int CHECK  = 64;
    private static final int PADING = 6;
    private static final int WAIT   = 2500;
    private static final float BOLD = 20;

    private static final String TIP = "请在黑板上写个数字吧";

    private CNN mnist = new CNN();
    private int[] record = new int[28*28];

    private JFrame frame = new JFrame("手写识别Demo");
    private JLabel status = new JLabel(TIP);

    private Graphics graphics;
    private BufferedImage image;
    private int lastX = 0;
    private int lastY = 0;
    private Thread hClearThread;    

    private JPanel panel = new JPanel() {
        private static final long serialVersionUID = 1L;
        @Override
        public void paint(Graphics g) {
            g.drawImage(image, 0, 0, null);
        }
    };

    private JPanel check = new JPanel() {
        private static final long serialVersionUID = 1L;
        private Font smallFont = new Font("courier new",Font.PLAIN, 7);

        @Override
        public void paint(Graphics g) {
            g.setFont(smallFont);
            String[] rows = record2Str();
            for (int i = 0; i < rows.length; i++) {
                g.drawString(rows[i], PADING, 8+i*10);
            }
        }

        private String[] record2Str() {
            String[] rows = new String[28];
            int[] row = new int[28];

            for (int i = 0; i < 28; i++) {
                System.arraycopy(record, i*28, row, 0, 28);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < 28; j++) {
                    sb.append(String.format("%-3d", record[i*28+j]));
                }
                rows[i] = sb.toString();
            }
            return rows;
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
                hClearThread = new Thread(this::waitOrInterupt);
                hClearThread.start();
            }
        }

        private void waitOrInterupt() {
            try{
                Thread.sleep(WAIT);
                recognize();
            }catch (InterruptedException e1) {}
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

    private void createGraphics() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        graphics = image.getGraphics();
        graphics.clearRect(0, 0, WIDTH, HEIGHT);
        graphics.setColor(new Color(250, 0, 0));

        Graphics2D g2d = ((Graphics2D) graphics);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(BOLD));
    }

    private void setMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu menuFile = new JMenu("File(F)");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem exit = new JMenuItem("Exit");
        menuFile.add(save);
        menuFile.add(exit);

        JMenu menuMnist = new JMenu("Train(T)");
        JMenuItem mnist = new JMenuItem("MNIST");
        JMenuItem evalu = new JMenuItem("Eval");
        menuMnist.add(mnist);
        menuMnist.add(evalu);

        menuBar.add(menuFile);
        menuBar.add(menuMnist);

        JMenu menuAbout = new JMenu("About");
        menuBar.add(menuAbout);
    }

    private void setHeader() {
        URL url = ClassLoader.getSystemClassLoader().getResource("jdeepro.png");
        ImageIcon icon = new ImageIcon(url);
        JButton banner = new JButton(icon);
        banner.setPreferredSize(new Dimension(icon.getIconWidth()-1, icon.getIconHeight()-1));
        frame.add(banner, BorderLayout.NORTH);
    }

    private void setFooter() {
        status.setPreferredSize(new Dimension(WIDTH, 24));
        frame.add(status, BorderLayout.SOUTH);
    }

    public Handwriting() {
        frame.setForeground(Color.BLUE);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        createGraphics();
        setMenuBar();
        setHeader();
        setFooter();

        JPanel container = new JPanel();
        frame.add(container);
        container.setBorder(new EmptyBorder(PADING,PADING,PADING,PADING));
        container.setLayout(new BorderLayout());

        panel.addMouseMotionListener(mouseMotion);
        panel.addMouseListener(mouse);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        container.add(panel, BorderLayout.WEST);

        check.setPreferredSize(new Dimension(WIDTH+CHECK, HEIGHT));
        container.add(check, BorderLayout.EAST);

        frame.pack();
        frame.setVisible(true);

        URL url = ClassLoader.getSystemClassLoader().getResource("mnist.npz");
        new Thread(()->mnist.loadNpz(url)).start();
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
        graphics.clearRect(0, 0, WIDTH, HEIGHT);
        panel.repaint();
        check.repaint();

        record = new int[28*28];

        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                int clr = hw.getRGB(i, j);
                int red   = (clr & 0x00ff0000) >> 16; // half red
                // int green = (clr & 0x0000ff00) >> 8;
                // int blue  =  clr & 0x000000ff;
                // int gray  = (red+green+blue)/3;
                if (red > 0) record[i+j*28] = red;
            }
        }

        NDArray a = np.array(record, 28, 28);
        a.dump();

        int predict = mnist.predict(a.reshape(28,28,1));
        String result = String.format("你刚写的是数字 %d 吧？", predict);

        SwingUtilities.invokeLater(()->status.setText(result));
    }
}
