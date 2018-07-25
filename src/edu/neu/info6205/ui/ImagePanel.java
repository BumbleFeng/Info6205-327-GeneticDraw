/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.neu.info6205.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author BumbleBee
 */
public class ImagePanel extends JPanel {

    protected String path = "resources/mona_lisa.jpg";
    protected int weight;
    protected int height;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        File file = new File(path);
        try {
            BufferedImage image = ImageIO.read(file);
            Font f = new Font("dialog", Font.PLAIN, 18);
            g.setFont(f);
            g.drawString("Orignal", 10, 20);
            g.drawImage(image, 10, 30, this);
            weight = image.getWidth();
            height = image.getHeight();
            setPreferredSize(new Dimension(weight + 20, height + 40));
        } catch (IOException ex) {
            Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
