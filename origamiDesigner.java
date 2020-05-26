package origamiProject;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.io.*;
import javax.swing.*;

import acm.graphics.GRect;

public class origamiDesigner extends JFrame implements ActionListener, MouseListener,MouseMotionListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Rectangle mouseRect;
	String mode;
	JMenuBar menuBar;
	JMenu fileMenu; 
	paper myPaper;
	int paper_width;
	int paper_height;
	Oplanner planner;
	Oeditor myEdit;

public origamiDesigner() {
	setSize(400,400);
	setTitle("designer");
	paper_width=16;
	paper_height=16;
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	myPaper= new paper(paper_width,paper_height);
	menuBar= new JMenuBar();
	mouseRect= new Rectangle(0,0,6,6);
	createMenu();
	menuBar.setDoubleBuffered(true);
	this.setJMenuBar(menuBar);
	planner=new Oplanner(myPaper);
	planner.setDoubleBuffered(true);
	this.addMouseListener(this);
	this.addMouseMotionListener(this);
	this.add(planner);
	myEdit= new Oeditor();
	myEdit.setVisible(true);
	myEdit.setLocation(this.getX()+this.getWidth(),this.getY());
	
	this.addKeyListener(this);
}
public void createMenu() {
	menuBar= new JMenuBar();
	createFileMenu();
	createDisplayMenu();
	createActionMenu();	
}

private void createFileMenu() {
	fileMenu= new JMenu("File");
	menuBar.add(fileMenu);
	fileMenu.setMnemonic(KeyEvent.VK_F);
	JMenuItem save= new JMenuItem("save",KeyEvent.VK_S);
	JMenuItem open= new JMenuItem("open",KeyEvent.VK_O);
	JMenuItem New= new JMenuItem("new",KeyEvent.VK_N);
	JMenuItem print= new JMenuItem("print",KeyEvent.VK_P);
	
	fileMenu.add(save);
	fileMenu.add(open);
	fileMenu.add(New);
	
	save.addActionListener(this);
	save.setActionCommand("save");
	New.addActionListener(this);
	New.setActionCommand("new");
	open.addActionListener(this);
	open.setActionCommand("open");
	print.addActionListener(this);
	print.setActionCommand("print");
	
	fileMenu.add(print);
	
}
private void createActionMenu() {
	JMenu actionMenu= new JMenu("Action");

	actionMenu.setMnemonic(KeyEvent.VK_A);
	JMenuItem optimize= new JMenuItem("optimize nodes",KeyEvent.VK_Z);
	JMenuItem optimizeWithoutCuts= new JMenuItem("optimize (without cuts)",KeyEvent.VK_0);
	JMenuItem verify= new JMenuItem("verify",KeyEvent.VK_V);
	JMenuItem undo= new JMenuItem("undo",KeyEvent.VK_U);
	JMenuItem build= new JMenuItem("build crease pattern",KeyEvent.VK_B);
	
	actionMenu.add(optimize);
	actionMenu.add(optimizeWithoutCuts);
	actionMenu.add(verify);
	actionMenu.add(undo);
	actionMenu.add(build);
	
	optimize.addActionListener(this);
	optimize.setActionCommand("optimize");
	optimizeWithoutCuts.addActionListener(this);
	optimizeWithoutCuts.setActionCommand("optimizeWithoutCuts");
	verify.addActionListener(this);
	verify.setActionCommand("verify");
	undo.addActionListener(this);
	undo.setActionCommand("undo");
	build.addActionListener(this);
	build.setActionCommand("build");
	
	menuBar.add(actionMenu);
	
}
private void createDisplayMenu() {
	JMenu DisplayMenu= new JMenu("Display");
	DisplayMenu.setMnemonic(KeyEvent.VK_D);
	JMenuItem plan= new JMenuItem("plan",KeyEvent.VK_L);
	JMenuItem grid= new JMenuItem("grid",KeyEvent.VK_G);
	JMenuItem creases= new JMenuItem("creases",KeyEvent.VK_C);
	DisplayMenu.add(grid);
	DisplayMenu.add(plan);
	DisplayMenu.add(creases);
	plan.addActionListener(this);
	plan.setActionCommand("plan");
	grid.addActionListener(this);
	grid.setActionCommand("grid");
	creases.addActionListener(this);
	creases.setActionCommand("creases");
	menuBar.add(DisplayMenu);
	
}

public static void main(String[] args){
	origamiDesigner design = new origamiDesigner();
	design.setVisible(true);
}
@Override
public void actionPerformed(ActionEvent action) {
	if(action.getActionCommand().equals("new")) {
		newFile();
	}
	if(action.getActionCommand().equals("open")) {
		openFile();
	}
	if(action.getActionCommand().equals("print")) {
		printFile();
	}
	if(action.getActionCommand().equals("save")) {
		saveFile();
	}
	if(action.getActionCommand().equals("build")) {
		buildAction();
	}
	if(action.getActionCommand().equals("optimize")) {
		optimizeAction();
	}
	if(action.getActionCommand().equals("optimizeWithoutCuts")) {
		optimizeWithoutCuts();
	}
	
	if(action.getActionCommand().equals("undo")) {
		undoAction();
	}
	if(action.getActionCommand().equals("verify")) {
		verifyAction();
	}
	
	if(action.getActionCommand().equals("creases")) {
		creasesDisplay();
	}
	if(action.getActionCommand().equals("plan")) {
		planDisplay();
	
	}
	if(action.getActionCommand().equals("grid")) {
		gridDisplay();
	}
		
}
private void optimizeWithoutCuts() {
	// TODO Auto-generated method stub
	optimizer myop= new optimizer(myPaper);
	paper optimized=myop.optimizeWithoutCuts();
	if(optimized!=null) {
		myPaper=optimized;
		myPaper.shrink();
		//myEdit.setPaperSize(myPaper.width,myPaper.height);
		paper_width=myPaper.width;
		paper_height=myPaper.height;
	}
	update();
	

}
private void gridDisplay() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"showing grid");
	mode="grid";
}

