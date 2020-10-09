


import java.awt.geom.Area;
import java.io.Serializable;
import java.rmi.server.UID;


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
int size;
UID ID;;
boolean isFixedToEdge;
boolean isFixedToSymmetryLine;
boolean isMirrored;

// these are set during crease generation.
Creases c;

public void setX(int newX) {
	this.x=newX;
}
public void setY(int newY) {
	this.y=newY;
}
public node(UID id) {
	x=0;
	y=0;
	size=1;
	ID=id;
	isFixedToEdge=false;
	isFixedToSymmetryLine=false;
	isMirrored=true;
}
public node(node n) {
	super();
	this.x=n.x;
	this.y=n.y;
	this.size=n.size;
	this.ID=n.ID;
	this.isFixedToEdge=n.isFixedToEdge;
	this.isFixedToSymmetryLine=n.isFixedToSymmetryLine;
	
}

public node(int X,int Y, int Size, UID id) {
	x=X;
	y=Y;
	size=Size;
	this.ID=id;
	isMirrored=true;
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
public void makeCreases(int scale, Area a,Area b){
	if(size!=0) {
	c= new Creases(scale,a,b);
	c.makeCreases();
	}
}


}
