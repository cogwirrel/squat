package squat;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import squat.tracking.SquatPipeline;
import squat.utils.VideoDisplay;
import squat.utils.VideoInput;

public class Squat {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	

	public static void main(String[] args) throws Exception {
		processVideo("/home/jack/squat_vids/stable/good_squats.avi");
		processVideo("/home/jack/squat_vids/stable/good_squat_single.avi");
	}
	
	public static void processVideo(String name) throws Exception {
		VideoInput videoInput = new VideoInput(name, true);
		VideoDisplay videoDisplay = new VideoDisplay("Display", videoInput.getWidth(), videoInput.getHeight());
		
		SquatPipeline squatPipeline = new SquatPipeline(videoInput, videoDisplay);
		squatPipeline.process();
		
		// Cycle through the last few frames
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			videoDisplay.show(frame);
			videoDisplay.draw();
		}
		
		videoDisplay.close();
		
		System.out.println("done");
	}

}
