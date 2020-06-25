package origamiProject;



import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;
import javafx.scene.shape.*;


public class node implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 2107895915449290373L;
private int x;
private int y;
int size;
boolean FixedX;
boolean FixedY;
int ID;
Area A;
ArrayList<Point> corners;
ArrayList<Line2D.Double> creases;


public void setX(int newX) {
	if(!FixedX) {
	this.x=newX;
	}
}
public void forceX(int newX) {

	this.x=newX;
}
public void forceY(int newY) {

	this.y=newY;
}
public void setY(int newY) {
	if(!FixedY) {
	this.y=newY;
	}
}
public node() {
	super();
	x=0;
	y=0;
	size=1;
	FixedX=false;
	FixedY=false;
	ID=0;
	
}
public node(node n) {
	super();
	this.x=n.x;
	this.y=n.y;
	this.size=n.size;
	this.ID=n.ID;
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
