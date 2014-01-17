package squat.optimization;

import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.opencv.core.Mat;

import squat.model.Model;

public class ModelFitter {
	public Model fit(Model model, Mat frame) {
		ModelFitFunction f = new ModelFitFunction(frame);
		
		double[] initialModel = model.toDoubles();
		
		NelderMeadSimplex nelderMead = new NelderMeadSimplex(initialModel.length);
		MultivariateOptimizer optim = new SimplexOptimizer(3,10);
		
		PointValuePair p = optim.optimize(nelderMead);
		
		double[] results = p.getPoint();

		return new Model(results);
	}
}
