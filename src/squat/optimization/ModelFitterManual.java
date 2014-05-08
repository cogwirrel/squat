package squat.optimization;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Mat;
import squat.model.Model;
import squat.utils.VideoDisplay;
import squat.utils.VideoTools;

public class ModelFitterManual implements ModelFitter {

	private VideoDisplay display;
	private JFrame jFrame;
	private final int width;
	private final int height;
	private boolean done = false;
	
	public ModelFitterManual(int width, int height) {
		display = new VideoDisplay("Manual Fitting", width, height);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void fit(final Model model, final Mat frame) {
		done = false;
		
		Mat m = new Mat(frame.size(), frame.type());
		model.draw(m);
		
		display.show(VideoTools.blend(frame, m));
		display.draw();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jFrame = new JFrame("Manual Fitting Controls");
				jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				JPanel panel = makePanel(model, frame);
				
				jFrame.setContentPane(panel);
				jFrame.setPreferredSize(new Dimension(width, height));
				jFrame.pack();
				jFrame.setVisible(true);
			}
		});
		
		while(!done) {}
	}
	
	private JPanel makePanel(final Model model, final Mat frame) {
		JPanel panel = new JPanel();
		
		double[] values = model.get();
		double[] lb = model.getLowerBounds();
		double[] ub = model.getUpperBounds();
		
		for(int i = 0; i < values.length; i++) {
			final int index = i;
			final JSlider slider = new JSlider((int)lb[i], (int)ub[i]);
			slider.setMinorTickSpacing(5);
		    slider.setMajorTickSpacing(25);
			slider.setPaintTicks(true);
		    slider.setSnapToTicks(true);
		    slider.setValue((int)values[i]);
		    slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					double[] vals = model.get();
					vals[index] = slider.getValue();
					model.set(vals);
					Mat m = new Mat(frame.size(), frame.type());
					model.draw(m);
					
					System.out.println(slider.getValue());

					display.show(VideoTools.blend(frame, m));
					display.draw();
				}
		    	
		    });
			panel.add(slider);
		}
		
		JButton button = new JButton("Done");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				done = true;
			}
			
		});
		
		panel.add(button);
		
		return panel;
	}
}
