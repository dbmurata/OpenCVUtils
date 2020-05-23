package dbm.opencv.utils.test;

import dbm.opencv.utils.OpenCVImage;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class OpenCVTextTest extends JFrame {

    OpenCVImage image = new OpenCVImage(320, 480, OpenCVImage.TYPE_3BYTE_BGR);
    BufferedImage j = new BufferedImage(320, 480, BufferedImage.TYPE_3BYTE_BGR);

    JComponent canvas = new JComponent() {
        public void paint(Graphics gr) {
            Graphics2D g = (Graphics2D)gr;

            g.drawImage(image, 0, 0, null);
            g.drawImage(j, 320, 0, null);
        }
    };

    public OpenCVTextTest() {
        super("OpenCV Text Test");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Graphics2D g = image.createGraphics();
        drawImage(g);
        g.dispose();

        g = j.createGraphics();
        drawImage(g);
        g.dispose();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(canvas);

        setSize(800, 600);
        setVisible(true);
    }

    protected void drawImage(Graphics2D g) {
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font("Helvetica", Font.PLAIN, 36));
        g.setColor(new Color(0, 0, 0));
        g.drawString("Hello", 10, 50);
        Path2D path = new Path2D.Double();
        path.moveTo(10, 100);
        path.lineTo(110, 100);
        path.quadTo(60, 150, 10, 100);
        g.draw(path);
        g.drawLine(20, 150, 120, 150);
    }

    public static void main(String[] args) {
        new OpenCVTextTest();
    }
}
