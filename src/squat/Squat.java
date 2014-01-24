package squat;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import squat.model.Model;
import squat.optimization.ModelFitter;
import squat.utils.BackgroundSubtractor;
import squat.utils.FigureDetector;
import squat.utils.Pair;
import squat.utils.Skeletoniser;
import squat.utils.Stabiliser;
import squat.utils.VideoTools;
import squat.utils.VideoInput;
import squat.utils.VideoDisplay;

public class Squat {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	public static void main(String[] args) throws Exception {
		
		VideoInput videoInput = new VideoInput("/home/jack/squat_vids/good_squats_small.avi");
		
		int width = videoInput.getWidth();
		int height = videoInput.getHeight();
		
		VideoDisplay videoDisplay = new VideoDisplay("Test", width, height);
		VideoDisplay videoDisplay2 = new VideoDisplay("Test2", width, height);
		Model model = new Model();
		
		// Get the first 600 frames quickly as it's all boring stuff
		int frameNumber = 0;
		while(frameNumber < 600 && videoInput.hasNextFrame()) {
			videoInput.getNextFrame();
			frameNumber++;
		}
		
		Mat firstFrame = new Mat();
		if(videoInput.hasNextFrame()) {
			firstFrame = videoInput.getNextFrame();
		}
		
		Stabiliser stabiliser = new Stabiliser(firstFrame);
		FigureDetector fd = new FigureDetector();
		BackgroundSubtractor bg = new BackgroundSubtractor();
		Skeletoniser sk = new Skeletoniser();
		ModelFitter fitter = new ModelFitter();
		
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			
			Mat smoothedFrame = frame;//stabiliser.stabilise(frame);
			
			Pair<Mat, List<MatOfPoint>> detection = fd.detect(smoothedFrame);
			
			Mat drawing = detection.l;
			//drawing = sk.skeletonise(drawing);
			
			model = fitter.fit(model, drawing, detection.r);
			
			videoDisplay.show(frame);
			videoDisplay.show(model);
			videoDisplay.draw();
			
			videoDisplay2.show(drawing);
			videoDisplay2.show(model);
			videoDisplay2.draw();
			
			System.out.println(frameNumber);
			frameNumber++;
		}
		
		videoDisplay.close();
		videoDisplay2.close();
		
		System.out.println("done");

	}

}
