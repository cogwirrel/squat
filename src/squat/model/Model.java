package squat.model;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public interface Model {
	public void draw(Mat m);
	public void draw(Mat m, Scalar colour);
	public void drawSkeleton(Mat m, Scalar colour);
	public void set(double[] values);
	public void setInitParams(double[] values);
	public void setScale(double scale);
	public double[] getInitParams();
	public double[] get();
	public double[] getUpperBounds();
	public double[] getLowerBounds();
	
	public double getVerticalHipPosition();
	
	public boolean isSquatBelowParallel();
	public boolean isSquatLockedOut();
	public boolean isSquatKneeForward();
	public boolean isSquatKneeBackward();
	public boolean isSquatHeelGrounded();
	public boolean isSquatWeightOverFeet();
	public boolean isSquatBackAngleInOptimalRange();
}
