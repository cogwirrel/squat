package squat.model;

import org.opencv.core.Mat;

public interface Model {
	public void draw(Mat m);
	public void set(double[] values);
	public double[] get();
}
