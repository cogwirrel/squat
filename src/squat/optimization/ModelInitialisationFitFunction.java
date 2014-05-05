
package squat.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import squat.model.Model;

public class ModelInitialisationFitFunction implements MultivariateFunction {

	private Mat frame;
	private Model model;

	public ModelInitialisationFitFunction(Mat frame, Model model) {
		this.frame = frame;
		this.model = model;
	}
	
	@Override
	public double value(double[] feet) {
		
		model.setInitParams(feet);
		
		Mat m = new Mat(frame.size(), frame.type());
		model.draw(m);
		
		// We want the most overlap
		int overlap = numberOverlappingPixels(frame, m);
		
		// We want to minimise non overlap
		int nonOverlap = frame.rows() * frame.cols() - overlap;
		
		return nonOverlap;
	}
	
	private int numberOverlappingPixels(Mat m1, Mat m2) {
		Mat res = new Mat();
		int overlap = 0;
		try {
			Core.bitwise_and(m1, m2, res);
			overlap = Core.countNonZero(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return overlap;
	}

	private int numberOverspillingPixels(Mat m1, Mat m2) {
		Mat inverted = new Mat();
		Core.bitwise_not(m1, inverted);
		return numberOverlappingPixels(inverted, m2);
	}
	
}
