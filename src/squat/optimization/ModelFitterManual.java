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
	private JFrame frame;
	
	public ModelFitterManual(final int width, final int height) {
		display = new VideoDisplay("Manual Fitting", width, height);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("Manual Fitting Controls");
				frame.setSize(width, height);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				JPanel panel = new JPanel();
				panel.setLayout(new FlowLayout(FlowLayout.CENTER));
				panel.add(new JSlider(0, 360));
				
				frame.setContentPane(panel);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	@Override
	public void fit(Model model, Mat frame) {
		
		
	}	
}
