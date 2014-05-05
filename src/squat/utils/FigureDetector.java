package squat.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class FigureDetector {
	public boolean hasFigure(Mat frame) {
		int foregroundPixels = Core.countNonZero(frame);
		double foregroundProportion = (double)foregroundPixels / (double)(frame.cols() * frame.rows());
		return foregroundProportion > 0.08;
	}
	
	public Pair<Mat, List<MatOfPoint>> detect(Mat frame) {
		Mat edges = new Mat(frame.size(), frame.type());
		
		Mat hsv = VideoTools.toHsv(frame);
		Imgproc.blur(hsv, hsv, new Size(2,2));
		
		Imgproc.Canny(hsv, edges, 100, 300);
		
		int sz = 3;
		Imgproc.dilate(edges, edges, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(sz,sz)));
		Imgproc.erode(edges, edges, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(sz,sz)));
		
		Pair<Mat, List<MatOfPoint>> drawing = largestObject(edges);
		
		for(int i = 0; i < 4; i++) {
			Imgproc.erode(drawing.l, drawing.l, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(sz,sz)));
			Imgproc.dilate(drawing.l, drawing.l, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(sz,sz)));
			
			drawing = largestObject(drawing.l);
		}
		
		return drawing;
	}
	
	private Pair<Mat, List<MatOfPoint>> largestObject(Mat frame) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(frame, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		int largestContourIdx = largestContourIndex(contours);
		
		Mat drawing = Mat.zeros(frame.size(), frame.type());

		Imgproc.drawContours(drawing, contours, largestContourIdx, new Scalar(255, 255, 255), -1);
		return new Pair<Mat, List<MatOfPoint>>(drawing, contours);
	}
	
	private int largestContourIndex(List<MatOfPoint> contours) {
		int largestContourIndex = 0;
		double largestContourArea = 0;
		for(int i = 0; i < contours.size(); i++) {
			double area = Imgproc.contourArea(contours.get(i));
			if(area > largestContourArea) {
				largestContourIndex = i;
				largestContourArea = area;
			}
		}
		return largestContourIndex;
	}
}
