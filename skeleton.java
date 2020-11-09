

import java.rmi.server.UID;
import java.util.Arrays;
import java.util.HashMap;
/**
 * 
 * @author Benjamin Wettle
 * This Class stores the basic data of a design. 
 * it is a list of nodes, and a few functions needed. 
 * this speeds up the optimization, over suing the paper class,
 * since the paper class contains a lot of extra information and computation.
 */
public class skeleton  implements Comparable<skeleton> {
	/**
	 * 
	 */
	int score;
	int size;
	int xmax;
	int xmin;
	int ymax;
	int ymin;
	int max;
	boolean isFixedRatio;
	boolean isSymetrical;
	double ratioX_Y;
	node[] nodes;
	public skeleton(skeleton old) {
		super();
		this.isFixedRatio= old.isFixedRatio;
		this.max=old.max;
		this.ratioX_Y=old.ratioX_Y;
		this.isSymetrical=old.isSymetrical;
		nodes = Arrays.stream(old.nodes)
                .map(n ->n == null ? null : new node(n))
                .toArray(node[]::new);
	
	}
	public skeleton(boolean isFixedRatio,boolean isSymetrical,
			double ratioX_Y, int max) {

		super();
		this.isFixedRatio= isFixedRatio;
		this.max=max;
		nodes= new node[max];
		this.ratioX_Y=ratioX_Y;
		this.isSymetrical=isSymetrical;
		score=0;
		 xmax=0;
		 xmin=0;
		 ymax=0;
		 ymin=0;
	}
	//gets the minimum box that contains all the nodes.
	//also keeps the ratios or differences specified from the layout
	public int getSize() {
		return size;
	}
	@Override
	public int compareTo(skeleton o) {
		//smaller skeletons are better
		int size= this.getSize();
		int osize=o.getSize();
		if(osize==size) {
			return(this.score-o.score);
		}
		return(size-osize);

	}
	//this checks to see if node n overlaps with anything already on the skeleton
	public boolean overlaps(HashMap<UID,Integer> distance, node n) {
		for(node m:this.nodes) {
			if(m.ID!=n.ID) {
				int deltax= Math.abs(n.getX()-m.getX());
				int deltay= Math.abs(n.getY()-m.getY());
				int dist=distance.get(m.ID);
				int gap=Math.abs(deltax)+Math.abs(deltay);
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
	

	public void add(node n,int index) {
		this.nodes[index]=n;


	}
	public boolean checkConditions() {
		if(!isSymetrical) {
			return true;
		}
		for( node n:this.nodes) {
			if(n!=null) {
			if(n.isFixedToSymmetryLine) {
				if(n.getX()!=0) {
					return false;
				}
			}else {
				if(n.getX()<=0) {
					return false;
				}
			}}else {break;}
		}
		return true;
	}
	public void resize() {
		xmax=0;
		 xmin=Integer.MAX_VALUE;
		 ymax=0;
		 ymin=Integer.MAX_VALUE;
		for( node n:this.nodes) {
			if(n!=null) {
		if(n.getY()>ymax) {
			ymax=n.getY();
		}
		if(n.getY()<ymin) {
			ymin=n.getY();
			
		}
		if(n.getX()>xmax) {
			xmax=n.getX();
		}
		if(n.getX()<xmin) {
			xmin=n.getX();
			
		}}
		}
		for( node m:this.nodes) {
			if(m!=null) {
			m.moveY(-ymin);
			}
		}
		ymax-=ymin;
		ymin=0;
		
		for( node m:this.nodes) {
			if(m!=null) {
			m.moveX(-xmin);
			}
		}
		xmax-=xmin;
		xmin=0;
		
		size=(int) Math.max(xmax-xmin,ratioX_Y*(ymax-ymin));
	}
public String toString() {
	String message="";
	for(node n:this.nodes) {
		if(n!=null) {
		message+=" ,"+n.getSize()+"."+n.getX()+"."+n.getY();
	}}
	return message;
}
}
