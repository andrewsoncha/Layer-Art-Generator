
package layerArtGenerator;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;


public class Main extends ImageShowWindow{
	BufferedImage origImg;
	BufferedImage segmentedImg;
	JPanel parameterPanel;
	JSpinner hueNSpinner, satNSpinner, valNSpinner;
	JSpinner sizeThresholdSpinner;
	boolean exportOpen; //flag indicating whether there is an export window open. If so, don't carry out click functions or button press.
	Main mainObj;
	JLabel picLabel;
	public Main() {
		mainObj = this;
		FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.LOAD);
		fd.setDirectory("C:\\");
		fd.setVisible(true);
		String filename = fd.getDirectory()+fd.getFile();
		if (filename == null)
		  System.out.println("You cancelled the choice");
		else {
			System.out.println("You chose " + filename);
			try {
				origImg = ImageIO.read(new File(filename));
				imageProcessorObj = new ImageProcessingHandler(origImg);
				int HueN, SatN, ValN;
				SpinnerModel hueModel, satModel, valModel;
				hueNSpinner = new JSpinner(new SpinnerNumberModel(40, 1, 360, 1)); satNSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1)); valNSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
				HueN = 40;
				SatN = 10;
				ValN = 10;
				segmentedImg = imageProcessorObj.getSegmentedImage(HueN, SatN, ValN);
				currentImg = segmentedImg;
				setUpWindow();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	void setUpWindow() {
		showPanel = new JPanel();
		mouseListener = new WindowMouseListener(this);
		showPanel.addMouseListener(mouseListener);
		showPanel.addMouseMotionListener(mouseListener);
		showPanel.addMouseWheelListener(mouseListener);
		
		//showPanel.setSize(new Dimension(pic.getWidth(), pic.getHeight()));
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton redivButton = new JButton("Redivide Image");
		redivButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!exportOpen) {
					int HueN, SatN, ValN;
					HueN = (int)hueNSpinner.getValue(); SatN = (int)satNSpinner.getValue(); ValN = (int)valNSpinner.getValue();
					System.out.printf("Redivide Image %d %d %d\n",HueN, SatN, ValN);
					segmentedImg = imageProcessorObj.getSegmentedImage(HueN, SatN, ValN);
					currentImg = segmentedImg;
					showImage(redrawImage());
				}
			}
		});
		parameterPanel = new JPanel();
		parameterPanel.setLayout(new FlowLayout());
		parameterPanel.add(new JLabel("Hue Bucket N:"));parameterPanel.add(hueNSpinner);
		parameterPanel.add(new JLabel("Sat Bucket N:")); parameterPanel.add(satNSpinner);
		parameterPanel.add(new JLabel("Val Bucket N:")); parameterPanel.add(valNSpinner);
		parameterPanel.add(redivButton);
		add(parameterPanel, BorderLayout.NORTH);
		
		add(showPanel, BorderLayout.CENTER);
		
		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BorderLayout());
		
		/*JPanel spinPanel = new JPanel();
		spinPanel.setLayout(new FlowLayout());
		hueNSpinner = new JSpinner(new SpinnerNumberModel(40, 1, 360, 1));
		satNSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
		valNSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
		spinPanel.add(hueNSpinner);spinPanel.add(satNSpinner);spinPanel.add(valNSpinner);*/
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton mergeSmallerButton = new JButton("Merge Smaller Areas");
		sizeThresholdSpinner = new JSpinner(new SpinnerNumberModel(40, 1, Integer.MAX_VALUE, 1));
		mergeSmallerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!exportOpen) {
					int sizeThreshold = (int)sizeThresholdSpinner.getValue();
					imageProcessorObj.mergeSmallerAreas(sizeThreshold);
					currentImg = imageProcessorObj.getUpdatedImage();
					redrawImage();
				}
			}
		});
		JButton mergeSelectedButton = new JButton("Merge Selected Areas");
		mergeSelectedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!exportOpen) {
					imageProcessorObj.mergeSelected();
					currentImg = imageProcessorObj.getUpdatedImage();
					redrawImage();
				}
			}
		});
		JButton clearButton = new JButton("Clear Selection");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!exportOpen) {
					imageProcessorObj.clearSelection();
					currentImg = imageProcessorObj.getUpdatedImage();
					redrawImage();
				}
			}
		});
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!exportOpen) {
					ExportWindow exportObject = new ExportWindow(mainObj, imageProcessorObj);
					exportOpen = true;
				}
			}
		});
		
		buttonPanel.add(new JLabel("Area Size Threshold:")); buttonPanel.add(sizeThresholdSpinner);
		buttonPanel.add(mergeSmallerButton); buttonPanel.add(mergeSelectedButton); buttonPanel.add(clearButton); buttonPanel.add(exportButton);
		lowerPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(lowerPanel, BorderLayout.SOUTH);
		setSize(new Dimension(1000,800));
		
		picLabel = new JLabel(new ImageIcon(redrawImage()));
		showPanel.add(picLabel);
		showImage(redrawImage());
	}
	void showImage(BufferedImage pic) {
		picLabel.setIcon(new ImageIcon(pic));
		setVisible(true);
	}
	void exportClose() {
		exportOpen = false;
	}
	void mouseClicked(int windowX, int windowY) {
		int imageX, imageY;
		System.out.printf("x:%f    y:%f\n",(double)(windowX-imageOffsetX)/currentImg.getWidth(),(double)(windowY-imageOffsetY)/currentImg.getHeight());
		imageX = (windowX-imageOffsetX)*origImg.getWidth()/currentWidth;
		imageY = (windowY-imageOffsetY)*origImg.getHeight()/currentHeight;
		int chosenArea = imageProcessorObj.selectArea(imageX, imageY);
		System.out.println(chosenArea);
		currentImg = imageProcessorObj.getUpdatedImage();
		redrawImage();
	}
	public static void main(String[] args) {
		Main asdf = new Main();
	}
}
