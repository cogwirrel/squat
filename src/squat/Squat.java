package squat;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import squat.model.Model;
import squat.utils.VideoInput;
import squat.utils.VideoOutput;

public class Squat {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	public static void main(String[] args) throws Exception {
		
		VideoInput videoInput = new VideoInput("/home/jack/squat_vids/good_squats_small.avi");
		
		int width = videoInput.getWidth();
		int height = videoInput.getHeight();
		
		VideoOutput videoOutput = new VideoOutput("Test", width, height);
		Model model = new Model();
		
		int frameNumber = 0;
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			
			videoOutput.show(frame);
			videoOutput.show(model);
			videoOutput.draw();
			
			System.out.println(frameNumber);
			frameNumber++;
		}
		
		videoOutput.close();
		
		System.out.println("done");

	}

}
