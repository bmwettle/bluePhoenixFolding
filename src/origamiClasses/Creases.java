package origamiClasses;

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
	ArrayList<Point> startCorners;
	public ArrayList<Line2D.Double> creases;
	ArrayList<int[]> directions;
	int[][]ignore;
	double scale;
	private static final long serialVersionUID = -5200999865205395759L;

	public Creases(double scale, Area trueA) {
		super(trueA);
		this.scale=(int)(scale/2);
		activeCorners= new ArrayList<Point>();
		corners= new ArrayList<Point>();
		creases= new ArrayList<Line2D.Double>();
		directions= new ArrayList<int[]>();
	}
	public Creases(double scale, Area inside, int[][] ignore) {
		super(inside);
		this.scale=(int)scale/2;
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
		double smallScale= scale/2;
		startCorners= new ArrayList<Point>();
		ArrayList<Point> baseCorners= new ArrayList<Point>();
		for(Point p:corners) {
			
			for(int a=-1;a<=1;a+=2) {
				for(int b=-1;b<=1;b+=2) {
					
					Point k= new Point((int)(p.getX()-(smallScale)*a),(int)(p.getY()-(smallScale)*b));
					if(this.contains(k)) {
						Point d= new Point((int)(p.getX()+smallScale*a),(int)(p.getY()-smallScale*b));
						Point e= new Point((int)(p.getX()-smallScale*a),(int)(p.getY()+smallScale*b));
						if(this.contains(d)==this.contains(e)) {

							directions.add(new int[] {-a,-b});
							activeCorners.add(new Point(p));
							startCorners.add(new Point(p));
							baseCorners.add(new Point(p));
						}
					}
					
				}
			}
		}
		this.corners=baseCorners;
	}
	private void makeBisectors() {
		
		
		while(activeCorners.size()>0) {
			//System.out.println(count);
			ArrayList<Point> removeActiveCorners= new ArrayList<Point>();
			ArrayList<Point> removeOldCorners= new ArrayList<Point>();
			ArrayList<int[]> removeDir= new ArrayList<int[]>();
			for(int i=0;i<activeCorners.size();i++) {
				int[] dir=directions.get(i) ;
				Point p= activeCorners.get(i);
				Point end= new Point((int)(p.getX()+scale*dir[0]),(int)(p.getY()+scale*dir[1]));
				
				//Point test= new Point((int)(end.getX()+(scale/2)*dir[0]),(int)(end.getY()+(scale/2)*dir[1]));
				//Point test1= new Point((int)(p.getX()-(scale/2)*dir[0]),(int)(p.getY()+(scale/2)*dir[1]));
				//Point test2= new Point((int)(p.getX()+(scale/2)*dir[0]),(int)(p.getY()-(scale/2)*dir[1]));
				boolean hits=false;
				for( Point c:corners) {
					if(Math.abs(c.getX()-end.getX())<scale/2&&Math.abs(c.getY()-end.getY())<scale/2) {
						hits=true;
					}
				}
				boolean edge=false;
				if(ignore!=null) {
					if(end.getX()<ignore[0][0]||end.getX()>ignore[0][1]) {
						edge=true;
					}
					if(end.getY()<ignore[1][0]|end.getY()>ignore[1][1]) {
						edge=true;
					}
				}
				if(!this.contains(end)||hits||edge) {
					 removeActiveCorners.add(p);
					removeDir.add(directions.get(i));
					
					removeOldCorners.add(startCorners.get(i));
					
					if(hits) {
						Line2D.Double crease=new Line2D.Double(startCorners.get(i),end);
						
						creases.add(crease);
						}else {
							if(edge) {
								Line2D.Double crease=new Line2D.Double(startCorners.get(i),new Point(p));
								
								creases.add(crease);
								}
						}
					
			
				}else {
				
					//creases.add(new Line2D.Double(p,end));
				//	activeCorners.get(i).move((int) end.getX(), (int) end.getY());
				}
				activeCorners.get(i).move((int) end.getX(), (int) end.getY());
				
			}
			for(int i=0;i<activeCorners.size();i++) {
				Point p1=activeCorners.get(i);
				for(int j=0;j<activeCorners.size();j++) {
					Point p2=activeCorners.get(j);
					if(i!=j&&Math.abs(p1.x-p2.x)<scale&&Math.abs(p1.y-p2.y)<scale) {
						removeActiveCorners.add(p2);
						removeActiveCorners.add(p1);
						removeDir.add(directions.get(j));
						removeDir.add(directions.get(i));
						removeOldCorners.add(startCorners.get(i));
						removeOldCorners.add(startCorners.get(j));
						creases.add(new Line2D.Double(startCorners.get(i),p1));
						creases.add(new Line2D.Double(startCorners.get(j),p2));
					}
				}
				}
		//	System.out.println(removeCorners.size());
			directions.removeAll(removeDir);
			activeCorners.removeAll(removeActiveCorners);
			startCorners.removeAll(removeOldCorners);
		}
	}
	private void makeCorners() {
		corners= new ArrayList<Point>();
		PathIterator borders= this.getPathIterator(null);
		while(!borders.isDone()) {
			double [] coords= new double[6];
			int type=borders.currentSegment(coords);
			if(type!=PathIterator.SEG_CLOSE) {
			int x=(int)coords[0];
			int y=(int)coords[1];
			boolean edge=false;
			if(ignore!=null) {
				if(x<ignore[0][0]||x>ignore[0][1]) {
					edge=true;
				}
				if(y<ignore[1][0]||y>ignore[1][1]) {
					edge=true;
				}
			}
			
			
			Point p=new Point(x,y);
			boolean added=false;
			for(Point old:corners) {
				if(old.equals(p)) {
					added=true;
				}
			}
			if(!added&&!edge) {
			corners.add(p);
			}
			}
			borders.next();
		}
	}
}
