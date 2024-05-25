package layerArtGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;

class Area{
	short[] color;
	Set<Pair> pixCoor;
	public Area(short[] color) {
		this.color = color;
		pixCoor = new HashSet<Pair>();
	}
	void addCoor(int x, int y) {
		Pair coor = new Pair(x,y);
		pixCoor.add(coor);
	}
	boolean isEdge(Pair coor, int distThresh) { //pixels that are less further than distThresh from a different area pixel or the edge of the image is the edge of the area.
												//ex: if coor[a][b] and coor[a-2][b+2] are different areas and the distThresh is 3, coor[a][b] is an edge pixel.
		int x = coor.x, y=coor.y;
		
		boolean flag = false;
		for(int i=-distThresh;i<=distThresh;i++) {
			for(int j=-distThresh;j<=distThresh;j++) {
				Pair neighbor = new Pair(x+i,y+j);
				if(!pixCoor.contains(neighbor)) {
					flag = true;
					break;
				}
			}
		}
		return flag;
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
		pixCoor.addAll(otherCoors);
	}
}

public class AreaDivider {
	short[][][] pixVals;
	int width, height;
	HashMap<Integer, Area> areaMap;
	int[][] areaIdx; //index of the area in ArrayList<Area> areaList in which the pixel belongs to.
	Set<Integer> selectedAreaIndex;
	
	public AreaDivider(short[][][] pixVals, int width, int height) {
		this.pixVals = pixVals;
		this.width = width;
		this.height = height;
		areaMap = new HashMap<Integer, Area>();
		selectedAreaIndex = new HashSet<Integer>();
	}
	Area bfs(boolean[][] visit, int startingX, int startingY, int currentAreaIdx) {
		short[] color = pixVals[startingX][startingY];
		System.out.printf("bfs:  %d %d %d   (%d,%d,%d)\n", startingX,startingY,currentAreaIdx,color[0],color[1],color[2]);
		Area resultArea = new Area(color);
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
						else {
							System.out.println("NOT!!!!!!!!!!!!!!!!!!!!!!!!!!");
						}
					}
				}
			}
		}
		System.out.printf("area size:%d\n", resultArea.pixCoor.size());
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
	}
	int selectArea(int imageX, int imageY) {
		int selectedIdx = areaIdx[imageX][imageY];
		selectedAreaIndex.add(selectedIdx);
		return selectedIdx;
	}
	
	void mergeSelectedAreas() {
		int maxCnt=0;
		Area biggestArea = null;
		int biggestAreaIdx=-1;
		for(Integer areaIdx : selectedAreaIndex) {
			if(maxCnt<areaMap.get(areaIdx).pixCoor.size()) {
				maxCnt = areaMap.get(areaIdx).pixCoor.size();
				biggestArea = areaMap.get(areaIdx);
				biggestAreaIdx = areaIdx;
			}
		}
		for(Integer areaIdx : selectedAreaIndex) {
			if(biggestAreaIdx!=areaIdx) {
				biggestArea.merge(areaMap.get(areaIdx));
				areaMap.remove(areaIdx);
			}
		}
		selectedAreaIndex = new HashSet<Integer>();
	}
}
