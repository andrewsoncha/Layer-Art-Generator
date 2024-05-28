
package layerArtGenerator;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

class WindowMouseListener implements MouseWheelListener, MouseListener, MouseMotionListener{
	Main mainObj;
	boolean dragged = false;
	int dragX, dragY;
	int clickX, clickY;
	public WindowMouseListener(Main mainObj) {
		this.mainObj = mainObj;
	}
	
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if(notches<=0) { //scroll up == zoom out
			System.out.println("Mouse weel moved Up by "+(-notches)+" notch(es)");
			mainObj.zoomLevel*=1.1;
		}
		else { //scroll down == zoom in
			System.out.println("Mouse weel moved Down by "+(notches)+" notch(es)");
			//if(mainObj.zoomLevel+notches<5.0) {
				mainObj.zoomLevel/=1.1;
			//}
		}
		System.out.println("zoomLevel:"+mainObj.zoomLevel);
		BufferedImage newImage = mainObj.redrawImage();
		mainObj.changeImage(newImage);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(dragged==false) {
			dragX = e.getX()-mainObj.imageOffsetX;
			dragY = e.getY()-mainObj.imageOffsetY;
			dragged = true;
		}
		else {
			int dx = e.getX()-dragX;
			int dy = e.getY()-dragY;
			System.out.printf("getX:%d   getY:%d\n",e.getX(), e.getY());
			System.out.printf("dx:%d   dy:%d\n", dx,dy);
			mainObj.imageOffsetX = dx;
			mainObj.imageOffsetY = dy;
			mainObj.redrawImage();
			dragged = true;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		clickX = e.getX();
		clickY = e.getY();
		System.out.printf("clickX:%d   clickY:%d\n", clickX,clickY);
		mainObj.mouseClicked(clickX, clickY);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragged = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

public class Main extends JFrame{
	double zoomLevel =1.0;
	BufferedImage origImg;
	BufferedImage segmentedImg;
	BufferedImage currentImg;
	JFrame showWindow;
	JPanel showPanel;
	JLabel areaCntLabel;
	ImageProcessingHandler imageProcessorObj;
	WindowMouseListener mouseListener;
	int imageOffsetX=0, imageOffsetY=0;
	int currentWidth, currentHeight;
	JSpinner hueNSpinner, satNSpinner, valNSpinner;
	public Main() {
		this.setTitle("FileDialogTest");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600,400);
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
		this.setVisible(true);
	}
	void showImage(BufferedImage pic) {
		showPanel = new JPanel();
		
		//showPanel.setSize(new Dimension(pic.getWidth(), pic.getHeight()));
		showWindow = new JFrame();
		showWindow.setLayout(new BorderLayout());
		mouseListener = new WindowMouseListener(this);
		showWindow.addMouseListener(mouseListener);
		showWindow.addMouseMotionListener(mouseListener);
		showWindow.addMouseWheelListener(mouseListener);
		showWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		areaCntLabel = new JLabel("Count: "+imageProcessorObj.getAreaN());
		showWindow.add(areaCntLabel, BorderLayout.NORTH);
		
		showWindow.add(showPanel, BorderLayout.CENTER);
		
		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BorderLayout());
		
		JPanel spinPanel = new JPanel();
		spinPanel.setLayout(new FlowLayout());
		hueNSpinner = new JSpinner(new SpinnerNumberModel(40, 1, 360, 1));
		satNSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
		valNSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
		spinPanel.add(hueNSpinner);spinPanel.add(satNSpinner);spinPanel.add(valNSpinner);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton mergeSmallerButton = new JButton("Merge Smaller Areas");
		mergeSmallerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageProcessorObj.mergeSmallerAreas(40);
				currentImg = imageProcessorObj.getUpdatedImage();
				areaCntLabel.setText("Count: "+imageProcessorObj.getAreaN());
				redrawImage();
			}
		});
		JButton mergeSelectedButton = new JButton("Merge Selected Areas");
		mergeSelectedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageProcessorObj.mergeSelected();
				currentImg = imageProcessorObj.getUpdatedImage();
				areaCntLabel.setText("Count: "+imageProcessorObj.getAreaN());
				redrawImage();
			}
		});
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageProcessorObj.clearSelection();
				currentImg = imageProcessorObj.getUpdatedImage();
				redrawImage();
			}
		});
		buttonPanel.add(mergeSmallerButton); buttonPanel.add(mergeSelectedButton); buttonPanel.add(clearButton);
		lowerPanel.add(buttonPanel, BorderLayout.SOUTH);
		showWindow.add(lowerPanel, BorderLayout.SOUTH);
		showWindow.setSize(new Dimension(1000,800));
		
		JLabel picLabel = new JLabel(new ImageIcon(redrawImage()));
		showPanel.add(picLabel);
		showWindow.setVisible(true);
	}
	void changeImage(BufferedImage newImg) {
		JLabel newLabel = new JLabel(new ImageIcon(newImg));
		showPanel.removeAll();
		showPanel.add(newLabel);
		showPanel.updateUI();
	}
	BufferedImage redrawImage() {
		currentWidth = (int)(currentImg.getWidth() * zoomLevel);
		currentHeight = (int)(currentImg.getHeight() * zoomLevel);
		BufferedImage resizedImage=null;
		if(showPanel.getWidth()>0&&showPanel.getHeight()>0) {
			resizedImage = new BufferedImage(showPanel.getWidth() , showPanel.getHeight(), currentImg.getType());
		}
		else {
			resizedImage = new BufferedImage(showWindow.getWidth() , showWindow.getHeight(), currentImg.getType());
		}
		Graphics2D g = resizedImage.createGraphics();
		System.out.printf("imageOffsetX:%d   imageOffsetY:%d\n", imageOffsetX,imageOffsetY);
		//g.drawImage(currentImg, imageOffsetX, imageOffsetY, newImageWidth , newImageHeight , null);
		g.drawImage(currentImg, imageOffsetX, imageOffsetY, currentWidth, currentHeight , null);
		g.dispose();
		changeImage(resizedImage);
		return resizedImage;
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
