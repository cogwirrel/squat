package squat.tracking;

import org.opencv.core.Mat;

import squat.utils.BackgroundSubtractor;
import squat.utils.FigureDetector;
import squat.utils.FixedQueue;
import squat.utils.MotionDetector;
import squat.utils.VideoTools;

public class SquatSetup {
	
	private BackgroundSubtractor bg;
	private FigureDetector figureDetector;
	private MotionDetector motionDetector;

	private boolean ready = false;
	
	public SquatSetup(BackgroundSubtractor backgroundSubtractor, Mat initialFrame) {
		bg = backgroundSubtractor;
		motionDetector = new MotionDetector(bg.subtract(initialFrame));
		figureDetector = new FigureDetector();
	}
	
	public void update(Mat frame) {
		Mat foreground = bg.subtract(frame);
		
		if(figureDetector.hasFigure(foreground)) {
			ready = motionDetector.stationary(foreground);
		}
	}
	
	public boolean ready() {
		return ready;
	}
}
