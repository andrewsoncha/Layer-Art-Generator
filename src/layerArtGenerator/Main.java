
package layerArtGenerator;
import java.awt.*;
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
		double notches = e.getWheelRotation()*0.1;
		if(notches<0) { //scroll up == zoom out
			System.out.println("Mouse weel moved Up by "+(-notches)+" notch(es)");
			if(mainObj.zoomLevel+notches>0) {
				mainObj.zoomLevel*=1.1;
			}
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
	ImageProcessingHandler imageProcessorObj;
	WindowMouseListener mouseListener;
	int imageOffsetX=0, imageOffsetY=0;
	int mouseX, mouseY;
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
				segmentedImg = imageProcessorObj.getSegmentedImage();
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
		showWindow.add(showPanel, BorderLayout.CENTER);
		
		JPanel lowerPanel = new JPanel();
		JButton mergeButton = new JButton("Merge");
		JButton clearButton = new JButton("Clear");
		lowerPanel.add(mergeButton); lowerPanel.add(clearButton);
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
		int newImageWidth = (int)(currentImg.getWidth() * zoomLevel);
		int newImageHeight = (int)(currentImg.getHeight() * zoomLevel);
		BufferedImage resizedImage = new BufferedImage(showWindow.getWidth() , showWindow.getHeight(), currentImg.getType());
		Graphics2D g = resizedImage.createGraphics();
		System.out.printf("imageOffsetX:%d   imageOffsetY:%d\n", imageOffsetX,imageOffsetY);
		//g.drawImage(currentImg, imageOffsetX, imageOffsetY, newImageWidth , newImageHeight , null);
		g.drawImage(currentImg, imageOffsetX, imageOffsetY, newImageWidth, newImageHeight , null);
		g.dispose();
		changeImage(resizedImage);
		return resizedImage;
	}
	void mouseClicked(int windowX, int windowY) {
		int imageX, imageY;
		imageX = (windowX-imageOffsetX)*currentImg.getWidth()/showWindow.getWidth();
		imageY = (windowY-imageOffsetY)*currentImg.getHeight()/showWindow.getHeight();
		int chosenArea = imageProcessorObj.selectArea(imageX, imageY);
		System.out.println(chosenArea);
		currentImg = imageProcessorObj.getUpdatedImage();
		redrawImage();
	}
	public static void main(String[] args) {
		Main asdf = new Main();
	}
}
