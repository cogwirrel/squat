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
	
	// The position of the toe is fixed.
	private Point foot;// = new Point(105, 280);
	
	private double[] angles = new double[NUM_JOINTS];
	private double[] lengths = new double[NUM_JOINTS];
	private double[] widths = new double[NUM_JOINTS];
	
	public AngularModel(double footX, double footY) {
		this.foot = new Point(footX, footY);
		initialiseWidths();
		initialiseLengths();
		initialiseAngles();
	}
	
	private void initialiseWidths() {
		widths[HEAD_SHOULDER] = 30;
		widths[SHOULDER_HIP] = 40;
		widths[HIP_KNEE] = 30;
		widths[KNEE_ANKLE] = 20;
		widths[ANKLE_TOE] = 10;
	}
	
	private void initialiseLengths() {
		lengths[HEAD_SHOULDER] = 20;
		lengths[SHOULDER_HIP] = 78;
		lengths[HIP_KNEE] = 55;
		lengths[KNEE_ANKLE] = 52;
		lengths[ANKLE_TOE] = 20;
	}
	
	private void initialiseAngles() {
		angles[HEAD_SHOULDER] = 90;
		angles[SHOULDER_HIP] = 90;
		angles[HIP_KNEE] = 120;
		angles[KNEE_ANKLE] = 45;
		angles[ANKLE_TOE] = 180;
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
			170
		};
	}

	public void draw(Mat m) {
		// Draw white by default (use this for optimisation that deals with black+white)
		draw(m, new Scalar(255,255,255));
	}
	
	private Point[] calculatePoints() {
		Point[] points = new Point[NUM_JOINTS + 1];
		points[NUM_JOINTS] = foot;
		Point from = foot;
		for(int i = NUM_JOINTS - 1; i >= 0; i--) {
			Point to = calculatePoint(from, i);
			points[i] = to;
			from = to;
		}
		return points;
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
		Core.circle(m, points[SHOULDER_HIP], 30, colour, -1);
		
		// Draw a small circle for the butt!
		Core.circle(m, points[HIP_KNEE], 15, colour, -1);
	}
	
	private void drawBodyPart(Mat m, Point from, Point to, int toIndex, Scalar colour) {
		Point centre = PointUtils.centre(from, to);
		RotatedRect r = new RotatedRect(centre, new Size(widths[toIndex], 10 + PointUtils.distance(from, to)), 90 + angles[toIndex]);

		Core.ellipse(m, r, colour, -1);
	}

	private Point calculatePoint(Point from, int to) {
		double d = lengths[to];
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
		return angles[KNEE_ANKLE] < 70;
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
		double footX = points[ANKLE_TOE].x;
		double difference = Math.abs(weightX - footX);
		// TODO put in appropriate difference between heel joint and shoulder
		return difference < 40;
	}
}
