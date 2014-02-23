package squat.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import squat.model.Model;

public class ModelFitFunction implements MultivariateFunction {

	private Mat frame;
	private Model model;

	public ModelFitFunction(Mat frame, Model model) {
		this.frame = frame;
		this.model = model;
	}
	
	@Override
	public double value(double[] modelAsDouble) {
		
		model.set(modelAsDouble);
		
		Mat m = new Mat(frame.size(), frame.type());
		model.draw(m);
		
		Mat res = new Mat();
		Core.bitwise_and(frame, m, res);
		
		int overlap = frame.rows() * frame.cols() - Core.countNonZero(res);
		
		System.out.println(overlap);
		
		return overlap;
	}

}
