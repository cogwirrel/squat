package squat.optimization;

import java.awt.Point;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import squat.model.Model;
import squat.model.Model.Joint;

public class ModelFitFunction implements MultivariateFunction {

	private Mat frame;
	
	public ModelFitFunction(Mat frame) {
		this.frame = frame;
	}
	
	@Override
	public double value(double[] modelAsDouble) {
		
		Model model = new Model(modelAsDouble);
		
		Moments m = Imgproc.moments(frame);
		Point centre = new Point((int)(m.get_m10()/m.get_m00()), (int)(m.get_m01()/m.get_m00()));
		
		return model.getJoints().get(Joint.SHOULDER).distanceSq(centre);
	}

}
