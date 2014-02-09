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
		
		VideoInput videoInput = new VideoInput("/home/jack/squat_vids/stable/good_squats.avi", true);
		
		int width = videoInput.getWidth();
		int height = videoInput.getHeight();
		
		VideoDisplay videoDisplay = new VideoDisplay("Test", width, height);
		VideoDisplay videoDisplay2 = new VideoDisplay("Test2", width, height);
		Model model = new Model();
		
		int frameNumber = 0;
		
		BackgroundSubtractor bg = new BackgroundSubtractor();
		
		while(videoInput.hasNextFrame()) {
			Mat frame = videoInput.getNextFrame();
			
			Mat drawing = bg.subtract(frame);
			
			videoDisplay.show(frame);
			//videoDisplay.show(model);
			videoDisplay.draw();
			
			videoDisplay2.show(drawing);
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
