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
	
	private BadFrameCounter badFrameCounter;
	private FrameCounter frameCounter;
	private ModelEventManager modelEventManager;
	
	public SquatScorer(ModelEventManager modelEventManager) {
		this.modelEventManager = modelEventManager;
		
		initialiseContributors();
		
		badFrameCounter = new BadFrameCounter();
		frameCounter = new FrameCounter();
		
		modelEventManager.addListener(ModelEventType.SQUAT_BAD_FORM, badFrameCounter);
		
		modelEventManager.addListener(ModelEventType.TICK, frameCounter);
	}
	
	private void initialiseContributors() {
		contributors = new HashMap<String, Integer>();
		
		contributors.put(WEIGHT_DISTRIBUTION, 0);
		contributors.put(KNEES_FORWARD, 0);
		contributors.put(KNEES_BACKWARD, 0);
		contributors.put(FOOT_PLACEMENT, 0);
	}
	
	private class BadFrameCounter implements ModelEventListener {
		public void onEvent(Model m) {
			badFrameCount++;
			
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
	
	public double getCurrentScore() {
		if(frameCount == 0) {
			return 0;
		}
				
		return (1 - ((double)badFrameCount / (double)frameCount)) * 100;
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
	}
}
