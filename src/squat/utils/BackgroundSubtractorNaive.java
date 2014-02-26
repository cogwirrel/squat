package squat.utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BackgroundSubtractorNaive implements BackgroundSubtractor {

	Mat background;
	double threshold;
	
	public BackgroundSubtractorNaive(Mat background, double threshold) {
		this.background = background;//VideoTools.toGreyscale(background);
		this.threshold = threshold;
	}
	
	@Override
	public Mat subtract(Mat frame) {
		//frame = VideoTools.toGreyscale(frame);
		Mat foreground = new Mat();
		Core.absdiff(background, frame, foreground);
		
		Mat ret = new Mat();
		Imgproc.threshold(foreground, ret, threshold, 255, Imgproc.THRESH_BINARY);

		return ret;
	}

}
