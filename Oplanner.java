package origamiProject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

public class Oplanner extends JPanel   {
/**
	 * 
	 */
	paper myPaper;

	private static final long serialVersionUID = -1071442708667655401L;
public Oplanner(paper p) {
	super();
	myPaper=p;
	this.setDoubleBuffered(true);

	//this.setBackground(Color.blue);
}

public void drawCursor(int x,int y) {
	this.getGraphics().drawOval(x-3,y-3,6,6);
}
public void drawNodes(paper p){
	Graphics g= this.getGraphics();
	int num_squares=p.num_squares;
	int squareSize= this.getWidth()/num_squares;
	int smallSquareSize=squareSize/6;
	Iterator<node> it= p.getNodes();
		// plot the node, and lines conecting them.
	while(it.hasNext()) {
		node myNode= (node) it.next();
		
		g.drawOval(myNode.x*squareSize-myNode.size*squareSize, myNode.y*squareSize-myNode.size*squareSize, myNode.size*2*squareSize, myNode.size*2*squareSize);
		g.setColor(Color.blue);
		g.fillOval(myNode.x*squareSize-myNode.size*smallSquareSize, myNode.y*squareSize-myNode.size*smallSquareSize, myNode.size*2*smallSquareSize, myNode.size*2*smallSquareSize);
		g.setColor(Color.BLACK);
		ArrayList<node> connections= p.getConections(myNode);
		Iterator<node> conIt= connections.iterator();
		while(conIt.hasNext()) {
			node endNode= conIt.next();
			g.drawLine(myNode.getX()*squareSize, myNode.getY()*squareSize, endNode.getX()*squareSize, endNode.getY()*squareSize);
			
		}
	}
	
	for (int i=0;i<=num_squares;i++){
		g.drawLine(0, i*squareSize, num_squares*squareSize, i*squareSize);
		g.drawLine( i*squareSize,0, i*squareSize,num_squares*squareSize);
	}
}
}
