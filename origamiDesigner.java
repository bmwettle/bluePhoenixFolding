package origamiProject;

import java.awt.event.*;
import java.util.Iterator;
import java.util.List;

import java.awt.*;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class origamiDesigner extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JMenuBar menuBar;
	JMenu fileMenu; 
	paper myPaper;
	int num_squares=8;
	JPanel myPanel;
public origamiDesigner() {
	setSize(400,400);
	setTitle("designer");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	myPaper= new paper();
	menuBar= new JMenuBar();
	createMenu();
	myPanel=new JPanel();
	
	
	this.setJMenuBar(menuBar);
	this.add(myPanel);
	myPanel.setDoubleBuffered(true);
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
	JMenuItem verify= new JMenuItem("verify",KeyEvent.VK_V);
	JMenuItem undo= new JMenuItem("undo",KeyEvent.VK_U);
	JMenuItem build= new JMenuItem("build crease pattern",KeyEvent.VK_B);
	
	actionMenu.add(optimize);
	actionMenu.add(verify);
	actionMenu.add(undo);
	actionMenu.add(build);
	
	optimize.addActionListener(this);
	optimize.setActionCommand("optimize");
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
private void gridDisplay() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"showing grid");
}

private void planDisplay() {
	// TODO Auto-generated method stub
	//JOptionPane.showMessageDialog(this,"showing plan");
	editor edit= new editor();
	edit.setVisible(true);
	Graphics g1= myPanel.getGraphics();

	int squareSize= this.getWidth()/num_squares;
	Iterator<node> it= myPaper.getNodes();
		// plot the node, and lines conecting them.
	while(it.hasNext()) {
		node myNode= (node) it.next();
		g1.drawOval(myNode.x*squareSize-myNode.size*squareSize, myNode.y*squareSize-myNode.size*squareSize, myNode.size*2*squareSize, myNode.size*2*squareSize);
		List<node> connections= myPaper.getConections(myNode);
		Iterator<node> conIt= connections.iterator();
		while(conIt.hasNext()) {
			node endNode= conIt.next();
			g1.drawLine(myNode.getX()*squareSize, myNode.getY()*squareSize, endNode.getX()*squareSize, endNode.getY()*squareSize);
			
		}
	}
	
	for (int i=0;i<num_squares;i++){
		g1.drawLine(0, i*squareSize, this.getWidth(), i*squareSize);
		g1.drawLine( i*squareSize,0, i*squareSize,this.getHeight());
	}
	
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
	JOptionPane.showMessageDialog(this,"optimizing nodes");
}
private void buildAction() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"building creases");
}
private void saveFile() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"saving file");
	JFileChooser fc = new JFileChooser();
	int returnVal = fc.showSaveDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		try {
			PrintWriter fileOut = new PrintWriter(file);
			fileOut.println("Hello, world!");
		    fileOut.close();
		} catch (FileNotFoundException e) {
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
	JOptionPane.showMessageDialog(this,"opening file");
	JFileChooser fc = new JFileChooser();
	int returnVal = fc.showOpenDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		try {
		    Scanner fileIn = new Scanner(file);
		    while (fileIn.hasNext()) {
			    System.out.println(fileIn.nextLine());
		    }
		    fileIn.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not open the file.");
			e.printStackTrace();
		}
	}
}
private void newFile() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"new file");
}
}
