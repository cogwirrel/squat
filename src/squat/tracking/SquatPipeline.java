package squat.tracking;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import squat.model.AngularModel;
import squat.model.Model;
import squat.model.event.ModelEventListener;
import squat.model.event.ModelEventManager;
import squat.model.event.ModelEventType;
import squat.optimization.ModelFitter;
import squat.optimization.ModelInitialisationFitterOptim;
import squat.utils.BackgroundSubtractor;
import squat.utils.BackgroundSubtractorAdvanced;
import squat.utils.BackgroundSubtractorNaive;
import squat.utils.Value;
import squat.utils.VideoDisplay;
import squat.utils.VideoInput;
import squat.utils.VideoTools;

public class SquatPipeline {
	private static final int INIT_FITTING_ITERATIONS = 3;
	private VideoInput videoInput;
	private VideoDisplay videoDisplay;
	private SquatPipelineListener listener;
	
	public SquatPipeline(VideoInput videoInput, VideoDisplay videoDisplay, SquatPipelineListener listener) {
		this.videoInput = videoInput;
		this.videoDisplay = videoDisplay;
		this.listener = listener;
	}
	
	public void process() {
		final Scalar modelColour = new Scalar(255,255,255);

		Mat firstFrame = new Mat();
		if(videoInput.hasNextFrame()) {
			firstFrame = videoInput.getNextFrame();
		}
		
		VideoDisplay debugDisplay = new VideoDisplay("Debug", videoInput.getWidth(), videoInput.getHeight());
		
		BackgroundSubtractor bg = new BackgroundSubtractorAdvanced(firstFrame, 30);
		
		SquatSetup squatSetup = new SquatSetup(bg, firstFrame);
		Mat readyFrame = new Mat();
		while(!squatSetup.ready() && videoInput.hasNextFrame()) {
			readyFrame = videoInput.getNextFrame();
			videoDisplay.show(readyFrame);
			videoDisplay.draw();
			squatSetup.update(readyFrame);
			debugDisplay.show(bg.subtract(readyFrame));
			debugDisplay.draw();
		}
		
		listener.onReadyToSquat();
		
		// We have got to the point where the lifter is ready to squat
		
		// Find where to put the model and how large to make it
		Mat readyForeground = bg.subtract(readyFrame);
		
		// Calculate the height and centre point of the foreground blob - ie. the figure
		MatOfPoint figureContours = VideoTools.largestObject(readyForeground);
		Rect figureBound = Imgproc.boundingRect(figureContours);
		int figureHeight = figureBound.height;
		Point figureCentre = new Point(figureBound.x + figureBound.width / 2, figureBound.y + figureBound.height / 2);
		
		// Initialise the model in a sensible location
		AngularModel model = new AngularModel(figureCentre.x - figureBound.width / 2, figureCentre.y + figureHeight / 2);
		Mat modelScaleMat = new Mat(readyForeground.size(), readyForeground.type());
		model.draw(modelScaleMat);
		
		// Calculate the height of the model
		MatOfPoint modelContours = VideoTools.largestObject(modelScaleMat);
		Rect modelBound = Imgproc.boundingRect(modelContours);
		int modelHeight = modelBound.height;
		
		// Calculate the scaling factor to best fit the model to the figure
		double scale = (double)figureHeight / (double)modelHeight;
		model.setScale(scale);
		
		// Initial fitting!!
		ModelFitter initFit = new ModelInitialisationFitterOptim();
		
		Mat frm = new Mat();
		if(videoInput.hasNextFrame()) {
			frm = videoInput.getNextFrame();
		}
		
		for(int i = 0; i < INIT_FITTING_ITERATIONS; i++) {
			initFit.fit(model, bg.subtract(frm));
		}
		
		listener.onInitialModelFit();
		
		// We have the initial model fitted
		// Start the main squat analysis
		
		final ModelEventManager modelEventManager = new ModelEventManager();
		final Value<Boolean> drawWeightDistroLine = new Value<Boolean>();
		drawWeightDistroLine.set(false);
		
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
		
		modelEventManager.addListener(ModelEventType.SQUAT_BAD_WEIGHT_DISTRIBUTION_START, new ModelEventListener() {
			public void onEvent(Model m) {
				drawWeightDistroLine.set(true);
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_BAD_WEIGHT_DISTRIBUTION_END, new ModelEventListener() {
			public void onEvent(Model m) {
				drawWeightDistroLine.set(false);
			}
		});
		
		SquatTracker squatTracker = new SquatTracker(model, modelEventManager, bg);
		squatTracker.start();
		
		// Main loop
		while(videoInput.hasNextFrame() && !squatTracker.finished()) {
			Mat frame = videoInput.getNextFrame();
			
			squatTracker.update(frame);
			
			Mat m = new Mat(frame.size(), frame.type());

			model.drawSkeleton(m, modelColour);
			
			if(drawWeightDistroLine.get()) {
				model.drawWeightDistributionLine(m, new Scalar(0,0,255));
			}
			
			videoDisplay.show(VideoTools.blend(frame, m));
			videoDisplay.draw();
			
			debugDisplay.show(bg.subtract(frame));
			debugDisplay.draw();
		}
		
		debugDisplay.close();
		
		squatTracker.stop();
		
		listener.onSquatsComplete(squatTracker.getScores());		
	}
}
