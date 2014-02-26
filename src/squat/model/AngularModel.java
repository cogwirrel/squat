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
	
	private static final int DEGREES_OF_FREEDOM = NUM_JOINTS + 2;
	
	private Point head = new Point(70, 50);
	
	private double[] angles = new double[NUM_JOINTS];
	private double[] lengths = new double[NUM_JOINTS];
	private double[] widths = new double[NUM_JOINTS];
	
	public AngularModel() {
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
			head.x = values[0];
			head.y = values[1];
			for(int i = 0; i < NUM_JOINTS; i++) {
				angles[i] = values[2+i];
			}
		}
	}
	
	public double[] get() {
		double[] values = new double[DEGREES_OF_FREEDOM];
		values[0] = head.x;
		values[1] = head.y;
		for(int i = 0; i < NUM_JOINTS; i++) {
			values[2+i] = angles[i];
		}
		return values;
	}
	
	// TODO: Sort out the actual bounds for knees - may need to do something clever with mod?
	public double[] getUpperBounds() {
		return new double[]{
			240, // head x
			360, // head y
			360,
			360,
			360,
			360,
			360
		};
	}
	
	public double[] getLowerBounds() {
		return new double[] {
			0,
			0,
			0,
			0,
			0,
			0,
			0
		};
	}

	public void draw(Mat m) {
		m.setTo(new Scalar(0,0,0));
		Point[] points = new Point[NUM_JOINTS + 1];
		points[0] = head;
		Point from = head;
		for(int i = 0; i < NUM_JOINTS; i++) {
			Point to = calculatePoint(from, i);
			points[i+1] = to;
			drawBodyPart(m, from, to, i);
			from = to;
		}
		
		// Draw the bar on the lifter's back
		Core.circle(m, points[1], 30, new Scalar(255,255,255), -1);
	}
	
	private void drawBodyPart(Mat m, Point from, Point to, int toIndex) {
		Point centre = PointUtils.centre(from, to);
		RotatedRect r = new RotatedRect(centre, new Size(widths[toIndex], 10 + PointUtils.distance(from, to)), 90 + angles[toIndex]);

		Core.ellipse(m, r, new Scalar(255, 255, 255), -1);
	}

	private Point calculatePoint(Point from, int to) {
		double d = lengths[to];
		double x = from.x + d * Math.cos(Math.toRadians(angles[to]));
		double y = from.y + d * Math.sin(Math.toRadians(angles[to]));
		return new Point((int)x, (int)y);
	}
}
