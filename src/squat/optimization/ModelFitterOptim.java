package squat.optimization;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.opencv.core.Mat;

import squat.model.Model;

public class ModelFitterOptim implements ModelFitter {
	public void fit(Model model, Mat frame) {
		ModelFitFunction fitFunction = new ModelFitFunction(frame, model);
		
		MultivariateOptimizer optim = new BOBYQAOptimizer(10);
		
		optim.optimize(
			new InitialGuess(model.get()),
			new MaxEval(20000),
			GoalType.MINIMIZE,
			new ObjectiveFunction(fitFunction),
			new SimpleBounds(model.getLowerBounds(), model.getUpperBounds())
		);
	}
	
	private void nelderMeadFit(Model model, Mat frame) {
		ModelFitFunction fitFunction = new ModelFitFunction(frame, model);
		
		NelderMeadSimplex nelderMead = new NelderMeadSimplex(model.get().length);
		
		SimplexOptimizer optim = new SimplexOptimizer(5,5);
		
		PointValuePair p = optim.optimize(
				nelderMead,
				new InitialGuess(model.get()),
				new MaxEval(1000000000),
				new MaxIter(1000000000),
				GoalType.MINIMIZE,
				new ObjectiveFunction(fitFunction)
		);
		
		double[] results = p.getPoint();
		model.set(results);
	}
}
