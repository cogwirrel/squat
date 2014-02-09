package squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class VideoInput {
	
	private VideoCapture capture;
	private List<Mat> frames;
	private boolean rotate;
	
	public VideoInput(String filename) throws Exception {
		this(filename, false);
	}
	
	public VideoInput(String filename, boolean rotate) throws Exception {
		frames = new ArrayList<Mat>();
		this.rotate = rotate;
		
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
			Mat frame = new Mat();
			if(rotate) {
				Core.flip(frames.remove(0).t(), frame, 1);
			} else {
				frame = frames.remove(0);
			}
			return frame;
		} else {
			return null;
		}
	}
	
	public int getWidth() {
		int widthFlag = rotate ? Highgui.CV_CAP_PROP_FRAME_HEIGHT : Highgui.CV_CAP_PROP_FRAME_WIDTH;
		return (int)capture.get(widthFlag);
	}
	
	public int getHeight() {
		int heightFlag = rotate ? Highgui.CV_CAP_PROP_FRAME_WIDTH : Highgui.CV_CAP_PROP_FRAME_HEIGHT;
		return (int)capture.get(heightFlag);
	}
}
