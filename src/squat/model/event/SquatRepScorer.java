package squat.model.event;

import java.util.ArrayList;
import java.util.List;

import squat.model.Model;

public class SquatRepScorer {
	private SquatScorer scorer;
	private List<Double> scores;
	private List<String> mainContributors;
	private ModelEventManager modelEventManager;
	
	public SquatRepScorer(final ModelEventManager modelEventManager) {
		scores = new ArrayList<Double>();
		mainContributors = new ArrayList<String>();
		this.modelEventManager = modelEventManager;
	}
	
	public void start() {
		modelEventManager.addListener(ModelEventType.SQUAT_DESCEND_START, new ModelEventListener() {
			public void onEvent(Model m) {
				if(scorer != null) {
					scores.add(scorer.getCurrentScore());
					mainContributors.add(scorer.getMainContributor());
				}
				
				scorer = new SquatScorer(modelEventManager);
			}
		});
	}
	
	public List<Double> getScores() {
		int r = 0;
		for(Double d : scores) {
			r++;
			System.out.println("Rep: " + r + " Score: " + d + " Problem: " + mainContributors.get(r-1));
		}
		return scores;
	}
}
