package squat.model.event;

import squat.model.Model;

public class SquatScorer {
	private int frameCount = 0;
	private int badFrameCount = 0;
	
	private BadFrameCounter badFrameCounter;
	private FrameCounter frameCounter;
	private ModelEventManager modelEventManager;
	
	public SquatScorer(ModelEventManager modelEventManager) {
		this.modelEventManager = modelEventManager;
		
		badFrameCounter = new BadFrameCounter();
		frameCounter = new FrameCounter();
		
		modelEventManager.addListener(ModelEventType.SQUAT_BAD_FORM, badFrameCounter);
		
		modelEventManager.addListener(ModelEventType.TICK, frameCounter);
	}
	
	private class BadFrameCounter implements ModelEventListener {
		public void onEvent(Model m) {
			badFrameCount++;
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
	
	public void shutdown() {
		modelEventManager.removeListener(ModelEventType.SQUAT_BAD_FORM, badFrameCounter);
		modelEventManager.removeListener(ModelEventType.TICK, frameCounter);
	}
}
