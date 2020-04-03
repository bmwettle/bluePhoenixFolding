package origamiProject;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Oeditor extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 42871189832605926L;
	ButtonGroup options;
	JRadioButton  cut;
	JRadioButton  newNode;
	JRadioButton  select;
	JRadioButton move;
	JTextField size;
	JTextArea sizeLabel;
	JTextField paperSize;
	JTextArea paperSizeLabel;

public Oeditor(){
	super();
	this.setAlwaysOnTop(true);
	setLayout(new GridLayout(9,1));
	this.setSize(200, 350);
	
	this.setBackground(Color.GREEN);
	cut= new JRadioButton("cut",false);
	cut.addActionListener(this);
	cut.setActionCommand("cut");
	this.add(cut);
	move= new JRadioButton("move",false);
	move.addActionListener(this);
	move.setActionCommand("move");
	this.add(move);
	sizeLabel= new JTextArea("enter size of node:");
	
	newNode= new JRadioButton("new node",true);
	newNode.addActionListener(this);
	newNode.setActionCommand("new node");
	this.add(newNode);
	select= new JRadioButton("select",false);
	select.addActionListener(this);
	select.setActionCommand("select");
	this.add(select);
	options=new ButtonGroup();
	options.add(cut);
	options.add(newNode);
	options.add(select);
	options.add(move);
	add(sizeLabel);
	size= new JTextField("1");
	
	add(size);
	paperSize= new JTextField("16:16");
	paperSizeLabel= new JTextArea("width:height of the grid");
	add(paperSizeLabel);
	add(paperSize);
}
@Override
public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub
	
	
}
public int getPaperWidth() {
	String text= paperSize.getText();
	String number= text.substring(0, text.indexOf(":"));
	System.out.print(number);
	return Integer.parseInt(number);
}
public int getPaperHeight() {
	String text= paperSize.getText();
	String number= text.substring(text.indexOf(":")+1);
	return Integer.parseInt(number);
}
public boolean isNewNode() {
	//JOptionPane.showMessageDialog(this,"finding nodes");

	return options.isSelected(newNode.getModel());
}
public boolean isCut() {
	//JOptionPane.showMessageDialog(this,"finding nodes");

	return options.isSelected(cut.getModel());
}
public void setPaperSize( int width, int height) {
	this.size.setText(width+":"+height);
}
public boolean isSelect() {
	//JOptionPane.showMessageDialog(this,"finding nodes");

	return options.isSelected(select.getModel());
}
public boolean getMoving() {
	return options.isSelected(move.getModel());
}

public int getNodeSize() {
	return Integer.parseInt(size.getText());
}
}
