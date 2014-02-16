package squat.model;

import java.awt.Graphics;
import java.awt.Image;

import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Mat;
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
	
	private Point head;
	
	private double[] angles = new double[NUM_JOINTS];
	private double[] lengths = new double[NUM_JOINTS];
	private double[] widths = new double[NUM_JOINTS];
	
	public AngularModel(int headX, int headY) {
		this.head = new Point(headX, headY);
		initialiseWidths();
		initialiseLengths();
		
		testAnglesAndDistances();
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
		lengths[SHOULDER_HIP] = 60;
		lengths[HIP_KNEE] = 40;
		lengths[KNEE_ANKLE] = 40;
		lengths[ANKLE_TOE] = 20;
	}
	
	private void testAnglesAndDistances() {
		angles[HEAD_SHOULDER] = 90;
		lengths[HEAD_SHOULDER] = 20;
		angles[SHOULDER_HIP] = 90;
		lengths[SHOULDER_HIP] = 60;
		angles[HIP_KNEE] = 120;
		lengths[HIP_KNEE] = 40;
		angles[KNEE_ANKLE] = 45;
		lengths[KNEE_ANKLE] = 40;
		angles[ANKLE_TOE] = 180;
		lengths[ANKLE_TOE] = 20;
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
