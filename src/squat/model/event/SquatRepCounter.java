package squat.model.event;

import squat.model.Model;

public class SquatRepCounter implements ModelEventListener {

	private int reps;
	
	public SquatRepCounter() {
		reps = 0;
	}
	
	@Override
	public void onEvent(Model m) {
		reps++;
	}
	
	public ModelEventType getEventType() {
		return ModelEventType.SQUAT_BELOW_PARALLEL_END;
	}

	public int getReps() {
		return reps;
	}
}
