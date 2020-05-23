package dbm.opencv.utils.test;

import dbm.opencv.utils.OpenCVFilters;
import dbm.opencv.utils.OpenCVImage;
import dbm.opencv.utils.camera.JCamera;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class OpenCVUtilsTest extends JFrame {

    BufferedImage image;
    //BufferedImage buffer;
    Image sz;
    JCamera camera;

    public OpenCVUtilsTest() {
        super("OpenCV Utilities Test");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        camera = new JCamera(1);

        camera.setFrameOverlayProcessor((OpenCVImage img)->{
            img = img.flip(OpenCVImage.FLIP_HORIZONTAL);
            OpenCVImage newImage = img.resize(img.getWidth() / 2, img.getHeight() / 2);
            //img = img.grayscale();
            img = img.magenta();
            //img.setCascadeClassifier(OpenCVImage.HAAR_CASCADE_CLASSIFIER);
            OpenCVImage.Face[] faces = newImage.detectFaces();
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (OpenCVImage.Face f: faces) {
                Rectangle face = f.getCoords();
                String sDesc = "Face";
                if (f.isSmiling()) {
                    g.setColor(new Color(0, 255, 0));
                    sDesc = "Smiling face";
                }
                else {
                    g.setColor(new Color(255, 0, 0));
                }
                //g.drawRect(face.x * 2, face.y * 2, face.width * 2, face.height * 2);
                int x = (face.x * 2) + face.width;
                int y = (face.y * 2) + face.height;

                g.drawRect(x - 100, y - 100, 200, 200);
                g.drawLine(x - 100, y - 100, x + 100, y + 100);
                //Rectangle smile = f.getSmile();
                //if (smile != null) {
                //    g.drawRect((smile.x - (smile.width / 2)) + x, (smile.y - (smile.height / 2)) + y, smile.width * 2, smile.height * 2);
                //}
                //g.drawString("Face", face.x * 2, (face.y + face.height) * 2);
                g.drawString(sDesc, x - 100, y + 100);
            } // */
            //return newImage.resize(img.getWidth(), img.getHeight());
            return img;
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(camera, BorderLayout.CENTER); // */


        /*
        //image = new OpenCVImage(Imgcodecs.imread("C:\\Users\\dbmur_000\\Desktop\\Pics\\IMG-8555.JPG"));
        /*try {
            image = ImageIO.read(new File("C:\\Users\\dbmur_000\\Desktop\\Pics\\IMG-8559.JPG"));
        }
        catch (IOException ioex) {
            ioex.printStackTrace(System.err);
        }
        image = new OpenCVImage(Imgcodecs.imread("C:\\Users\\dbmur_000\\Desktop\\Pics\\IMG-8555.JPG"));
        System.out.println(System.currentTimeMillis() - lStart);
        //image = OpenCVFilters.grayscale(image.getMat());
        //image = OpenCVFilters.negative(image.getMat());
        //try {
            //image = new OpenCVImage(ImageIO.read(new File("C:\\Users\\dbmur_000\\Desktop\\Pics\\IMG-8558.JPG")));
            //image = new OpenCVImage(Imgcodecs.imread("C:\\Users\\dbmur_000\\Desktop\\Pics\\IMG-8558.JPG"));
            //image.setCascadeClassifier(OpenCVImage.HAAR_CASCADE_CLASSIFIER);
        //}
        //catch (IOException ioex) {
        //    ioex.printStackTrace(System.err);
        //}
        //buffer = image.getBufferedImage();
        //image.resize(800, 600);
        if (image instanceof OpenCVImage) {
            sz = ((OpenCVImage) image).resize(800, 600);
            ((OpenCVImage)sz).detectFaces();
        }
        else {
            sz = image.getScaledInstance(800, 600, Image.SCALE_SMOOTH);
        }
        //image.detectFaces(); // */

        setSize(800, 800);
        setVisible(true);
    }

    public void paint(Graphics gr) {
        Graphics2D g = (Graphics2D)gr;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        OpenCVImage.Face[] faces = null;
        if (sz instanceof OpenCVImage) {
            faces = ((OpenCVImage)sz).detectFaces();
        }

        //g.drawImage(sz, 0, 0, null);
        //g.drawImage(buffer, 0, 0, null);
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(0, 0, 255));
        if (faces != null) {
            for (OpenCVImage.Face f: faces) {
                Rectangle face = f.getCoords();
                Graphics gs = sz.getGraphics();
                gs.setColor(new Color(0, 255, 0));
                gs.drawRect(face.x, face.y, face.width, face.height);
            //g.setColor(new Color(0, 0, 255, 32));
            //g.fillArc(face.x - (face.width / 2), face.y - (face.y / 2), face.width * 2, face.height * 2, 0, 360);
            //for (int j = 24; j > 0; j -= 4) {
                //g.setStroke(new BasicStroke(2));
                //g.setColor(new Color(0, 0, 255, 128));
                //g.drawArc(face.x, face.y, face.width, face.height, 0, 360);
            }
        }
        g.drawImage(sz, 0, 0, null);
    }

    public static void main(String[] args) {
        new OpenCVUtilsTest();
    }
}
