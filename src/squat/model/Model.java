package squat.model;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public interface Model {
	public void draw(Mat m);
	public void draw(Mat m, Scalar colour);
	public void set(double[] values);
	public double[] get();
	public double[] getUpperBounds();
	public double[] getLowerBounds();
	
	public boolean isSquatBelowParallel();
	public boolean isSquatUpright();
}
