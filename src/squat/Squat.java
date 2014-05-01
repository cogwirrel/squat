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
import squat.model.event.SquatRepScorer;
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
		
		VideoDisplay videoDisplay = new VideoDisplay("Skeleton", width, height);
		VideoDisplay videoDisplay2 = new VideoDisplay("Model Fit", width, height);
		AngularModel model = new AngularModel(105, 280);
		ModelFitter fitter = new ModelFitterOptim();
		//ModelFitter fitter = new ModelFitterManual(width, height);
		
		final Scalar modelColour = new Scalar(255,255,255);
		
		final ModelEventManager modelEventManager = new ModelEventManager();
		
		modelEventManager.addListener(ModelEventType.SQUAT_BELOW_PARALLEL_START, new ModelEventListener() {
			public void onEvent(Model m) {
				modelColour.set(new double[]{0, 255, 0});
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_BELOW_PARALLEL_END, new ModelEventListener() {
			public void onEvent(Model m) {
				modelColour.set(new double[]{255, 255, 255});
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_LOCKOUT_START, new ModelEventListener() {
			public void onEvent(Model m) {
				modelColour.set(new double[]{0, 255, 0});
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_LOCKOUT_END, new ModelEventListener() {
			public void onEvent(Model m) {
				modelColour.set(new double[]{255, 255, 255});
			}
		});
		
		final SquatRepScorer squatScorer = new SquatRepScorer(modelEventManager);
		final SquatRepCounter sqrc = new SquatRepCounter(modelEventManager);
		
		modelEventManager.addListener(ModelEventType.SQUAT_LOCKOUT_START, new ModelEventListener() {
			boolean active = true;
			public void onEvent(Model m) {
				if(active) {
					// Start rep scoring and counting when squat is first locked out
					squatScorer.start();
					sqrc.start();
					active = false;
				}
			}
		});	
			
		Mat firstFrame = new Mat();
		if(videoInput.hasNextFrame()) {
			firstFrame = videoInput.getNextFrame();
		}
		
		int frameNumber = 0;
//		while(frameNumber < 400 && videoInput.hasNextFrame()) {
//			videoInput.getNextFrame();
//			frameNumber++;
//		}

		BackgroundSubtractor bg = new BackgroundSubtractorNaive(firstFrame, 30);
		
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			
			Mat foreground = bg.subtract(frame);
			
			fitter.fit(model, foreground);
			Mat m = new Mat(frame.size(), frame.type());
			Mat m2 = new Mat(frame.size(), frame.type());
			model.draw(m2, modelColour);
			model.drawSkeleton(m, modelColour);
			
			modelEventManager.update(model);
			
			videoDisplay.show(VideoTools.blend(frame, m));
			videoDisplay.draw();
			
			videoDisplay2.show(VideoTools.blend(frame, m2));
			videoDisplay2.draw();
			
			//System.out.println(frameNumber);
			frameNumber++;
		}
		
		videoDisplay.close();
		videoDisplay2.close();
		
		System.out.println("done");
		System.out.println("Reps: " + sqrc.getReps());
		System.out.println("Score: " + squatScorer.getScores());

	}

}
