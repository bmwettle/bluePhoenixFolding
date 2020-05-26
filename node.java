package origamiProject;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class node implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 2107895915449290373L;
public int x;
public int y;
int size;

int ID;


public node() {
	super();
	x=0;
	y=0;
	size=1;

	ID=0;
	
}
public node(node n) {
	super();
	this.x=n.x;
	this.y=n.y;
	this.size=n.size;
	this.ID=n.ID;
}
public Shape getShape(int scale, int buffer) {
	
	int Size=(size+buffer);
	Shape poly= new Rectangle2D.Double(scale*(x-Size),scale*(y-Size),2*scale*Size,2*scale*Size);
	return poly;
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
	return("."+ID);
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
