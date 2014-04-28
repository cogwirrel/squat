package squat.model.event;

import java.util.ArrayList;
import java.util.List;

import squat.model.Model;

public class SquatPhaseTracker {
	private List<Double> hipLocations = new ArrayList<Double>();
	private int numLocations;
	private double thresh;
	
	public SquatPhaseTracker(int numLocations) {
		this.numLocations = numLocations;
		this.thresh = numLocations;
	}
	
	public void add(double location) {
		hipLocations.add(location);
		
		if(hipLocations.size() > numLocations) {
			hipLocations.remove(0);
		}
	}
	
	public boolean isDescending() {
		return !allSimilar() && descendingOrder();
	}
	
	public boolean isAscending() {
		return !allSimilar() && ascendingOrder();
	}
	
	private boolean allSimilar() {
		return (maximum() - minimum()) < thresh;
	}
	
	private boolean descendingOrder() {
		double curr = hipLocations.get(0);
		for(int i = 1; i < hipLocations.size(); i++) {
			if(hipLocations.get(i) > curr) {
				return false;
			}
			curr = hipLocations.get(i);
		}
		return true;
	}
	
	private boolean ascendingOrder() {
		double curr = hipLocations.get(0);
		for(int i = 1; i < hipLocations.size(); i++) {
			if(hipLocations.get(i) < curr) {
				return false;
			}
			curr = hipLocations.get(i);
		}
		return true;
	}
	
	private double minimum() {
		double min = Double.MAX_VALUE;
		for(Double d : hipLocations) {
			if(d < min) {
				min = d;
			}
		}
		return min;
	}
	
	private double maximum() {
		double max = Double.MIN_VALUE;
		for(Double d : hipLocations) {
			if(d > max) {
				max = d;
			}
		}
		return max;
	}
	
	private double average() {
		double total = 0;
		for(Double d : hipLocations) {
			total += d;
		}
		return total / hipLocations.size();
	}
}
