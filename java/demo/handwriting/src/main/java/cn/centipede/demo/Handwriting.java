package cn.centipede.demo;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import cn.centipede.model.cnn.CNN;
import cn.centipede.model.data.MNIST;
import cn.centipede.numpy.NDArray;
import cn.centipede.numpy.Numpy.np;

import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.awt.geom.AffineTransform;
import java.awt.BorderLayout;

class Handwriting {
    private static final int WIDTH = 280;
    private static final int HEIGHT = 280;
    private static final int CHECK = 64;
    private static final int PADING = 6;
    private static final int WAIT = 2500;
    private static final float BOLD = 20;

    private static final int WRITE_MODE = 0;
    private static final int MNIST_MODE = 1;

    private static final String CMD_SAVE = "SAVE";
    private static final String CMD_EXIT = "EXIT";
    private static final String CMD_MNIST = "MNIST";
    private static final String CMD_EVALU = "EVALU";
    private static final String CMD_ABOUT = "ABOUT";

    private static final String TIP = "Write a number on the blackboard.";

    private CNN mnist = new CNN();
    private NDArray[] cache;
    private int[] record = new int[28 * 28];
    private int[] predict = new int[100]; // failed is true

    private JFrame frame = new JFrame("Handwriting Demo");
    private JLabel status = new JLabel(TIP);
    private JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 99, 0);

    private Font smallFont = new Font("courier new", Font.PLAIN, 7);
    private ImageIcon appIcon;

    private Graphics graphics;
    private BufferedImage image;
    private int lastX = 0;
    private int lastY = 0;
    private int board = 0; // 0: write mode, 1: mnist
    private Thread hClearThread;

    private JPanel panel = new JPanel() {
        private static final long serialVersionUID = 1L;

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(image, 0, 0, null);
        }
    };

    private JPanel check = new JPanel() {
        private static final long serialVersionUID = 1L;

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            drawHexCheck(g);
        }
    };

    private MouseAdapter mouse = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == panel) {
                onPanelClicked(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() == panel) {
                onPanelPressed(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getSource() == panel) {
                onPanelReleased(e);
            } else {
                onSliderReleased(e);
            }
        }
    };

    private MouseMotionAdapter mouseMotion = new MouseMotionAdapter() {
        public void mouseDragged(MouseEvent e) {
            if (board == MNIST_MODE) {
                return;
            }
            int x = e.getX();
            int y = e.getY();
            graphics.drawLine(lastX, lastY, x, y);

            lastX = x;
            lastY = y;
            panel.repaint();
        }
    };

    private void drawHexCheck(Graphics g) {
        g.setFont(smallFont);
        String[] rows = record2Str();
        for (int i = 0; i < rows.length; i++) {
            g.drawString(rows[i], PADING, 7 + i * 10);
        }
    }

    private void waitOrInterupt() {
        try {
            Thread.sleep(WAIT);
            recognize();
        } catch (InterruptedException e1) {
        }
    }

    protected void onPanelReleased(MouseEvent e) {
        if (hClearThread == null && board == WRITE_MODE) {
            hClearThread = new Thread(this::waitOrInterupt);
            hClearThread.start();
        }
    }

    protected void onPanelPressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();

        if (hClearThread != null) {
            hClearThread.interrupt();
            hClearThread = null;
        }
    }

    private void fillRecord(int index) {
        int curPage = slider.getValue();
        index = curPage * 100 + index;

        NDArray array = cache[0].row(index);
        int[] pixles = (int[]) np.getArrayData(array);

        System.arraycopy(pixles, 0, record, 0, 28 * 28);
        check.repaint();
    }

    protected void onPanelClicked(MouseEvent e) {
        if (board != MNIST_MODE || e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        if (e.getClickCount() == 2) {
            onPanelDbClicked(e);
        } else {
            fillRecord(e.getX() / 28 + e.getY() / 28 * 10);
        }
    }

    protected void onPanelDbClicked(MouseEvent e) {
        int x = e.getX();
        predict = new int[100];
        mnistNavPage(x >= WIDTH / 2);
    }

    protected void onSliderReleased(MouseEvent e) {
        int page = slider.getValue();
        loadMNIST(page, false);
    }

    private void mnistNavPage(boolean next) {
        int curPage = slider.getValue();
        if (next) {
            curPage++;
        } else {
            curPage--;
        }

        int max = cache[0].dimens()[0];
        if (curPage < 0)
            curPage = 0;
        if (curPage >= max)
            curPage = max - 1;

        drawMNIST(curPage);
    }

    private void loadMNIST(int page, boolean train) {
        status.setText("Loading minist...");
        new Thread(() -> {
            cache = MNIST.numpy(train);
            SwingUtilities.invokeLater(() -> {
                status.setText("load mnist done!");
                drawMNIST(page);
            });
        }).start();
    }

    private void drawCell(int r, int c, NDArray array) {
        int[] mnist = (int[]) np.getArrayData(array); // 28*28
        boolean isFailed = predict[r*10+c]>=10;
        for (int i = 0; i < 28 * 28; i++) { // pixel by pixel
            image.setRGB(c * 28 + i % 28, r * 28 + i / 28, isFailed?((mnist[i]!=0)?0xFF0000:0):mnist[i]);
        }
    }

    private void drawMNIST(int page) {
        int[][] range = { { page * 100, page * 100 + 100 } };
        NDArray dat = cache[0].get(range); // 100*28*28
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                drawCell(r, c, dat.get(r * 10 + c));
            }
        }
        panel.repaint();
        slider.setValue(page);
        status.setText(String.format("Page: %d/100", page + 1));
    }

    private String[] record2Str() {
        String[] rows = new String[28];
        int[] row = new int[28];

        for (int i = 0; i < 28; i++) {
            System.arraycopy(record, i * 28, row, 0, 28);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 28; j++) {
                sb.append(String.format("%-3x", record[i * 28 + j]));
            }
            rows[i] = sb.toString();
        }
        return rows;
    }

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
        JMenu menuFile = new JMenu("File");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem exit = new JMenuItem("Exit");
        menuFile.add(save).addActionListener(e -> onSave());
        menuFile.add(exit).addActionListener(e -> System.exit(0));

        JMenu menuMnist = new JMenu("Train");
        JMenuItem mnist = new JMenuItem("MNIST");
        JMenuItem evalu = new JMenuItem("Eval");
        menuMnist.add(mnist).addActionListener(e -> onMnist(e));
        menuMnist.add(evalu).addActionListener(e -> onEval());

        JMenu menuHelp = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        menuHelp.add(about).addActionListener(e -> onAbout());

        menuBar.add(menuFile);
        menuBar.add(menuMnist);
        menuBar.add(menuHelp);

        save.setActionCommand(CMD_SAVE);
        exit.setActionCommand(CMD_EXIT);
        mnist.setActionCommand(CMD_MNIST);
        evalu.setActionCommand(CMD_EVALU);
        about.setActionCommand(CMD_ABOUT);
    }

    private void setHeader() {
        URL url = ClassLoader.getSystemClassLoader().getResource("jdeepro.png");
        appIcon = new ImageIcon(url);
        JButton banner = new JButton(appIcon);
        banner.setFocusPainted(false);
        banner.setPreferredSize(new Dimension(appIcon.getIconWidth() - 1, appIcon.getIconHeight() - 1));
        frame.add(banner, BorderLayout.NORTH);
    }

    private void setFooter() {
        JPanel footer = new JPanel();
        status.setPreferredSize(new Dimension(WIDTH, 24));
        footer.setBorder(new EmptyBorder(PADING, PADING, PADING, PADING));
        footer.setLayout(new BorderLayout());
        footer.add(status, BorderLayout.WEST);
        footer.add(slider, BorderLayout.EAST);
        slider.setVisible(false);
        frame.add(footer, BorderLayout.SOUTH);

        slider.addChangeListener(e -> {
            slider.getValue();
        });
        slider.addMouseListener(mouse);
    }

    public Handwriting() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        createGraphics();
        setMenuBar();
        setHeader();
        setFooter();

        JPanel container = new JPanel();
        frame.add(container);
        container.setBorder(new EmptyBorder(PADING, PADING, PADING, PADING));
        container.setLayout(new BorderLayout());

        panel.addMouseMotionListener(mouseMotion);
        panel.addMouseListener(mouse);
        panel.setPreferredSize(new Dimension(WIDTH + PADING, HEIGHT + PADING));
        container.add(panel, BorderLayout.WEST);

        check.setPreferredSize(new Dimension(WIDTH + CHECK, HEIGHT));
        container.add(check, BorderLayout.EAST);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        URL url = ClassLoader.getSystemClassLoader().getResource("mnist.npz");
        new Thread(() -> mnist.loadNpz(url)).start();
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
        } else if (flag && sx <= sy) {
            sy = sx;
            targetH = (int) (sy * source.getHeight());
        }

        target = new BufferedImage(targetW, targetH, type);

        Graphics2D g = target.createGraphics();
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }

    private void udpateUI(String statusText) {
        graphics.clearRect(0, 0, WIDTH, HEIGHT);
        SwingUtilities.invokeLater(() -> {
            panel.repaint();
            check.repaint();
            status.setText(statusText);}
        );
    }

    private void recognize() {
        BufferedImage hw = resizeBufferedImage(image, false);

        if (record != null) {
            record = new int[28 * 28];
        }

        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                int clr = hw.getRGB(i, j);
                record[i + j * 28] = (clr & 0x00ff0000) >> 16;
            }
        }

        NDArray a = np.array(record, 28, 28).reshape(28, 28, 1);
        String result = String.format("Did you just write the number %d?", mnist.predict(a));
        udpateUI(result);
    }

    private void onSave() {
        try {
            String fileName = System.currentTimeMillis()+"_jdeepro.png";
            ImageIO.write(image, "png", new File(fileName));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
                e.getMessage(),
                "Save Failed!",
            JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onMnist(ActionEvent e) {
        JMenuItem mnist = (JMenuItem)e.getSource();
        boolean isMnistMode = (board==MNIST_MODE);

        slider.setVisible(!isMnistMode);

        if (isMnistMode) {
            mnist.setForeground(Color.BLACK);
            board = WRITE_MODE;
            udpateUI(TIP);
        } else {
            mnist.setForeground(Color.BLUE);
            board = MNIST_MODE;
            loadMNIST(0, false);
        }
    }

    private int[] evalCurPage(int page) {
        int[][] range = { { page * 100, page * 100 + 100 } };

        NDArray test = cache[0].get(range); // 100*28*28
        NDArray label = cache[1].get(range);
        int[] actual = (int[])np.getArrayData(label);

        for (int i = 0; i < actual.length; i++) {
            NDArray dat = test.get(i);
            int number = mnist.predict(dat.reshape(28,28,1));
            predict[i] = (number!=actual[i])?number+10:number;
        }
        return actual;
    }

    private String failedIDs(int[] actuals) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < predict.length; i++) {
            if (predict[i] >= 10) {
                sb.append(predict[i]-10).append(">").append(actuals[i]).append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private void onEval() {
        int page = slider.getValue();
        status.setText("Eval current page...");

        new Thread(() -> { int[] actuals = evalCurPage(page);
            SwingUtilities.invokeLater(() -> {
                drawMNIST(page);
                status.setText("Failed ID: " + failedIDs(actuals));
            });
        }).start();
    }

    private void onAbout() {
        JOptionPane.showMessageDialog(frame,
            "Thanks for giving a try!\nThis project is initiated by Yang.\nAnd the main programmer is simbaba.",
            "JDeepro",
            JOptionPane.INFORMATION_MESSAGE, appIcon);
    }
}
