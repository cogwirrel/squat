package squat.model.event;

import java.util.List;

import squat.model.Model;
import squat.tracking.SquatPhaseTracker;
import squat.utils.MultiMap;

public class ModelEventManager {
	MultiMap<ModelEventType, ModelEventListener> listeners =
			new MultiMap<ModelEventType, ModelEventListener>();
	
	private Model model;
	private SquatPhaseTracker tracker;
	
	private ModelEventManagerStateSwitch squatBelowParallel =
			new ModelEventManagerStateSwitch(
					ModelEventType.SQUAT_BELOW_PARALLEL_START,
					ModelEventType.SQUAT_BELOW_PARALLEL_END);
	
	private ModelEventManagerStateSwitch squatLockout =
			new ModelEventManagerStateSwitch(
					ModelEventType.SQUAT_LOCKOUT_START,
					ModelEventType.SQUAT_LOCKOUT_END);
	
	private ModelEventManagerStateSwitch squatKneeForward =
			new ModelEventManagerStateSwitch(
					ModelEventType.SQUAT_KNEE_FORWARD_START,
					ModelEventType.SQUAT_KNEE_FORWARD_END);
	
	private ModelEventManagerStateSwitch squatKneeBackward =
			new ModelEventManagerStateSwitch(
					ModelEventType.SQUAT_KNEE_BACKWARD_START,
					ModelEventType.SQUAT_KNEE_BACKWARD_END);
	
	private ModelEventManagerStateSwitch squatBadWeightDistribution =
			new ModelEventManagerStateSwitch(
					ModelEventType.SQUAT_BAD_WEIGHT_DISTRIBUTION_START,
					ModelEventType.SQUAT_BAD_WEIGHT_DISTRIBUTION_END);
	
	private ModelEventManagerStateSwitch squatOnHeelOrToe =
			new ModelEventManagerStateSwitch(
					ModelEventType.SQUAT_ON_HEEL_OR_TOE_START,
					ModelEventType.SQUAT_ON_HEEL_OR_TOE_END);
	
	private ModelEventManagerStateSwitch squatBadBackAngle =
			new ModelEventManagerStateSwitch(
					ModelEventType.SQUAT_BAD_BACK_ANGLE_START,
					ModelEventType.SQUAT_BAD_BACK_ANGLE_END);
	
	private ModelEventManagerStateSwitch squatPhase =
			new ModelEventManagerStateSwitch(
					ModelEventType.SQUAT_DESCEND_START,
					ModelEventType.SQUAT_ASCEND_START);
	
	public ModelEventManager() {
		tracker = new SquatPhaseTracker(5);
	}
	
	public void update(Model model) {
		this.model = model;
		
		// Every time we update, we call all TICK listeners
		callListeners(ModelEventType.TICK, model);
		
		if(squatBadForm(model)) {
			callListeners(ModelEventType.SQUAT_BAD_FORM, model);
		}
		
		// Stateful events that have starts and ends
		squatBelowParallel.update(model.isSquatBelowParallel());
		squatLockout.update(model.isSquatLockedOut());
		
		squatKneeForward.update(model.isSquatKneeForward());
		squatKneeBackward.update(model.isSquatKneeBackward());
		
		squatBadWeightDistribution.update(!model.isSquatWeightOverFeet());
		squatOnHeelOrToe.update(!model.isSquatHeelGrounded());
		
		squatBadBackAngle.update(!model.isSquatBackAngleInOptimalRange());
		
		tracker.add(model.getVerticalHipPosition());
		squatPhase.update(tracker.isDescending(), tracker.isAscending());
	}
	
	public void addListener(ModelEventType type, ModelEventListener listener) {
		listeners.put(type, listener);
	}
	
	public void removeListener(ModelEventType type, ModelEventListener listener) {
		listeners.remove(type, listener);
	}
	
	private boolean squatBadForm(Model model) {
		return model.isSquatKneeForward() || model.isSquatKneeBackward() ||
				!model.isSquatWeightOverFeet();
	}
	
	private void callListeners(ModelEventType type, Model model) {
		List<ModelEventListener> listenersForType = listeners.get(type);
		if(listenersForType != null) {
			for(ModelEventListener listener : listenersForType) {
				listener.onEvent(model);
			}
		}
	}
	
	private class ModelEventManagerStateSwitch {
		private boolean previousState;
		private ModelEventType startEvent;
		private ModelEventType endEvent;
		
		
		public ModelEventManagerStateSwitch(
				ModelEventType startEventToRaise,
				ModelEventType endEventToRaise) {
			this.previousState = false;
			this.startEvent = startEventToRaise;
			this.endEvent = endEventToRaise;
		}
		
		public void update(boolean positiveState, boolean negativeState) {
			if(positiveState && !previousState) {
				// Our state has changed to true, so raise the "start" event
				callListeners(startEvent, model);
				previousState = true;
			} else if(negativeState && previousState) {
				// Our state has changed to false, so raise the "end" event
				callListeners(endEvent, model);
				previousState = false;
			}
		}
		
		public void update(boolean newState) {
			// When one state is given, assume the negative state is it negated
			update(newState, !newState);
		}
	}
	

	
}
