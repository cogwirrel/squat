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
	

	public static void main(String[] args) throws Exception {
		
		VideoInput videoInput = new VideoInput("/home/jack/squat_vids/stable/good_squats.avi", true);
		VideoDisplay videoDisplay = new VideoDisplay("Display", videoInput.getWidth(), videoInput.getHeight());
		
		SquatPipeline squatPipeline = new SquatPipeline(videoInput, videoDisplay);
		while(!squatPipeline.completed()) {
			squatPipeline.process();
		}
		
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
