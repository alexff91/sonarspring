package com.uzielasto.app.prot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 29.11.12
 * Time: 1:43
 * All rights recieved.(c)
 */
public class Filer extends JComponent {
    public static JFileChooser fileChooser= new JFileChooser();
    JTextArea textArea = new JTextArea();
    public Filer(){
        this.add(textArea);
        this.add(fileChooser);
        fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               OpenActionPerformed(e);
            }
        });
    }
    private void OpenActionPerformed(java.awt.event.ActionEvent evt) {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // What to do with the file, e.g. display it in a TextArea
                textArea.read( new FileReader( file.getAbsolutePath() ), null );
            } catch (IOException ex) {
                System.out.println("problem accessing file"+file.getAbsolutePath());
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }
}
