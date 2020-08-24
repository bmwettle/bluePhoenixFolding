package origamiProject;

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
	int scale;
	private static final long serialVersionUID = -5200999865205395759L;

	public Creases(int scale, Area inside) {
		super(inside);
		this.scale=scale;
		activeCorners= new ArrayList<Point>();
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
					Point k= new Point(p.x-smallScale*a,p.y-smallScale*b);
					if(this.contains(k)) {
						Point d= new Point(p.x+smallScale*a,p.y-smallScale*b);
						Point e= new Point(p.x-smallScale*a,p.y+smallScale*b);
						if(this.contains(d)==this.contains(e)) {

							directions.add(new int[] {-a,-b});
							activeCorners.add(p);
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
				if(!this.contains(end)) {
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
			directions.removeAll(removeDir);
			activeCorners.removeAll(removeCorners);
		}
	}
	private void makeCorners() {

		PathIterator borders= this.getPathIterator(null);
		while(!borders.isDone()) {
			double [] coords= new double[6];
			borders.currentSegment(coords);
			Point p=new Point((int)coords[0],(int)coords[1]);
			corners.add(p);
			borders.next();
		}
	}
}
