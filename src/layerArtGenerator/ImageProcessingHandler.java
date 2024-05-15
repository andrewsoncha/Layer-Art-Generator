package layerArtGenerator;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

class MathAndConverter{
	static double abs(double x) {
		return (x<0)?-x:x;
	}
	static int abs(int x) {
		return (x<0)?-x:x;
	}
	static int mod(int x, int m) {
		if(x>=0) {
			return x%m;
		}
		else {
			return x+(-x/m+1)*m;
		}
	}
	static double mod(double x, int m) {
		if(x>=0) {
			return x%m;
		}
		else {
			return x+(int)(-x/m+1)*m;
		}
	}
	static int[] RGBtoHSV(short R, short G, short B) {
		double Rc,Gc,Bc;
		Rc = R/255.0;Gc = G/255.0;Bc = B/255.0;
		double Cmax, Cmin;
		Cmax = (((Rc>Gc)?Rc:Gc)>Bc)?((Rc>Gc)?Rc:Gc):Bc;
		Cmin = (((Rc<Gc)?Rc:Gc)<Bc)?((Rc<Gc)?Rc:Gc):Bc;
		char chCMax, chCMin;
		chCMax = (((R>G)?R:G)>B)?((R>G)?'R':'G'):'B';
		chCMin = (((R<G)?R:G)<B)?((R<G)?'R':'G'):'B';
		double delta = Cmax-Cmin;
		
		int Hue=0, Saturation, Value;//Hue range 0~360(degrees). Saturation range 0~100(percentage). Value range: 0~100(percentage).
		if(delta==0) {
			Hue = 0;
		}
		else{
			switch(chCMax) {
				case 'R':
					Hue = (int)(60*mod(((Gc-Bc)/delta),6));
					break;
				case 'G':
					Hue = (int)(60*(((Bc-Rc)/delta)+2));
					break;
				case 'B':
					Hue = (int)(60*(((Rc-Gc)/delta)+4));
					break;
			}
		}
		
		if(Cmax==0) {
			Saturation = 0;
		}
		else {
			Saturation = (int)(100*(delta/Cmax));
		}
		
		Value = (int)(100*Cmax);
		
		int[] result = {Hue, Saturation, Value};
		return result;
	}
	
	static short[] HSVtoRGB(int H, int S, int V) {
		double Sd, Vd;
		Sd = S/100.0; Vd = V/100.0;
		double C = Sd*Vd;
		double X = C*(1-abs((H/60)%2-1));
		double m = Vd-C;
		double Rd=0,Gd=0,Bd=0;
		switch(H/60) {
		case 0:
			Rd = C;
			Gd = X;
			Bd = 0;
			break;
		case 1:
			Rd = X;
			Gd = C;
			Bd = 0;
			break;
		case 2:
			Rd = 0;
			Gd = C;
			Bd = X;
			break;
		case 3:
			Rd = 0;
			Gd = X;
			Bd = C;
			break;
		case 4:
			Rd = X;
			Gd = 0;
			Bd = C;
			break;
		case 5:
			Rd = C;
			Gd = 0;
			Bd = X;
			break;
		}
		
		short R,G,B;
		R = (short)((Rd+m)*255); G = (short)((Gd+m)*255);B = (short)((Bd+m)*255);
		
		//System.out.printf("H:%d  S:%d  V:%d   R:%d G:%d B:%d\n", H,S,V,R,G,B);
		short result[]= {R,G,B};
		return result;
	}
}

public class ImageProcessingHandler { //the functions could probably be divided better
	int width, height;
	BufferedImage origImg;
	BufferedImage hsvImg;
	short[][][] origPixelVal;
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
	
	public BufferedImage getSegmentedImage() {
		int[][][] hsvVal = getHSVImgFromRGB(origPixelVal);
		HSVPeakFinder climberObj = new HSVPeakFinder(hsvVal, width, height, 20, 10, 10);
		int[][][] segmentedHSVVal = climberObj.getSegmentedImage();
		short[][][] segmentedRGB = getRGBImgFromHSV(segmentedHSVVal);
		
		BufferedImage segmentedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = segmentedImage.getRaster();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				int[] pixVal = new int[3];
				pixVal[0]=(int)segmentedRGB[i][j][2];pixVal[1]=(int)segmentedRGB[i][j][1];pixVal[2]=(int)segmentedRGB[i][j][0];
				raster.setPixel(i,j,pixVal);
			}
		}
		return segmentedImage;
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
}

