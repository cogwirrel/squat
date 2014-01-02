package squat.utils;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class VideoTools {
	public static Mat toGreyscale(Mat frame) {
		return colourTransform(frame, Imgproc.COLOR_BGR2GRAY);
	}
	
	public static Mat toHsv(Mat frame) {
		return colourTransform(frame, Imgproc.COLOR_BGR2HSV);
	}
	
	public static Mat toHls(Mat frame) {
		return colourTransform(frame, Imgproc.COLOR_BGR2HLS);
	}
	
	private static Mat colourTransform(Mat frame, int type) {
		Mat m = new Mat();
		Imgproc.cvtColor(frame, m, type);
		return m;
	}
	
	public static Mat floodFill(Mat frame, int x, int y, int val) {
		Mat m = new Mat(frame.rows() + 2, frame.cols() + 2, frame.type());
		Imgproc.floodFill(frame, m, new Point(x, y), new Scalar(val));
		return m;
	}
}
