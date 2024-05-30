package layerArtGenerator;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class WindowMouseListener implements MouseWheelListener, MouseListener, MouseMotionListener{
	ImageShowWindow windowObj;
	boolean dragged = false;
	int dragX, dragY;
	int clickX, clickY;
	public WindowMouseListener(ImageShowWindow windowObj) {
		this.windowObj = windowObj;
	}
	
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if(notches<=0) { //scroll up == zoom out
			System.out.println("Mouse weel moved Up by "+(-notches)+" notch(es)");
			windowObj.zoomLevel*=1.1;
		}
		else { //scroll down == zoom in
			System.out.println("Mouse weel moved Down by "+(notches)+" notch(es)");
			//if(windowObj.zoomLevel+notches<5.0) {
				windowObj.zoomLevel/=1.1;
			//}
		}
		System.out.println("zoomLevel:"+windowObj.zoomLevel);
		BufferedImage newImage = windowObj.redrawImage();
		windowObj.changeImage(newImage);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(dragged==false) {
			dragX = e.getX()-windowObj.imageOffsetX;
			dragY = e.getY()-windowObj.imageOffsetY;
			dragged = true;
		}
		else {
			int dx = e.getX()-dragX;
			int dy = e.getY()-dragY;
			System.out.printf("getX:%d   getY:%d\n",e.getX(), e.getY());
			System.out.printf("dx:%d   dy:%d\n", dx,dy);
			windowObj.imageOffsetX = dx;
			windowObj.imageOffsetY = dy;
			windowObj.redrawImage();
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
		windowObj.mouseClicked(clickX, clickY);
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

public class ImageShowWindow extends JFrame{
	double zoomLevel =1.0;
	BufferedImage currentImg;
	JPanel showPanel;
	ImageProcessingHandler imageProcessorObj;
	WindowMouseListener mouseListener;
	int imageOffsetX=0, imageOffsetY=0;
	int currentWidth, currentHeight;
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
			resizedImage = new BufferedImage(this.getWidth() , this.getHeight(), currentImg.getType());
		}
		Graphics2D g = resizedImage.createGraphics();
		System.out.printf("imageOffsetX:%d   imageOffsetY:%d\n", imageOffsetX,imageOffsetY);
		//g.drawImage(currentImg, imageOffsetX, imageOffsetY, newImageWidth , newImageHeight , null);
		g.drawImage(currentImg, imageOffsetX, imageOffsetY, currentWidth, currentHeight , null);
		g.dispose();
		changeImage(resizedImage);
		return resizedImage;
	}
	void mouseClicked(int mouseX, int mouseY) {
	}
}
