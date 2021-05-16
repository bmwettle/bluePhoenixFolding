import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class origamiDesigner extends JFrame implements ActionListener,ChangeListener, MouseListener, KeyListener{

	/**
	 * this class takes input from the user to do any actions. 
	 * It controls the Oeditor, planner, paper, and layout
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
	CreasePlanner myCreases;
	JFrame showCreases;
	OEditor myEdit;
	layout lay;
	JMenu DisplayMenu;
	JMenu openEditorsMenu;
	JMenuItem Editor;
	double squareSize;
	
	public origamiDesigner() {
		initalizeDesigner();
		setUpPlanner();
		setUpCloseing();
		makeEditor();
	}

	private void makeEditor() {
		openEditorsMenu= new JMenu("Edit");
		menuBar.add(openEditorsMenu);
		JMenuItem optimize = new JMenuItem("optimize");
		optimize.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				optimizeAction();
				drawPlanner();
			} 
		} );
		openEditorsMenu.add(optimize);
		makeUndoActions();
	}
	/***
	 * makes sure each of the windows closes properly
	 */
	private void setUpCloseing() {
		myEdit.setAlwaysOnTop(true);
		addWindowListener(new WindowAdapter() { // MC
			@Override
			public void windowClosing(WindowEvent e) {
				Object closedThing = e.getSource();
				if (closedThing instanceof origamiDesigner) {
					disposeEditors();
					int dialogResult = JOptionPane.showConfirmDialog(
							null, "Would you like to save your work?", 
							"Save?", JOptionPane.YES_NO_OPTION);
					if (dialogResult == JOptionPane.YES_OPTION) {
						((origamiDesigner) closedThing).saveFile();
					}else {
						// remove it from the list of active windows
						origamiDesigner.allDesignerWindows.remove(closedThing);
						// dispose of the closed window
						((origamiDesigner) closedThing).setVisible(false);
						((origamiDesigner) closedThing).dispose();
					}
					// if this was the last active window, end the program
					if (origamiDesigner.allDesignerWindows.isEmpty()) {
						((origamiDesigner) closedThing).setVisible(false);
						((origamiDesigner) closedThing).dispose();
						System.exit(0);
					}
				}
			}
		});
	}
	/***
	 * sets up the planner, and adds listeners
	 */
	private void setUpPlanner() {
		planner=new Oplanner(myPaper);
		planner.setDoubleBuffered(true);
		add(planner);
		planner.setVisible(true);
		planner.addMouseListener(this);
		makeEdit();
		myEdit.addMouseListener(this);
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
		setJMenuBar(menuBar);
		addKeyListener(this);
	}
	public void createMenu() {
		menuBar= new JMenuBar();
		createFileMenu();
		createDisplayMenu();
	}
	/***
	 * creates the save, open, print and new menu items
	 */
	private void createFileMenu() {
		fileMenu= new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem save= new JMenuItem("save");
		JMenuItem open= new JMenuItem("open");
		JMenuItem New= new JMenuItem("new");
		JMenuItem print= new JMenuItem("print");
		fileMenu.add(save);
		fileMenu.add(open);
		fileMenu.add(New);
		save.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				saveFile();
			} 
		} );
		New.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				newFile();
			} 
		} );
		open.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				openFile();
			} 
		} );
		print.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				printFile();
			} 
		} );
		fileMenu.add(print);
	}
	/***
	 * makes a new Oeditor object,
	 * and adds functionality to it
	 */
	private void makeEdit() {
		disposeEditors();
		myEdit=new OEditor(myPaper);
		myEdit.setAlwaysOnTop(true);
		myEdit.nodeFixed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean isF= myEdit.nodeFixed.isSelected();
				if(myPaper.selected!=null) {
					myPaper.selected.isFixedToSymmetryLine=isF;
					myPaper.selected.isMirrored=!isF;
				}
				mouseClicked(null);				
			}});
		myEdit.isPaperMirrored.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean isM= myEdit.isPaperMirrored.isSelected();
				myEdit.nodeMirrored.setEnabled(isM);
				myEdit.nodeFixed.setEnabled(isM);
				myPaper.hasSymmetry=isM;
				mouseClicked(null);				
			}});
		myEdit.Width.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				mouseClicked(null);				
			}});
		myEdit.Height.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				mouseClicked(null);				
			}});
		myEdit.nodeSize.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(myPaper.selected.isLeaf||myPaper.selected.size!=0) {
					myPaper.selected.size=(int)myEdit.nodeSize.getValue();
					mouseClicked(null);			
				}
				myEdit.nodeSize.setValue(myPaper.selected.size);
			}});
	}
	private void disposeEditors() {
		if(myEdit!=null) {
			myEdit.dispose();
			myEdit=null;
		}		
	}
	private void createDisplayMenu() {
		DisplayMenu= new JMenu("Display");
		makeDisplayOptions();
		menuBar.add(DisplayMenu);
	}
	private void makeDisplayOptions() {
		JMenuItem drawCreases= new JMenuItem("show crease pattern");
		drawCreases.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 

				drawCreases();
			} 
		} );
		DisplayMenu.add(drawCreases);
		JMenuItem ShowEditor= new JMenuItem("show Editor");
		ShowEditor.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 

				makeEdit();
				mouseClicked(null);
			} 
		} );
		DisplayMenu.add(ShowEditor);
		JMenuItem reshapePaper= new JMenuItem("reshape paper");
		reshapePaper.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { 

				if(myPaper!=null) {
					myPaper.refreshNodes();
					mouseClicked(null);
				}
			} 
		});
		DisplayMenu.add(reshapePaper);
	}
	private void makeUndoActions() {
		JMenuItem undo = new JMenuItem("undo");
		undo.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 		
				undoAction();
				drawPlanner();
			} 
		} );
		JMenuItem redo = new JMenuItem("redo");
		redo.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				redoAction();
				drawPlanner();
			} 
		} );
		openEditorsMenu.add(undo);
		openEditorsMenu.add(redo);
	}
	/***
	 * creates a list of origmai designers, so multiple windows can be open at once
	 */
	public static void main(String[] args){
		origamiDesigner design = new origamiDesigner();
		origamiDesigner.allDesignerWindows.add(design); // MC
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { System.err.println("Error: " + e.getMessage()); }
		design.setVisible(true);
	}
	/***
	 * undoes the last action
	 */
	private void undoAction() {
		int last=(undoPapers.size()-1);
		if(last>=0) {
			redoPapers.add(myPaper);
			myPaper=undoPapers.get(last);
			planner.p=myPaper;
			undoPapers.remove(last);
			this.drawPlanner();
			this.myEdit.redraw(myPaper);
		}
	}
	/***
	 * creates a new layout, and optimizes the paper
	 */
	private void optimizeAction() {
		//Collections.shuffle(myPaper.nodes);

		undoPapers.add(new paper(myPaper));
		//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		int buffer=0;
		lay= new layout(myPaper,buffer,Integer.MAX_VALUE);
		try {
			lay.doInBackground();
			myPaper=lay.getPaper(myPaper);
		} catch (Exception e) {
			e.printStackTrace();
		}
		makeEdit();
		myEdit.escapeMode.setSelected(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		drawPlanner();
		System.out.println("overlaps: "+myPaper.hasOverlap());
	}
	/***
	 * saves the paper object as a file
	 */
	private void saveFile() {
		disposeEditors();
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				myPaper.trueAreas=null;
				myPaper.largeAreas=null;
				ObjectOutputStream fileOut = new ObjectOutputStream(new FileOutputStream (file));
				fileOut.writeObject(myPaper);
				fileOut.close();
				redoPapers= new ArrayList<paper>();
				undoPapers= new ArrayList<paper>();
			} catch (Exception e) {
				System.err.println("Could not open the file.");
				e.printStackTrace();
			}
		}
	}
	private void printFile() {
		drawCreases();
		myCreases.printCreases(myPaper);
	}
	/***
	 * opens a new window and displays the crease pattern on it
	 */
	private void drawCreases() {
		if(showCreases!=null) {
			showCreases.dispose();
		}
		myCreases= new CreasePlanner(myPaper);
		myCreases.hasChanged=true;
		showCreases= new JFrame();
		showCreases.setLocation(0,0);
		showCreases.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		showCreases.setUndecorated(false);
		showCreases.add(myCreases);
		showCreases.setVisible(true);
		myCreases.setVisible(true);
		if(showCreases!=null) {
			myCreases.repaint();
		}
	}
	/***
	 * opens a file as a paper object
	 */
	private void openFile() {
		disposeEditors();
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(file));
				myPaper=new paper((paper)fileIn.readObject());
				fileIn.close();  
			} catch (Exception e) {
				System.err.println("Could not open the file.");
				e.printStackTrace();
			}
		}
		makeEdit();
		planner.p=myPaper;
		planner.hasChanged=true;
		drawPlanner();
	}
	/***
	 * makes a new origami designer
	 */
	private void newFile() {
		origamiDesigner design = new origamiDesigner();
		origamiDesigner.allDesignerWindows.add(design);
		design.setVisible(true);
		planner.hasChanged=true;
	}
	/***
	 * This finds the largest value for the size of a square with the current window size.
	 */
	private double getSquareSize() {
		//size and proportion of the window can change, and all parts of the paper should be visible
		return Math.min(((double)planner.getWidth())/myPaper.width, ((double)planner.getHeight())/myPaper.height);
	}
	public void stateChanged(ChangeEvent e) {}
	public void mouseDragged(MouseEvent arg0) {}
	public void mouseMoved(MouseEvent arg0) {}
	public void mouseClicked(MouseEvent arg0) {
		squareSize= getSquareSize();
		int x = 0;
		int y = 0;
		if(arg0!=null) {
			try {
				x= (int) ((double)(arg0.getX())/squareSize+.5);
				y= (int) (((double)arg0.getY())/squareSize+.5);
			} catch (Exception e) { }
			makeSelectedAction( x, y);
		}
		if(myPaper.selected!=null) {
			myPaper.selected.size=(int) Integer.parseInt(myEdit.nodeSize.getValue().toString());;
		}
		myPaper.width=(int) Integer.parseInt(myEdit.Width.getValue().toString());
		myPaper.height=(int)Integer.parseInt(myEdit.Height.getValue().toString());
		if(this.myEdit!=null) {
			if(myEdit.optimizeToRatio.isSelected()) {
		myPaper.ratioX_Y=(double)myPaper.width/(double)myPaper.height;
			}else{
				myPaper.ratioX_Y=1;
			}
		}
		drawPlanner();
	}
	/***
	 * Handles the actions for the different modes 
	 */
	private void makeSelectedAction(int x, int y) {
		if(myEdit!=null) {
			if(myEdit.leafMode.isSelected()) {
				addLeaf(x,y);
				planner.hasChanged=true;
			}
			if(myEdit.riverMode.isSelected()) {
				addRiver(x,y);
				planner.hasChanged=true;
			}
			if(myEdit.moveMode.isSelected()) {
				moveNode(x,y);
				planner.hasChanged=true;
			}
			if(myEdit.selectMode.isSelected()) {
				selectNode(x,y);
				planner.hasChanged=true;
			}

			if(myEdit.deleteMode.isSelected()) {
				deleteNode(x,y);
				planner.hasChanged=true;
			}
		}
	}
	/***
	 * deletes a node from the paper, and selects an new node is necessary
	 */
	private void deleteNode(int x, int y) {
		node selected= myPaper.getNodeAt(x,y);
		if(selected==(null)) {
		}else {
			undoPapers.add(new paper(myPaper));
			myPaper.deleteNode(selected);
			myEdit.nodeSize.setValue(myPaper.selected.size);
		}
	}
	/***
	 * selects the node at the specified location, if there is one.
	 */
	private void selectNode(int x, int y) {
		node selected= myPaper.getNodeAt(x,y);
		if(selected==(null)) {	
		}else {
			myPaper.setSelectedNode(selected);
			myEdit.nodeSize.setValue(selected.size);
			myEdit.nodeMirrored.setSelected(selected.isMirrored);
		}
	}
	/***
	 * moves the selected node to the specified location
	 */
	private void moveNode(int x, int y) {
		undoPapers.add(new paper(myPaper));
		myPaper.moveSelect(x,y);
	}
	/***
	 * Adds a river node to the paper, at the specified location
	 */
	private void addRiver(int x, int y) {
		undoPapers.add(new paper(myPaper));

		int startID=this.myPaper.maxSize;
		if(myPaper.selected.size!=0) {
			// if the selected node is size zero, adding a second size 0 node is not necessary 
			node newNode1= new node(x, y-1, 0, startID);
			startID+=1;
			myPaper.addNode(newNode1);
			myPaper.setSelectedNode(newNode1);
		}
		node newNode2= new node(x, y, 1,(startID));
		node newNode3= new node(x, y+1, 0,(startID+1));
		myPaper.addNode(newNode2);
		myPaper.setSelectedNode(newNode2);
		myPaper.addNode(newNode3);
		myPaper.setSelectedNode(newNode3);
		myEdit.nodeSize.setValue(0);
	}
	/***
	 * Adds a leaf node at the specified location to the paper
	 */
	private void addLeaf(int x, int y) {
		undoPapers.add(new paper(myPaper));
		node newNode= new node(x, y,1, this.myPaper.maxSize);
		myPaper.addNode(newNode);
		myPaper.setSelectedNode(newNode);
		myEdit.nodeSize.setValue(1);
	}
	public  void actionPerformed(ActionEvent arg0) {
		mouseClicked(null);
	}
	/***
	 * redoes the last undo to the paper
	 */
	private void redoAction() {
		int last=(redoPapers.size()-1);
		if(last>=0) {
			undoPapers.add(myPaper);
			myPaper=redoPapers.get(last);
			redoPapers.remove(last);
			planner.p=myPaper;
		}
	}
	/***
	 * updates and redraws the planner
	 */
	public void drawPlanner() {
		planner.repaint();	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void keyPressed(KeyEvent arg0) {}
	@Override
	public void keyReleased(KeyEvent arg0) {
		mouseClicked(null);
	}
	/***
	 * This lets the user change modes by typing
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		char key = arg0.getKeyChar();
		if(key=='l') {
			myEdit.leafMode.setSelected(true);
		}
		if(key=='r') {
			myEdit.riverMode.setSelected(true);
		}
		if(key=='s') {
			myEdit.selectMode.setSelected(true);
		}
		if(key=='d') {
			myEdit.deleteMode.setSelected(true);
		}
		if(key=='m') {
			myEdit.moveMode.setSelected(true);
		}
		if(key=='e') {
			myEdit.escapeMode.setSelected(true);
		}
	}
	
}