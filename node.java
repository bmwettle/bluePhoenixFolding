package origamiProject;

public class node{
int x;
int y;
int size;
String type;
public node() {
	x=0;
	y=0;
	size=1;
	type="leaf";
}
public node(int X,int Y, int Size,String Type) {
	x=X;
	y=Y;
	size=Size;
	type=type;
	
}
public int getX() {
	return x;
}
public void setX(int x) {
	this.x = x;
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
