package squat.model.event;

import squat.model.Model;

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
				System.out.println("Start ascention");
				halfReps++;
			}
		});
		
		modelEventManager.addListener(ModelEventType.SQUAT_DESCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				System.out.println("Start descention");
				halfReps++;
			}
		});
	}

	public int getReps() {
		return halfReps / 2;
	}
}
