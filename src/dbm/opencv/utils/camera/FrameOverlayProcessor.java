package dbm.opencv.utils.camera;

import dbm.opencv.utils.OpenCVImage;
import org.opencv.core.Mat;

public interface FrameOverlayProcessor
{
    public OpenCVImage processFrame(OpenCVImage matrix);
}
