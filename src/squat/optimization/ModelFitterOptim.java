package squat.optimization;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.opencv.core.Mat;

import squat.model.Model;

public class ModelFitterOptim implements ModelFitter {
	public void fit(Model model, Mat frame) {
		ModelFitFunction fitFunction = new ModelFitFunction(frame, model);
		
		MultivariateOptimizer optim = new BOBYQAOptimizer(2*model.get().length);
		
		try {
			PointValuePair p = optim.optimize(
					new InitialGuess(model.get()),
					new MaxEval(5000),
					GoalType.MINIMIZE,
					new ObjectiveFunction(fitFunction),
					new SimpleBounds(model.getLowerBounds(), model.getUpperBounds())
				);
				
				double[] results = p.getPoint();
				model.set(results);
		} catch(Exception e) {
			//e.printStackTrace();
		}
		
	}
}
