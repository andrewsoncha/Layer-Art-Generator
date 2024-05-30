package layerArtGenerator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
		
		currentImg = imageProcessorObj.getBiggestAreaImage();
		setSize(new Dimension(1000,800));
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mainObj.exportClose();
			}
		});
		
		JLabel picLabel = new JLabel(new ImageIcon(redrawImage()));
		showPanel.add(picLabel);
		add(showPanel);
		setVisible(true);
	}
}
