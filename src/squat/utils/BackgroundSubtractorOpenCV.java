package squat.utils;

import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;

public class BackgroundSubtractorOpenCV implements BackgroundSubtractor {
	private BackgroundSubtractorMOG subtractor;
	
	public BackgroundSubtractorOpenCV() {
		subtractor = new BackgroundSubtractorMOG();
	}
	
	public Mat subtract(Mat frame) {
		Mat mask = new Mat();
		subtractor.apply(frame, mask);
		return mask;
	}
}
