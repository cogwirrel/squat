package squat.model.event;

import java.util.List;

import squat.model.Model;
import squat.utils.MultiMap;

public class ModelEventManager {
	MultiMap<ModelEventType, ModelEventListener> listeners =
			new MultiMap<ModelEventType, ModelEventListener>();
	
	private boolean previouslyBelowParallel = false;
	
	public void update(Model model) {
		// Check angles and call appropriate listeners
		callListeners(ModelEventType.TICK, model);
		
		if(model.isSquatBelowParallel() && !previouslyBelowParallel) {
			callListeners(ModelEventType.SQUAT_BELOW_PARALLEL_START, model);
			previouslyBelowParallel = true;
		} else if(!model.isSquatBelowParallel() && previouslyBelowParallel) {
			callListeners(ModelEventType.SQUAT_BELOW_PARALLEL_END, model);
			previouslyBelowParallel = false;
		}
	}
	
	public void addListener(ModelEventType type, ModelEventListener listener) {
		listeners.put(type, listener);
	}
	
	private void callListeners(ModelEventType type, Model model) {
		List<ModelEventListener> listenersForType = listeners.get(type);
		if(listenersForType != null) {
			for(ModelEventListener listener : listenersForType) {
				listener.onEvent(model);
			}
		}
	}
}
