package layerArtGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;

class Area{
	private short[] color;
	private Set<Pair> pixCoor;
	private Set<Area> neighbors;
	private int idxCode;
	private AreaDivider dividerObj;
	public Area(short[] color, int idxCode, AreaDivider dividerObj) {
		this.color = color;
		this.idxCode = idxCode;
		this.dividerObj = dividerObj;
		pixCoor = new HashSet<Pair>();
		neighbors = new HashSet<Area>();
	}
	int getIdx() {
		return idxCode;
	}
	short[] getColor() {
		return color;
	}
	Set<Pair> getPixCoor(){
		return pixCoor;
	}
	Set<Area> getNeighbors(){
		return neighbors;
	}
	int getSize() {
		if(pixCoor==null) {
			return 0;
		}
		else {
			return pixCoor.size();
		}
	}
	void addCoor(int x, int y) {
		Pair coor = new Pair(x,y);
		pixCoor.add(coor);
	}
	void addNeighbor(Area newNeighbor) {
		neighbors.add(newNeighbor);
	}
	void addNeighbor(Set<Area> newNeighbors) {
		neighbors.addAll(newNeighbors);
	}
	void removeNeighbor(Area toRemove) {
		neighbors.remove(toRemove);
	}
	boolean isEdge(Pair coor, int distThresh) { //pixels that are less further than distThresh from a different area pixel or the edge of the image is the edge of the area.
												//ex: if coor[a][b] and coor[a-3][b+3] are different areas and the distThresh is 3, coor[a][b] is an edge pixel.
		int x = coor.x, y=coor.y;
		if(!pixCoor.contains(coor)) {
			return false;
		}
		for(int i=-distThresh;i<=distThresh;i++) {
			for(int j=-distThresh;j<=distThresh;j++) {
				Pair neighbor = new Pair(x+i,y+j);
				if(neighbor.x>0&&neighbor.x<dividerObj.width&&neighbor.y>0&&neighbor.y<dividerObj.height) { //I really don't like accessing dividerObj to do things within Area class but
																											//but this is the only way I can get rid of the bug where edge border after merging areas remain
					if(dividerObj.areaIdx[neighbor.x][neighbor.y]!=idxCode) {
						return true;
					}
				}
			}
		}
		return false;
	}
	Set<Pair> getEdges(int distThresh){
		
		Set<Pair> edgeCoor = new HashSet<Pair>();
		
		Iterator<Pair> it = pixCoor.iterator();
		while(it.hasNext()) {
			Pair coor = it.next();
			if(isEdge(coor, distThresh)==true) {
				edgeCoor.add(coor);
			}
		}
		return edgeCoor;
	}
	void merge(Area otherArea) {
		Set<Pair> otherCoors = otherArea.pixCoor;
		Set<Area> otherNeighbors = otherArea.neighbors;
		for(Pair pix : otherCoors) {
			dividerObj.areaIdx[pix.x][pix.y]= idxCode; 
		}
		for(Area neighbor : new HashSet<Area>(otherNeighbors)) {
			neighbor.removeNeighbor(otherArea);
			neighbor.addNeighbor(this);
		}
		pixCoor.addAll(otherCoors);
		neighbors.addAll(otherNeighbors);
	}
}

public class AreaDivider {
	short[][][] pixVals;
	int width, height;
	HashMap<Integer, Area> areaMap;
	int[][] areaIdx; //index of the area in ArrayList<Area> areaList in which the pixel belongs to.
	Set<Area> selectedAreas;
	
