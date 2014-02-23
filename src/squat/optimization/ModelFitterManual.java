package squat.optimization;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;

import squat.model.Model;
import squat.utils.VideoDisplay;

public class ModelFitterManual implements ModelFitter {

	private VideoDisplay display;
	private JFrame jFrame;
	private final int width;
	private final int height;
	
	public ModelFitterManual(int width, int height) {
		display = new VideoDisplay("Manual Fitting", width, height);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void fit(final Model model, Mat frame) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jFrame = new JFrame("Manual Fitting Controls");
				jFrame.setSize(width, height);
				jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				JPanel panel = makePanel(model);
				
				jFrame.setContentPane(panel);
				jFrame.pack();
				jFrame.setVisible(true);
			}
		});
	}
	
	private JPanel makePanel(Model model) {
		JPanel panel = new JPanel();
		
		double[] values = model.get();
		double[] lb = model.getLowerBounds();
		double[] ub = model.getUpperBounds();
		
		for(int i = 0; i < values.length; i++) {
			JSlider slider = new JSlider((int)lb[i], (int)ub[i]);
			panel.add(slider);
		}
		
		return panel;
	}
}
