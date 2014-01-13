package squat.utils;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Skeletoniser {
	public Mat skeletonise(Mat frame) {
		
		Mat skeleton = new Mat(frame.size(), frame.type());
		Mat temp = new Mat(frame.size(), frame.type());
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3,3));
		
		boolean done = false;
		while(!done) {
			Imgproc.morphologyEx(frame, temp, Imgproc.MORPH_OPEN, element);
			Core.bitwise_not(temp, temp);
			Core.bitwise_and(frame, temp, temp);
			Core.bitwise_or(skeleton, temp, skeleton);
			Imgproc.erode(frame, frame, element);
			
			MinMaxLocResult m = Core.minMaxLoc(frame);
			done = m.maxVal == 0;
		}
		
		return skeleton;
	}
}
