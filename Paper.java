import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import acm.graphics.*;
import acm.gui.*;
import acm.program.*;

public class Paper extends Program implements MouseListener, MouseMotionListener {
	
public void run() {}
public void init() {
	rootPanel = new VPanel();
	this.add(rootPanel);

	paperPanel = new GCanvas();
	paperPanel.addMouseListener(this);
	paperPanel.addMouseMotionListener(this);

	boxes= new ArrayList<GRect>();

	sampleButton = new Button("Click Me");
	sampleButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			messageLabel.setText("Button Clicked!");
		}});

	sampleChoice = new Choice();
	sampleChoice.add("Option 1");
	sampleChoice.add("Option 2");
	sampleChoice.add("Option 3");
	sampleChoice.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			messageLabel.setText(sampleChoice.getSelectedItem() + " was selected");
		}});

	messageLabel = new Label("Messages will appear here");

	rootPanel.add(paperPanel);
	rootPanel.add(sampleButton);
	rootPanel.add(sampleChoice);
	rootPanel.add(messageLabel);

	TableLayout rootLayout = (TableLayout) rootPanel.getLayout();

	TableConstraints paperConstraints = new TableConstraints(
			"height=325 width=500 fill=BOTH anchor=NORTHEAST weightx=100 weighty=100"); 
	rootLayout.setConstraints(paperPanel, paperConstraints);

	TableConstraints buttonConstraints = new TableConstraints(
			"height=25 width=100 fill=NONE anchor=SOUTHWEST");
	rootLayout.setConstraints(sampleButton, buttonConstraints);

	TableConstraints choiceConstraints = new TableConstraints(
			"height=25 width=100 fill=NONE anchor=SOUTHWEST");
	rootLayout.setConstraints(sampleChoice, choiceConstraints);

	TableConstraints labelConstraints = new TableConstraints(
			"height=25 width=500 fill=NONE anchor=SOUTHWEST");
	rootLayout.setConstraints(messageLabel, labelConstraints);

	update();
}
public void mouseMoved(MouseEvent e) {
	messageLabel.setText("mouse moved event");
	update();
	int x=  e.getX()- e.getX()% rowSize;
	int y=e.getY()- e.getY()% rowSize;
	center.setLocation(x-5,y-5);
	center.sendToBack();
	paperPanel.add(center);
}
public void mouseClicked(MouseEvent e) {
	messageLabel.setText("mouse clicked event");
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
	paperPanel.add(box);
	boxes.add(box);
	update();
}
public void update() {
	paperPanel.removeAll();
	center= new GOval(10,10);
	
	rowSize= paperPanel.getHeight()/numRows;
	if (paperPanel.getWidth()/numRows <= rowSize){
		rowSize= paperPanel.getWidth()/numRows;
	}
	for(int i=0; i<=numRows;i++) {
		paperPanel.add(new GLine(0,i*rowSize,rowSize*numRows,i*rowSize));
		paperPanel.add(new GLine(i*rowSize,0,i*rowSize,rowSize*numRows));
	}
	for(GRect box:boxes) {
		paperPanel.add(box);
	}
}
GOval center;
int numRows=16;
ArrayList<GRect> boxes;
ArrayList<Origami_Node> nodes;
int rowSize= 500/numRows;
Button sampleButton;
Choice sampleChoice;
Label messageLabel;
VPanel rootPanel;
GCanvas paperPanel;	
}
