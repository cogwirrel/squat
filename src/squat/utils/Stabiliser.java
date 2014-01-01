package squat.utils;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

public class Stabiliser {
	private Mat first;
	private Size size;
	
	public Stabiliser(Mat initialFrame) {
		first = initialFrame;
		size = new Size(first.width(), first.height());
	}
	
	public Mat stabilise(Mat next) {
		Mat output = new Mat();
		
		Mat m = Video.estimateRigidTransform(first, next, false);
		
		if(m.rows() == 2 && m.cols() == 3) {
			Imgproc.warpAffine(next, output, m, size, Imgproc.INTER_NEAREST | Imgproc.WARP_INVERSE_MAP);
			first = output;
		} else {
			first = next;
		}
		
		return first;
	}
}
