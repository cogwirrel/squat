package squat.model;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Mat;

import squat.utils.Pair;

public class Model {
	
	public enum Joint {
		ANKLE,
		KNEE,
		HIP,
		SHOULDER,
		HEAD,
		//ELBOW,
		//HAND,
	}
	
	private Map<Joint, Point> joints;

	private List<Pair<Joint, Joint>> connections;
	
	public Model() {
		// Create an array of zeros
		this(new double[Joint.values().length*2]);
		setJointsToPositionForDrawTest();
	}
	
	public Model(double[] inJoints) {
		joints = new HashMap<Joint, Point>();
		
		Joint[] allJoints = Joint.values();
		for(int i = 0; i < allJoints.length; i++) {
			joints.put(allJoints[i], new Point((int)inJoints[2*i], (int)inJoints[2*i+1]));
		}
		
		connectJoints();
	}
	
	public Map<Joint, Point> getJoints() {
		return joints;
	}
	
	private void connectJoints() {
		connections = new ArrayList<Pair<Joint, Joint>>();
		
		connections.add(new Pair<Joint, Joint>(Joint.ANKLE, Joint.KNEE));
		connections.add(new Pair<Joint, Joint>(Joint.KNEE, Joint.HIP));
		connections.add(new Pair<Joint, Joint>(Joint.HIP, Joint.SHOULDER));
		connections.add(new Pair<Joint, Joint>(Joint.SHOULDER, Joint.HEAD));
		//connections.add(new Pair<Joint, Joint>(Joint.SHOULDER, Joint.ELBOW));
		//connections.add(new Pair<Joint, Joint>(Joint.ELBOW, Joint.HAND));
	}
	
	private void setJointsToPositionForDrawTest() {
		joints.get(Joint.ANKLE).x = 100;
		joints.get(Joint.ANKLE).y = 300;
		
		joints.get(Joint.KNEE).x = 60;
		joints.get(Joint.KNEE).y = 250;
		
		joints.get(Joint.HIP).x = 100;
		joints.get(Joint.HIP).y = 200;
		
		joints.get(Joint.SHOULDER).x = 100;
		joints.get(Joint.SHOULDER).y = 100;
		
		joints.get(Joint.HEAD).x = 100;
		joints.get(Joint.HEAD).y = 50;
		
//		joints.get(Joint.ELBOW).x = 80;
//		joints.get(Joint.ELBOW).y = 150;
//		
//		joints.get(Joint.HAND).x = 60;
//		joints.get(Joint.HAND).y = 100;
	}

	public void draw(Graphics g) {
		for(Pair<Joint, Joint> connection : connections) {
			Point from = joints.get(connection.l);
			Point to = joints.get(connection.r);
			g.drawLine(from.x, from.y, to.x, to.y);
		}
	}
	
	/**
	 * @return the model represented as an array of doubles
	 */
	public double[] toDoubles() {
		Joint[] allJoints = Joint.values();
		
		double[] doubles = new double[allJoints.length*2];
		
		for(int i = 0; i < allJoints.length; i++) {
			Point p = joints.get(allJoints[i]);
			doubles[2*i] = p.getX();
			doubles[2*i+1] = p.getY();
		}
		
		return doubles;
	}
	
	/**
	 * @return the height of the model
	 */
	public double height() {
		double h = 0;
		
		h += joints.get(Joint.HEAD).distance(joints.get(Joint.SHOULDER));
		h += joints.get(Joint.SHOULDER).distance(joints.get(Joint.HIP));
		h += joints.get(Joint.HIP).distance(joints.get(Joint.KNEE));
		h += joints.get(Joint.KNEE).distance(joints.get(Joint.ANKLE));
		
		return h;
	}
	
	public double proportionDifference(Model m) {
		
		Map<Joint, Point> theirJoints = m.getJoints();
		
		double difference = 0;
		for(Pair<Joint, Joint> connection : connections) {
			double ourDist = joints.get(connection.l).distance(joints.get(connection.r));
			double theirDist = theirJoints.get(connection.l).distance(theirJoints.get(connection.r));
			difference += Math.abs(theirDist - ourDist);
		}
		return difference;
	}
}
