package squat.tracking;

import java.util.List;

import squat.utils.Pair;

public interface SquatPipelineListener {
	public void onReadyToSquat();
	public void onInitialModelFit();
	public void onSquatsComplete(List<Pair<Double,String>>scores);
}
