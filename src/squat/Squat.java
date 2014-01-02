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
import squat.utils.BackgroundSubtractor;
import squat.utils.Stabiliser;
import squat.utils.VideoTools;
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
			firstFrame = VideoTools.toGreyscale(videoInput.getNextFrame());
		}
		
		Stabiliser stabiliser = new Stabiliser(firstFrame);
		BackgroundSubtractor bg = new BackgroundSubtractor();
		
		int frameNumber = 0;
		while(videoInput.hasNextFrame()) {
			Mat frame = VideoTools.toGreyscale(videoInput.getNextFrame());
			
			Mat smoothedFrame = stabiliser.stabilise(frame);
			Mat edges = new Mat();
			
			Imgproc.Canny(smoothedFrame, edges, 100, 300);
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			
			Mat drawing = Mat.zeros(edges.size(), edges.type());
			Imgproc.drawContours(drawing, contours, -1, new Scalar(255, 255, 0), 5);
			
			videoOutput.show(drawing);
			videoOutput.show(model);
			videoOutput.draw();
			
			System.out.println(frameNumber);
			frameNumber++;
		}
		
		videoOutput.close();
		
		System.out.println("done");

	}

}
