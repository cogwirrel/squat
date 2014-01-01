package squat;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;

import squat.utils.VideoInput;
import squat.utils.VideoOutput;

public class Squat {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	public static void main(String[] args) throws Exception {
		
		VideoInput videoInput = new VideoInput("/home/jack/squat_vids/good_squats_small.avi");
		
		int width = videoInput.getWidth();
		int height = videoInput.getHeight();
		
		VideoOutput videoOutput = new VideoOutput("Test", width, height);
		VideoOutput videoOutput2 = new VideoOutput("Mask", width, height);
		
		BackgroundSubtractorMOG backgroundSubtractor = new BackgroundSubtractorMOG(10, 2, 0.1);
		Mat mask = new Mat();
		
		int i = 0;
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			
			backgroundSubtractor.apply(frame, mask);
			
			videoOutput.show(frame);
			videoOutput2.show(mask);
			
			System.out.println(i);
			i++;
		}
		
		videoOutput.close();
		videoOutput2.close();
		
		System.out.println("done");

	}

}
