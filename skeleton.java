package origamiProject;

import java.util.ArrayList;
/**
 * 
 * @author Benjamin Wettle
 * This Class stores the basic data of a design. 
 * it is a list of nodes, and a few functions needed. 
 * this speeds up the optimization, over suing the paper class,
 * since the paper class contains a lot of extra information and computation.
 */
public class skeleton extends ArrayList<node> implements Comparable<skeleton> {
	/**
	 * 
	 */
	int score;
	int size;
	int xmax;
	int xmin;
	int ymax;
	int ymin;
	boolean isFixedRatio;
	double ratioX_Y;
	private static final long serialVersionUID = 1L;
	public skeleton(boolean isFixedRatio,
			double ratioX_Y) {

		super();
		this.isFixedRatio= isFixedRatio;

		this.ratioX_Y=ratioX_Y;
		score=0;
		 xmax=0;
		 xmin=Integer.MAX_VALUE;
		 ymax=0;
		 ymin=Integer.MAX_VALUE;
	}
	//gets the minimum box that contains all the nodes.
	//also keeps the ratios or differences specified from the layout
	public int getSize() {
		/*int xmax=0;
		int xmin=Integer.MAX_VALUE;
		int ymax=0;
		int ymin=Integer.MAX_VALUE;
		for(node n:this) {
			if(n.getX()>xmax) {
				xmax=n.getX();
			}
			if(n.getX()<xmin) {
				xmin=n.getX();
			}
			if(n.getY()>ymax) {
				ymax=n.getY();
			}
			if(n.getY()<ymin) {
				ymin=n.getY();
			}
		}
		//if(isFixedRatio) {
		//return Math.max(xmax-xmin,(int)((ymax-ymin)*this.ratioX_Y));
		//}
		return Math.max(xmax-xmin,ymax-ymin);*/
		return size;


	}
	@Override
	public int compareTo(skeleton o) {
		//smaller skeletons are better
		int size= this.getSize();
		int osize=o.getSize();

		return(size-osize);

	}
	//this checks to see if node n overlaps with anything already on the skeleton
	public boolean overlaps(int[][] distances, node n, int index) {
		for(node m:this) {
			if(!m.equals(n)) {
				int deltax= Math.abs(n.getX()-m.getX());
				int deltay= Math.abs(n.getY()-m.getY());
				int dist=distances[index][this.indexOf(m)];
				int gap=deltax+deltay;
				this.score+=gap;
				if(deltax<dist) {
					if(deltay<dist) {
						return true;
					}
				}
			}
		}
		return false;
	}
	@Override

	public boolean add(node n) {
		boolean ok= super.add(n);
		if(n.getX()>xmax) {
			xmax=n.getX();
		}
		if(n.getX()<xmin) {
			xmin=n.getX();
		}
		if(n.getY()>ymax) {
			ymax=n.getY();
		}
		if(n.getY()<ymin) {
			ymin=n.getY();
		}
		size=Math.max(xmax-xmin,ymax-ymin);
		return ok;


	}

}
