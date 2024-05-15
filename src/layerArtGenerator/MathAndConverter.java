package layerArtGenerator;

public class MathAndConverter{
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