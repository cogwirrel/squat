package squat.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

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
		videoOutputPanel.setImage(toBufferedImage(m));
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
	
	private BufferedImage toBufferedImage(Mat m) throws Exception {
		int type = BufferedImage.TYPE_BYTE_GRAY;
	    if ( m.channels() > 1 ) {
	        Mat m2 = new Mat();
	        Imgproc.cvtColor(m,m2,Imgproc.COLOR_BGR2RGB);
	        type = BufferedImage.TYPE_3BYTE_BGR;
	        m = m2;
	    }
	    byte [] b = new byte[m.channels()*m.cols()*m.rows()];
	    m.get(0,0,b); // get all the pixels
	    BufferedImage im = new BufferedImage(m.cols(),m.rows(), type);
	    im.getRaster().setDataElements(0, 0, m.cols(),m.rows(), b);
	    return im;
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
				Color originalColor = g.getColor();
				g.setColor(Color.GREEN);
				model.draw(g);
				g.setColor(originalColor);
			}
		}
	}
	
	
}