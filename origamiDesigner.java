package origamiProject;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class origamiDesigner extends JFrame implements ActionListener,ChangeListener, MouseListener{

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
	JFrame advanced;
	layout lay;

	JMenu DisplayMenu;
	
	JLabel Node;
	ButtonGroup Mode;
	JToggleButton leafMode;
	JToggleButton riverMode;
	JToggleButton selectMode;
	JToggleButton deleteMode;
	JToggleButton moveMode;
	JToggleButton escapeMode;
	JLabel nodeSizeLabel;
	JSpinner nodeSize;

	JLabel conditions;
	JToggleButton addsymmetry;
	JToggleButton removesymmetry;
	JToggleButton selectNode;
	JRadioButton nosymmetry;
	JRadioButton Xsymmetry;
	JRadioButton Ysymmetry;
	
	JLabel paper;
	JSpinner Width;
	JSpinner Height;
	ButtonGroup optimizeParams;
	JRadioButton square;
	JRadioButton fixRatio;

	private JRadioButton creases;
	private JRadioButton treePlan;
	int squareSize;
	public origamiDesigner() {
		initalizeDesigner();
		setUpPlanner();
		setUpCloseing();
	}
	private void setUpCloseing() {
		planner.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				myEdit.toFront();
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
		this.setJMenuBar(menuBar);
		this.advanced=new JFrame();
	}
	public void createMenu() {
		menuBar= new JMenuBar();
		createFileMenu();
		createDisplayMenu();
		JMenuItem advancedOptions= new JMenuItem("advanced options");
		advancedOptions.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				makeAdvanced();
				javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
			} 
		} );
		menuBar.add(advancedOptions);
	}
	private void createFileMenu() {
		fileMenu= new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem save= new JMenuItem("save",KeyEvent.VK_S);
		JMenuItem open= new JMenuItem("open",KeyEvent.VK_O);
		JMenuItem New= new JMenuItem("new",KeyEvent.VK_N);
		JMenuItem print= new JMenuItem("print",KeyEvent.VK_P);
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
	private void makeEdit() {
		if(myEdit!=null) {
			myEdit.dispose();
		}
		myEdit= new JFrame();
		myEdit.setLocation(this.getX()+this.getWidth(),this.getY());
		myEdit.setSize(300, 400);
		myEdit.setLayout(new GridLayout(10,2));
		createNodeGui();
		createPaperGui();
		myEdit.setVisible(true);
	}
	private void createDisplayMenu() {
		DisplayMenu= new JMenu("Display");
		makeDisplayActions();
		makeUndoActions();
		makeDisplayOptions();
		menuBar.add(DisplayMenu);
	}
	private void makeDisplayOptions() {
		ButtonGroup design= new ButtonGroup();
		creases= new JRadioButton("show crease pattern");
		creases.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				drawPlanner();
				javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
			} 
		} );
		treePlan= new JRadioButton("show tree plan");
		treePlan.setSelected(true);
		treePlan.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				drawPlanner();
				javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
			} 
		} );
		DisplayMenu.add(treePlan);
		DisplayMenu.add(creases);
		design.add(treePlan);
		design.add(creases);
	}

	private void makeUndoActions() {
		JMenuItem undo = new JMenuItem("undo");
		undo.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				undoAction();
				javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
			} 
		} );
		JMenuItem redo = new JMenuItem("redo");
		redo.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				redoAction();
				javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
			} 
		} );
		DisplayMenu.add(undo);
		DisplayMenu.add(redo);
	}

	private void makeDisplayActions() {
		JMenuItem ShowEditor= new JMenuItem("show Editor");
		ShowEditor.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				makeEdit();
				javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
			} 
		} );
		DisplayMenu.add(ShowEditor);
		JMenuItem optimize = new JMenuItem("optimize");
		optimize.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				optimizeAction();
				javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
			} 
		} );
		DisplayMenu.add(optimize);
	}

	public static void main(String[] args){
		origamiDesigner design = new origamiDesigner();
		origamiDesigner.allDesignerWindows.add(design); // MC

		design.setVisible(true);
	}
	private void undoAction() {
		int last=(undoPapers.size()-1);
		if(last>0) {
			redoPapers.add(myPaper);
			this.myPaper=this.undoPapers.get(last);
			undoPapers.remove(last);
		}
	}
	private void optimizeAction() {
		myPaper.hasSymmetry=!this.nosymmetry.isSelected();
		if(myPaper.hasSymmetry) {
			System.out.println("ok");
			myPaper.isXsymmetry=this.Xsymmetry.isSelected();
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		lay= new layout(myPaper);
		long start_time=System.currentTimeMillis();
		lay.optimize();
		long end_time=System.currentTimeMillis();
		long time=end_time-start_time;
		System.out.print("optimized in" +time +"milli seconds");
		myPaper=lay.getPaper(myPaper);
		int w=myPaper.width;
		int h= myPaper.height;
		this.Height.setValue(h);
		this.Width.setValue(w);
		escapeMode.setSelected(true);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	private void saveFile() {
		for(node n:myPaper.nodes) {
			n.A=null;
		}
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
		escapeMode.setSelected(true);
		if(myPaper.selected!=null) {
			this.nodeSize.setValue(myPaper.selected.size);

		}
	}
	private void newFile() {
		origamiDesigner design = new origamiDesigner();
		origamiDesigner.allDesignerWindows.add(design);
		design.setVisible(true);
	}
	private int getSquareSize() {
		return Math.min(planner.getWidth()/myPaper.width, planner.getHeight()/myPaper.height);
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
		escapeMode= new JToggleButton("escape");
		Mode.add(escapeMode);
		myEdit.add(escapeMode);
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
	
		}
	public void stateChanged(ChangeEvent e) {
		this.mouseClicked(null);
	}

	public void mouseDragged(MouseEvent arg0) {}
	public void mouseMoved(MouseEvent arg0) {}
	public void mouseClicked(MouseEvent arg0) {
		squareSize= getSquareSize();
		int x = 0;
		int y = 0;
		if(arg0!=null) {
			try {
				x= arg0.getX()/squareSize;
				y= (arg0.getY())/squareSize;
			} catch (Exception e) { }
			makeSelectedAction( x, y);
		}
		if(myPaper.selected!=null) {
			myPaper.selected.size=(int) Integer.parseInt(nodeSize.getValue().toString());;

		}
		if(advanced.isActive()) {
		myPaper.isFixedRatio=this.fixRatio.isSelected();
		}
		myPaper.width=(int) Integer.parseInt(Width.getValue().toString());
		myPaper.height=(int)Integer.parseInt(Height.getValue().toString());;
		myPaper.ratioX_Y=myPaper.width/myPaper.height;

		drawPlanner();
	}
	private void makeSelectedAction(int x, int y) {
		if(addsymmetry!=null) {
			if(this.addsymmetry.isSelected()) {
				addsymmetry(x,y);	
			}
			if(this.removesymmetry.isSelected()) {
				removesymmetry(x,y);	
			}
			if(this.selectNode.isSelected()) {
				selectNode(x,y);	
			}
		}
		if(leafMode.isSelected()) {
			addLeaf(x,y);
		}
		if(riverMode.isSelected()) {
			addRiver(x,y);
		}
		if(moveMode.isSelected()) {
			moveNode(x,y);
		}
		if(selectMode.isSelected()) {
			selectNode(x,y);
		}

		if(deleteMode.isSelected()) {
			deleteNode(x,y);
		}
	}

	private void removesymmetry(int x, int y) {
		node selected= myPaper.getNodeAt(x,y);
		if(selected==(null)) {	
		}else {
			myPaper.removeConditions(selected, myPaper.selected);
		}
	}

	private void addsymmetry(int x, int y) {
		node selected= myPaper.getNodeAt(x,y);
		if(selected==(null)) {	
		}else {
			myPaper.addConditions(selected, myPaper.selected);
		}
	}

	private void deleteNode(int x, int y) {
		node selected= myPaper.getNodeAt(x,y);
		if(selected==(null)) {
		}else {
			this.undoPapers.add(new paper(myPaper));
			myPaper.deleteNode(selected);
		}
	}
	private void selectNode(int x, int y) {
		node selected= myPaper.getNodeAt(x,y);
		if(selected==(null)) {	
		}else {
			myPaper.setSelectedNode(selected);
			nodeSize.setValue(selected.size);

		}
	}
	private void moveNode(int x, int y) {
		this.undoPapers.add(new paper(myPaper));
		myPaper.moveSelect(x,y);
	}
	private void addRiver(int x, int y) {
		this.undoPapers.add(new paper(myPaper));
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
	}
	private void addLeaf(int x, int y) {
		this.undoPapers.add(new paper(myPaper));
		node newNode= new node(x, y,1);
		myPaper.addNode(newNode);
		myPaper.setSelectedNode(newNode);
		nodeSize.setValue(1);
	}

	public void actionPerformed(ActionEvent arg0) {
		
		this.mouseClicked(null);
	}

	private void makeAdvanced() {
		setUpAdvanced();
		makeSymmetryConditions();
		makeSymmetryActions();
		makeOptimizeConditions();
		finishAdvancedSetup();
	}
	private void finishAdvancedSetup() {
		advanced.setVisible(true);
		planner.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				advanced.toFront();
				mouseClicked(null);
			}});
	}
	private void makeOptimizeConditions() {
		optimizeParams= new ButtonGroup();
		JLabel optimizeLabel= new JLabel("Optimize to:");
		advanced.add(optimizeLabel);
		square= new JRadioButton("Square");
		square.setSelected(true);
		advanced.add(square);
		optimizeParams.add(square);
		fixRatio= new JRadioButton("fix ratio");
		advanced.add(fixRatio);
		optimizeParams.add(fixRatio);
	}
	private void makeSymmetryActions() {
		ButtonGroup symmetryAction= new ButtonGroup();
		addsymmetry= new JToggleButton("add symmetry");
		advanced.add(addsymmetry);
		symmetryAction.add(addsymmetry);
		addsymmetry.addActionListener(this);

		removesymmetry= new JToggleButton("remove symmetry");
		advanced.add(removesymmetry);
		symmetryAction.add(removesymmetry);
		removesymmetry.addActionListener(this);
		selectNode= new JToggleButton("selectNode");
		symmetryAction.add(selectNode);
		advanced.add(selectNode);
		selectNode.addActionListener(this);
	}

	private void makeSymmetryConditions() {
		this.advanced.add(new JLabel("symmetry"));
		ButtonGroup symmetryCon= new ButtonGroup();
		nosymmetry= new JRadioButton("no symmetry");
		symmetryCon.add(nosymmetry);
		nosymmetry.addActionListener(this);
		this.advanced.add(nosymmetry);
		nosymmetry.setSelected(true);
		Xsymmetry= new JRadioButton("X symmetry");
		symmetryCon.add(Xsymmetry);
		Xsymmetry.addActionListener(this);
		this.advanced.add(Xsymmetry);
		Ysymmetry= new JRadioButton("Y symmetry");
		symmetryCon.add(Ysymmetry);
		Ysymmetry.addActionListener(this);
		this.advanced.add(Ysymmetry);
	}

	private void setUpAdvanced() {
		escapeMode.setSelected(true);
		this.advanced= new JFrame();
		this.advanced.setSize(250,300);
		this.advanced.setLocation(this.getWidth()+this.getX(), this.getY());
		this.advanced.setLayout(new GridLayout(10,1));
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
		if(this.creases.isSelected()) {
			planner.drawCreases(myPaper);
		}
		if(this.treePlan.isSelected()) {
			planner.drawPlan(myPaper);
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}

}
