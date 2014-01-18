package squat.optimization;

import java.util.List;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import squat.model.Model;

public class ModelFitter {
	public Model fit(Model model, Mat frame, List<MatOfPoint> contours) {
		
		ModelFitFunction fitFunction = new ModelFitFunction(model, frame, contours);
		
		double[] initialModel = model.toDoubles();
		
		NelderMeadSimplex nelderMead = new NelderMeadSimplex(initialModel.length);
		
		
		SimplexOptimizer optim = new SimplexOptimizer(5,5);
		
		PointValuePair p = optim.optimize(
				nelderMead,
				new InitialGuess(initialModel),
				new MaxEval(10000),
				new MaxIter(10000),
				GoalType.MINIMIZE,
				new ObjectiveFunction(fitFunction));
		
		double[] results = p.getPoint();

		return new Model(results);
	}
}
