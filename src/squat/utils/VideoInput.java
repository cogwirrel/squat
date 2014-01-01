package squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class VideoInput {
	
	private VideoCapture capture;
	private List<Mat> frames;
	
	public VideoInput(String filename) throws Exception {
		frames = new ArrayList<Mat>();
		
		capture = new VideoCapture(filename);
		if(!capture.isOpened()) {
			throw new Exception("Unable to open video file: " + filename);
		}
	}
	
	public boolean hasNextFrame() {
		Mat frame = new Mat();
		boolean success = capture.read(frame);
		if(success) {
			frames.add(frame);
		}
		return success;
	}
	
	public Mat getNextFrame() {
		if(frames.size() > 0) {
			return frames.remove(0);
		} else {
			return null;
		}
	}
	
	public int getWidth() {
		return (int)capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH);
	}
	
	public int getHeight() {
		return (int)capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT);
	}
}
