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
	private int cursorX = 0;
	private int cursorY = 0;

	private static final long serialVersionUID = -1071442708667655401L;
public Oplanner(paper p) {
	super();
	myPaper=p;
	this.setDoubleBuffered(true);

	//this.setBackground(Color.blue);
}

public void drawCursor(int x,int y) {
	cursorX = x;
	cursorY = y;
	repaint();
}

public void drawNodesTemp(paper p){
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

public void paintComponent(Graphics g) {    
    super.paintComponent(g);       

    // draw the nodes
    int num_squares=myPaper.num_squares;
    int squareSize= this.getWidth()/num_squares;
    int smallSquareSize=squareSize/6;
    Iterator<node> it= myPaper.getNodes();
	    // plot the node, and lines conecting them.
    while(it.hasNext()) {
	    node myNode= (node) it.next();
	    
	    g.drawOval(myNode.x*squareSize-myNode.size*squareSize, myNode.y*squareSize-myNode.size*squareSize, myNode.size*2*squareSize, myNode.size*2*squareSize);
	    g.setColor(Color.blue);
	    g.fillOval(myNode.x*squareSize-myNode.size*smallSquareSize, myNode.y*squareSize-myNode.size*smallSquareSize, myNode.size*2*smallSquareSize, myNode.size*2*smallSquareSize);
	    g.setColor(Color.BLACK);
	    ArrayList<node> connections= myPaper.getConections(myNode);
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

    // draw the cursor
    g.drawOval(cursorX-3,cursorY-3,6,6);
}
}
