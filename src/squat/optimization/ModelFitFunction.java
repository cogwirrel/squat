package squat.optimization;

import java.awt.Point;
import java.util.List;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import squat.model.Model;
import squat.model.Model.Joint;

public class ModelFitFunction implements MultivariateFunction {

	private Mat frame;
	private List<MatOfPoint> contours;
	
	public ModelFitFunction(Mat frame, List<MatOfPoint> contours) {
		this.frame = frame;
		this.contours = contours;
	}
	
	@Override
	public double value(double[] modelAsDouble) {
		
		Model model = new Model(modelAsDouble);
		
		Joint[] allJoints = Joint.values();
		double totalDistance = 0;
		for(int i = 0; i < allJoints.length; i++) {
			MatOfPoint2f c = new MatOfPoint2f(contours.get(0).toArray());
			Point p = model.getJoints().get(allJoints[i]);
			double dist = Imgproc.pointPolygonTest(c, new org.opencv.core.Point(p.getX(), p.getY()), true);
			totalDistance -= dist;
			System.out.println(dist);
		}
		
		return totalDistance;
	}
	
	private double distanceFromCentre(Model model) {
		Moments m = Imgproc.moments(frame);
		Point centre = new Point((int)(m.get_m10()/m.get_m00()), (int)(m.get_m01()/m.get_m00()));
		
		return model.getJoints().get(Joint.SHOULDER).distanceSq(centre);
	}

}
