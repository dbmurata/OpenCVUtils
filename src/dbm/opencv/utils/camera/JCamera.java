package dbm.opencv.utils.camera;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;

import dbm.opencv.utils.OpenCVImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class JCamera extends JComponent implements Runnable
{
    private static final long serialVersionUID = 7559934792568936688L;

    public static final int FLIP_NONE = 0;
    public static final int FLIP_HORIZONTAL = 1;
    public static final int FLIP_VERTICAL = 2;

    private VideoCapture capture;
    private Thread runner;
    private OpenCVImage frame = null;
    private BufferedImage buffer = null;
    private FrameOverlayProcessor overlayProcessor = null;
    private boolean bRunning = true;
    private Object paintlock = new Object();
    private int orientation = 0;

    public JCamera()
    {
        this(0);
    }

    public JCamera(int index)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        capture = new VideoCapture(index);

        Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    JCamera.this.stop();
                }
            });

        runner = new Thread(this);
        // This thread is NOT a daemon.  We want to make sure
        // that the shutdown hook runs.  That allows the thread
        // to end gracefully and release the camera.
        runner.setDaemon(false);
        runner.start();
    }

    public void setFrameOverlayProcessor(FrameOverlayProcessor overlayProcessor)
    {
        this.overlayProcessor = overlayProcessor;
    }

    public FrameOverlayProcessor getFrameOverlayProcessor()
    {
        return overlayProcessor;
    }

    public void run()
    {
        long lStart, lDiff;

        Mat matrix = new Mat();

        while (bRunning)
        {
            lStart = System.currentTimeMillis();
            if (capture.isOpened())
            {
                // Synchronize with the paint() function.
                // Don't paint the matrix while the camera
                // is capturing the new image.
                synchronized(paintlock) {
                    if (capture.read(matrix)) {
                        // If there's no frame, then set the frame
                        // to an OpenCVImage with the matrix as the
                        // backing data.  The camera is always
                        // going to have the same width and height and
                        // the same color depth, so all I need to do
                        // is update the backing matrix and the image
                        // will update accordingly.
                        if (frame == null) {
                            frame = new OpenCVImage(matrix);
                            buffer = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                        }
                        Graphics g = buffer.getGraphics();
                        int x, y, w, h;
                        x = y = 0;
                        w = frame.getWidth();
                        h = frame.getHeight();
                        if ((orientation & FLIP_HORIZONTAL) == FLIP_HORIZONTAL) {
                            x = w;
                            w *= -1;
                        }
                        if ((orientation & FLIP_VERTICAL) == FLIP_VERTICAL) {
                            y = h;
                            h *= -1;
                        }
                        if (overlayProcessor != null) {
                            OpenCVImage tmp = overlayProcessor.processFrame(frame);
                            //g.drawImage(tmp, 0, 0, null);
                            g.drawImage(tmp, x, y, w, h, null);
                        }
                        else {
                            //g.drawImage(frame, 0, 0, null);
                            g.drawImage(frame, x, y, w, h, null);
                        }
                        g.dispose();
                    }
                }
            }
            repaint();
            lDiff = System.currentTimeMillis() - lStart;
            if (lDiff < 33)
                try { Thread.sleep(33 - lDiff); } catch (InterruptedException inex) { }
        }

        capture.release();
    }

    public void setOrientation(int flags) {
        // TODO: This should actually be removed and turned into a FrameOverlayProcessor.  Something that could be extended so mirroring is done correctly.
        orientation = flags;
    }

    public void stop()
    {
        bRunning = false;

        while (capture.isOpened())
            try { Thread.sleep(33); } catch (InterruptedException inex) {}
    }

    public BufferedImage getSnapshot() {
        BufferedImage copy = null;
        synchronized(paintlock) {
            if (buffer != null) {
                copy = new BufferedImage(buffer.getWidth(), buffer.getHeight(), buffer.getType());
                Graphics2D g = copy.createGraphics();
                g.drawImage(buffer, 0, 0, null);
            }
        }
        return copy;
    }

    public void paint(Graphics gr)
    {
        Graphics2D g = (Graphics2D)gr;

        if (buffer != null) {
            synchronized(paintlock) {
                g.drawImage(buffer, 0, 0, null);
            }
        }
    }
}
