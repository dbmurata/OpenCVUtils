package dbm.opencv.utils.test;

import dbm.opencv.utils.camera.JCamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class OpenCVScannerTest extends JFrame {

    private JCamera camera;
    private ImageDisplay display;

    public OpenCVScannerTest() {
        super("Scanner");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        camera = new JCamera(2);
        camera.setOrientation(JCamera.FLIP_HORIZONTAL);
        display = new ImageDisplay();

        getContentPane().setLayout(new BorderLayout());
        JPanel panImages = new JPanel();
        panImages.setLayout(new GridLayout(1, 2));
        JButton snapshot = new JButton("Snapshot");

        snapshot.addActionListener((ActionEvent ae)->{
            display.image = camera.getSnapshot();
        });

        panImages.add(camera);
        panImages.add(display);

        getContentPane().add(panImages, BorderLayout.CENTER);
        getContentPane().add(snapshot, BorderLayout.SOUTH);

        setSize(1024, 768);
        setVisible(true);
    }

    protected class ImageDisplay extends JComponent implements Runnable {

        public BufferedImage image;
        private Thread runner;

        public ImageDisplay() {
            runner = new Thread(this);
            runner.start();
        }

        @Override
        public void run() {
            while (true) {
                repaint();
                try { Thread.sleep(33); } catch (InterruptedException e) {}
            }
        }

        @Override
        public void paint(Graphics gr) {
            Graphics2D g = (Graphics2D)gr;

            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }

    public static void main(String[] args) {
        new OpenCVScannerTest();
    }
}
