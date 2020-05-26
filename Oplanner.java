package origamiProject;

import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import java.awt.*;
import javax.swing.JPanel;

public class Oplanner extends JPanel    {
/**
	 * 
	 */
	paper myPaper;
int mouseX=0;
int mouseY=0;
Button picker;
Graphics2D g;
int squareSize;
int smallSquareSize;
paper p;
int width;
int height;
	private static final long serialVersionUID = -1071442708667655401L;
public Oplanner(paper p) {
	super();
	myPaper=p;
	this.setDoubleBuffered(true);
	
	//mouseBox= new GRect(0,0,6,6);
	picker= new Button("pick");
	picker.setEnabled(false);
this.add(picker);
	//this.setBackground(Color.blue);
}
void paintCommponent(Graphics g){
	super.paintComponent(g);
	//drawNodes(myPaper);
	//g.drawOval(mouseX-3, mouseY-3, 6, 6);
	
}

public void drawCursor(int x,int y) {
	//this.getGraphics().drawOval(x-3,y-3,6,6);
	mouseX=x;
	mouseY=y;
	picker.setLocation(mouseX, mouseY);
	this.setComponentZOrder(picker, 0);
	//System.out.println("at:"+x+","+y+"now");
}
public void drawNodes(paper myP){
	this.p=myP;
	p.getTreeDistances();
	g= (Graphics2D)this.getGraphics();
	g.setColor(Color.BLACK);
	
	width= p.width;
	height=p.height;
	squareSize= Math.min(getWidth()/width,getHeight()/height);
	smallSquareSize=squareSize/6;
	Iterator<node> it= p.getNodes();
		// plot the node, and lines conecting them.
	while(it.hasNext()) {
		node myNode= (node) it.next();
		drawNode(myNode);
		
	}
	
	for (int i=0;i<=height;i++){
		g.drawLine(0, i*squareSize, width*squareSize, i*squareSize);
	
	}
	for (int i=0;i<=width;i++){
		g.drawLine( i*squareSize,0,i*squareSize, height*squareSize);
	
	}
	picker.setLocation(mouseX, mouseY);
	this.setComponentZOrder(picker, 0);
}
private void drawNode(node myNode) {
	if(this.myPaper.connections.get(myNode).size()==1) {
		drawLeafNode(myNode);
	}else{
		if(myNode.size==0) {
			drawHub(myNode);
		}else {
			if(this.myPaper.connections.get(myNode).size()==2) {
				drawRiverNode(myNode);
			}
		}
	}
	g.setColor(Color.blue);
	if( p.isSelcted(myNode)) {
		g.setColor(Color.ORANGE);
	}
	g.fillOval(myNode.x*squareSize-myNode.size*smallSquareSize, myNode.y*squareSize-myNode.size*smallSquareSize, myNode.size*2*smallSquareSize, myNode.size*2*smallSquareSize);
	g.setColor(Color.BLACK);
	ArrayList<node> connections= p.getConections(myNode);
	Iterator<node> conIt= connections.iterator();
	while(conIt.hasNext()) {
		node endNode= conIt.next();
		if(p.isLeaf(myNode)&&p.overlaps(myNode,endNode)) {
			g.setColor(Color.red);
		}
		g.drawLine(myNode.getX()*squareSize, myNode.getY()*squareSize, endNode.getX()*squareSize, endNode.getY()*squareSize);
		
	}
	g.setColor(Color.BLACK);
}
private void drawRiverNode(node myNode) {
	int buffer =myNode.size;
	node hub= myPaper.connections.get(myNode).get(0);
	Area river= new  Area();
	Area inside= new Area();
	for(node n: myPaper.connections.get(hub)) {
		if(myPaper.isLeaf(n)) {
		river.add(new Area(n.getShape(squareSize, buffer)));
		inside.add(new Area(n.getShape(squareSize, 0)));
		}
	}
	
	g.setColor(Color.blue);
	g.setStroke(new BasicStroke(3));
	g.draw(river);
	g.setColor(Color.GRAY);
	g.draw(inside);
	g.setStroke(new BasicStroke(1));
	
}
private void drawHub(node myNode) {
	int X=0;
	int Y=0;
	for(node n:myPaper.connections.get(myNode)) {
		X+=n.x;
		Y+=n.y;
	}
	X= (int)X/myPaper.connections.get(myNode).size();
	Y= (int)Y/myPaper.connections.get(myNode).size();
	myNode.x=X;
	myNode.y=Y;
	g.fillRect(myNode.x*squareSize, myNode.y*squareSize,squareSize/10, squareSize/10);
	
}
private void drawLeafNode(node myNode) {
	g.setColor(Color.orange);
	g.fill(myNode.getShape(squareSize,0));
	//g.fillRect(myNode.x*squareSize-myNode.size*squareSize, myNode.y*squareSize-myNode.size*squareSize, myNode.size*2*squareSize, myNode.size*2*squareSize);
	
}
}
