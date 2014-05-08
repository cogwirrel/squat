package squat;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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
import squat.optimization.ModelInitialisationFitterOptim;
import squat.utils.BackgroundSubtractor;
import squat.utils.BackgroundSubtractorNaive;
import squat.utils.FigureDetector;
import squat.utils.VideoDisplay;
import squat.utils.VideoInput;
import squat.utils.VideoTools;

public class Squat {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	private static final int INIT_FITTING_ITERATIONS = 3;
	
	public static void main(String[] args) throws Exception {
		
		VideoInput videoInput = new VideoInput("/home/jack/squat_vids/stable/good_squats.avi", true);
		
		int width = videoInput.getWidth();
		int height = videoInput.getHeight();
		
		VideoDisplay videoDisplay = new VideoDisplay("Skeleton", width, height);
		VideoDisplay videoDisplay2 = new VideoDisplay("Model Fit", width, height);
		
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
			
		Mat firstFrame = new Mat();
		if(videoInput.hasNextFrame()) {
			firstFrame = videoInput.getNextFrame();
		}
		
		BackgroundSubtractor bg = new BackgroundSubtractorNaive(firstFrame, 30);
		
		SquatSetup squatSetup = new SquatSetup(bg, firstFrame);
		Mat readyFrame = new Mat();
		while(!squatSetup.ready() && videoInput.hasNextFrame()) {
			readyFrame = videoInput.getNextFrame();
			videoDisplay.show(readyFrame);
			videoDisplay.draw();
			squatSetup.update(readyFrame);
		}
		
		System.out.println("Ready to squat!");
		
		// We have got to the point where the lifter is ready to squat
		
		// Find where to put the model and how large to make it
		
		Mat readyForeground = bg.subtract(readyFrame);
		// Calculate the height and centre point of the foreground blob - ie. the figure
		MatOfPoint figureContours = VideoTools.largestObject(readyForeground);
		Rect figureBound = Imgproc.boundingRect(figureContours);
		int figureHeight = figureBound.height;
		System.out.println("Figure Height: " + figureHeight);
		Point figureCentre = new Point(figureBound.x + figureBound.width / 2, figureBound.y + figureBound.height / 2);
		System.out.println("Figure x: " + figureCentre.x + " y: " + figureCentre.y);
		
		// Initialise the model in a sensible location
		AngularModel model = new AngularModel(figureCentre.x - figureBound.width / 2, figureCentre.y + figureHeight / 2);
		Mat modelScaleMat = new Mat(readyForeground.size(), readyForeground.type());
		model.draw(modelScaleMat);
		
		// Calculate the height of the model
		MatOfPoint modelContours = VideoTools.largestObject(modelScaleMat);
		Rect modelBound = Imgproc.boundingRect(modelContours);
		int modelHeight = modelBound.height;
		
		System.out.println("Model Height: " + modelHeight);
		
		// Calculate the scaling factor to best fit the model to the figure
		double scale = (double)figureHeight / (double)modelHeight;
		model.setScale(scale);
		
		System.out.println("SCALE: " + scale);
		
		ModelFitter fitter = new ModelFitterOptim();
		//ModelFitter fitter = new ModelFitterManual(width, height);
		
		// Initial fitting!!
		ModelFitter initFit = new ModelInitialisationFitterOptim();
		
		Mat frm = new Mat();
		if(videoInput.hasNextFrame()) {
			frm = videoInput.getNextFrame();
		}
		
		for(int i = 0; i < INIT_FITTING_ITERATIONS; i++) {
			initFit.fit(model, bg.subtract(frm));
		}
		
		// We have the initial model fitted
		// Start the main squat analysis
		SquatTracker squatTracker = new SquatTracker(model, modelEventManager, bg);
		squatTracker.start();
		
		// Main loop
		while(videoInput.hasNextFrame() && !squatTracker.finished()) {
			Mat frame = videoInput.getNextFrame();
			
			squatTracker.update(frame);
			
			Mat m = new Mat(frame.size(), frame.type());
			Mat m2 = new Mat(frame.size(), frame.type());
			model.draw(m2, modelColour);
			model.drawSkeleton(m, modelColour);
			
			videoDisplay.show(VideoTools.blend(frame, m));
			videoDisplay.draw();
			
			videoDisplay2.show(VideoTools.blend(frame, m2));
			videoDisplay2.draw();
			
			//System.out.println(frameNumber);
			//frameNumber++;
		}
		
		// Cycle through the last few frames
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			videoDisplay.show(frame);
			videoDisplay.draw();
		}
		
		videoDisplay.close();
		videoDisplay2.close();
		
		System.out.println("done");
		System.out.println("Reps: " + squatTracker.getReps());
		System.out.println("Score: " + squatTracker.getScores());

	}

}
