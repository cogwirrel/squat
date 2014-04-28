package squat.model.event;

import squat.model.Model;

public class SquatScorer {
	private int frameCount = 0;
	private int badFrameCount = 0;
	
	public SquatScorer(ModelEventManager modelEventManager) {
		modelEventManager.addListener(ModelEventType.SQUAT_BAD_FORM, new ModelEventListener() {
			public void onEvent(Model m) {
				badFrameCount++;
			}
		});
		
		modelEventManager.addListener(ModelEventType.TICK, new ModelEventListener() {
			public void onEvent(Model m) {
				frameCount++;
			}
		});
	}
	
	public double getCurrentScore() {
		return (1 - ((double)badFrameCount / (double)frameCount)) * 100;
	}
}
