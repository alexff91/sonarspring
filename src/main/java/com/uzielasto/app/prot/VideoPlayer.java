package com.uzielasto.app.prot; /**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 24.11.12
 * Time: 18:39
 * All rights recieved.(c)
 */

import com.uzielasto.app.VideoImage;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class VideoPlayer {
    JButton start;
    JButton stop;
    JSlider slider;
    public static void main(String[] args) {
        final String sourceUrl = "test.mp4";

        final VideoPlayer videoPlayer = new VideoPlayer();

                videoPlayer.play(sourceUrl);


    }

    public void play(String sourceUrl) {


        final IMediaReader reader = ToolFactory.makeReader(sourceUrl);
        reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        final MyVideoComponent mediaPlayerComponent = new MyVideoComponent("UzI");



                JFrame aFrame = new JFrame();
                aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JPanel c = new JPanel();
                aFrame.setPreferredSize(new Dimension(800,800));


                GridBagConstraints gbc = new GridBagConstraints();

                JPanel bottomPanel = new JPanel();
                bottomPanel.setOpaque(true);
                aFrame.getContentPane().add(mediaPlayerComponent);


                start = new JButton("Start");

                c.add(start);
                aFrame.add(c);
                stop = new JButton("Stop");
                stop.setSize(50, 50);
                c.add(stop);
                aFrame.pack();
                aFrame.setLocationRelativeTo(null);
                aFrame.setVisible(true);





         MediaListenerAdapter adapter = new MediaListenerAdapter() {
            @Override
            public void onVideoPicture(IVideoPictureEvent event) {
                mediaPlayerComponent.setImage((BufferedImage) event.getImage());
            }
        };
        reader.addListener(adapter);



        while (reader.readPacket() == null)
            do {
            } while (false);


    }

    private class MyVideoComponent extends JPanel {
        private Image image;

        public MyVideoComponent(String s){
            this.setName(s);

        }

        public void setImage( Image image) {
            this.setSize(image.getWidth(null)*2,image.getHeight(null));
                    MyVideoComponent.this.image = image;
                    repaint();

        }

        @Override
                 public synchronized void paint(Graphics g) {
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            }
        }
    }
    private static void createAndShowGui() throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        JFrame frame = new JFrame("RectangleDraw");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new VideoImage());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}