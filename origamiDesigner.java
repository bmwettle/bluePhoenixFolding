package origamiProject;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import javax.swing.SwingWorker;

public class origamiDesigner extends JFrame implements ActionListener,ChangeListener, MouseListener,MouseMotionListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ArrayList<origamiDesigner> allDesignerWindows = new ArrayList<>();
public static final int STARTING_WIDTH=400;
public static final int STARTING_HEIGHT=400;
public static final int STARTING_PAPER_WIDTH=16;
public static final int STARTING_PAPER_HEIGHT=16;


	String mode;
	JMenuBar menuBar;
	JMenu fileMenu; 
	paper myPaper;
	ArrayList<paper>undoPapers;
	ArrayList<paper>redoPapers;
	Oplanner planner;
	JFrame myEdit;
	layout lay;

	JLabel Node;
	ButtonGroup Mode;
	JToggleButton leafMode;
	JToggleButton riverMode;
	JToggleButton selectMode;
	JToggleButton deleteMode;
	JToggleButton moveMode;
	JToggleButton escMode;
	JLabel nodeSizeLabel;
	JSpinner nodeSize;
	
	JLabel conditions;
	JToggleButton addCondition;
	JCheckBox matchX;
	JCheckBox matchY;
	
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
	int squareSize;
	public origamiDesigner() {
		
		
		initalizeDesigner();
		
		setUpPlanner();
		
		setUpCloseing();
		
	}
	private void setUpConditions() {
		this.conditions= new JLabel("add conditions");
		myEdit.add(conditions);
		addCondition= new JToggleButton("add conditions");
		myEdit.add(addCondition);
		addCondition.setActionCommand("add condition");
		addCondition.addActionListener(this);
		 JLabel matchXLabel= new JLabel("match X");
		 myEdit.add(matchXLabel);
		 this.matchX= new JCheckBox();
		 matchX.addActionListener(this);
		 myEdit.add(matchX);
		 JLabel matchYLabel= new JLabel("match Y");
		 myEdit.add(matchYLabel);
		 this.matchY= new JCheckBox();
		 myEdit.add(matchY);
		 matchY.addActionListener(this);
	}
	private void setUpCloseing() {
		planner.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				myEdit.toFront();
				System.out.println("ok");
				mouseClicked(null);
			}});

		this.addWindowListener(new WindowAdapter() { // MC
			@Override
			public void windowClosing(WindowEvent e) {

				Object closedThing = e.getSource();

				// if the thing that was closed was an origamiDesigner
				if (closedThing instanceof origamiDesigner) {

					int dialogResult = JOptionPane.showConfirmDialog(
							null, "Would you like to save your work?", 
							"Save?", JOptionPane.YES_NO_OPTION);
					if (dialogResult == JOptionPane.YES_OPTION) {
						System.out.println("Saving...");
						((origamiDesigner) closedThing).saveFile();
					}else {
						// remove it from the list of active windows
						origamiDesigner.allDesignerWindows.remove(closedThing);

						// dispose of the closed window
						((origamiDesigner) closedThing).setVisible(false);
						((origamiDesigner) closedThing).dispose();
						((origamiDesigner) closedThing).myEdit.setVisible(false);
						((origamiDesigner) closedThing).myEdit.dispose();
					}
					// if this was the last active window, end the program
					if (origamiDesigner.allDesignerWindows.isEmpty()) {
						((origamiDesigner) closedThing).setVisible(false);
						((origamiDesigner) closedThing).dispose();
						((origamiDesigner) closedThing).myEdit.setVisible(false);
						((origamiDesigner) closedThing).myEdit.dispose();
						System.exit(0);

					}
				}
			}
		});
	}
	private void setUpPlanner() {
		planner=new Oplanner(myPaper);
		planner.setDoubleBuffered(true);

		this.add(planner);

		planner.setVisible(true);
		planner.addMouseListener(this);
		planner.addMouseMotionListener(this);
		makeEdit();
		myEdit.addMouseListener(this);
		//myEdit.addComponentListener(l);
		this.addKeyListener(this);
		//setComponentZOrder(this.myEdit,1);
	}
	private void initalizeDesigner() {
		setSize(STARTING_WIDTH,STARTING_HEIGHT);
		setTitle("designer");
		myPaper= new paper(STARTING_PAPER_WIDTH,STARTING_PAPER_HEIGHT);
		redoPapers=new ArrayList<paper>();
		undoPapers= new ArrayList<paper>();
		menuBar= new JMenuBar();
		createMenu();
		menuBar.setDoubleBuffered(true);
		this.setJMenuBar(menuBar);
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
		setUpConditions();
		createPaperGui();
		myEdit.setVisible(true);
	}
	private void createDisplayMenu() {
		JMenu DisplayMenu= new JMenu("Display");
		
		ButtonGroup design= new ButtonGroup();
		JMenuItem ShowEditor= new JMenuItem("show Editor");
		ShowEditor.setActionCommand("showEdit");
		JMenuItem optimize = new JMenuItem("optimize");
		optimize.setActionCommand("optimize");
		JMenuItem undo = new JMenuItem("undo");
		undo.setActionCommand("undo");
		JMenuItem redo = new JMenuItem("redo");
		redo.setActionCommand("redo");
		creases= new JRadioButton("show crease pattern");
		treePlan= new JRadioButton("show tree plan");
		treePlan.setSelected(true);
		
		DisplayMenu.add(ShowEditor);
		DisplayMenu.add(optimize);
		DisplayMenu.add(treePlan);
		DisplayMenu.add(creases);
		DisplayMenu.add(undo);
		DisplayMenu.add(redo);
		design.add(treePlan);
		design.add(creases);

		creases.addActionListener(this);
		treePlan.addActionListener(this);
		ShowEditor.addActionListener(this);
		optimize.addActionListener(this);
		undo.addActionListener(this);
		redo.addActionListener(this);
		creases.setActionCommand("creases");
		menuBar.add(DisplayMenu);

	}

	public static void main(String[] args){
		origamiDesigner design = new origamiDesigner();
		origamiDesigner.allDesignerWindows.add(design); // MC
		
		design.setVisible(true);
	}
	/*	@Override
	public void actionPerformed(ActionEvent action) {

	}*/
	private void planDisplay() {
		//planner.repaint();
		//	planner.drawNodes(myPaper);

	}
	private void creasesDisplay() {
		// TODO Auto-generated method stub
	}
	private void undoAction() {
		// TODO Auto-generated method stub

		int last=(undoPapers.size()-1);
		if(last>0) {
			redoPapers.add(myPaper);
			System.out.println(this.myPaper.equals(this.undoPapers.get(last)));
			this.myPaper=this.undoPapers.get(last);
			undoPapers.remove(last);
		}
	}
	private void optimizeAction() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		lay= new layout(myPaper);
		lay.optimize();
		myPaper=lay.getPaper(myPaper);
		System.out.println("size is"+myPaper.height+" "+myPaper.width);
		int w=myPaper.width;
		int h= myPaper.height;
		this.Height.setValue(h);
		this.Width.setValue(w);
		this.escMode.setSelected(true);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
				this.redoPapers= new ArrayList<paper>();
				this.undoPapers= new ArrayList<paper>();

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
		int w=myPaper.width;
		int h= myPaper.height;
		this.Height.setValue(h);
		this.Width.setValue(w);

		this.escMode.setSelected(true);
		if(myPaper.selected!=null) {
			this.nodeSize.setValue(myPaper.selected.size);
			
		}
	}
	private void newFile() {
		// TODO Auto-generated method stub
		origamiDesigner design = new origamiDesigner();
		origamiDesigner.allDesignerWindows.add(design); // MC
		System.out.println(this.equals(design));
		design.setVisible(true);
	}
	private int getSquareSize() {
		return Math.min(planner.getWidth()/myPaper.width, planner.getHeight()/myPaper.height);
	}
	private void update() {
		// TODO Auto-generated method stub
		this.planner.myPaper=this.myPaper;

		//planner.repaint();
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
		nodeSize=new JSpinner();
		JLabel nodeSizeLabel= new JLabel("SetNodeSize");
		myEdit.add(nodeSizeLabel);
		nodeSize.setModel(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
		nodeSize.addChangeListener(this);
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
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		this.mouseClicked(null);
	}
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		char action =arg0.getKeyChar();
		System.out.print(action+"ok");
		if(action==' ') {
			update();
		}

	}
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		squareSize= getSquareSize();
		int x = 0;
		int y = 0;
		if(arg0!=null) {
			try {
				x= arg0.getX()/squareSize;
				y= (arg0.getY())/squareSize;
				System.out.println(planner.getX()+" "+arg0.getX()+", "+planner.getY()+" "+arg0.getYOnScreen());
				System.out.println("x is"+x+" y is "+y);
			} catch (Exception e) { }
			makeSelectedAction( x, y);
		}
		if(myPaper.selected!=null) {
			myPaper.selected.size=(int) Integer.parseInt(nodeSize.getValue().toString());;
			
		}
		myPaper.width=(int) Integer.parseInt(Width.getValue().toString());
		myPaper.height=(int)Integer.parseInt(Height.getValue().toString());;
		if(square.isSelected()) {
			System.out.println("square");
		}
		if(fixDifference.isSelected()) {
			System.out.println("difference ");
		}
		if(fixRatio.isSelected()) {
			System.out.println("ratio ");
		}
		drawPlanner();
		planner.drawCursor(x*squareSize, y*squareSize);



	}
	private void makeSelectedAction(int x, int y) {
		if(this.addCondition.isSelected()) {
			escMode.setSelected(true);
			node selected= myPaper.getNodeAt(x,y);
			if(selected==(null)) {

			}else {
				this.undoPapers.add(new paper(myPaper));
				System.out.println(undoPapers.size());

				myPaper.addConditions(myPaper.selected,selected,this.matchX.isSelected(), this.matchY.isSelected());
			}
		}
		if(leafMode.isSelected()) {
			this.undoPapers.add(new paper(myPaper));
			System.out.println(undoPapers.size());

			node newNode= new node(x, y,1);
			myPaper.addNode(newNode);
			myPaper.setSelectedNode(newNode);
			nodeSize.setValue(1);

			planDisplay();
		}
		if(riverMode.isSelected()) {
			this.undoPapers.add(new paper(myPaper));
			System.out.println(undoPapers.size());

			node newNode1= new node(x, y-1, 0);
			node newNode2= new node(x, y, 1);
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

			this.undoPapers.add(new paper(myPaper));
			System.out.println(undoPapers.size());

			planner.clearNode(myPaper.selected);
			myPaper.moveSelect(x,y);

		}
		if(selectMode.isSelected()) {
			node selected= myPaper.getNodeAt(x,y);
			if(selected==(null)) {	
			}else {
				myPaper.setSelectedNode(selected);
				nodeSize.setValue(selected.size);
				
			}
		}
		if(deleteMode.isSelected()) {
			node selected= myPaper.getNodeAt(x,y);
			if(selected==(null)) {

			}else {
				this.undoPapers.add(new paper(myPaper));
				System.out.println(undoPapers.size());

				myPaper.deleteNode(selected);
			}
		}
	}
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void actionPerformed(ActionEvent arg0) {

		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("new")) {
			newFile();
		}
		if(arg0.getActionCommand().equals("open")) {
			openFile();
		}
		if(arg0.getActionCommand().equals("print")) {
			printFile();
		}
		if(arg0.getActionCommand().equals("save")) {
			saveFile();
		}
		if(arg0.getActionCommand().equals("optimize")) {
			optimizeAction();
		}
		if(arg0.getActionCommand().equals("showEdit")) {

			makeEdit();
		}
		if(arg0.getActionCommand().equals("creases")) {
			creasesDisplay();
		}
		if(arg0.getActionCommand().equals("plan")) {
			planDisplay();

		}
		if(arg0.getActionCommand().equals("undo")) {
			undoAction();

		}
		if(arg0.getActionCommand().equals("redo")) {
			redoAction();

		}
		
		this.mouseClicked(null);
		//drawPlanner();
		//planner.drawCursor(x*squareSize, y*squareSize);

	}
	
	private void redoAction() {
		int last=(redoPapers.size()-1);
		if(last>0) {
			undoPapers.add(myPaper);
			this.myPaper=this.redoPapers.get(last);
			redoPapers.remove(last);
		}
	}
	public void drawPlanner() {
		planner.validate();
		//planner.repaint();
		if(this.creases.isSelected()) {
			planner.drawCreases(myPaper);
		}
		if(this.treePlan.isSelected()) {
			planner.drawPlan(myPaper);
		}
		//planner.drawCursor(x*squareSize, y*squareSize);

	}


}
