package dbm.opencv.utils;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.opencv.core.Core.FONT_HERSHEY_SIMPLEX;

public class OpenCVGraphics extends Graphics2D {

    private Mat matrix;
    private Scalar color;
    private Map<RenderingHints.Key, Object> renderingHints = new HashMap<RenderingHints.Key, Object>();
    private static Map<String, Font> allFonts = new HashMap<String, Font>();
    private Font font = new Font(Font.SERIF, Font.PLAIN, 12);
    private AffineTransform transform = new AffineTransform();

    static {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font font: fonts)
            allFonts.put(font.getName(), font);
    }

    protected OpenCVGraphics(Mat matrix) {
        this.matrix = matrix;
        transform.setToIdentity();
        color = new Scalar(255, 255, 255);
    }

    @Override
    public Graphics create() {
        return null;
    }

    @Override
    public void translate(int x, int y) {
        translate((double)x, (double)y);
    }

    @Override
    public void translate(double tx, double ty) {
        transform.translate(tx, ty);
    }

    @Override
    public void rotate(double theta) {

    }

    @Override
    public void rotate(double theta, double x, double y) {

    }

    @Override
    public void scale(double sx, double sy) {

    }

    @Override
    public void shear(double shx, double shy) {

    }

    @Override
    public void transform(AffineTransform Tx) {
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        transform = Tx;
    }

    @Override
    public AffineTransform getTransform() {
        return transform;
    }

    @Override
    public Paint getPaint() {
        return null;
    }

    @Override
    public Composite getComposite() {
        return null;
    }

    @Override
    public void setBackground(Color color) {

    }

    @Override
    public Color getBackground() {
        return null;
    }

    @Override
    public Stroke getStroke() {
        return null;
    }

    @Override
    public void clip(Shape s) {

    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return null;
    }

    @Override
    public Color getColor() {
        return new Color((int)color.val[2], (int)color.val[1], (int)color.val[0]);
    }

    @Override
    public void setColor(Color c) {
        color = new Scalar(c.getBlue(), c.getGreen(), c.getRed());
    }

    @Override
    public void setPaintMode() {

    }

    @Override
    public void setXORMode(Color c1) {

    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return null;
    }

    @Override
    public Rectangle getClipBounds() {
        return null;
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {

    }

    @Override
    public void setClip(int x, int y, int width, int height) {

    }

    @Override
    public Shape getClip() {
        return null;
    }

    @Override
    public void setClip(Shape clip) {

    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {

    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        // LINE_4 and LINE_8 is Bresenham (LINE_8 is default)
        // LINE_AA is antialiased
        Imgproc.line(matrix, new Point(x1, y1), new Point(x2, y2), color, 1, (renderingHints.get(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_ON ? Imgproc.LINE_AA : Imgproc.LINE_8), 0);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        // TODO: Fix transform
        Imgproc.rectangle(matrix, new Point(x, y), new Point(x + width, y + height), color, 1, Imgproc.LINE_8, 0);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        // Thickness = -1 means filled.
        // TODO: Fix transform
        Imgproc.rectangle(matrix, new Point(x, y), new Point(x + width, y + height), color, -1, Imgproc.LINE_8, 0);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {

    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {

    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {

    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        // TODO: Fix transform
        Imgproc.ellipse(matrix, new Point(x + (width / 2), y + (height / 2)), new Size(width / 2, height / 2), 0.0, 0.0, 360.0, color);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {

    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        // TODO: Fix transform
        Imgproc.ellipse(matrix, new Point(x + (width / 2), y + (height / 2)), new Size(width / 2, height / 2), 0.0, startAngle, arcAngle, color);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {

    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {

    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {

    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {

    }

    @Override
    public void draw(Shape s) {
        double x = transform.getTranslateX();
        double y = transform.getTranslateY();
        drawShape(s, x, y, false);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return false;
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {

    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {

    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {

    }

    @Override
    public void drawString(String str, int x, int y) {
        FontRenderContext frc = new FontRenderContext(null, false, false);
        GlyphVector vector = font.createGlyphVector(frc, str);
        Shape s = vector.getOutline();
        AffineTransform at = new AffineTransform(getTransform());
        translate(x, y);
        fill(s);
        setTransform(at);
    }

    @Override
    public void drawString(String str, float x, float y) {

    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {

    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {

    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {

    }

    @Override
    public void fill(Shape s) {
        double x = transform.getTranslateX();
        double y = transform.getTranslateY();
        drawShape(s, x, y, true);
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return false;
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return null;
    }

    @Override
    public void setComposite(Composite comp) {

    }

    @Override
    public void setPaint(Paint paint) {

    }

    @Override
    public void setStroke(Stroke s) {

    }

    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        renderingHints.put(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return renderingHints.get(hintKey);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {

    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {

    }

    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return false;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return false;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return false;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return false;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return false;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        return false;
    }

    @Override
    public void dispose() {

    }

    public boolean mapImage(Image img, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, ImageObserver observer) {

        return false;
    }

    private Mat toMat(Image image) {
        // https://stackoverflow.com/questions/33403526/how-to-match-the-color-models-of-bufferedimage-and-mat
        // Thanks Cecilia, whoever you are!
        int curCVtype = CvType.CV_8UC4; //Default type
        boolean supportedType = true;
        BufferedImage img;

        //if (image instanceof BufferedImage) {
        //    img = (BufferedImage) image;
        //}
        //else {
            img = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
            Graphics g = img.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
        //}

        switch (img.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR:
                curCVtype = CvType.CV_8UC3;
                break;
            case BufferedImage.TYPE_BYTE_GRAY:
            case BufferedImage.TYPE_BYTE_BINARY:
                curCVtype = CvType.CV_8UC1;
                break;
            case BufferedImage.TYPE_INT_BGR:
            case BufferedImage.TYPE_INT_RGB:
                curCVtype = CvType.CV_32SC3;
                break;
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
                curCVtype = CvType.CV_32SC4;
                break;
            case BufferedImage.TYPE_USHORT_GRAY:
                curCVtype = CvType.CV_16UC1;
                break;
            case BufferedImage.TYPE_4BYTE_ABGR:
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                curCVtype = CvType.CV_8UC4;
                break;
            default:
                // BufferedImage.TYPE_BYTE_INDEXED;
                // BufferedImage.TYPE_CUSTOM;
                //System.out.println("Unsupported format:" + img.getType());
                supportedType = false;
        }

        //Convert to Mat
        Mat data = new Mat(img.getHeight(), img.getWidth(), curCVtype);
        if (supportedType) {
            // Insert pixel buffer directly
            byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
            data.put(0, 0, pixels);
        } else {
            // Convert to RGB first
            int height = img.getHeight();
            int width = img.getWidth();
            int[] pixels = img.getRGB(0, 0, width - 1, height - 1, null, 0, width);

            // Convert ints to bytes
            ByteBuffer byteBuffer = ByteBuffer.allocate(pixels.length * 4);
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(pixels);

            byte[] pixelBytes = byteBuffer.array();

            data.put(0, 0, pixelBytes);

            // Reorder the channels for Opencv BGRA format from
            // BufferedImage ARGB format
            Mat imgMix = data.clone();
            ArrayList<Mat> imgSrc = new ArrayList<Mat>();
            imgSrc.add(imgMix);

            ArrayList<Mat> imgDest = new ArrayList<Mat>();
            imgDest.add(data);

            int[] fromTo = { 0, 3, 1, 2, 2, 1, 3, 0 }; //Each pair is a channel swap
            Core.mixChannels(imgSrc, imgDest, new MatOfInt(fromTo));
        }

        return data;
    }

    private void drawShape(Shape s, double x, double y, boolean fill) {
        PathIterator path = s.getPathIterator(null);
        ArrayList<Point> points = new ArrayList<Point>();
        ArrayList<MatOfPoint> pts = new ArrayList<MatOfPoint>();
        double x1, y1;
        double xc, yc;
        x1 = y1 = xc = yc = 0;
        System.out.println("DRAWING SHAPE");
        while (!path.isDone()) {
            double[] coords = new double[6];
            int type;
            String sType = "";
            type = path.currentSegment(coords);
            int rule = path.getWindingRule();
            path.next();

            switch (type) {
                case PathIterator.SEG_MOVETO:
                    sType = "MOVE";
                    x1 = coords[0] + x;
                    y1 = coords[1] + y;
                    xc = x1;
                    yc = y1;
                    points.add(new Point(coords[0] + x, coords[1] + y));
                    System.out.println("\t" + (coords[0] + x) + ", " + (coords[1] + y));
                    break;
                case PathIterator.SEG_LINETO:
                    sType = "LINE";
                    x1 = coords[0] + x;
                    y1 = coords[1] + y;
                    points.add(new Point(coords[0] + x, coords[1] + y));
                    System.out.println("\t" + (coords[0] + x) + ", " + (coords[1] + y));
                    break;
                case PathIterator.SEG_QUADTO:
                    sType = "QUAD";
                    Point[] quad = quadCurve(x1, y1, coords[0] + x, coords[1] + y, coords[2] + x, coords[3] + y, 9);
                    x1 = coords[0] + x;
                    y1 = coords[1] + y;
                    System.out.println("\t" + (coords[0] + x) + ", " + (coords[1] + y));
                    for (Point q: quad)
                        points.add(q);
                    break;
                case PathIterator.SEG_CUBICTO:
                    sType = "CUBIC";
                    drawLine((int)x1, (int)y1, (int)(coords[0] + x), (int)(coords[1] + y));
                    x1 = coords[0] + x;
                    y1 = coords[1] + y;
                    points.add(new Point(coords[0] + x, coords[1] + y));
                    System.out.println("\t" + (coords[0] + x) + ", " + (coords[1] + y));
                    break;
                case PathIterator.SEG_CLOSE:
                    sType = "CLOSE";
                    //System.out.println("CLOSE: " + xc + ", " + yc + " - " + x1 + ", " + y1);
                    points.add(new Point(xc, yc));
                    MatOfPoint p = new MatOfPoint();
                    p.fromList(points);
                    pts.add(p);
                    points.clear();
                    break;
            }
        }

        if (!points.isEmpty()) {
            MatOfPoint p = new MatOfPoint();
            p.fromList(points);
            pts.add(p);
            points.clear();
        }

        int ox, oy;
        ox = oy = 0;
        boolean bLine = false;
        for (MatOfPoint p: pts) {
            Point[] pt = p.toArray();
            bLine = false;
            for (Point point: pt) {
                if (bLine) {
                    drawLine(ox, oy, (int)point.x, (int)point.y);
                }
                ox = (int)point.x;
                oy = (int)point.y;
                System.out.println(ox + ", " + oy);
                bLine = true;
            }
        } // */

        /*if (fill) {
            Imgproc.fillPoly(matrix, pts, color, Core.LINE_4, 0, new Point(0, 0));
        }
        else {
            Imgproc.polylines(matrix, pts, true, color);
        } // */
        //Imgproc.drawContours(matrix, pts, -1, color, (fill ? Core.FILLED : 0), (renderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING) == RenderingHints.VALUE_TEXT_ANTIALIAS_ON ? Imgproc.LINE_AA : Imgproc.LINE_8), new Mat(), 1, new Point(0, 0));
    }

    private Point[] quadCurve(double x1, double y1, double x2, double y2, double x3, double y3, int segments) {
        Point[] points = new Point[segments + 1];

        for (int i = 0; i < points.length; i++) {
            double t = (double)i / (double)segments;
            double a = Math.pow(1.0 - t, 2);
            double b = 2.0 * t * (1.0 - t);
            double c = Math.pow(t, 2);
            double x = (a * x1) + (b * x2) + (c * x3);
            double y = (a * y1) + (b * y2) + (c * y3);

            points[i] = new Point(x, y);
        }

        return points;
    }
}
