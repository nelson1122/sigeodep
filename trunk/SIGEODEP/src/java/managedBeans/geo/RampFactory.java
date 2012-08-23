/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo;

/**
 *
 * @author and
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class RampFactory {

    public RampFactory() {
    }

    public String createRamp(String max, String path){
        try {
            int width = 200, height = 50;
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D ig2 = bi.createGraphics();


            Font font = new Font("TimesRoman", Font.BOLD, 15);
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();
            ColorRamp ramp = new ColorRamp();
            for (int i = 0; i < 150; i++) {
                ig2.setPaint(new Color(ramp.getRampedValueRGB((double)i/(double)150)));
                ig2.drawRect(i + 25, 10, 1, 30);
            }
            ig2.setPaint(Color.black);
            ig2.drawString("0", 27, 30);
            int maxWidth = fontMetrics.stringWidth(max);
            ig2.drawString(max, 175 - maxWidth, 30);
            String relative_path = "resources/legends/l" + max + ".png";
            ImageIO.write(bi, "PNG", new File(path + relative_path));
            return relative_path;
        } catch (IOException ex) {
            Logger.getLogger(RampFactory.class.getName()).log(Level.SEVERE, null, ex);
            return "resources/legends/l100.png";
        }
        
    }

    static public void main(String args[]){
        String text = "3+6";
        String[] split = text.split("+");
        System.out.println(split[0]);
    }
}