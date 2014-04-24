package squat.model.event;

import squat.model.Model;
import squat.utils.MultiMap;

public class ModelEventManager {
	MultiMap<ModelEventType, ModelEventListener> listeners =
			new MultiMap<ModelEventType, ModelEventListener>();
	
	public void update(Model model) {
		// Check angles and call appropriate listeners
		callListeners(ModelEventType.TICK, model);
		
		
	}
	
	public void addListener(ModelEventType type, ModelEventListener listener) {
		listeners.put(type, listener);
	}
	
	private void callListeners(ModelEventType type, Model model) {
		for(ModelEventListener listener : listeners.get(type)) {
			listener.onEvent(model);
		}
	}
}
