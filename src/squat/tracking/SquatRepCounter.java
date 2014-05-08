package squat.tracking;

import squat.model.Model;
import squat.model.event.ModelEventListener;
import squat.model.event.ModelEventManager;
import squat.model.event.ModelEventType;

public class SquatRepCounter {

	private int halfReps;
	private ModelEventManager modelEventManager;
	
	public SquatRepCounter(ModelEventManager modelEventManager) {
		halfReps = 0;
		this.modelEventManager = modelEventManager;
	}
	
	public void start() {
		modelEventManager.addListener(ModelEventType.SQUAT_ASCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				halfReps++;
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_DESCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				halfReps++;
			}
		});
	}

	public int getReps() {
		// Integer division means reps rounded down, so only complete reps count
		return halfReps / 2;
	}
}
