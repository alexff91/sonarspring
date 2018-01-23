package com.idas.app;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 10.11.12
 * Time: 18:14
 * All rights recieved.(c)
 */


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class VideoImage extends JFrame {
    //fields initialization
    private final ImageComponent mOnscreenPicture;
    public TabImageComponent tab1Picture;
    public TabImageComponent tab2Picture;
    public static JButton jstart = new JButton("Start");
    public static JButton jstop = new JButton("Stop");
    public static JButton exit = new JButton("Exit");
    public static JButton slower = new JButton("Slower");
    public static JButton faster = new JButton("Faster");
    public static JButton screen = new JButton("Make a screen");
    public static JButton filepath = new JButton("Select file..");
    public static JSlider slider = new JSlider(1, 30);
    public static JSlider pixelSlider = new JSlider(1, 10);
    public static JSlider octavesSlider = new JSlider(1, 10);
    public static JSlider treshSlider = new JSlider(0, 800);
    public static JSlider balanseSlider = new JSlider(1, 20);
    public static JButton selectDev = new JButton("Connect to device");
    public static JButton zoomIn = new JButton("Zoom+");
    public static JButton zoomOut = new JButton("Zoom-");
    public static BufferedImage myPicture = null;
    public static JTabbedPane tabbedPane;
    public static JPanel jp;
    public static int x;
    public static int y;
    public static int xTitle = 0;
    public static int yTitle = 0;
    public static int width;
    public static int height;
    public static int sumxy;
    public static int sumwh;
    public static int jpanel;
    public static int jpaneH;
    private static final Color DRAWING_RECT_COLOR = new Color(92, 122, 255);
    private static final Color DRAWN_RECT_COLOR = new Color(0, 255, 65);
    static JMenuBar menuBar;
    static JMenu menu, submenu;
    static JMenuItem menuItem;
    static JRadioButtonMenuItem rbMenuItem;
    static JRadioButtonMenuItem cbMenuItem;
    public static JPanel ip;
    public BufferedImage image;
    private Rectangle rect = null;
    private boolean drawing = false;
    public static JLabel picLabel;
    public boolean showStatTitle = false;
    public String title = "Difference of pixels";

    /**
     * Create the frame
     */

    public VideoImage() {
        super();

        super.setDefaultLookAndFeelDecorated(true);
        this.setTitle("IDAS");
        ImageIcon img = new ImageIcon("logo.gif");

        this.setIconImage(img.getImage());
        screen.setToolTipText("Сохранение текущего изображения в папку screens рабочего приложения");
        jstart.setToolTipText("Начать воспроизведение видео");
        exit.setToolTipText("Выйти");
        faster.setToolTipText("Увеличить скорость воспроизведения");
        slower.setToolTipText("Уменьшить скорость воспроизведения");
        pixelSlider.setToolTipText("Регулировать размер окрашиваемого пиксела");
        selectDev.setToolTipText("Выбрать устройство");
        slider.setToolTipText("Регулировать количество милисекунд между сохранением фреймов для сравнения");
        zoomIn.setToolTipText("Увеличить картинку");
        zoomOut.setToolTipText("Уменьшить картинку");
        jstop.setToolTipText("Остановить воспроизведение");
        octavesSlider.setToolTipText("Изменить октавы");
        treshSlider.setToolTipText("Порог");
        mOnscreenPicture = new ImageComponent();
        tab1Picture = new TabImageComponent();
        tab2Picture = new TabImageComponent();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        tabbedPane = new JTabbedPane();


        tabbedPane.addTab("Original image and processed 1", null, mOnscreenPicture,
                "Two images");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        JComponent panel2 = makeTextPanel("Only original #2");
        tabbedPane.addTab("Only original 2", null, panel2,
                "Without processing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        JComponent panel3 = makeTextPanel("Only processed #3");
        tabbedPane.addTab("With processing 3", null, panel3,
                "With processing");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
//        DSFilterInfo[][] dsi = DSCapture.queryDevices();
//        String[] devices = new String[dsi[0].length];
//        for (int i = 0; i < dsi[0].length-1; i ++) {
//            devices[i] = i+" "+dsi[0][i].getName();
//        }
//        listOfButtons = new JComboBox(devices);
//
//        listOfButtons.setSelectedIndex(dsi[0].length-1);
        //listOfButtons.addActionListener(this);
        // image = (BufferedImage)mOnscreenPicture.mImage;
        MyMouseAdapter mouseAdapter = new MyMouseAdapter();
        mOnscreenPicture.addMouseListener(mouseAdapter);
        mOnscreenPicture.addMouseMotionListener(mouseAdapter);

        JPanel buttonsPanel = new JPanel();

        buttonsPanel.setLayout(new BorderLayout());
        buttonsPanel.setBorder(BorderFactory.createTitledBorder(
                "Action menu"));
        ip = new JPanel();
        ip.setLayout(new FlowLayout());


        try {
            myPicture = ImageIO.read(new File("1027_fig4.png"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        picLabel = new JLabel(new ImageIcon());

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = ip.getWidth();
                int h = ip.getHeight();
                if (myPicture!= null && h > 0 && w > 0 && h != myPicture.getHeight()) {
                    Image scaledImage = myPicture.getScaledInstance((int) (w / 1.2), (int) (h / 1.1), 1);

                    BufferedImage img = new BufferedImage((int) (w / 1.2), (int) (h / 1.1), BufferedImage.TYPE_3BYTE_BGR);
                    img.getGraphics().drawImage(scaledImage, 0, 0, null);
                    picLabel.setIcon(new ImageIcon(img));
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void componentShown(ComponentEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        ip.add(picLabel);

        this.add(ip, BorderLayout.EAST);
        jp = new JPanel();
        // buttonsPanel.add(jp);
        jp.setLayout(new GridLayout(11, 1));
        jp.setBorder(BorderFactory.createTitledBorder(
                "Video player"));
        JPanel pmPanel = new JPanel();
        buttonsPanel.add(pmPanel);
        pmPanel.setLayout(new GridLayout(1, 4));
        pmPanel.setBorder(BorderFactory.createTitledBorder(
                "Point matching adjustments"));
        JPanel optionalPanel = new JPanel();
        //buttonsPanel.add(optionalPanel);
        optionalPanel.setLayout(new GridLayout(1, 4));
        optionalPanel.setBorder(BorderFactory.createTitledBorder(
                "Settings and actions"));

        // mOnscreenPicture.add(new JScrollPane(), BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);
        this.add(jp, BorderLayout.WEST);
        jp.add(zoomIn, BorderLayout.NORTH);
        jp.add(zoomOut);


        //jp.add(listOfButtons);
        jp.add(filepath);
        jp.add(jstart);
        jp.add(jstop);
        jp.add(slower);
        jp.add(faster);

        jp.add(screen);
        jp.add(selectDev, BorderLayout.LINE_END);
        //buttonsPanel.add(listOfButtons);
        JLabel diffL = new JLabel("\nDiff. in ms");
        diffL.setHorizontalAlignment(JLabel.CENTER);
        diffL.setVerticalAlignment(JLabel.CENTER);
        pmPanel.add(diffL);
        //Turn on labels at major tick marks.
        slider.setMajorTickSpacing(9);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        pmPanel.add(slider);
        treshSlider.setMajorTickSpacing(200);
        treshSlider.setMinorTickSpacing(10);
        treshSlider.setPaintTicks(true);
        treshSlider.setPaintLabels(true);
        octavesSlider.setMajorTickSpacing(1);
        octavesSlider.setMinorTickSpacing(5);
        octavesSlider.setPaintTicks(true);
        octavesSlider.setPaintLabels(true);
        JLabel octvesL = new JLabel("\nOctaves");
        octvesL.setHorizontalAlignment(JLabel.CENTER);
        octvesL.setVerticalAlignment(JLabel.CENTER);

        pmPanel.add(octvesL);
        pmPanel.add(octavesSlider);
        JLabel treshL = new JLabel("\nTreshhold");
        treshL.setVerticalAlignment(JLabel.CENTER);
        treshL.setHorizontalAlignment(JLabel.CENTER);
        pmPanel.add(treshL);
        pmPanel.add(treshSlider);
        JLabel sizeL = new JLabel("\nSize in pix");
        sizeL.setVerticalAlignment(JLabel.CENTER);
        sizeL.setHorizontalAlignment(JLabel.CENTER);
        pmPanel.add(sizeL);
        //Turn on labels at major tick marks.
        pixelSlider.setMajorTickSpacing(10);
        pixelSlider.setMinorTickSpacing(1);
        pixelSlider.setPaintTicks(true);
        pixelSlider.setPaintLabels(true);
        pmPanel.add(pixelSlider);
        jp.add(exit);


//Create the menu bar.
        menuBar = new JMenuBar();
        ButtonGroup buttonGroup = new ButtonGroup();
//Build the first menu.
        menu = new JMenu("A Settings");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "Меню настроек параметров отображения");
        menuBar.add(menu);

//a group of check box menu items
        JCheckBoxMenuItem checkBoxMenuItemPointMatching = new JCheckBoxMenuItem("Point matching");
        checkBoxMenuItemPointMatching.setMnemonic(KeyEvent.VK_P);
        checkBoxMenuItemPointMatching.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ImageAdjaster.points) {
                    ImageAdjaster.points = false;
                } else ImageAdjaster.points = true;
            }
        });
        menu.add(checkBoxMenuItemPointMatching);
        JRadioButtonMenuItem cbMenuItem1 = new JRadioButtonMenuItem("Histogram Equate");
        cbMenuItem1.setMnemonic(KeyEvent.VK_H);
        cbMenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ImageAdjaster.histogramEq) {
                    ImageAdjaster.quantoIm = false;
                    ImageAdjaster.histogramEq = true;
                    ImageAdjaster.invertFlag = false;
                    ImageAdjaster.none = false;
                }
            }
        });

        //menu.add(cbMenuItem);

        JRadioButtonMenuItem cbMenuItem2 = new JRadioButtonMenuItem("InvertColors");
        cbMenuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ImageAdjaster.invertFlag) {
                    ImageAdjaster.quantoIm = false;
                    ImageAdjaster.histogramEq = false;
                    ImageAdjaster.invertFlag = true;
                    ImageAdjaster.none = false;
                }

            }
        });
        cbMenuItem2.setMnemonic(KeyEvent.VK_I);
        //menu.add(cbMenuItem2);
        JRadioButtonMenuItem cbMenuItem3 = new JRadioButtonMenuItem("Quanto");
        cbMenuItem3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ImageAdjaster.quantoIm) {
                    ImageAdjaster.quantoIm = true;
                    ImageAdjaster.histogramEq = false;
                    ImageAdjaster.invertFlag = false;
                    ImageAdjaster.none = false;
                }
            }
        });
        cbMenuItem3.setMnemonic(KeyEvent.VK_Q);
        JRadioButtonMenuItem cbMenuItem4 = new JRadioButtonMenuItem("None");
        cbMenuItem4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ImageAdjaster.none) {
                    ImageAdjaster.quantoIm = false;
                    ImageAdjaster.histogramEq = false;
                    ImageAdjaster.invertFlag = false;
                    ImageAdjaster.none = true;
                }
            }
        });
        cbMenuItem4.setMnemonic(KeyEvent.VK_N);
        buttonGroup.add(cbMenuItem1);
        buttonGroup.add(cbMenuItem2);
        buttonGroup.add(cbMenuItem3);
        buttonGroup.add(cbMenuItem4);
        menu.add(cbMenuItem1);
        menu.add(cbMenuItem2);
        menu.add(cbMenuItem3);
        menu.add(cbMenuItem4);
