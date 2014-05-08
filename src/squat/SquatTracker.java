package squat;

import java.util.List;

import org.opencv.core.Mat;

import squat.model.Model;
import squat.model.event.ModelEventManager;
import squat.model.event.SquatRepCounter;
import squat.model.event.SquatRepScorer;
import squat.optimization.ModelFitter;
import squat.optimization.ModelFitterOptim;
import squat.utils.BackgroundSubtractor;

public class SquatTracker {
	
	private static final int FITTING_ITERATIONS = 2;
	
	private ModelFitter fitter;
	private SquatRepScorer squatScorer;
	private SquatRepCounter sqrc;
	private ModelEventManager modelEventManager;
	private BackgroundSubtractor bg;
	private Model model;
	private boolean finished = false;
	
	public SquatTracker(Model model, ModelEventManager modelEventManager, BackgroundSubtractor backgroundSubtractor) {
		squatScorer = new SquatRepScorer(modelEventManager);
		sqrc = new SquatRepCounter(modelEventManager);
		fitter = new ModelFitterOptim();
		//fitter = new ModelFitterManual(width, height);
		bg = backgroundSubtractor;
		this.model = model;
		this.modelEventManager = modelEventManager;
	}
	
	public void start() {
		sqrc.start();
		squatScorer.start();
	}
	
	public void update(Mat frame) {
		Mat foreground = bg.subtract(frame);
		
		for(int i = 0; i < FITTING_ITERATIONS; i++) {
			fitter.fit(model, foreground);
		}
		
		modelEventManager.update(model);
		
		double[] footPosition = model.getInitParams();

		double[] pix = foreground.get((int)footPosition[1], (int)footPosition[0]);

		finished = pix[0] < 1;
	}
	
	public boolean finished() {
		return finished;
	}
	
	public int getReps() {
		return sqrc.getReps();
	}
	
	public List<Double> getScores() {
		return squatScorer.getScores();
	}
}
