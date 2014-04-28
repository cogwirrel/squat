package squat.model.event;

import java.util.HashMap;
import java.util.Map;

import squat.model.Model;

public class SquatScorer {
	private int frameCount = 0;
	private int badFrameCount = 0;
	
	private Map<String, Integer> contributors;
	
	private static final String WEIGHT_DISTRIBUTION = "Weight Distribution";
	private static final String KNEES_FORWARD = "Knees Forward";
	private static final String KNEES_BACKWARD = "Knees Backward";
	private static final String FOOT_PLACEMENT = "Foot Placement";
	private static final String ABOVE_PARALLEL = "Above Parallel";
	
	private static final double DEPTH_PENALTY = 0.30; // As a percentage
	
	private BadFrameCounter badFrameCounter;
	private FrameCounter frameCounter;
	private ParallelChecker parallelChecker;
	
	private ModelEventManager modelEventManager;
	
	private boolean didSquatBelowParallel = false;
	
	// This class is to be used to evaluate a single rep.
	public SquatScorer(ModelEventManager modelEventManager) {
		this.modelEventManager = modelEventManager;
		
		initialiseContributors();
		
		badFrameCounter = new BadFrameCounter();
		frameCounter = new FrameCounter();
		parallelChecker = new ParallelChecker();
		
		modelEventManager.addListener(ModelEventType.SQUAT_BAD_FORM, badFrameCounter);
		modelEventManager.addListener(ModelEventType.TICK, frameCounter);
		modelEventManager.addListener(ModelEventType.SQUAT_BELOW_PARALLEL_START, parallelChecker);
	}
	
	private void initialiseContributors() {
		contributors = new HashMap<String, Integer>();
		
		contributors.put(WEIGHT_DISTRIBUTION, 0);
		contributors.put(KNEES_FORWARD, 0);
		contributors.put(KNEES_BACKWARD, 0);
		contributors.put(FOOT_PLACEMENT, 0);
		contributors.put(ABOVE_PARALLEL, 0);
	}
	
	private class BadFrameCounter implements ModelEventListener {
		public void onEvent(Model m) {
			
			if(!m.isSquatWeightOverFeet()) {
				contributors.put(WEIGHT_DISTRIBUTION, contributors.get(WEIGHT_DISTRIBUTION) + 1);
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
			didSquatBelowParallel = true;
		}
	}
	
	public double getCurrentScore() {
		if(frameCount == 0) {
			return 0;
		}
		
		if(!didSquatBelowParallel) {
			contributors.put(ABOVE_PARALLEL, (int)(frameCount * DEPTH_PENALTY));
		}
		
		double score = (1 - ((double)badFrameCount() / (double)frameCount)) * 100;
		
		// Remove negative scores!
		score = Math.max(0, score);
		
		return score;
	}
	
	private int badFrameCount() {
		int count = 0;
		for(Integer i : contributors.values()) {
			count += i;
		}
		return count;
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
