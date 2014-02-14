package squat.model;

import java.awt.Graphics;
import java.awt.Point;

public class AngularModel implements Model {
	
	private static final int HEAD_SHOULDER = 0;
	private static final int SHOULDER_HIP = 1;
	private static final int HIP_KNEE = 2;
	private static final int KNEE_ANKLE = 3;
	private static final int ANKLE_TOE = 4;
	
	private static final int NUM_JOINTS = 5;
	
	private Point head;
	
	private double[] angles = new double[NUM_JOINTS];
	private double[] distances = new double[NUM_JOINTS];
	
	public AngularModel(int headX, int headY) {
		this.head = new Point(headX, headY);
		
		testAnglesAndDistances();
	}
	
	private void testAnglesAndDistances() {
		angles[HEAD_SHOULDER] = 90;
		distances[HEAD_SHOULDER] = 20;
		angles[SHOULDER_HIP] = 90;
		distances[SHOULDER_HIP] = 60;
		angles[HIP_KNEE] = 120;
		distances[HIP_KNEE] = 40;
		angles[KNEE_ANKLE] = 45;
		distances[KNEE_ANKLE] = 40;
		angles[ANKLE_TOE] = 180;
		distances[ANKLE_TOE] = 20;
	}

	public void draw(Graphics g) {
		Point p1 = head;
		for(int i = 0; i < NUM_JOINTS; i++) {
			Point p2 = calculatePoint(p1, i);
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
			p1 = p2;
		}
	}
	
	private Point calculatePoint(Point from, int to) {
		double d = distances[to];
		double x = from.getX() + d * Math.cos(Math.toRadians(angles[to]));
		double y = from.getY() + d * Math.sin(Math.toRadians(angles[to]));
		return new Point((int)x, (int)y);
	}
}
