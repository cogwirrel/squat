package squat.optimization;

import org.opencv.core.Mat;

import squat.model.Model;

public interface ModelFitter {
	public void fit(Model model, Mat frame);
}