private void planDisplay() {
	//planner.repaint();
	planner.drawNodes(myPaper);
	
}
private void creasesDisplay() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"showing creases");
}
private void undoAction() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"action undone");
}
private void verifyAction() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"checking geometry");
}
private void optimizeAction() {
	// TODO Auto-generated method stub
	
	
}
private void buildAction() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"building creases");
}
private void saveFile() {
	//JOptionPane.showMessageDialog(this,"saving file");
	JFileChooser fc = new JFileChooser();
	int returnVal = fc.showSaveDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		try {
			ObjectOutputStream fileOut = new ObjectOutputStream(new FileOutputStream (file));
			fileOut.writeObject(myPaper);
		    fileOut.close();
		} catch (Exception e) {
			System.err.println("Could not open the file.");
			e.printStackTrace();
		}
	}

}

private void printFile() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"printing file");
}
private void openFile() {
	// TODO Auto-generated method stub
	//JOptionPane.showMessageDialog(this,"opening file");
	JFileChooser fc = new JFileChooser();
	int returnVal = fc.showOpenDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		try {
		    ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(file));
		    myPaper=(paper)fileIn.readObject();
		    fileIn.close();
		    
		} catch (Exception e) {
			System.err.println("Could not open the file.");
			e.printStackTrace();
		}
	}
}

private void newFile() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"new file");
}
private int getSquareSize() {
	return Math.min(planner.getWidth()/paper_width, planner.getHeight()/paper_height);
}
@Override
public void mouseClicked(MouseEvent arg0) {
	// TODO Auto-generated method stub
	int squareSize= getSquareSize();
	int x= arg0.getX()/squareSize;
	int y= (arg0.getY()-2*menuBar.getHeight())/squareSize;
	if(myEdit.leafMode.isSelected()) {
		System.out.print("leaf ");
node newNode= new node(x, y, Integer.parseInt(myEdit.nodeSize.getValue().toString()));
		
		myPaper.addNode(newNode);
		myPaper.setSelectedNode(newNode);
		planDisplay();
	}
	if(myEdit.riverMode.isSelected()) {
		System.out.print("river ");
		
	}
	if(myEdit.moveMode.isSelected()) {
		System.out.print("move ");
		myPaper.moveSelect(x,y);
		repaint();
	}
	if(myEdit.selectMode.isSelected()) {
		System.out.print("select ");
		node selected= myPaper.getNodeAt(x,y);
		if(selected==(null)) {
			
		}else {
			myPaper.setSelectedNode(selected);
		}
	}
	if(myEdit.deleteMode.isSelected()) {
		System.out.print("delete ");
		node selected= myPaper.getNodeAt(x,y);
		if(selected==(null)) {
			
		}else {
			myPaper.deleteNode(selected);
		}
	}
	if(myEdit.fixX.isSelected()) {
		System.out.print("X ");
	}
	if(myEdit.fixY.isSelected()) {
		System.out.print("Y ");
	}
	System.out.print(myEdit.nodeSize.getValue());
	System.out.print(myEdit.Width.getValue());
	System.out.print(myEdit.Height.getValue());
	if(myEdit.square.isSelected()) {
		System.out.print("square");
	}
	if(myEdit.fixDifference.isSelected()) {
		System.out.print("difference ");
	}
	if(myEdit.fixRatio.isSelected()) {
		System.out.print("ratio ");
	}
	
	/*
	if(myEdit.isNewNode()) {
		//JOptionPane.showMessageDialog(this,"adding nodes");
		
		node newNode= new node(x, y, myEdit.getNodeSize());
		
		myPaper.addNode(newNode);
		myPaper.setSelectedNode(newNode);
		planDisplay();
	}
	else if(myEdit.isSelect()) {
		
		node selected= myPaper.getNodeAt(x,y);
		if(selected==(null)) {
			
		}else {
			myPaper.setSelectedNode(selected);
		}
	}else if(myEdit.isCut()) {

		node second= myPaper.getNodeAt(x,y);
		if (second!=null){
			myPaper.addCut(second);
		}
	}else if(myEdit.getMoving()) {
		myPaper.moveSelect(x,y);
		repaint();
		
	}*/
	planner.drawNodes(myPaper);
	planner.drawCursor(x*squareSize, y*squareSize);

	System.out.println("ok, that worked");
}
public void mouseMoved(MouseEvent arg0) {
	// TODO Auto-generated method stub
	int squareSize= getSquareSize();
	int x= (arg0.getX())/squareSize;

	int y= ((arg0.getY()-2*menuBar.getHeight()))/squareSize;
	this.planner.myPaper=this.myPaper;
	planner.drawNodes(myPaper);
	planner.drawCursor(x*squareSize, y*squareSize);	

	
}

@Override
public void mouseEntered(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void mouseExited(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void mousePressed(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void mouseReleased(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void mouseDragged(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void keyPressed(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void keyTyped(KeyEvent arg0) {
	char action =arg0.getKeyChar();
	System.out.print(action+"ok");
	if(action==' ') {
		update();
	}
	
}
private void update() {
	// TODO Auto-generated method stub
	this.planner.myPaper=this.myPaper;
	planner.repaint();
	//this.paper_height=myEdit.getPaperWidth();
	//this.paper_width=myEdit.getPaperHeight();
	myPaper.setPaperSize(paper_width, paper_height);
	planner.drawNodes(myPaper);
}
}
