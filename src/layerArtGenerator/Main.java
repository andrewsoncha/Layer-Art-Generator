
package layerArtGenerator;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Main extends JFrame implements MouseWheelListener{
	double zoomLevel =1.0;
	BufferedImage origImg;
	BufferedImage segmentedImg;
	JFrame showWindow;
	JPanel showPanel;
	ImageProcessingHandler ImageProcessor;
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
				ImageProcessor = new ImageProcessingHandler(origImg);
				segmentedImg = ImageProcessor.getSegmentedImage();
				showImage(segmentedImg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.setVisible(true);
	}
	void showImage(BufferedImage pic) {
		JLabel picLabel = new JLabel(new ImageIcon(pic));
		showPanel = new JPanel();
		showPanel.add(picLabel);
		showWindow = new JFrame();
		showWindow.addMouseWheelListener(this);
		showWindow.setSize(new Dimension(pic.getWidth(), pic.getHeight()));
		showWindow.add(showPanel);
		showWindow.setVisible(true);
	}
	void changeImage(BufferedImage newImg) {
		JLabel newLabel = new JLabel(new ImageIcon(newImg));
		showPanel.removeAll();
		showPanel.add(newLabel);
		showPanel.updateUI();
	}
	BufferedImage zoomImage(BufferedImage originalImage, double zoomLevel) {
		int newImageWidth = (int)(originalImage.getWidth() * zoomLevel);
		int newImageHeight = (int)(originalImage.getHeight() * zoomLevel);
		BufferedImage resizedImage = new BufferedImage(newImageWidth , newImageHeight, originalImage.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newImageWidth , newImageHeight , null);
		g.dispose();
		return resizedImage;
	}
	public void mouseWheelMoved(MouseWheelEvent e) {
		double notches = e.getWheelRotation()*0.1;
		if(notches<0) { //scroll up == zoom out
			System.out.println("Mouse weel moved Up by "+(-notches)+" notch(es)");
			if(zoomLevel+notches>0) {
				zoomLevel+=notches;
			}
		}
		else { //scroll down == zoom in
			System.out.println("Mouse weel moved Down by "+(notches)+" notch(es)");
			if(zoomLevel+notches<2.0) {
				zoomLevel+=notches;
			}
		}
		System.out.println("zoomLevel:"+zoomLevel);
		BufferedImage newImage = zoomImage(segmentedImg, zoomLevel);
		changeImage(newImage);
	}
	public static void main(String[] args) {
		Main asdf = new Main();
	}
}
