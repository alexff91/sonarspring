package com.uzielasto.app.prot;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 26.11.12
 * Time: 13:01
 * All rights recieved.(c)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class RectangleDraw extends JPanel {
    public static int x;
    public static int y;
    public static int width;
    public static int height;
    public static int sumxy;
    public static int sumwh;
    private static final Color DRAWING_RECT_COLOR = new Color(200, 200, 255);
    private static final Color DRAWN_RECT_COLOR = Color.MAGENTA;

    public BufferedImage image;
    private Rectangle rect = null;
    private boolean drawing = false;

    public RectangleDraw(BufferedImage img) {
        try {
            image = img;
            MyMouseAdapter mouseAdapter = new MyMouseAdapter();
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            return new Dimension(image.getWidth(), image.getHeight());
        }
        return super.getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
        if (rect == null) {
            return;
        } else if (drawing) {
            g2.setColor(DRAWING_RECT_COLOR);
            g2.draw(rect);
        } else {
            g2.setColor(DRAWN_RECT_COLOR);
            sumxy = x + y;
            sumwh = width+height;
                    g2.draw(rect);
        }
    }

    private class MyMouseAdapter extends MouseAdapter {
        private Point mousePress = null;

        @Override
        public void mousePressed(MouseEvent e) {
           if(e.getButton()==1) mousePress = e.getPoint();
            else {rect = null; repaint();}
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            drawing = true;
            x = Math.min(mousePress.x, e.getPoint().x);
            y = Math.min(mousePress.y, e.getPoint().y);
            width = Math.abs(mousePress.x - e.getPoint().x);
            height = Math.abs(mousePress.y - e.getPoint().y);

            rect = new Rectangle(x, y, width, height);
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            drawing = false;
            repaint();
        }

    }

//    private static void createAndShowGui() {
//        JFrame frame = new JFrame("RectangleDraw");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        //frame.getContentPane().add(new RectangleDraw());
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                createAndShowGui();
//            }
//        });
//    }
}