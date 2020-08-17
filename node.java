package origamiProject;



import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;


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
int ID;
boolean isFixedToEdge;
boolean isFixedToSymmetryLine;
Area A;
// these are set during crease generation.
ArrayList<Point> corners;
ArrayList<Line2D.Double> creases;

public void setX(int newX) {
	this.x=newX;
}
public void setY(int newY) {
	this.y=newY;
}
public node() {
	super();
	x=0;
	y=0;
	size=1;
	ID=0;
	isFixedToEdge=false;
	isFixedToSymmetryLine=false;
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

public void setID(int id) {
	this.ID=id;
}
public node(int X,int Y, int Size) {
	x=X;
	y=Y;
	size=Size;
	
}
public int getX() {
	return x;
}
public void moveX(int x) {
	
	this.x += x;
	
}
public String toString() {
	return("    id"+ID+", size"+size+",x: "+x+", y: "+y+":");
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


}
