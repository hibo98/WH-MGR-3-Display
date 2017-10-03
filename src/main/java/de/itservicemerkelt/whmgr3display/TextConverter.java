package de.itservicemerkelt.whmgr3display;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TextConverter {

    public static BufferedImage textToImage(String text, int size) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, size));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.clearRect(0, 0, img.getWidth(), img.getHeight());
        g2d.drawString(text, (img.getWidth() - fm.stringWidth(text)) / 2, 13);
        g2d.dispose();
        return img;
    }
}
