package squat.tracking;

import java.util.HashMap;
import java.util.Map;

import squat.model.Model;
import squat.model.event.ModelEventListener;
import squat.model.event.ModelEventManager;
import squat.model.event.ModelEventType;

public class SquatScorer {
	private int frameCount = 0;
	
	private Map<String, Integer> contributors;
	private Map<String, Double> maxPenalties;
	
	// Contributors that can lower the score
	private static final String WEIGHT_DISTRIBUTION = "Weight Distribution";
	private static final String BAD_BACK_ANGLE = "Bad Back Angle";
	private static final String KNEES_FORWARD = "Knees Forward";
	private static final String KNEES_BACKWARD = "Knees Backward";
	private static final String FOOT_PLACEMENT = "Foot Placement";
	private static final String ABOVE_PARALLEL = "Above Parallel";
	private static final String NO_LOCKOUT = "No Lockout";
	
	private BadFrameCounter badFrameCounter;
	private FrameCounter frameCounter;
	private ParallelChecker parallelChecker;
	private LockoutChecker lockoutChecker;
	
	private ModelEventManager modelEventManager;
	
	// This class is to be used to evaluate a single rep.
	public SquatScorer(ModelEventManager modelEventManager) {
		this.modelEventManager = modelEventManager;
		
		initialiseContributors();
		initialiseMaxPenalties();
		
		badFrameCounter = new BadFrameCounter();
		frameCounter = new FrameCounter();
		parallelChecker = new ParallelChecker();
		lockoutChecker = new LockoutChecker();
		
		modelEventManager.addListener(ModelEventType.SQUAT_BAD_FORM, badFrameCounter);
		modelEventManager.addListener(ModelEventType.TICK, frameCounter);
		modelEventManager.addListener(ModelEventType.SQUAT_BELOW_PARALLEL_START, parallelChecker);
		modelEventManager.addListener(ModelEventType.SQUAT_LOCKOUT_START, lockoutChecker);
	}
	
	private void initialiseContributors() {
		contributors = new HashMap<String, Integer>();
		
		contributors.put(WEIGHT_DISTRIBUTION, 0);
		contributors.put(BAD_BACK_ANGLE, 0);
		contributors.put(KNEES_FORWARD, 0);
		contributors.put(KNEES_BACKWARD, 0);
		contributors.put(FOOT_PLACEMENT, 0);
		
		// Assume didn't squat below parallel or lockout
		contributors.put(ABOVE_PARALLEL, Integer.MAX_VALUE);
		contributors.put(NO_LOCKOUT, Integer.MAX_VALUE);
	}
	
	private void initialiseMaxPenalties() {
		maxPenalties = new HashMap<String, Double>();
		
		// Percentage penalties (do not need to add to 100)
		maxPenalties.put(WEIGHT_DISTRIBUTION, 0.20);
		maxPenalties.put(BAD_BACK_ANGLE, 0.20);
		maxPenalties.put(KNEES_FORWARD, 0.20);
		maxPenalties.put(KNEES_BACKWARD, 0.10);
		maxPenalties.put(FOOT_PLACEMENT, 0.10);
		maxPenalties.put(ABOVE_PARALLEL, 0.30);
		maxPenalties.put(NO_LOCKOUT, 0.30);
	}
	
	private class BadFrameCounter implements ModelEventListener {
		public void onEvent(Model m) {
			if(!m.isSquatWeightOverFeet()) {
				contributors.put(WEIGHT_DISTRIBUTION, contributors.get(WEIGHT_DISTRIBUTION) + 1);
			}
			
			if(!m.isSquatBackAngleInOptimalRange()) {
				contributors.put(BAD_BACK_ANGLE, contributors.get(BAD_BACK_ANGLE) + 1);
			}
			
			if(m.isSquatKneeForward()) {
				contributors.put(KNEES_FORWARD, contributors.get(KNEES_FORWARD) + 1);
			}
			
			if(m.isSquatKneeBackward()) {
				contributors.put(KNEES_BACKWARD, contributors.get(KNEES_BACKWARD) + 1);
			}
		}
	}
	
	private class FrameCounter implements ModelEventListener {
		public void onEvent(Model m) {
			frameCount++;
		}
	}
	
	private class ParallelChecker implements ModelEventListener {
		public void onEvent(Model m) {
			contributors.put(ABOVE_PARALLEL, 0);
		}
	}
	
	private class LockoutChecker implements ModelEventListener {
		public void onEvent(Model m) {
			contributors.put(NO_LOCKOUT, 0);
		}
	}
	
	public double getCurrentScore() {
		if(frameCount == 0) {
			return 0;
		}
		
		double score = (1 - calculatePenalty()) * 100;
		
		// Remove negative scores!
		score = Math.max(0, score);
		
		return score;
	}
	
	private double calculatePenalty() {
		double penalty = 0;
		for(String contributor : contributors.keySet()) {
			int penaltyFrames = contributors.get(contributor);
			double maxPenalty = maxPenalties.get(contributor);
			double contributorPenalty = (double)penaltyFrames / (double)frameCount;
			
			// Normalise penalty within max penalty
			// eg. 50% of time bad weight distro means penalty of 0.1 (as max penalty 0.2)
			contributorPenalty *= maxPenalty;
			
			// Cap the penalty at the maximum value
			if(contributorPenalty > maxPenalty) {
				contributorPenalty = maxPenalty;
			}
			
			penalty += contributorPenalty;
		}
		return penalty;
	}
	
	public Map<String, Integer> getContributors() {
		return contributors;
	}
	
	public String getMainContributor() {
		
		int maxContribution = 0;
		String mainContributor = "Perfect Squat";
		for (Map.Entry<String, Integer> entry : contributors.entrySet()) {
		    if(entry.getValue() > maxContribution) {
		    	maxContribution = entry.getValue();
		    	mainContributor = entry.getKey();
		    }
		}
		return mainContributor;
	}
	
	public void shutdown() {
		modelEventManager.removeListener(ModelEventType.SQUAT_BAD_FORM, badFrameCounter);
		modelEventManager.removeListener(ModelEventType.TICK, frameCounter);
		modelEventManager.removeListener(ModelEventType.SQUAT_BELOW_PARALLEL_START, parallelChecker);
	}
}
