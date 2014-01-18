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
	private Model initialModel;
	
	public ModelFitFunction(Model initialModel, Mat frame, List<MatOfPoint> contours) {
		this.frame = frame;
		this.contours = contours;
		this.initialModel = initialModel;
	}
	
	@Override
	public double value(double[] modelAsDouble) {
		
		Model model = new Model(modelAsDouble);
		
		Joint[] allJoints = Joint.values();
		double totalDistance = 0;
		for(Joint joint : allJoints) {
			MatOfPoint2f c = new MatOfPoint2f(contours.get(0).toArray());
			Point p = model.getJoints().get(joint);
			
			// Get the distance from the joint to the silhouette
			double dist = Imgproc.pointPolygonTest(c, new org.opencv.core.Point(p.getX(), p.getY()), true);
			
			// We want distance outside polygon to be +ve
			dist = -dist;
			
			// If it's inside, don't penalise
			if(dist <= 0) {
				dist = 0;
			}
			
			totalDistance += dist;
		}
		
		double propDiff = initialModel.proportionDifference(model);
		
		System.out.println("Prop: " + propDiff);
		System.out.println("Dist: " + totalDistance);
		
		totalDistance += Math.sqrt(propDiff);
		
		return totalDistance;
	}
	
	private double distanceFromCentre(Model model) {
		Moments m = Imgproc.moments(frame);
		Point centre = new Point((int)(m.get_m10()/m.get_m00()), (int)(m.get_m01()/m.get_m00()));
		
		double distance = 0;
		
		Joint[] allJoints = Joint.values();
		for(Joint joint : allJoints) {
			distance += model.getJoints().get(joint).distance(centre);
		}
		
		return distance;
	}

}
