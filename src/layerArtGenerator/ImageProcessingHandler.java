package layerArtGenerator;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
public class ImageProcessingHandler { //the functions could probably be divided better
	int width, height;
	BufferedImage origImg;
	BufferedImage hsvImg;
	short[][][] origPixelVal;
	short[][][] segmentedImg;
	AreaDivider dividerObj;
	public ImageProcessingHandler(BufferedImage inputImg) { //getting RGB values from BufferedImage. Probably the slow method but I don't want to do bit manipulation
		origImg = inputImg;
		width = origImg.getWidth();
		height = origImg.getHeight();
		
		origPixelVal = new short[width][height][3];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				int color = origImg.getRGB(i, j);
				short blue = (short)(color&0xff);
				short green = (short)((color&0xff00)>>8);
				short red = (short)((color&0xff0000)>>16);
				origPixelVal[i][j][0] = blue;
				origPixelVal[i][j][1] = green;
				origPixelVal[i][j][2] = red;
			}
		}
	}
	
	int selectArea(int imageX, int imageY) {
		if(imageX<0||imageY<0) {
			return -1;
		}
		if(imageX>=width||imageY>=height) {
			return -1;
		}
		int selectedIdx = dividerObj.selectArea(imageX, imageY);
		return selectedIdx;
	}
	
	static BufferedImage makeImageFromPix(short[][][] pixelVal,int width, int height) {
		BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = resultImage.getRaster();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				int[] pixVal = new int[3];
				pixVal[0]=(int)pixelVal[i][j][2];pixVal[1]=(int)pixelVal[i][j][1];pixVal[2]=(int)pixelVal[i][j][0];
				raster.setPixel(i,j,pixVal);
			}
		}
		return resultImage;
	}
	
	public BufferedImage getSegmentedImage(int HueN, int SatN, int ValN) {
		int[][][] hsvVal = getHSVImgFromRGB(origPixelVal);
		HSVPeakFinder climberObj = new HSVPeakFinder(hsvVal, width, height, HueN, SatN, ValN);
		int[][][] segmentedHSVVal = climberObj.getSegmentedImage();
		segmentedImg = getRGBImgFromHSV(segmentedHSVVal);
		
		//return makeImageFromPix(segmentedRGB, width, height);
		dividerObj = new AreaDivider(segmentedImg, width, height);
		dividerObj.divideAreas();
		return drawAreaEdges(origPixelVal, 1);
	}
	
	public BufferedImage getUpdatedImage() {
		return drawAreaEdges(origPixelVal, 1);
	}
	
	public void mergeSelected() {
		dividerObj.mergeSelectedAreas();
	}
	
	public void clearSelection() {
		dividerObj.clearSelection();
	}
	
	public void mergeSmallerAreas(int sizeThresh) {
		dividerObj.mergeSmallerAreas(sizeThresh);
	}
	
	public ArrayList<BufferedImage> getEveryAreaImage(){
		ArrayList<BufferedImage> result = new ArrayList<BufferedImage>();
		for(Area aI:dividerObj.areaMap.values()) {
			result.add(getAreaImage(aI));
		}
		return result;
	}
	
	BufferedImage getAreaImage(Area area) {
		short[][][] areaImg = new short[width][height][3];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				for(int k=0;k<3;k++) {
					areaImg[i][j][k]=255;
				}
			}
		}
		
		for(Pair pI: area.getPixCoor()) {
			areaImg[pI.x][pI.y][0]=0;areaImg[pI.x][pI.y][1]=0;areaImg[pI.x][pI.y][2]=0;
		}
		
		return makeImageFromPix(areaImg, width, height);
	}
	
	BufferedImage drawAreaEdges(short[][][] pixelVal, int distThresh) {
		short[][][] edgePixVal = new short[width][height][3];
		
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				edgePixVal[i][j][0]=pixelVal[i][j][0];edgePixVal[i][j][1]=pixelVal[i][j][1];edgePixVal[i][j][2]=pixelVal[i][j][2];
			}
		}
		
		for(int i: dividerObj.areaMap.keySet()) {
			Set<Pair> edge = dividerObj.areaMap.get(i).getEdges(distThresh);
			//System.out.printf("i:%d   edge size:%d\n", i,edge.size());
			
			Iterator<Pair> it = edge.iterator();
			while(it.hasNext()) {
				Pair coor = it.next();
				int x = coor.x, y=coor.y;
				edgePixVal[x][y][0]=0;edgePixVal[x][y][1]=0;edgePixVal[x][y][2]=0;
			}
		}
		
		Set<Area> selectedAreas = dividerObj.selectedAreas;   //highlight the edges of selected areas as white
		for(Area areaI: selectedAreas) {
			System.out.printf("i:%d\n", areaI.getIdx());
			Set<Pair> edge = areaI.getEdges(distThresh);
			//System.out.printf("i:%d   edge size:%d\n", i,edge.size());
			
			Iterator<Pair> it = edge.iterator();
			while(it.hasNext()) {
				Pair coor = it.next();
				int x = coor.x, y=coor.y;
				edgePixVal[x][y][0]=255;edgePixVal[x][y][1]=255;edgePixVal[x][y][2]=255;
			}
		}
		
		return makeImageFromPix(edgePixVal, width, height);
	}
	
	int[][][] getHSVImgFromRGB(short[][][] rgbVal) {
		int[][][] hsvVal = new int[width][height][3];   //[x][y][0:hue, 1:saturation, 2:value]
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				hsvVal[i][j] = MathAndConverter.RGBtoHSV(rgbVal[i][j][0],rgbVal[i][j][1],rgbVal[i][j][2]);
			}
		}
		return hsvVal;
	}
	
	short[][][] getRGBImgFromHSV(int[][][] hsvVal) {
		short[][][] rgbVal = new short[width][height][3];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				rgbVal[i][j] = MathAndConverter.HSVtoRGB(hsvVal[i][j][0],hsvVal[i][j][1],hsvVal[i][j][2]);
			}
		}
		return rgbVal;
	}
	public int getAreaN() {
		return dividerObj.areaMap.size();
	}
}
