package squat.model;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import squat.utils.PointUtils;

public class AngularModel implements Model {
	
	private static final int HEAD_SHOULDER = 0;
	private static final int SHOULDER_HIP = 1;
	private static final int HIP_KNEE = 2;
	private static final int KNEE_ANKLE = 3;
	private static final int ANKLE_TOE = 4;
	
	private static final int NUM_JOINTS = 5;
	
	private static final int DEGREES_OF_FREEDOM = NUM_JOINTS;
	
	private double scale = 1;
	
	// The position of the toe is fixed.
	private Point foot;// = new Point(105, 280);
	
	private double[] angles = new double[NUM_JOINTS];
	private double[] lengths = new double[NUM_JOINTS];
	private double[] widths = new double[NUM_JOINTS];
	
	private Point[] cachedPoints = null;
	
	public AngularModel(double footX, double footY) {
		this.foot = new Point(footX, footY);
		initialiseWidths();
		initialiseLengths();
		initialiseAngles();
	}
	
	public void setScale(double scale) {
		this.scale = scale;
	}
	
	private void initialiseWidths() {
		widths[HEAD_SHOULDER] = 15; //30;
		widths[SHOULDER_HIP] = 20;//40;
		widths[HIP_KNEE] = 15;//30;
		widths[KNEE_ANKLE] = 10;//20;
		widths[ANKLE_TOE] = 5;//10;
	}
	
	private void initialiseLengths() {
		lengths[HEAD_SHOULDER] = 10;//20;
		lengths[SHOULDER_HIP] = 39;//78;
		lengths[HIP_KNEE] = 27.5;//55;
		lengths[KNEE_ANKLE] = 26;//52;
		lengths[ANKLE_TOE] = 10;//20;
	}
	
	private void initialiseAngles() {
		angles[HEAD_SHOULDER] = 90;
		angles[SHOULDER_HIP] = 80;
		angles[HIP_KNEE] = 95;
		angles[KNEE_ANKLE] = 90;
		angles[ANKLE_TOE] = 170;
	}
	
	public void set(double[] values) {
		if(values.length == DEGREES_OF_FREEDOM) {
			for(int i = 0; i < NUM_JOINTS; i++) {
				angles[i] = values[i];
			}
		}
	}
	
	public double[] get() {
		double[] values = new double[DEGREES_OF_FREEDOM];
		for(int i = 0; i < NUM_JOINTS; i++) {
			values[i] = angles[i];
		}
		return values;
	}
	
	public void setInitParams(double[] values) {
		foot = new Point(values[0], values[1]);
	}
	
	public double[] getInitParams() {
		return new double[]{foot.x, foot.y};
	}
	
	// TODO: Sort out the actual bounds for knees - may need to do something clever with mod?
	public double[] getUpperBounds() {
		return new double[]{
			90,
			135,
			225,
			135,
			185
		};
	}
	
	public double[] getLowerBounds() {
		return new double[] {
			80,
			0,
			45,
			45,
			165
		};
	}

	public void draw(Mat m) {
		// Draw white by default (use this for optimisation that deals with black+white)
		draw(m, new Scalar(255,255,255));
	}
	
	private Point[] calculatePoints() {
		if(cachedPoints == null) {
			Point[] points = new Point[NUM_JOINTS + 1];
			points[NUM_JOINTS] = foot;
			Point from = foot;
			for(int i = NUM_JOINTS - 1; i >= 0; i--) {
				Point to = calculatePoint(from, i);
				points[i] = to;
				from = to;
			}
			cachedPoints = points;
		}
		return cachedPoints;
	}
	
	@Override
	public void draw(Mat m, Scalar colour) {
		m.setTo(new Scalar(0,0,0));
		
		// TODO optimise this by inlining
		Point[] points = calculatePoints();
		
		for(int i = NUM_JOINTS - 1; i >= 0; i--) {
			drawBodyPart(m, points[i+1], points[i], i, colour);
		}
		
		// Draw the bar on the lifter's back
		Core.circle(m, points[SHOULDER_HIP], (int)(15 * scale), colour, -1);
		
		// Draw a small circle for the butt!
		Core.circle(m, points[HIP_KNEE], (int)(2.5 * scale), colour, -1);
		
		// We have drawn the model, so clear the points cache
		cachedPoints = null;
	}
	
	private void drawBodyPart(Mat m, Point from, Point to, int toIndex, Scalar colour) {
		Point centre = PointUtils.centre(from, to);
		RotatedRect r = new RotatedRect(centre, new Size(scale * widths[toIndex], (10 + PointUtils.distance(from, to))), 90 + angles[toIndex]);

		Core.ellipse(m, r, colour, -1);
	}
	
	@Override
	public void drawSkeleton(Mat m, Scalar colour) {
		m.setTo(new Scalar(0,0,0));
		
		Point[] points = calculatePoints();
		
		for(Point p : points) {
			Core.circle(m, p, 3, colour);
		}
		
		Point prev = points[0];
		for(int i = 1; i < points.length; i++) {
			Core.line(m, prev, points[i], colour);
			prev = points[i];
		}
	}
	
	public void drawWeightDistributionLine(Mat m, Scalar colour) {
		Point[] points = calculatePoints();
		Core.line(m, points[SHOULDER_HIP], new Point(points[SHOULDER_HIP].x, points[ANKLE_TOE].y), colour);
	}

	private Point calculatePoint(Point from, int to) {
		double d = scale * lengths[to];
		double x = from.x + d * Math.cos(Math.toRadians(180 + angles[to]));
		double y = from.y + d * Math.sin(Math.toRadians(180 + angles[to]));
		return new Point((int)x, (int)y);
	}

	@Override
	public boolean isSquatBelowParallel() {
		return angles[HIP_KNEE] > 180;
	}

	@Override
	public boolean isSquatLockedOut() {
		return angles[KNEE_ANKLE] > 75 &&
			   angles[KNEE_ANKLE] < 95 &&
			   angles[HIP_KNEE] > 85 &&
			   angles[HIP_KNEE] < 110 &&
			   angles[SHOULDER_HIP] > 75 &&
			   angles[SHOULDER_HIP] < 100;
	}

	@Override
	public boolean isSquatKneeForward() {
		// Knee is forward if it goes too far beyond the front of the foot
		Point[] points = calculatePoints();
		double kneeX = points[HIP_KNEE].x;
		double footX = points[ANKLE_TOE].x;
		return kneeX < (footX - 10 * scale);
	}

	@Override
	public boolean isSquatKneeBackward() {
		return angles[KNEE_ANKLE] > 110;
	}

	@Override
	public boolean isSquatHeelGrounded() {
		return angles[ANKLE_TOE] < 190 && angles[ANKLE_TOE] > 170;
	}

	@Override
	public boolean isSquatWeightOverFeet() {
		Point[] points = calculatePoints();
		double weightX = points[SHOULDER_HIP].x;
		double heelX = points[KNEE_ANKLE].x;
		double toeX = points[ANKLE_TOE].x;
		return (toeX - 10 * scale) < weightX && weightX < (heelX + 10 * scale);
	}
	
	@Override
	public boolean isSquatBackAngleInOptimalRange() {
		return angles[SHOULDER_HIP] > 35 && angles[SHOULDER_HIP] < 90;
	}
	
	@Override
	public double getVerticalHipPosition() {
		Point[] points = calculatePoints();
		return points[HIP_KNEE].y;
	}
}
