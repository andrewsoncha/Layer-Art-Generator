package layerArtGenerator;

import java.awt.image.BufferedImage;

public class ImageProcessingHandler { //the functions could probably be divided better
	int width, height;
	BufferedImage origImg;
	short[][][] origPixelVal;   //RGB pixel values
	int[][][] hsvVal;
	BufferedImage hsvImg;
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
	
	static double abs(double x) {
		return (x<0)?-x:x;
	}
	static int abs(int x) {
		return (x<0)?-x:x;
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
		
		int Hue = 0, Saturation, Value;//Hue range 0~360. Saturation range 0~100. Value range: 
		if(delta==0) {
			Hue = 0;
		}
		switch(chCMax) {
			case 'R':
				Hue = (int)(60*(((Gc-Bc)/delta)%6));
				break;
			case 'G':
				Hue = (int)(60*(((Gc-Rc)/delta)+2));
				break;
			case 'B':
				Hue = (int)(60*(((Rc-Gc)/delta)+4));
				break;
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
		Sd = S/255.0; Vd = V/255.0;
		double C = Sd*Vd;
		double X = C*(1-abs((H/60)%2-1));
		double m = V-C;
		
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
		
		short result[]= {R,G,B};
		return result;
	}
	
	void getHSVVal() {
		hsvVal = new int[width][height][3];   //[x][y][0:hue, 1:saturation, 2:value]
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				hsvVal[i][j] = RGBtoHSV(origPixelVal[i][j][0],origPixelVal[i][j][1],origPixelVal[i][j][2]);
			}
		}
	}
	
	void getRGBImgFromHSV() {
		short[][][] rgbVals = new short[width][height][3];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				rgbVals[i][j] = HSVtoRGB(hsvVal[i][j][0],hsvVal[i][j][1],hsvVal[i][j][2]);
			}
		}
	}
}

class HSVHillClimber{
	int HueN;
	int SatN;
	int ValN;
	int[][][] hist;
	int width, height;
	public HSVHillClimber(int[][][] HSVVal, int width, int height, int HueN, int SatN, int ValN) {
		this.HueN = HueN;
		this.SatN = SatN;
		this.ValN = ValN;
		this.width = width;
		this.height = height;
		
		hist = new int[HueN][SatN][ValN];
		makeHist(HSVVal, width, height);
	}
	void makeHist(int[][][] HSVVal) {
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				int H,S,V;
				H = HSVVal[i][j][0];
				S = HSVVal[i][j][1];
				V = HSVVal[i][j][2];
				
				int HBucket, SBucket, VBucket;
				HBucket = H/(360/HueN);
				SBucket = S/(100/SatN);
				VBucket = V/(100/ValN);
				
				hist[HBucket][SBucket][VBucket]++;
			}
		}
	}
	public int[][][] makeHSVFromHist() {
		int[][][] resultHSV = new int[width][height][3];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				int H,S,V;
				H = 
			}
		}
	}
}
