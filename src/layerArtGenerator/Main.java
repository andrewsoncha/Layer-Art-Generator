
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
	JLabel areaCntLabel;
	JSpinner hueNSpinner, satNSpinner, valNSpinner;
	boolean exportOpen; //flag indicating whether there is an export window open. If so, don't carry out click functions or button press.
	Main mainObj;
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
				if(hueNSpinner!=null&&satNSpinner!=null&&valNSpinner!=null) {
					HueN = (int)hueNSpinner.getValue();
					SatN = (int)satNSpinner.getValue();
					ValN = (int)valNSpinner.getValue();
				}
				else {
					HueN = 40;
					SatN = 10;
					ValN = 10;
				}
				segmentedImg = imageProcessorObj.getSegmentedImage(HueN, SatN, ValN);
				currentImg = segmentedImg;
				showImage(segmentedImg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	void showImage(BufferedImage pic) {
		showPanel = new JPanel();
		mouseListener = new WindowMouseListener(this);
		showPanel.addMouseListener(mouseListener);
		showPanel.addMouseMotionListener(mouseListener);
		showPanel.addMouseWheelListener(mouseListener);
		
		//showPanel.setSize(new Dimension(pic.getWidth(), pic.getHeight()));
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		areaCntLabel = new JLabel("Count: "+imageProcessorObj.getAreaN());
		add(areaCntLabel, BorderLayout.NORTH);
		
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
		mergeSmallerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!exportOpen) {
					imageProcessorObj.mergeSmallerAreas(40);
					currentImg = imageProcessorObj.getUpdatedImage();
					areaCntLabel.setText("Count: "+imageProcessorObj.getAreaN());
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
					areaCntLabel.setText("Count: "+imageProcessorObj.getAreaN());
					redrawImage();
				}
			}
		});
		JButton clearButton = new JButton("Clear");
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
		buttonPanel.add(mergeSmallerButton); buttonPanel.add(mergeSelectedButton); buttonPanel.add(clearButton); buttonPanel.add(exportButton);
		lowerPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(lowerPanel, BorderLayout.SOUTH);
		setSize(new Dimension(1000,800));
		
		JLabel picLabel = new JLabel(new ImageIcon(redrawImage()));
		showPanel.add(picLabel);
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
