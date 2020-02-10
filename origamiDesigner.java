package origamiProject;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

public class origamiDesigner extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JMenuBar menuBar;
	JMenu fileMenu; 

public origamiDesigner() {
	setSize(600,600);
	setTitle("designer");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	menuBar= new JMenuBar();
	createMenu();
	
	this.setJMenuBar(menuBar);
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
	JOptionPane.showMessageDialog(this,"showing plan");
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
}
private void printFile() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"printing file");
}
private void openFile() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"opening file");
}
private void newFile() {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(this,"new file");
}
}
