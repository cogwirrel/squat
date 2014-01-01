package squat.utils;

import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractorMOG;

public class BackgroundSubtractor {
	private BackgroundSubtractorMOG subtractor;
	
	public BackgroundSubtractor() {
		subtractor = new BackgroundSubtractorMOG();
	}
	
	public Mat subtract(Mat frame) {
		Mat mask = new Mat();
		subtractor.apply(frame, mask);
		return mask;
	}
}
