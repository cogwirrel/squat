package squat;

import org.opencv.core.Mat;

import squat.utils.BackgroundSubtractor;
import squat.utils.FigureDetector;
import squat.utils.FixedQueue;
import squat.utils.VideoTools;

public class SquatSetup {
	private static final int FRAME_SKIP = 3;
	private static final int NUM_DIFFERENCES = 6;
	private static final double MOTION_THRESHOLD = 0.5;
	
	private BackgroundSubtractor bg;
	private FigureDetector figureDetector;
	private FixedQueue<Double> differences;
	private Mat prevForeground;
	private int frameCount = 0;
	private boolean ready = false;
	
	public SquatSetup(BackgroundSubtractor backgroundSubtractor, Mat initialFrame) {
		bg = backgroundSubtractor;
		differences = new FixedQueue<Double>(NUM_DIFFERENCES);
		figureDetector = new FigureDetector();
		prevForeground = bg.subtract(initialFrame);
	}
	
	public void update(Mat frame) {
		Mat foreground = bg.subtract(frame);
		
		if(figureDetector.hasFigure(foreground) && frameCount % FRAME_SKIP == 0) {
			int pixelDifference = VideoTools.countDifference(foreground, prevForeground);
			double difference = 100 * (double)pixelDifference / (double)(frame.cols() * frame.rows());
			differences.add(difference);
			
			System.out.println(difference);
			
			ready = differencesBelowThreshold();
			
			prevForeground = foreground;
		}
		
		frameCount++;
	}
	
	private boolean differencesBelowThreshold() {
		boolean belowThreshold = differences.size() == NUM_DIFFERENCES;
		for(Double d : differences.getList()) {
			belowThreshold &= d < MOTION_THRESHOLD;
		}
		return belowThreshold;
	}
	
	public boolean ready() {
		return ready;
	}
}