//Build second menu in the menu bar.
        menu = new JMenu("About");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription(
                "About autor and programm");
        menuBar.add(menu);

        // this.add(mOnscreenPicture, BorderLayout.CENTER);
        // panel1.add(mOnscreenPicture);
        panel2.add(tab1Picture);
        panel3.add(tab2Picture);
        this.add(tabbedPane);
        this.setVisible(true);
        this.setJMenuBar(menuBar);
        jpanel = buttonsPanel.getHeight();
        jpaneH = buttonsPanel.getWidth();
        //this.pack();
    }

    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(true);

        panel.setLayout(new GridLayout(1, 1));

        return panel;
    }

    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            return new Dimension(image.getWidth(), image.getHeight());
        }
        return super.getPreferredSize();
    }

    private class MyMouseAdapter extends MouseAdapter {
        private Point mousePress = null;
        private Point mousePressTitle = null;
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1) {
                Point pos = VideoImage.this.jp.getLocationOnScreen();
                Point fPos = VideoImage.this.getLocationOnScreen();

                Point p = e.getPoint();
                Point npoint = new Point(p.x + (-pos.x + fPos.x), p.y + pos.y - fPos.y);
                mousePressTitle = p;
                showStatTitle = true;
                xTitle = Math.min(mousePressTitle.x, e.getPoint().x);
                yTitle = Math.min(mousePressTitle.y, e.getPoint().y);
                repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == 1) {
                Point pos = VideoImage.this.jp.getLocationOnScreen();
                Point fPos = VideoImage.this.getLocationOnScreen();

                Point p = e.getPoint();
                Point npoint = new Point(p.x + (-pos.x + fPos.x), p.y + pos.y - fPos.y);
                mousePress = p;
            } else {

                rect = null;
                showStatTitle = false;
                repaint();

            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            drawing = true;
            Point pos = mOnscreenPicture.getLocationOnScreen();
            Point fPos = VideoImage.this.getLocationOnScreen();
            Point p = e.getPoint();
            Point npoint = new Point(p.x + (-pos.x + fPos.x), p.y - pos.y + fPos.y);

            x = Math.min(mousePress.x, e.getPoint().x);
            y = Math.min(mousePress.y, e.getPoint().y);
            width = Math.abs(mousePress.x - e.getPoint().x);
            height = Math.abs(mousePress.y - e.getPoint().y);
//            x = Math.min(mousePress.x, npoint.x);
//            y = Math.min(mousePress.y, npoint.y);
//            width = Math.abs(mousePress.x - npoint.x);
//            height = Math.abs(mousePress.y - npoint.y);

            rect = new Rectangle(x, y, width, height);
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            drawing = false;
            repaint();
        }

    }

    public void setImage(final BufferedImage aImage) {
        mOnscreenPicture.setImage(aImage);
        //mOnscreenPicture.setMinimumSize(new Dimension(600,800));
    }

    public class TabImageComponent extends JComponent {
        private Image mImage;
        private Dimension mSize;

        public void setImage(Image image) {
            SwingUtilities.invokeLater(new ImageRunnable(image));
        }

        public void setImageSize(Dimension newSize) {
            TabImageComponent.this.setSize(newSize);
        }

        private class ImageRunnable implements Runnable {
            private final Image newImage;

            public ImageRunnable(Image newImage) {
                super();
                this.newImage = newImage;
            }

            public void run() {
                TabImageComponent.this.mImage = newImage;
                final Dimension newSize = new Dimension(mImage.getWidth(null),
                        mImage.getHeight(null));
                if (!newSize.equals(mSize)) {
                    TabImageComponent.this.mSize = newSize;
                    // VideoImage.this.setSize(mImage.getWidth(null), mImage.getHeight(null));
//                    VideoImage.this.setVisible(true);
                }
                repaint();
            }
        }

        public TabImageComponent() {

            mSize = new Dimension(0, 0);
            setSize(mSize);
        }

        public synchronized void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            if (mImage != null)
                g.drawImage(mImage, 0, 0, this);

        }
    }

    public class ImageComponent extends JComponent {

        private Image mImage;
        private Dimension mSize;

        public void setImage(Image image) {
            SwingUtilities.invokeLater(new ImageRunnable(image));
        }

        public void setImageSize(Dimension newSize) {
            ImageComponent.this.setSize(newSize);
        }

        private class ImageRunnable implements Runnable {
            private final Image newImage;

            public ImageRunnable(Image newImage) {
                super();
                this.newImage = newImage;
            }

            public void run() {
                ImageComponent.this.mImage = newImage;
                final Dimension newSize = new Dimension(mImage.getWidth(null),
                        mImage.getHeight(null));
                if (!newSize.equals(mSize)) {
                    ImageComponent.this.mSize = newSize;
                    // VideoImage.this.setSize(mImage.getWidth(null), mImage.getHeight(null));
//                    VideoImage.this.setVisible(true);
                }
                repaint();
            }
        }

        public ImageComponent() {

            mSize = new Dimension(0, 0);
            setSize(mSize);
        }

        public synchronized void paint(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;


            if (mImage != null)
                g2.drawImage(mImage, 0, 0, this);
            if (rect == null) {
                ;

            } else if (drawing) {
                g2.setColor(DRAWING_RECT_COLOR);

                g2.draw(rect);
            } else {
                g2.setColor(DRAWN_RECT_COLOR);
                float[] dashl = {5, 5};
                BasicStroke pen = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 7, dashl, 0);
                g2.setStroke(pen);
                sumxy = x + y;
                sumwh = width + height;
                g2.draw(rect);

              Arc2D.Double greenArc = new Arc2D.Double(x, y + height * 1 / 4, width,
                  height + height * 1 / 2, 0, 180,
                  Arc2D.OPEN);
              Arc2D.Double orangeArc = new Arc2D.Double(x, y + height * 2/ 4, width,
                  height , 0, 180,
                  Arc2D.OPEN);
              Arc2D.Double redArc = new Arc2D.Double(x, y + height * 3 / 4, width,
                  height-height * 1 / 2, 0, 180,
                  Arc2D.OPEN);
              g2.draw(greenArc);
              g2.setColor(Color.ORANGE);
              pen = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 7, dashl, 0);
              g2.setStroke(pen);

              g2.draw(orangeArc);
              g2.setColor(Color.RED);
              pen = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 7, dashl, 0);
              g2.setStroke(pen);

              g2.draw(redArc);

            }
            if (showStatTitle) {
                g.setColor(Color.ORANGE);
                g.drawString(title, xTitle, yTitle);
            }
        }

    }

    public int getXCoord() {
        return xTitle;
    }

    public static void setX(int x) {
        VideoImage.x = x;
    }

    public  int getYCoord() {
        return yTitle;
    }

    public static void setY(int y) {
        VideoImage.y = y;
    }
}