class HSVPeakFinder{
	int HueN;
	int SatN;
	int ValN;
	int[][][] hist;
	ArrayList<int[]> peaks;
	int[][][] assignedPeak;
	int width, height;
	int[][][] ImgHSVVal;
	public HSVPeakFinder(int[][][] HSVVal, int width, int height, int HueN, int SatN, int ValN) {
		this.HueN = HueN;
		this.SatN = SatN;
		this.ValN = ValN;
		this.width = width;
		this.height = height;
		this.ImgHSVVal = HSVVal;
		
		hist = new int[HueN][SatN][ValN];
		makeHist();
	}
	public int[][][] getSegmentedImage(){
		if(width==0||height==0) {
			return null;
		}
		findHistPeaks();
		assignPeak();
		int[][][] segmentedImage = drawPeakImg();
		return segmentedImage;
	}
	void makeHist() {
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				int H,S,V;
				H = ImgHSVVal[i][j][0];
				S = ImgHSVVal[i][j][1];
				V = ImgHSVVal[i][j][2];
				
				int HBucket, SBucket, VBucket;
				HBucket = (H-1)/(360/HueN);
				SBucket = (S-1)/(100/SatN);
				VBucket = (V-1)/(100/ValN);
				
				hist[HBucket][SBucket][VBucket]++;
			}
		}
	}
	int modIn(int x, int modVal) {
		if(x<0) {
			return x+(x/modVal+1)*modVal;
		}
		else {
			return x%modVal;
		}
	}
	void findHistPeaks(){
		peaks = new ArrayList<int[]>();
		for(int hI=0;hI<HueN;hI++) {
			for(int sI=0;sI<SatN;sI++) {
				for(int vI=0;vI<ValN;vI++) {
					int current = hist[hI][sI][vI];
					boolean hasHigherNeighbor = false;
					if(current==0) {
						continue;
					}
					if(current<30) {
						continue;
					}
					for(int i=-1;i<=1;i++) {//iterate over all 26 neighbors in 3d color space. Hue coordinates is modular.
						for(int j=-1;j<=1;j++) {
							if(sI+j<0||sI+j>=SatN) {
								continue;
							}
							for(int k=-1;k<=1;k++) {
								if(vI+k<0||vI+k>=ValN) {
									continue;
								}
								if(hist[modIn(hI+i,HueN)][sI+j][vI+k]>current) {
									hasHigherNeighbor = true;
								}
							}
							if(hasHigherNeighbor) {
								break;
							}
						}
						if(hasHigherNeighbor) {
							break;
						}
					}
					
					if(!hasHigherNeighbor) {
							int[] peakCoordinates = {hI,sI,vI};
							peaks.add(peakCoordinates);
					}
				}
			}
		}
		for(int i=0;i<peaks.size();i++) {
			System.out.printf("peaks[%d]:  %d %d %d     %d\n", i,peaks.get(i)[0],peaks.get(i)[1],peaks.get(i)[2],hist[peaks.get(i)[0]][peaks.get(i)[1]][peaks.get(i)[2]]);
		}
	}
	void assignPeak() {
		int peakN = peaks.size();
		if(peakN==0) {
			return;
		}
		
		assignedPeak = new int[HueN][SatN][ValN];
		for(int hI=0;hI<HueN;hI++) {
			for(int sI=0;sI<SatN;sI++) {
				for(int vI=0;vI<ValN;vI++) {
					int closestPeak=-1;
					int minDist=Integer.MAX_VALUE;
					for(int peakI=0;peakI<peakN;peakI++) {
						int peakH,peakS,peakV;
						int[] peakArr = peaks.get(peakI);
						peakH = peakArr[0]; peakS = peakArr[1]; peakV = peakArr[2];
						
						int dist = (peakH-hI)*(peakH-hI)/HueN+(peakS-sI)*(peakS-sI)/SatN+(peakV-vI)*(peakV-vI)/ValN;
						if(dist<minDist) {
							minDist = dist;
							closestPeak = peakI;
						}
					}
					assignedPeak[hI][sI][vI] = closestPeak;
					System.out.printf("hI:%d sI:%d vI:%d       peak:%d %d %d\n",hI,sI,vI,peaks.get(closestPeak)[0],peaks.get(closestPeak)[1],peaks.get(closestPeak)[2]);
				}
			}
		}
	}
	int[][][] drawPeakImg(){
		int[][][] resultHSVImg = new int[width][height][3];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				int[] origHSV = ImgHSVVal[i][j];
				int origH,origS,origV;
				origH=origHSV[0];origS=origHSV[1];origV=origHSV[2];
				int HBucket, SBucket, VBucket;
				HBucket = (origH-1)/(360/HueN);
				SBucket = (origS-1)/(100/SatN);
				VBucket = (origV-1)/(100/ValN);
				int peakI=-1;
				try {
					peakI = assignedPeak[HBucket][SBucket][VBucket];
				}catch(ArrayIndexOutOfBoundsException e) {
					System.out.printf("origH:%d   origS:%d   origV:%d\n", origH,origS,origV);
				}
				int peakH,peakS,peakV;
				peakH=peaks.get(peakI)[0];peakS=peaks.get(peakI)[1];peakV=peaks.get(peakI)[2];
				//System.out.printf("%d %d  peakI:%d   H:%d S:%d V:%d\n",i,j,peakI,peakH,peakS,peakV);
				resultHSVImg[i][j][0]=peakH*(360/HueN);resultHSVImg[i][j][1]=peakS*(100/SatN);resultHSVImg[i][j][2]=peakV*(100/ValN);
			}
		}
		return resultHSVImg;
	}
}
