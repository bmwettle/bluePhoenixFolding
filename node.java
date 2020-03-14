package origamiProject;

import java.awt.Polygon;
import java.io.Serializable;

public class node extends Polygon implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 2107895915449290373L;
int x;
int y;
int size;
String type;
boolean isSelected;
int ID;
double Vx;
double Vy;

public node() {
	super();
	x=0;
	y=0;
	size=1;
	type="leaf";
	ID=0;
	
}
public void setID(int id) {
	this.ID=id;
}
public node(int X,int Y, int Size,String Type) {
	x=X;
	y=Y;
	size=Size;
	type=Type;
	
}
public int getX() {
	return x;
}
public void setX(int x) {
	this.x = x;
}
public String toString() {
	return(""+ID);
}
public int getY() {
	return y;
}
public void setY(int y) {
	this.y = y;
}
public int getSize() {
	return size;
}
public void setSize(int size) {
	this.size = size;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}

}
