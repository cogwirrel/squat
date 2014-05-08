package squat;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import squat.tracking.SquatPipeline;
import squat.tracking.SquatPipelineListener;
import squat.utils.Pair;
import squat.utils.VideoDisplay;
import squat.utils.VideoInput;

public class Squat {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main(String[] args) throws Exception {
		processVideo("/home/jack/squat_vids/stable/good_squats.avi");
		processVideo("/home/jack/squat_vids/stable/good_squat_single.avi");
	}
	
	public static void processVideo(String name) throws Exception {
		VideoInput videoInput = new VideoInput(name, true);
		VideoDisplay videoDisplay = new VideoDisplay("Display", videoInput.getWidth(), videoInput.getHeight());
		
		SquatPipeline squatPipeline = new SquatPipeline(videoInput, videoDisplay, new SquatPipelineListener() {
			public void onReadyToSquat() {
				System.out.println("Ready to Squat!");
			}

			public void onInitialModelFit() {
				System.out.println("Initial Model Fitted");
			}

			public void onSquatsComplete(List<Pair<Double, String>> scores) {
				System.out.println("Reps: " + scores.size());
				for(int i = 0; i < scores.size(); i++) {
					System.out.println("Rep " + (i+1) + " {Score: " + scores.get(i).l + "%, Problem: " + scores.get(i).r + "}");
				}
			}
		});
		
		squatPipeline.process();
		
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
