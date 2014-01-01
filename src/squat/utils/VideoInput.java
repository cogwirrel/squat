package squat.utils;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class VideoInput {
	
	private VideoCapture capture;
	
	public VideoInput(String filename) throws Exception {
		capture = new VideoCapture(filename);
		if(!capture.isOpened()) {
			throw new Exception("Unable to open video file: " + filename);
		}
	}
	
	public Mat getNextFrame() throws Exception {
		Mat mat = new Mat();
		if(!capture.read(mat)) {
			throw new Exception("Unable to read next frame");
		}
		return mat;
	}
}
