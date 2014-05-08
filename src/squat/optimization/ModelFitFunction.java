package squat.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.opencv.core.Mat;

import squat.model.Model;
import squat.utils.VideoTools;

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
		
		// We want the most overlap
		int overlap = VideoTools.numberOverlappingPixels(frame, m);
		
		// We want to minimise non overlap
		int nonOverlap = frame.rows() * frame.cols() - overlap;
		
		return nonOverlap;
	}
}
