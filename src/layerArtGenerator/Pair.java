package layerArtGenerator;

public class Pair {
	public int x,y;
	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public boolean equals(Object o) {
		if( o instanceof Pair) {
			Pair p = (Pair)o;
			return (p.x==x&&p.y==y);
		}
		return false;
	}
	
	public int hashCode() {
		return x*31+y;
	}
}
