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
	int origWidth, origHeight;
	public ExportWindow(Main mainObj, ImageProcessingHandler imageProcessorObj) {
		this.mainObj = mainObj;
		origWidth = mainObj.origImg.getWidth();
		origHeight = mainObj.origImg.getHeight();
		this.imageProcessorObj = imageProcessorObj;
		System.out.println("ExportWindow Constructor Called!\n");
		setLayout(new BorderLayout());
		
		showPanel = new JPanel();
		mouseListener = new WindowMouseListener(this);
		showPanel.addMouseListener(mouseListener);
		showPanel.addMouseMotionListener(mouseListener);
		showPanel.addMouseWheelListener(mouseListener);
		
		currentImg = imageProcessorObj.getSelectedAreaImage(false);
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
		
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageProcessorObj.clearSelection();
				currentImg = imageProcessorObj.getSelectedAreaImage(false);
				redrawImage();
			}
		});
		JButton writeButton = new JButton("Write to File");
		writeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedImage saveImg = imageProcessorObj.getSelectedAreaImage(true);
				writeImage(saveImg);
			}
		});
		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new FlowLayout());
		lowerPanel.add(clearButton);lowerPanel.add(writeButton);
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
	void mouseClicked(int windowX, int windowY) {
		int imageX, imageY;
		System.out.printf("x:%f    y:%f\n",(double)(windowX-imageOffsetX)/currentImg.getWidth(),(double)(windowY-imageOffsetY)/currentImg.getHeight());
		imageX = (windowX-imageOffsetX)*origWidth/currentWidth;
		imageY = (windowY-imageOffsetY)*origHeight/currentHeight;
		int chosenArea = imageProcessorObj.selectArea(imageX, imageY);
		System.out.println(chosenArea);
		currentImg = imageProcessorObj.getSelectedAreaImage(false);
		redrawImage();
	}
}
