package origamiTestOne;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import acm.graphics.*;
import acm.program.*;
public class Paper extends GraphicsProgram{
	
public void run() {}
public void init() {
	addMouseListeners();
	boxes= new ArrayList<GRect>();
}
public void mouseMoved(MouseEvent e) {
	update();
	int x=  e.getX()- e.getX()% rowSize;
	int y=e.getY()- e.getY()% rowSize;
	center.setLocation(x-5,y-5);
	center.sendToBack();
	add(center);
}
public void mouseClicked(MouseEvent e) {
	double x= center.getX()+5;
	double y=center.getY()+5;
	GRect box= new GRect(rowSize*2 -2,rowSize*2 -2);
	box.setLocation(x-rowSize+1,y-rowSize+1);
	box.setFillColor(Color.GREEN);
	for(GRect box2:boxes) {
		if(box.getBounds().intersects(box2.getBounds())) {
			box.setFillColor(Color.RED);
		}
	}
	box.setFilled(true);
	add(box);
	boxes.add(box);
}
public void update() {
	removeAll();
	center= new GOval(10,10);
	
	rowSize= this.getHeight()/numRows;
	if (this.getWidth()/numRows <= rowSize){
		rowSize= this.getWidth()/numRows;
	}
	for(int i=0; i<=numRows;i++) {
		add(new GLine(0,i*rowSize,rowSize*numRows,i*rowSize));
		add(new GLine(i*rowSize,0,i*rowSize,rowSize*numRows));
	}
	for(GRect box:boxes) {
		add(box);
	}
	
}
GOval center;
int numRows=16;
ArrayList<GRect> boxes;
ArrayList<Origami_Node> nodes;
int rowSize= 500/numRows;
}
