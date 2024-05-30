package layerArtGenerator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
		add(showPanel, BorderLayout.CENTER);
		
		JButton writeButton = new JButton("Write to File");
		writeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeImage(currentImg);
			}
		});
		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new FlowLayout());
		lowerPanel.add(writeButton);
		add(lowerPanel, BorderLayout.SOUTH);
		setVisible(true);
	}
	void writeImage(BufferedImage saveImage) {
		FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.SAVE);
		fd.setDirectory("C:\\");
		fd.setFile("*.jpg;*.png");
		fd.setVisible(true);
		String filename = fd.getDirectory()+fd.getFile();
		System.out.println("filename:"+filename);
		File file = new File(filename);
		try {
			ImageIO.write(saveImage, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
