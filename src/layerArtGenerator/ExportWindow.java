package layerArtGenerator;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ExportWindow extends ImageShowWindow{
	Main mainObj;
	public ExportWindow(Main mainObj, ImageProcessingHandler imageProcessorObj) {
		this.mainObj = mainObj;
		this.imageProcessorObj = imageProcessorObj;
		System.out.println("ExportWindow Constructor Called!\n");
		setLayout(new BorderLayout());
		
		showPanel = new JPanel();
		mouseListener = new WindowMouseListener(this);
		showPanel.addMouseListener(mouseListener);
		showPanel.addMouseMotionListener(mouseListener);
		showPanel.addMouseWheelListener(mouseListener);
		
		currentImg = imageProcessorObj.getUpdatedImage();
		setSize(new Dimension(1000,800));
		
		JLabel picLabel = new JLabel(new ImageIcon(redrawImage()));
		showPanel.add(picLabel);
		setVisible(true);
	}
}
