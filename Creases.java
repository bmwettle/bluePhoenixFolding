
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.io.Serializable;
import java.util.ArrayList;

public class Creases extends Area implements Serializable {

	/**
	 * 
	 */
	ArrayList<Point> activeCorners;
	ArrayList<Point> corners;
	ArrayList<Line2D.Double> creases;
	ArrayList<int[]> directions;
	int[][]ignore;
	int scale;
	Area large;
	private static final long serialVersionUID = -5200999865205395759L;

	public Creases(int scale, Area trueA, Area large) {
		super(trueA);
		this.large=large;
		this.scale=scale;
		activeCorners= new ArrayList<Point>();
		corners= new ArrayList<Point>();
		creases= new ArrayList<Line2D.Double>();
		directions= new ArrayList<int[]>();
	}
	public Creases(int scale, Area inside, int[][] ignore) {
		super(inside);
		this.large=inside;
		this.scale=scale;
		activeCorners= new ArrayList<Point>();
		this.ignore=ignore;
		corners= new ArrayList<Point>();
		creases= new ArrayList<Line2D.Double>();
		directions= new ArrayList<int[]>();
	}
	public void makeCreases() {
		makeCorners();
		makeDirections();
		makeBisectors();
	}
	private void makeDirections() {
		int smallScale= scale/2;
		for(Point p:corners) {
			for(int a=-1;a<=1;a+=2) {
				for(int b=-1;b<=1;b+=2) {
					Point k= new Point(p.x-(smallScale-1)*a,p.y-(smallScale-1)*b);
					if(this.contains(k)) {
						Point d= new Point(p.x+smallScale*a,p.y-smallScale*b);
						Point e= new Point(p.x-smallScale*a,p.y+smallScale*b);
						if(this.contains(d)==this.contains(e)) {

							directions.add(new int[] {-a,-b});
							activeCorners.add(p);
							//System.out.println("now at"+p.x/scale+","+p.y/scale+"::"+-a+""+-b);
						}
					}
				}
			}
		}
	}
	private void makeBisectors() {
		while(activeCorners.size()>0) {
			ArrayList<Point> removeCorners= new ArrayList<Point>();
			ArrayList<int[]> removeDir= new ArrayList<int[]>();
			for(int i=0;i<activeCorners.size();i++) {
				int[] dir=directions.get(i) ;
				Point p= activeCorners.get(i);

				Point end= new Point(p.x+scale*dir[0],p.y+scale*dir[1]);
				Point test= new Point(p.x+(scale-1)*dir[0],p.y+(scale-1)*dir[1]);
				if(!this.contains(test)) {
					removeCorners.add(p);
					removeDir.add(directions.get(i));
				}else {
					creases.add(new Line2D.Double(p,end));
					activeCorners.get(i).move(end.x, end.y);
				}
			}
			for(int i=0;i<activeCorners.size();i++) {
				Point p1=activeCorners.get(i);
				for(int j=0;j<activeCorners.size();j++) {
					Point p2=activeCorners.get(j);
					if(i!=j&&p1.x==p2.x&&p1.y==p2.y) {
						removeCorners.add(p2);
						removeCorners.add(p1);
						removeDir.add(directions.get(j));
						removeDir.add(directions.get(i));
					}
				}
				}
		//	System.out.println(removeCorners.size());
			directions.removeAll(removeDir);
			activeCorners.removeAll(removeCorners);
		}
	}
	private void makeCorners() {
		corners= new ArrayList<Point>();
		PathIterator borders= this.large.getPathIterator(null);
		while(!borders.isDone()) {
			double [] coords= new double[6];
			int type=borders.currentSegment(coords);
			//System.out.println("type"+type);
			if(type!=PathIterator.SEG_CLOSE) {
			int x=(int)coords[0];
			int y=(int)coords[1];
			boolean edge=false;
			if(ignore!=null) {
				if(x==ignore[0][0]||x>=ignore[0][1]) {
					edge=true;
				}
				if(y==ignore[1][0]||y>=ignore[1][1]) {
					edge=true;
				}
			}
			if(!edge) {
			Point p=new Point(x,y);
			corners.add(p);
			}
			}
			borders.next();
		}
		//System.out.println("corners "+corners);
	}
}
