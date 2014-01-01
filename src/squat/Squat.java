package squat;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import squat.model.Model;
import squat.utils.BackgroundSubtractor;
import squat.utils.Stabiliser;
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
		
		Mat firstFrame = new Mat();
		if(videoInput.hasNextFrame()) {
			firstFrame = videoInput.getNextFrame();
		}
		
		Stabiliser stabiliser = new Stabiliser(firstFrame);
		BackgroundSubtractor bg = new BackgroundSubtractor();
		
		int frameNumber = 0;
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			
			Mat smoothedFrame = stabiliser.stabilise(frame);
			Mat mask = bg.subtract(smoothedFrame);
			
			videoOutput.show(mask);
			videoOutput.show(model);
			videoOutput.draw();
			
			System.out.println(frameNumber);
			frameNumber++;
		}
		
		videoOutput.close();
		
		System.out.println("done");

	}

}
