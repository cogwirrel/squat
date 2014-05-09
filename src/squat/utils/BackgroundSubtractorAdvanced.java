package squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class BackgroundSubtractorAdvanced implements BackgroundSubtractor {

	private BackgroundSubtractorNaive bg;
	
	public BackgroundSubtractorAdvanced(Mat background, double threshold) {
		bg = new BackgroundSubtractorNaive(background, threshold);
	}
	
	@Override
	public Mat subtract(Mat frame) {
		Mat foreground = bg.subtract(frame);

		MatOfPoint figure = VideoTools.largestObject(foreground);
		if(figure == null) {
			return foreground;
		}
		
		Mat result = new Mat(foreground.size(), foreground.type());
		result.setTo(new Scalar(0,0,0));
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		contours.add(figure);
		Imgproc.drawContours(result, contours, 0, new Scalar(255,255,255), -1);
		
		return result;
	}

}
