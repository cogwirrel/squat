package squat;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import squat.model.AngularModel;
import squat.optimization.ModelFitter;
import squat.optimization.ModelFitterManual;
import squat.optimization.ModelFitterOptim;
import squat.utils.BackgroundSubtractor;
import squat.utils.BackgroundSubtractorNaive;
import squat.utils.VideoDisplay;
import squat.utils.VideoInput;

public class Squat {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	public static void main(String[] args) throws Exception {
		
		VideoInput videoInput = new VideoInput("/home/jack/squat_vids/stable/good_squats.avi", true);
		
		int width = videoInput.getWidth();
		int height = videoInput.getHeight();
		
		VideoDisplay videoDisplay = new VideoDisplay("Test", width, height);
		VideoDisplay videoDisplay2 = new VideoDisplay("Test2", width, height);
		AngularModel model = new AngularModel();
		ModelFitter fitter = new ModelFitterManual(width, height);
		
		Mat firstFrame = new Mat();
		if(videoInput.hasNextFrame()) {
			firstFrame = videoInput.getNextFrame();
		}
		
		int frameNumber = 0;
		while(frameNumber < 400 && videoInput.hasNextFrame()) {
			videoInput.getNextFrame();
			frameNumber++;
		}

		BackgroundSubtractor bg = new BackgroundSubtractorNaive(firstFrame, 40);
		
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			
			Mat foreground = bg.subtract(frame);
			
			fitter.fit(model, foreground);
			videoDisplay.show(model);
			videoDisplay.draw();
			
			videoDisplay2.show(foreground);
			//videoDisplay2.show(model);
			videoDisplay2.draw();
			
			//System.out.println(frameNumber);
			frameNumber++;
		}
		
		videoDisplay.close();
		videoDisplay2.close();
		
		System.out.println("done");

	}

}
