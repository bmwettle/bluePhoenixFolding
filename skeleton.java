package origamiProject;

import java.util.ArrayList;

public class skeleton extends ArrayList<node> implements Comparable<skeleton> {
/**
	 * 
	 */
	int score;
	private static final long serialVersionUID = 1L;
	public skeleton() {
		super();
		score=0;
		// TODO Auto-generated constructor stub
	}
	public skeleton(ArrayList<node> nodes, int score) {
		super(nodes);
		this.score=score;
		// TODO Auto-generated constructor stub
	}
	
	public int getSize() {
		
			int xmax=0;
			int xmin=Integer.MAX_VALUE;
			int ymax=0;
			int ymin=Integer.MAX_VALUE;
			for(node n:this) {
				//if(isLeaf(n)) {
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
				//}
			}
			return Math.max(xmax-xmin,ymax-ymin);
			
		
	}
	@Override
	public int compareTo(skeleton o) {
		// TODO Auto-generated method stub
		return this.getSize()-o.getSize();
	}
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
}
