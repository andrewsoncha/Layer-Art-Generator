package layerArtGenerator;

import java.util.ArrayList;

public class HSVPeakFinder{
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
				peakI = assignedPeak[HBucket][SBucket][VBucket];
				int peakH,peakS,peakV;
				peakH=peaks.get(peakI)[0];peakS=peaks.get(peakI)[1];peakV=peaks.get(peakI)[2];
				//System.out.printf("%d %d  peakI:%d   H:%d S:%d V:%d\n",i,j,peakI,peakH,peakS,peakV);
				resultHSVImg[i][j][0]=peakH*(360/HueN);resultHSVImg[i][j][1]=peakS*(100/SatN);resultHSVImg[i][j][2]=peakV*(100/ValN);
			}
		}
		return resultHSVImg;
	}
}