package squat.utils;

import org.opencv.core.Mat;

public class MotionDetector {
	private static final int FRAME_SKIP = 3;
	private static final int NUM_DIFFERENCES = 7;
	private static final double MOTION_THRESHOLD = 1;
	
	private FixedQueue<Double> differences;
	private Mat prev;
	private boolean moving = false;
	private int frameCount = 0;
	
	public MotionDetector(Mat initialFrame) {
		prev = initialFrame;
		differences = new FixedQueue<Double>(NUM_DIFFERENCES);
	}
	
	public boolean stationary(Mat frame) {
		if(frameCount % FRAME_SKIP == 0) {
			int pixelDifference = VideoTools.countDifference(frame, prev);
			double difference = 100 * (double)pixelDifference / (double)(frame.cols() * frame.rows());
			differences.add(difference);
			
			System.out.println(difference);
			
			moving = differencesBelowThreshold();
			
			prev = frame;
		}
		
		frameCount++;
		
		return moving;
	}
	
	private boolean differencesBelowThreshold() {
		boolean belowThreshold = differences.size() == NUM_DIFFERENCES;
		for(Double d : differences.getList()) {
			belowThreshold &= d < MOTION_THRESHOLD;
		}
		return belowThreshold;
	}
}
