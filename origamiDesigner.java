package origamiProject;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class origamiDesigner extends JFrame implements ActionListener,ChangeListener, MouseListener,MouseMotionListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Rectangle mouseRect;
	String mode;
	JMenuBar menuBar;
	JMenu fileMenu; 
	paper myPaper;
	Oplanner planner;
	JFrame myEdit;
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
	JLabel paper;
	JSpinner Width;
	JSpinner Height;
	ButtonGroup optimizeParams;
	JRadioButton square;
	JRadioButton fixRatio;
	JRadioButton fixDifference;
	JButton Update;
	private JRadioButton creases;
	private JRadioButton treePlan;
	public origamiDesigner() {
		setSize(400,400);
		setTitle("designer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myPaper= new paper(16,16);
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
		makeEdit();
		this.addKeyListener(this);
	}
	public void createMenu() {
		menuBar= new JMenuBar();
		createFileMenu();
		createDisplayMenu();
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
	private void makeEdit() {
		if(myEdit!=null) {
			myEdit.dispose();
		}
		myEdit= new JFrame();
		myEdit.setLocation(this.getX()+this.getWidth(),this.getY());
		myEdit.setSize(300, 600);
		myEdit.setLayout(new GridLayout(16,2));
		createNodeGui();
		createPaperGui();
		myEdit.setVisible(true);
	}
	private void createDisplayMenu() {
		JMenu DisplayMenu= new JMenu("Display");
		DisplayMenu.setMnemonic(KeyEvent.VK_D);
		ButtonGroup design= new ButtonGroup();
		JMenuItem ShowEditor= new JMenuItem("show Editor");
		ShowEditor.setActionCommand("showEdit");
		
		creases= new JRadioButton("show crease pattern");
		treePlan= new JRadioButton("show tree plan");
		treePlan.setSelected(true);
		DisplayMenu.add(ShowEditor);
		DisplayMenu.add(treePlan);
		DisplayMenu.add(creases);
		design.add(treePlan);
		design.add(creases);
		creases.addActionListener(this);
		treePlan.addActionListener(this);
		ShowEditor.addActionListener(this);
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
		if(action.getActionCommand().equals("optimize")) {
			optimizeAction();
		}

		if(action.getActionCommand().equals("undo")) {
			undoAction();
		}
		if(action.getActionCommand().equals("showEdit")) {
			System.out.print(true);
			makeEdit();
		}
		if(action.getActionCommand().equals("creases")) {
			creasesDisplay();
		}
		if(action.getActionCommand().equals("plan")) {
			planDisplay();

		}
	}
	private void planDisplay() {
		//planner.repaint();
		//	planner.drawNodes(myPaper);

	}
	private void creasesDisplay() {
		// TODO Auto-generated method stub
	}
	private void undoAction() {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(this,"action undone");
	}
	private void optimizeAction() {
		// TODO Auto-generated method stub
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
		planner.printCreases(myPaper);
	}
	private void openFile() {
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
		return Math.min(planner.getWidth()/myPaper.width, planner.getHeight()/myPaper.height);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		// TODO Auto-generated method stub
		int squareSize= getSquareSize();
		int x= arg0.getX()/squareSize;
		int y= (arg0.getY()-2*menuBar.getHeight())/squareSize;
		if(leafMode.isSelected()) {
			node newNode= new node(x, y, (int)nodeSize.getValue());
			myPaper.addNode(newNode);
			myPaper.setSelectedNode(newNode);
			planDisplay();
		}
		if(riverMode.isSelected()) {
			node newNode1= new node(x, y-1, 0);
			node newNode2= new node(x, y, (int)nodeSize.getValue());
			node newNode3= new node(x, y+1, 0);
			myPaper.addNode(newNode1);
			myPaper.setSelectedNode(newNode1);
			myPaper.addNode(newNode2);
			myPaper.setSelectedNode(newNode2);
			myPaper.addNode(newNode3);
			myPaper.setSelectedNode(newNode1);
			nodeSize.setValue(0);
			planDisplay();
		}
		if(moveMode.isSelected()) {
			repaint();
			myPaper.moveSelect(x,y);
			
		}
		if(selectMode.isSelected()) {
			node selected= myPaper.getNodeAt(x,y);
			if(selected==(null)) {	
			}else {
				myPaper.setSelectedNode(selected);
				nodeSize.setValue(selected.size);
				fixX.setSelected(selected.FixedX);
				fixY.setSelected(selected.FixedY);
			}
		}
		if(deleteMode.isSelected()) {
			node selected= myPaper.getNodeAt(x,y);
			if(selected==(null)) {

			}else {
				myPaper.deleteNode(selected);
			}
		}
		if(myPaper.selected!=null) {
			myPaper.selected.size=(int)nodeSize.getValue();
			myPaper.selected.FixedX=fixX.isSelected();
			myPaper.selected.FixedY=fixY.isSelected();
		}
		myPaper.width=(int) Width.getValue();
		myPaper.height=(int) Height.getValue();
		if(square.isSelected()) {
			System.out.println("square");
		}
		if(fixDifference.isSelected()) {
			System.out.println("difference ");
		}
		if(fixRatio.isSelected()) {
			System.out.println("ratio ");
		}
		if(this.creases.isSelected()) {
			planner.drawCreases(myPaper);
		}
		if(this.treePlan.isSelected()) {
			planner.drawPlan(myPaper);
		}
		planner.drawCursor(x*squareSize, y*squareSize);

		System.out.println("ok, that worked");

	}
	public void mouseMoved(MouseEvent arg0) {
		
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
		if(this.creases.isSelected()) {
			planner.drawCreases(myPaper);
		}
		if(this.treePlan.isSelected()) {
			planner.drawPlan(myPaper);
		}
	}
	private void createNodeGui() {
		Node = new JLabel("Node:");
		myEdit.add(Node);
		JLabel Buffer= new JLabel("");
		myEdit.add(Buffer);
		Mode= new ButtonGroup();
		leafMode= new JToggleButton("add leaf node");
		Mode.add(leafMode);
		myEdit.add(leafMode);

		riverMode= new JToggleButton("add river node");
		Mode.add(riverMode);
		myEdit.add(riverMode);

		selectMode= new JToggleButton("select node");
		Mode.add(selectMode);
		myEdit.add(selectMode);
		deleteMode= new JToggleButton("delete node");
		Mode.add(deleteMode);
		myEdit.add(deleteMode);
		moveMode= new JToggleButton("move node");
		Mode.add(moveMode);
		myEdit.add(moveMode);
		escMode= new JToggleButton("ecsape");
		Mode.add(escMode);
		myEdit.add(escMode);
		JLabel fixXLabel= new JLabel("Fix X:");
		myEdit.add(fixXLabel);
		fixX= new JCheckBox("fix node X");
		myEdit.add(fixX);
		JLabel fixYLabel= new JLabel("Fix Y:");
		myEdit.add(fixYLabel);
		fixY= new JCheckBox("fix node Y");
		myEdit.add(fixY);
		nodeSize=new JSpinner();
		JLabel nodeSizeLabel= new JLabel("SetNodeSize");
		myEdit.add(nodeSizeLabel);
		nodeSize.setModel(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
		myEdit.add(nodeSize);	
	}
	private void createPaperGui() {
		paper= new JLabel("Paper:");
		myEdit.add(paper);
		JLabel Buffer= new JLabel("");
		myEdit.add(Buffer);
		Width=new JSpinner();
		Width.setModel(new SpinnerNumberModel(16, 1, Integer.MAX_VALUE, 1));
		JLabel WidthLabel= new JLabel("Set Width:");
		myEdit.add(WidthLabel);
		Width.addChangeListener(this);
		myEdit.add(Width);
		Height=new JSpinner();
		Height.setModel(new SpinnerNumberModel(16, 1, Integer.MAX_VALUE, 1));
		Height.addChangeListener(this);
		JLabel HeightLabel= new JLabel("Set Height:");
		myEdit.add(HeightLabel);
		myEdit.add(Height);
		optimizeParams= new ButtonGroup();
		JLabel optimizeLabel= new JLabel("Optimize to:");
		myEdit.add(optimizeLabel);
		square= new JRadioButton("Square");
		square.setSelected(true);
		myEdit.add(square);
		optimizeParams.add(square);
		fixDifference= new JRadioButton("fix difference");
		myEdit.add(fixDifference);
		optimizeParams.add(fixDifference);;
		fixRatio= new JRadioButton("fix ratio");
		myEdit.add(fixRatio);
		optimizeParams.add(fixRatio);
		Update= new JButton("Update");
		myEdit.add(Update);
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		update();
	}
}
