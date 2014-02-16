package squat.utils;

import org.opencv.core.Point;

public class PointUtils {
	public static Point centre(Point p, Point q) {
		return new Point((p.x + q.x) / 2, (p.y + q.y)/ 2);
	}
	
	public static double distance(Point p, Point q) {
		return Math.sqrt(Math.pow(p.x - q.x, 2) + Math.pow(p.y - q.y, 2));
	}
}
