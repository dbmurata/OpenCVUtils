package dbm.opencv.utils;

import org.opencv.core.*;

public class OpenCVFilters {
    private static Mat sepiaKernel = new Mat(3, 3, CvType.CV_32F);
    private static Mat grayscaleKernel = new Mat(1, 3, CvType.CV_32F);

    static {
        float[] data = new float[9];
        data[0] = 0.272f; data[1] = 0.534f; data[2] = 0.131f;
        data[3] = 0.349f; data[4] = 0.686f; data[5] = 0.168f;
        data[6] = 0.393f; data[7] = 0.769f; data[8] = 0.189f;

        sepiaKernel.put(0, 0, data);

        data = new float[3];
        data[0] = 0.07f; data[1] = 0.72f; data[2] = 0.21f;

        grayscaleKernel.put(0, 0, data);
    }

    /*public static OpenCVImage sepia(OpenCVImage image) {
        return sepia(image.getMat());
    } // */

    public static OpenCVImage sepia(Mat image) {
        Mat retImage = new Mat();

        Core.transform(image, retImage, sepiaKernel);

        return new OpenCVImage(retImage);
    }

    /* public static OpenCVImage grayscale(OpenCVImage image) {
        return grayscale(image.getMat());
    }// */

    public static OpenCVImage grayscale(Mat image) {
        Mat retImage = new Mat();

        Core.transform(image, retImage, grayscaleKernel);

        return new OpenCVImage(retImage);
    }

    public static OpenCVImage negative(Mat image) {
        //Mat tmp = new Mat(image.rows(), image.cols(), image.type(), new Scalar(255.0, 255.0, 255.0));
        Mat retImage = new Mat();
        //Core.subtract(tmp, image, retImage);
        Core.bitwise_not(image, retImage);
        return new OpenCVImage(retImage);
    }

    public static OpenCVImage magenta(Mat image) {
        Mat retImage = new Mat();
        Size sz = image.size();
        float[] pixel = new float[3];

        for (int y = 0; y < sz.height; y++) {
            for (int x = 0; x < sz.width; x++) {
                image.get(y, x, pixel);

                System.out.println(pixel[0] + ", " + pixel[1] + ", " + pixel[2]);
            }
        }

        return new OpenCVImage(retImage);
    }
}
