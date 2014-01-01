package squat;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import squat.utils.VideoInput;

public class Squat {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	public static void main(String[] args) throws Exception {
		
		VideoInput videoInput = new VideoInput("/home/jack/squat_vids/good_squats.avi");
		Mat frame = videoInput.getNextFrame();
		
		System.out.println(frame.dump());

	}

}
