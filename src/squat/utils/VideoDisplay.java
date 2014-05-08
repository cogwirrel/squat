package squat.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;

public class VideoDisplay {
	private JFrame jFrame;
	private final VideoOutputPanel videoOutputPanel;
	
	public VideoDisplay(final String name, final int width, final int height) {
		videoOutputPanel = new VideoOutputPanel(width, height);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jFrame = new JFrame(name);
				jFrame.setSize(width, height);
				jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jFrame.setContentPane(videoOutputPanel);
				jFrame.pack();
				jFrame.setVisible(true);
			}
		});
		
	}
	
	public void show(Mat m) {
		videoOutputPanel.setFrame(m);
	}
	
	public void draw() {
		videoOutputPanel.repaint();
	}
	
	public void close() {
		jFrame.dispose();
	}
	
	private class VideoOutputPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Mat frame;
		private Dimension size;
		
		public VideoOutputPanel(int width, int height) {
			size = new Dimension(width, height);
		}

		public void setFrame(Mat frame) {
			this.frame = frame;
		}
		
		@Override
		public Dimension getPreferredSize() {
			return size;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if(frame != null) {
				Image image = VideoTools.toBufferedImage(frame);
				g.drawImage(image.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_FAST), 0, 0, null);
			}
		}
	}
	
	
}