	public AreaDivider(short[][][] pixVals, int width, int height) {
		this.pixVals = pixVals;
		this.width = width;
		this.height = height;
		areaMap = new HashMap<Integer, Area>();
		selectedAreas = new HashSet<Area>();
	}
	Area bfs(boolean[][] visit, int startingX, int startingY, int currentAreaIdx) {
		short[] color = pixVals[startingX][startingY];
		System.out.printf("bfs:  %d %d %d   (%d,%d,%d)\n", startingX,startingY,currentAreaIdx,color[0],color[1],color[2]);
		Area resultArea = new Area(color,currentAreaIdx, this);
		Queue<Pair> coorQ = new LinkedList<Pair>();
		Pair startingCoor = new Pair(startingX,startingY);
		coorQ.add(startingCoor);
		visit[startingX][startingY] = true;
		resultArea.addCoor(startingX,startingY);
		while(coorQ.size()>0) {
			Pair currentCoor = coorQ.remove();
			int x = currentCoor.x,y = currentCoor.y;
			areaIdx[x][y] = currentAreaIdx;
			//System.out.printf("(%d,%d)  idx:%d\n", x,y,currentAreaIdx);

			for(int i=-1;i<=1;i++) {
				for(int j=-1;j<=1;j++) {
					Pair neighbor = new Pair(x+i,y+j);
					if(x+i<0||x+i>width-1||y+j<0||y+j>height-1) {
						continue;
					}
					//System.out.printf("currentX:%d  currentY:%d   %d\n", neighbor.x,neighbor.y, (visit.contains(currentCoor)==true)?1:0);
					if(visit[neighbor.x][neighbor.y]==false) {
						short[] neighborColor = pixVals[neighbor.x][neighbor.y];
						if(neighborColor[0]==color[0]&&neighborColor[1]==color[1]&&neighborColor[2]==color[2]) {
							coorQ.add(neighbor);
							visit[neighbor.x][neighbor.y]=true;
							resultArea.addCoor(neighbor.x, neighbor.y);
						}
					}
				}
			}
		}
		System.out.printf("area size:%d\n", resultArea.getPixCoor().size());
		System.out.printf("edges size:%d\n", resultArea.getEdges(3).size());
		return resultArea;
	}
	void divideAreas() {
		boolean[][] visit = new boolean[width][height];
		areaIdx = new int[width][height];
		int cnt=0;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				Pair current = new Pair(i,j);
				if(visit[current.x][current.y]==false) {
					areaMap.put(cnt, bfs(visit, i,j, cnt));
					cnt++;
				}
			}
		}
		
		for(Area area : areaMap.values()){
			int currentAreaCode = area.getIdx();
			for(Pair pix : area.getEdges(1)) {
				int x = pix.x, y=pix.y;
				if(x-1>0) {
					if(areaIdx[x-1][y]!=currentAreaCode) {
						Area neighbor = areaMap.get(areaIdx[x-1][y]);
						area.addNeighbor(neighbor);
					}
				}
				if(y-1>0) {
					if(areaIdx[x][y-1]!=currentAreaCode) {
						Area neighbor = areaMap.get(areaIdx[x][y-1]);
						area.addNeighbor(neighbor);
					}
				}
				if(x+1<width) {
					if(areaIdx[x+1][y]!=currentAreaCode) {
						Area neighbor = areaMap.get(areaIdx[x+1][y]);
						area.addNeighbor(neighbor);
					}
				}
				if(y+1<height) {
					if(areaIdx[x][y+1]!=currentAreaCode) {
						Area neighbor = areaMap.get(areaIdx[x][y+1]);
						area.addNeighbor(neighbor);
					}
				}
			}
		}
	}
	int selectArea(int imageX, int imageY) {
		int selectedIdx = areaIdx[imageX][imageY];
		selectedAreas.add(areaMap.get(selectedIdx));
		return selectedIdx;
	}
	
	Area getBiggestArea(Set<Area> areaSet) {
		int maxCnt=0;
		Area biggestArea = null;
		int biggestAreaIdx=-1;
		for(Area selectedArea : areaSet) {
			if(maxCnt<selectedArea.getSize()) {
				maxCnt = selectedArea.getSize();
				biggestArea = selectedArea;
				biggestAreaIdx = selectedArea.getIdx();
			}
		}
		return biggestArea;
	}
	
	void mergeSelectedAreas() {
		Area biggestArea = null;
		int biggestIdx;
		biggestArea = getBiggestArea(selectedAreas);
		biggestIdx = biggestArea.getIdx();
		
		for(Area selectedArea : selectedAreas) {
			if(biggestArea!=selectedArea) {
				biggestArea.merge(selectedArea);
				for(Pair replacedCoor : selectedArea.getPixCoor()) {
					areaIdx[replacedCoor.x][replacedCoor.y]=biggestIdx;
				}
				areaMap.remove(selectedArea.getIdx());
			}
		}
		
		selectedAreas.clear();
	}
	
	void clearSelection() {
		selectedAreas.clear();
	}
	
	void mergeSmallerAreas(int sizeThreshold) {
		int tooSmallCnt=0;
		for(Area area : areaMap.values()) {
			if(area.getSize()<sizeThreshold) {
				Area biggestNeighbor = getBiggestArea(area.getNeighbors());
				biggestNeighbor.merge(area);
				tooSmallCnt++;
			}
			System.out.printf("tooSmallCnt:%d\n", tooSmallCnt);
		}
		clearSelection();
	}
}
