package origamiProject;

import java.util.ArrayList;

public class skeleton extends ArrayList<node> implements Comparable<skeleton> {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public skeleton() {
		super();
		// TODO Auto-generated constructor stub
	}
	public skeleton(ArrayList<node> nodes) {
		super(nodes);
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

}
