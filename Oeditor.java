package origamiProject;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Oeditor extends JFrame implements ActionListener, ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 42871189832605926L;
	JLabel Node;
	ButtonGroup Mode;
	JToggleButton leafMode;
	JToggleButton riverMode;
	JToggleButton selectMode;
	JToggleButton deleteMode;
	JToggleButton moveMode;
	JToggleButton escMode;
	JCheckBox fixX;
	JCheckBox fixY;
	JLabel nodeSizeLabel;
	JSpinner nodeSize;
	paper myPaper;
	JLabel paper;
	JSpinner Width;
	JSpinner Height;
	ButtonGroup optimizeParams;
	JRadioButton square;
	JRadioButton fixRatio;
	JRadioButton fixDifference;
	JButton Update;
	
public Oeditor(paper p){
	super();
	myPaper=p;
	this.setLayout(new GridLayout(16,2));
	this.setSize(250, 500);
	createNodeGui();
	createPaperGui();
}
private void createNodeGui() {
	Node = new JLabel("Node:");
	this.add(Node);
	JLabel Buffer= new JLabel("");
	this.add(Buffer);
	Mode= new ButtonGroup();
	leafMode= new JToggleButton("add leaf node");
	Mode.add(leafMode);
	this.add(leafMode);
	
	riverMode= new JToggleButton("add river node");
	Mode.add(riverMode);
	this.add(riverMode);
	
	selectMode= new JToggleButton("select node");
	Mode.add(selectMode);
	this.add(selectMode);
	deleteMode= new JToggleButton("delete node");
	Mode.add(deleteMode);
	this.add(deleteMode);
	moveMode= new JToggleButton("move node");
	Mode.add(moveMode);
	this.add(moveMode);
	escMode= new JToggleButton("ecsape");
	Mode.add(escMode);
	this.add(escMode);
	JLabel fixXLabel= new JLabel("Fix X:");
	this.add(fixXLabel);
	fixX= new JCheckBox("fix node X");
	this.add(fixX);
	JLabel fixYLabel= new JLabel("Fix Y:");
	this.add(fixYLabel);
	fixY= new JCheckBox("fix node Y");
	this.add(fixY);
	nodeSize=new JSpinner();
	JLabel nodeSizeLabel= new JLabel("SetNodeSize");
	this.add(nodeSizeLabel);
	nodeSize.setModel(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
	this.add(nodeSize);	
}
private void createPaperGui() {
	paper= new JLabel("Paper:");
	this.add(paper);
	JLabel Buffer= new JLabel("");
	this.add(Buffer);
	Width=new JSpinner();
	Width.setModel(new SpinnerNumberModel(16, 1, Integer.MAX_VALUE, 1));
	JLabel WidthLabel= new JLabel("Set Width:");
	this.add(WidthLabel);
	Width.addChangeListener(this);
	this.add(Width);
	Height=new JSpinner();
	Height.setModel(new SpinnerNumberModel(16, 1, Integer.MAX_VALUE, 1));
	Height.addChangeListener(this);
	JLabel HeightLabel= new JLabel("Set Height:");
	this.add(HeightLabel);
	this.add(Height);
	optimizeParams= new ButtonGroup();
	JLabel optimizeLabel= new JLabel("Optimize to:");
	this.add(optimizeLabel);
	square= new JRadioButton("Square");
	square.setSelected(true);
	this.add(square);
	optimizeParams.add(square);
	fixDifference= new JRadioButton("fix difference");
	this.add(fixDifference);
	optimizeParams.add(fixDifference);;
	fixRatio= new JRadioButton("fix ratio");
	this.add(fixRatio);
	optimizeParams.add(fixRatio);
	Update= new JButton("Update");
	this.add(Update);
}
@Override
public void actionPerformed(ActionEvent arg0) {
	
	
}
@Override
public void stateChanged(ChangeEvent arg0) {
}
}
