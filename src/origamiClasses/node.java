package origamiClasses;



import java.awt.geom.Area;
import java.io.Serializable;


/**
 * 
 * @author Benjamin Wettle
 * this class store that data for a origami node.
 */
public class node implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 2107895915449290373L;
	
private int x;
private int y;
public int size;
public int ID;
public boolean isFixedToSymmetryLine;
public boolean isLeaf;
public boolean isMirrored;
// these are set during crease generation.
public Creases[] c;

public void setX(int newX) {
	this.x=newX;
}
public void setY(int newY) {
	this.y=newY;
}
public node(int id) {
	x=0;
	y=0;
	size=1;
	ID=id;
	isFixedToSymmetryLine=false;
	isLeaf=true;
	isMirrored=true;
}
public node(node n) {
	super();
	this.x=n.x;
	this.y=n.y;
	this.size=n.size;
	this.ID=n.ID;
	this.isFixedToSymmetryLine=n.isFixedToSymmetryLine;
	isLeaf=n.isLeaf;
	isMirrored=n.isMirrored;
}

public node(int X,int Y, int Size, int id) {
	x=X;
	y=Y;
	size=Size;
	this.ID=id;
	isLeaf=true;
	this.isFixedToSymmetryLine=false;
	this.isMirrored=true;
}
public int getX() {
	return x;
}
public void moveX(int x) {
	
	this.x += x;
	
}
public String toString() {
	return("    id"+ID);
}

/*@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result =  result + ID;
	result = prime * result + x;
	result = prime * result + y;
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
	node other = (node) obj;
	if (ID!=other.ID ){
		return false;
	}
	if (x != other.x)
		return false;
	if (y != other.y)
		return false;
	return true;
}*/
public int getY() {
	
	return y;
}
public void moveY(int y) {
	
	this.y += y;
	
}
public int getSize() {
	return size;
}
public void setSize(int size) {
	this.size = size;
}
public void makeCreases(double scale, Area[] a, int[][] ignore){
	if(size!=0) {
		c=new Creases[2*size];
		for( int i=0;i<size*2;i++) {
			c[i]= new Creases(scale,a[i],ignore);
			c[i].makeCreases();
		}
	
	}
}


}
