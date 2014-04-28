package squat;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import squat.model.AngularModel;
import squat.model.Model;
import squat.model.event.ModelEventListener;
import squat.model.event.ModelEventManager;
import squat.model.event.ModelEventType;
import squat.model.event.SquatRepCounter;
import squat.model.event.SquatScorer;
import squat.optimization.ModelFitter;
import squat.optimization.ModelFitterManual;
import squat.optimization.ModelFitterOptim;
import squat.utils.BackgroundSubtractor;
import squat.utils.BackgroundSubtractorNaive;
import squat.utils.VideoDisplay;
import squat.utils.VideoInput;
import squat.utils.VideoTools;

public class Squat {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	public static void main(String[] args) throws Exception {
		
		VideoInput videoInput = new VideoInput("/home/jack/squat_vids/stable/good_squats.avi", true);
		
		int width = videoInput.getWidth();
		int height = videoInput.getHeight();
		
		VideoDisplay videoDisplay = new VideoDisplay("Test", width, height);
		VideoDisplay videoDisplay2 = new VideoDisplay("Test2", width, height);
		AngularModel model = new AngularModel(105, 280);
		ModelFitter fitter = new ModelFitterOptim();
		//ModelFitter fitter = new ModelFitterManual(width, height);
		
		final Scalar modelColour = new Scalar(255,255,255);
		
		ModelEventManager modelEventManager = new ModelEventManager();
		
		SquatScorer squatScorer = new SquatScorer(modelEventManager);
		
		SquatRepCounter sqrc = new SquatRepCounter(modelEventManager);
		
		Mat firstFrame = new Mat();
		if(videoInput.hasNextFrame()) {
			firstFrame = videoInput.getNextFrame();
		}
		
		int frameNumber = 0;
		while(frameNumber < 400 && videoInput.hasNextFrame()) {
			videoInput.getNextFrame();
			frameNumber++;
		}

		BackgroundSubtractor bg = new BackgroundSubtractorNaive(firstFrame, 30);
		
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			
			Mat foreground = bg.subtract(frame);
			
			fitter.fit(model, foreground);
			Mat m = new Mat(frame.size(), frame.type());
			model.draw(m, modelColour);
			
			modelEventManager.update(model);
			
			videoDisplay.show(VideoTools.blend(frame, m));
			videoDisplay.draw();
			
			videoDisplay2.show(foreground);
			videoDisplay2.draw();
			
			//System.out.println(frameNumber);
			frameNumber++;
		}
		
		videoDisplay.close();
		videoDisplay2.close();
		
		System.out.println("done");
		System.out.println("Reps: " + sqrc.getReps());
		System.out.println("Score: " + squatScorer.getCurrentScore());

	}

}
