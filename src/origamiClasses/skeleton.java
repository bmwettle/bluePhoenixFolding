package origamiClasses;


import java.util.Arrays;
/**
 * 
 * @author Benjamin Wettle
 * This Class stores the basic data of a design. 
 * it is a list of nodes, and a few functions needed. 
 * this speeds up the optimization, over using the paper class,
 * since the paper class contains a lot of extra information and computation.
 */
public class skeleton  implements Comparable<skeleton> {
	/**
	 * 
	 */
	int score=0;
	int index;
	int size;
	int smallSize;
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
		this.index=old.index;
		this.xmax=old.xmax;
		this.xmin=old.xmin;
		this.smallSize=old.smallSize;
		this.size=old.size;
		this.ymax=old.ymax;
		this.ymin=old.ymin;
		this.isFixedRatio= old.isFixedRatio;
		this.max=old.max;
		this.ratioX_Y=old.ratioX_Y;
		this.isSymetrical=old.isSymetrical;
		nodes = Arrays.stream(old.nodes)
                .map(n ->n == null ? null : new node(n))
                .toArray(node[]::new);
	
	}
	public void Printout() {
		System.out.println();
		String[][] DispNodes= new String[xmax+1][ymax+1];
		for(node n:nodes) {
			if(n!=null) {
			DispNodes[n.getX()][n.getY()]=n.size+""+n.ID;
			}
		}
		for( String[] row:DispNodes) {
			for(String s:row) {
				if(s!=null) {
				System.out.print(s);
				}else {
					System.out.print("|_|");
				}
			}
			System.out.println();
		}
	}
	public skeleton(boolean isFixedRatio,boolean isSymetrical,
			double ratioX_Y, int max) {

		super();
		index=0;
		this.isFixedRatio= isFixedRatio;
		this.max=max;
		nodes= new node[max];
		this.ratioX_Y=ratioX_Y;
		this.isSymetrical=isSymetrical;
	
		 xmax=0;
		 xmin=0;
		 ymax=0;
		 ymin=0;
	}
	//gets the minimum box that contains all the nodes.
	//also keeps the ratios or differences specified from the layout
	/**
	 * gets the stored size value
	 * @return
	 */
	public int getSize() {
		return size;
	}
	public int[] getInnerSize() {
		return new int[] {xmax-xmin,(int) (ratioX_Y*(ymax-ymin))};
	}
	@Override
	/**
	 * compare the sizes and scores of two skeltons
	 */
	public int compareTo(skeleton o) {
		//smaller skeletons are better
	/*	int size= this.getSize();
		int osize=o.getSize();
	if(osize==size) {
			//int smallGap= this.smallSize-o.smallSize;
		//	if(smallGap==0) {
			return(this.score-o.score);
			//}else {
			//return smallGap;
		//}
	}
		return(size-osize);
*/	//	if(o.index==this.index) {
		//	return this.size-o.size;
//}
		return (o.index/(o.size*o.size)-this.index/(this.size*this.size));
	}
	/**
	 * this checks to see if node n overlaps with anything already on the skeleton
	 * @param distance
	 * @param n
	 * @return
	 */

	public void add(node n,int index) {
		this.nodes[n.ID]=n;
		int newX=n.getX();
		int newY=n.getY();
		boolean changed=false;
		if(newX>this.xmax) {
			xmax=newX;
			changed=true;
			//if(this.xmax-this.xmin==this.size) {
				//this.size=newX-this.xmin;
			//}else {
				//this.smallSize=newX-this.xmin;
			//}
			this.xmax=newX;
		}else if(newX<this.xmin){
			changed=true;
			xmin=newX;
			//if(this.xmax-this.xmin==this.size) {
			//	this.size=this.xmax-newX;
			//}else {
				//this.smallSize=this.xmax-newX;
			//}
		}
		
		
		if(newY>this.ymax) {
			changed=true;
			//if(this.ymax-this.ymin==this.size) {
			//	this.size=newY-this.ymin;
			//}else {
			//	this.smallSize=newY-this.ymin;
			//}
			this.ymax=newY;
		}else if(newY<this.ymin){
			changed=true;
			this.ymin=newY;
			//if(this.ymax-this.ymin==this.size) {
			//	this.size=this.ymax-newY;
		//	}else {
			//	this.smallSize=this.ymax-newY;
			//}
		}
		if(changed) {
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
			
		}
		this.index++;
		size=(int) Math.max(xmax-xmin,ratioX_Y*(ymax-ymin));
		smallSize=(int) Math.min(xmax-xmin,ratioX_Y*(ymax-ymin));
	}
	/**
	 * checks to see if the design satisfies the symmetry conditions
	 * @return
	 */
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
	/**
	 * recalculates the size and score of the skeleton, and then
	 * shifts the skeleton so that the nodes are all at positive positions.
	 * 
	 */
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
		smallSize=(int) Math.min(xmax-xmin,ratioX_Y*(ymax-ymin));
	}
	
	
/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(nodes);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		skeleton other = (skeleton) obj;
		for(int i=0;i<this.max;i++) {
			node n=nodes[i];
			node m=other.nodes[i];
			if(n!=null&&m!=null) {
				if(!n.equals(m)) {
					return false;
				}
			}
		}
		//if (!Arrays.equals(nodes, other.nodes))
			//return false;
		return true;
	}
public String toString() {
	String message="";
	for(node n:this.nodes) {
		if(n!=null) {
		message+=" ,"+n.getSize()+"."+n.getX()+"."+n.getY();
	}}
	return message;
}*/
}
