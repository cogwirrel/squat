package squat.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;

import squat.model.Model;

public class VideoDisplay {
	private JFrame frame;
	private final VideoOutputPanel videoOutputPanel;
	
	public VideoDisplay(final String name, final int width, final int height) {
		videoOutputPanel = new VideoOutputPanel(width, height);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame(name);
				frame.setSize(width, height);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setContentPane(videoOutputPanel);
				frame.pack();
				frame.setVisible(true);
			}
		});
		
	}
	
	public void show(Mat m) throws Exception {
		videoOutputPanel.setImage(VideoTools.toBufferedImage(m));
	}
	
	public void show(Model m) {
		videoOutputPanel.setModel(m);
	}
	
	public void draw() {
		videoOutputPanel.repaint();
	}
	
	public void close() {
		frame.dispose();
	}
	
	private class VideoOutputPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Image image;
		private Model model;
		private Dimension size;
		
		public VideoOutputPanel(int width, int height) {
			size = new Dimension(width, height);
		}
		
		public void setModel(Model m) {
			model = m;
		}

		public void setImage(Image image) {
			this.image = image;
		}
		
		@Override
		public Dimension getPreferredSize() {
			return size;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if(image != null) {
				g.drawImage(image.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_FAST), 0, 0, null);
			}
			
			if(model != null) {
				Mat m = new Mat(this.getWidth(), this.getHeight(), 16);
				model.draw(m);
				Image im = VideoTools.toBufferedImage(m);
				g.drawImage(im.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_FAST), 0, 0, null);
			}
		}
	}
	
	
}
