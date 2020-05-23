package dbm.opencv.utils;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.*;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class OpenCVImage extends BufferedImage{

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static CascadeClassifier LPB_CASCADE_CLASSIFIER; // = new CascadeClassifier("C:\\opencv\\build\\etc\\lbpcascades\\lbpcascade_frontalface_improved.xml");
    public static CascadeClassifier HAAR_CASCADE_CLASSIFIER; // = new CascadeClassifier("C:\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_default.xml");

    public static CascadeClassifier HAAR_CASCADE_SMILE;
    public static CascadeClassifier HAAR_CASCADE_EYES;

    public static final int FLIP_HORIZONTAL = 1;
    public static final int FLIP_VERTICAL = 0;
    public static final int FLIP_BOTH = -1;

    private static Map<Integer, Integer> IMAGE_TYPE_MAP;
    private static Map<Integer, Integer> DATA_TYPE_MAP;
    private static Map<Integer, int[]> BAND_MASKS_MAP;

    private static Mat sepiaKernel = new Mat(3, 3, CvType.CV_32F);
    private static Mat grayscaleKernel = new Mat(1, 3, CvType.CV_32F);

    static {
        LPB_CASCADE_CLASSIFIER = new CascadeClassifier("C:\\opencv\\build\\etc\\lbpcascades\\lbpcascade_frontalface_improved.xml");
        HAAR_CASCADE_CLASSIFIER = new CascadeClassifier("C:\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_default.xml");

        HAAR_CASCADE_SMILE = new CascadeClassifier("C:\\opencv\\build\\etc\\haarcascades\\haarcascade_smile.xml");
        HAAR_CASCADE_EYES = new CascadeClassifier("C:\\opencv\\build\\etc\\haarcascades\\haarcascade_eye.xml");

        IMAGE_TYPE_MAP = new HashMap<Integer, Integer>();

        IMAGE_TYPE_MAP.put(BufferedImage.TYPE_BYTE_GRAY, CvType.CV_8U);
        IMAGE_TYPE_MAP.put(BufferedImage.TYPE_3BYTE_BGR, CvType.CV_8UC3);

        DATA_TYPE_MAP = new HashMap<Integer, Integer>();

        DATA_TYPE_MAP.put(CvType.CV_8U, DataBuffer.TYPE_BYTE);
        DATA_TYPE_MAP.put(CvType.CV_8UC3, DataBuffer.TYPE_INT);

        BAND_MASKS_MAP = new HashMap<Integer, int[]>();
        BAND_MASKS_MAP.put(CvType.CV_8U, new int[] {0x0});
        BAND_MASKS_MAP.put(CvType.CV_8UC3, new int[] {0xff, 0xff00, 0xff0000});

        float[] data = new float[9];
        data[0] = 0.272f; data[1] = 0.534f; data[2] = 0.131f;
        data[3] = 0.349f; data[4] = 0.686f; data[5] = 0.168f;
        data[6] = 0.393f; data[7] = 0.769f; data[8] = 0.189f;

        sepiaKernel.put(0, 0, data);

        data = new float[3];
        data[0] = 0.07f; data[1] = 0.72f; data[2] = 0.21f;

        grayscaleKernel.put(0, 0, data);
    }

    private CascadeClassifier classifier = LPB_CASCADE_CLASSIFIER;
    private Face[] faces;

    public OpenCVImage(Mat mat) {
        super(createColorModel(mat.type()), createPackedRaster(mat), false, new Hashtable<Object, Object>());
    }

    public OpenCVImage(int width, int height, int type) {
        super(createColorModel(IMAGE_TYPE_MAP.get(type)), createPackedRaster(new Mat(height, width, IMAGE_TYPE_MAP.get(type))), false, new Hashtable<Object, Object>());
    }

    private static ColorModel createColorModel(int type) {
        switch (type) {
            case CvType.CV_8U:
                // Grayscale Color Model
                return new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        }
        // BGR Color Model
        return new DirectColorModel(24, 0xff, 0xff00, 0xff0000, 0x00);
    }

    private static WritableRaster createPackedRaster(Mat mat) {
        MatDataBuffer dataBuffer = new MatDataBuffer(mat);
        int w = mat.width();
        int h = mat.height();

        SampleModel sampleModel;
        if (mat.type() == CvType.CV_8U)
            sampleModel = new PixelInterleavedSampleModel(dataBuffer.getDataType(), w, h, 1, w, BAND_MASKS_MAP.get(mat.type()));
        else
            sampleModel = new SinglePixelPackedSampleModel(dataBuffer.getDataType(), w, h, w, BAND_MASKS_MAP.get(mat.type()));

        return new MatRaster(sampleModel, dataBuffer);
    }

    private static class MatDataBuffer extends DataBuffer {

        private Mat data;
        int c, r;
        int idx;

        protected MatDataBuffer(Mat data) {
            super(DATA_TYPE_MAP.get(data.type()), data.width() * data.height());
            c = r = -1;

            this.data = data;
        }

        protected Mat getMat() {
            return data;
        }

        @Override
        public int getElem(int bank, int i) {
            c = i % data.width();
            r = i / data.width();
            idx = i;
            byte[] p = new byte[3];
            int x = data.get(r, c, p);
            int pixel;

            pixel = (((int)p[2]) & 0xff) | ((((int)p[1]) << 8) & 0xff00) | ((((int)p[0]) << 16) & 0xff0000);

            return pixel;
        }

        @Override
        public void setElem(int bank, int i, int val) {
            System.out.println("setElem(" + bank + ", " + i + ", " + val + ")");
        }
    }

    private static class MatRaster extends WritableRaster {

        public MatRaster(SampleModel sampleModel, MatDataBuffer dataBuffer) {
            super(sampleModel, dataBuffer, new Rectangle(0, 0, sampleModel.getWidth(), sampleModel.getHeight()), new Point(0, 0), null);
        }
    }

    public void setCascadeClassifier(CascadeClassifier classifier) {
        this.classifier = classifier;
    }

    private Rectangle[] detect(CascadeClassifier classifier) {
        return detect(getMat(), classifier);
    }

    private Rectangle[] detect(Mat mat, CascadeClassifier classifier) {
        MatOfRect faceDetections = new MatOfRect();
        //MatDataBuffer dataBuffer = (MatDataBuffer)(getRaster().getDataBuffer());
        Mat gray = new Mat();
        //Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
        Core.transform(mat, gray, grayscaleKernel);
        classifier.detectMultiScale(gray, faceDetections);
        Rectangle[] objects = new Rectangle[faceDetections.toArray().length];

        //faces = new Rectangle[faceDetections.toArray().length];
        for (int i = 0; i < objects.length; i++) {
            Rect face = faceDetections.toArray()[i];
            objects[i] = new Rectangle(face.x, face.y, face.width, face.height);
        }

        return objects;
    }

    public Face[] detectFaces() {
        Rectangle[] f = detect(this.classifier);
        faces = new Face[f.length];

        for (int i = 0; i < f.length; i++)
            faces[i] = new Face(f[i]);

        return faces;
    }

    public Face[] getFaces() {
        return faces;
    }

    /*public Rectangle[] detectSmiles() {
        return detect(HAAR_CASCADE_SMILE);
    } // */

    public Rectangle[] detectEyes() {
        return detect(HAAR_CASCADE_EYES);
    }

    public class Face {

        private Rectangle coords;

        protected Face(Rectangle coords) {
            this.coords = coords;
        }

        public Rectangle getCoords() {
            return coords;
        }

        public Rectangle getSmile() {
            Mat face = new Mat(getMat(), new Rect(coords.x, coords.y, coords.width, coords.height));

            Rectangle[] r = detect(face, HAAR_CASCADE_SMILE);

            if (r == null)
                return null;

            if (r.length == 0)
                return null;

            return r[0];
        }

        public boolean isSmiling() {
            return getSmile() != null;
        }
    }

    public OpenCVImage resize(int w, int h) {
        Mat newData = new Mat();
        MatDataBuffer dataBuffer = (MatDataBuffer)(getRaster().getDataBuffer());
        Imgproc.resize(dataBuffer.getMat(), newData, new Size(w, h), 0.0, 0.0, Imgproc.INTER_LINEAR);

        return new OpenCVImage(newData);
    }

    private Mat getMat() {
        return ((MatDataBuffer)(getRaster().getDataBuffer())).getMat();
    }

    public OpenCVImage flip(int mode) {
        Mat dest = new Mat();
        Core.flip(getMat(), dest, mode);

        return new OpenCVImage(dest);
    }

    public OpenCVImage canny(double threshold1, double threshold2) {
        Mat edges = new Mat();

        Imgproc.Canny(getMat(), edges, threshold1, threshold2);

        return new OpenCVImage(edges);
    }

    public OpenCVImage bitwise_not() {
        Mat not = new Mat();

        Core.bitwise_not(getMat(), not);

        return new OpenCVImage(not);
    }

    public OpenCVImage negative() {
        return bitwise_not();
    }

    public OpenCVImage sepia() {
        Mat retImage = new Mat();

        Core.transform(getMat(), retImage, sepiaKernel);

        return new OpenCVImage(retImage);
    }

    public OpenCVImage grayscale() {
        Mat retImage = new Mat();

        Core.transform(getMat(), retImage, grayscaleKernel);

        return new OpenCVImage(retImage);
    }

    public OpenCVImage magenta() {
        Mat image = getMat();
        Size sz = image.size();
        Mat retImage = new Mat(sz, CvType.CV_8UC3);

        for (int y = 0; y < sz.height; y++) {
            for (int x = 0; x < sz.width; x++) {
                double[] pixel = image.get(y, x);
                pixel[0] /= 255.0;
                pixel[1] /= 255.0;
                pixel[2] /= 255.0;
                double max = Math.max(Math.max(pixel[0], pixel[1]), pixel[2]);
                double m = (max - pixel[0]) / (max);
                int r = (int)(255.0 * (1.0 - max));
                int g = (int)(255.0 * (1.0 - max)); //(int)(255.0 * (1.0 - m) * max);
                int b = (int)(255.0 * (1.0 - max)); //(int)(255.0 * max);
                retImage.put(y, x, new byte[] {(byte)r, (byte)g, (byte)b});
            }
        }

        return new OpenCVImage(retImage);
    } // */

    @Override
    public Graphics getGraphics() {
        return new OpenCVGraphics(getMat());
    }

    @Override
    public Graphics2D createGraphics() {
        return new OpenCVGraphics(getMat());
    }
}
