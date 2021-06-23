package GuiClasses;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

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

import origamiClasses.layout;
import origamiClasses.node;
import origamiClasses.paper;

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
		String FileData= getFileData();
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				myPaper.trueAreas=null;
				myPaper.largeAreas=null;
				ObjectOutputStream fileOut = new ObjectOutputStream(new FileOutputStream (file));
				//FileWriter write=new FileWriter(file);
				fileOut.writeObject(myPaper);
				//fileOut.writeObject(FileData);
				//write.write(FileData);
				//.writeChars(FileData);
				fileOut.close();
				//write.close();
				redoPapers= new ArrayList<paper>();
				undoPapers= new ArrayList<paper>();
			} catch (Exception e) {
				System.err.println("Could not open the file.");
				e.printStackTrace();
			}
		}
	}
	private String getFileData() {
		String fileData="";
		fileData="tree\r\n" + 
				"5.0\r\n" + 
				"1.0000000000\r\n" + 
				"1.0000000000\r\n" + 
				"0.1000000000\r\n" + 
				"false\r\n" + 
				"0.5000000000\r\n" + 
				"0.5000000000\r\n" + 
				"90.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"8\r\n" + 
				"7\r\n" + 
				"28\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"node\r\n" + 
				"1\r\n" + 
				"\r\n" + 
				"0.3061946903\r\n" + 
				"0.9008849558\r\n" + 
				"-999.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"2\r\n" + 
				"1\r\n" + 
				"7\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"node\r\n" + 
				"2\r\n" + 
				"\r\n" + 
				"0.4176991150\r\n" + 
				"0.5964601770\r\n" + 
				"-999.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"4\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"4\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"node\r\n" + 
				"3\r\n" + 
				"\r\n" + 
				"0.6424778761\r\n" + 
				"0.7858407080\r\n" + 
				"-999.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"6\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"8\r\n" + 
				"13\r\n" + 
				"19\r\n" + 
				"26\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"node\r\n" + 
				"4\r\n" + 
				"\r\n" + 
				"0.4495575221\r\n" + 
				"0.8159292035\r\n" + 
				"-999.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"1\r\n" + 
				"3\r\n" + 
				"6\r\n" + 
				"4\r\n" + 
				"5\r\n" + 
				"7\r\n" + 
				"14\r\n" + 
				"20\r\n" + 
				"25\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"node\r\n" + 
				"5\r\n" + 
				"\r\n" + 
				"0.1929203540\r\n" + 
				"0.3362831858\r\n" + 
				"-999.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"3\r\n" + 
				"4\r\n" + 
				"5\r\n" + 
				"6\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"node\r\n" + 
				"6\r\n" + 
				"\r\n" + 
				"0.4566371681\r\n" + 
				"0.1115044248\r\n" + 
				"-999.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"1\r\n" + 
				"5\r\n" + 
				"5\r\n" + 
				"12\r\n" + 
				"13\r\n" + 
				"14\r\n" + 
				"16\r\n" + 
				"23\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"node\r\n" + 
				"7\r\n" + 
				"\r\n" + 
				"0.1079646018\r\n" + 
				"0.2336283186\r\n" + 
				"-999.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"1\r\n" + 
				"6\r\n" + 
				"5\r\n" + 
				"16\r\n" + 
				"18\r\n" + 
				"19\r\n" + 
				"20\r\n" + 
				"22\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"node\r\n" + 
				"8\r\n" + 
				"\r\n" + 
				"0.1185840708\r\n" + 
				"0.7805309735\r\n" + 
				"-999.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"1\r\n" + 
				"7\r\n" + 
				"4\r\n" + 
				"22\r\n" + 
				"23\r\n" + 
				"25\r\n" + 
				"26\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"edge\r\n" + 
				"1\r\n" + 
				"\r\n" + 
				"1.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"1.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"2\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"edge\r\n" + 
				"2\r\n" + 
				"\r\n" + 
				"1.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"1.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"2\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"edge\r\n" + 
				"3\r\n" + 
				"\r\n" + 
				"1.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"1.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"2\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"edge\r\n" + 
				"4\r\n" + 
				"\r\n" + 
				"1.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"1.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"2\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"edge\r\n" + 
				"5\r\n" + 
				"\r\n" + 
				"2.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"1.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"6\r\n" + 
				"edge\r\n" + 
				"6\r\n" + 
				"\r\n" + 
				"2.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"1.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"7\r\n" + 
				"edge\r\n" + 
				"7\r\n" + 
				"\r\n" + 
				"1.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"1.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"2\r\n" + 
				"1\r\n" + 
				"8\r\n" + 
				"path\r\n" + 
				"1\r\n" + 
				"1.0000000000\r\n" + 
				"0.1000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"2\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"1\r\n" + 
				"1\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"2\r\n" + 
				"2.0000000000\r\n" + 
				"0.2000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"3\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"2\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"3\r\n" + 
				"1.0000000000\r\n" + 
				"0.1000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"2\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"4\r\n" + 
				"2.0000000000\r\n" + 
				"0.2000000000\r\n" + 
				"1.9525260702\r\n" + 
				"0.1952526070\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"3\r\n" + 
				"4\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"2\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"5\r\n" + 
				"2.0000000000\r\n" + 
				"0.2000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"3\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"2\r\n" + 
				"1\r\n" + 
				"3\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"6\r\n" + 
				"1.0000000000\r\n" + 
				"0.1000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"2\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"1\r\n" + 
				"3\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"7\r\n" + 
				"2.0000000000\r\n" + 
				"0.2000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"3\r\n" + 
				"5\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"3\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"8\r\n" + 
				"2.0000000000\r\n" + 
				"0.2000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"3\r\n" + 
				"5\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"2\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"9\r\n" + 
				"2.0000000000\r\n" + 
				"0.2000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"3\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"2\r\n" + 
				"1\r\n" + 
				"4\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"10\r\n" + 
				"1.0000000000\r\n" + 
				"0.1000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"2\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"1\r\n" + 
				"4\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"11\r\n" + 
				"3.0000000000\r\n" + 
				"0.3000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"3\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"6\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"5\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"12\r\n" + 
				"4.0000000000\r\n" + 
				"0.4000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"4\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"6\r\n" + 
				"3\r\n" + 
				"1\r\n" + 
				"4\r\n" + 
				"5\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"13\r\n" + 
				"4.0000000000\r\n" + 
				"0.4000000000\r\n" + 
				"6.9947565473\r\n" + 
				"0.6994756547\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"4\r\n" + 
				"6\r\n" + 
				"5\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"3\r\n" + 
				"5\r\n" + 
				"4\r\n" + 
				"2\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"14\r\n" + 
				"4.0000000000\r\n" + 
				"0.4000000000\r\n" + 
				"7.0446035397\r\n" + 
				"0.7044603540\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"4\r\n" + 
				"6\r\n" + 
				"5\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"3\r\n" + 
				"5\r\n" + 
				"4\r\n" + 
				"3\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"15\r\n" + 
				"2.0000000000\r\n" + 
				"0.2000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"6\r\n" + 
				"1\r\n" + 
				"5\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"16\r\n" + 
				"4.0000000000\r\n" + 
				"0.4000000000\r\n" + 
				"3.6944120504\r\n" + 
				"0.3694412050\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"3\r\n" + 
				"7\r\n" + 
				"5\r\n" + 
				"6\r\n" + 
				"2\r\n" + 
				"6\r\n" + 
				"5\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"17\r\n" + 
				"3.0000000000\r\n" + 
				"0.3000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"3\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"7\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"6\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"18\r\n" + 
				"4.0000000000\r\n" + 
				"0.4000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"4\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"7\r\n" + 
				"3\r\n" + 
				"1\r\n" + 
				"4\r\n" + 
				"6\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"19\r\n" + 
				"4.0000000000\r\n" + 
				"0.4000000000\r\n" + 
				"7.6853299436\r\n" + 
				"0.7685329944\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"4\r\n" + 
				"7\r\n" + 
				"5\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"3\r\n" + 
				"6\r\n" + 
				"4\r\n" + 
				"2\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"20\r\n" + 
				"4.0000000000\r\n" + 
				"0.4000000000\r\n" + 
				"6.7510002507\r\n" + 
				"0.6751000251\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"4\r\n" + 
				"7\r\n" + 
				"5\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"3\r\n" + 
				"6\r\n" + 
				"4\r\n" + 
				"3\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"21\r\n" + 
				"2.0000000000\r\n" + 
				"0.2000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"7\r\n" + 
				"1\r\n" + 
				"6\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"22\r\n" + 
				"5.0000000000\r\n" + 
				"0.5000000000\r\n" + 
				"5.4700574679\r\n" + 
				"0.5470057468\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"5\r\n" + 
				"8\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"7\r\n" + 
				"4\r\n" + 
				"7\r\n" + 
				"1\r\n" + 
				"4\r\n" + 
				"6\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"23\r\n" + 
				"5.0000000000\r\n" + 
				"0.5000000000\r\n" + 
				"7.4958416436\r\n" + 
				"0.7495841644\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"5\r\n" + 
				"8\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"6\r\n" + 
				"4\r\n" + 
				"7\r\n" + 
				"1\r\n" + 
				"4\r\n" + 
				"5\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"24\r\n" + 
				"3.0000000000\r\n" + 
				"0.3000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"4\r\n" + 
				"8\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"5\r\n" + 
				"3\r\n" + 
				"7\r\n" + 
				"1\r\n" + 
				"4\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"25\r\n" + 
				"3.0000000000\r\n" + 
				"0.3000000000\r\n" + 
				"3.3286102232\r\n" + 
				"0.3328610223\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"4\r\n" + 
				"8\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"4\r\n" + 
				"3\r\n" + 
				"7\r\n" + 
				"1\r\n" + 
				"3\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"26\r\n" + 
				"3.0000000000\r\n" + 
				"0.3000000000\r\n" + 
				"5.2392071206\r\n" + 
				"0.5239207121\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"true\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"4\r\n" + 
				"8\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"3\r\n" + 
				"7\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"27\r\n" + 
				"2.0000000000\r\n" + 
				"0.2000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"3\r\n" + 
				"8\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"2\r\n" + 
				"7\r\n" + 
				"1\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"path\r\n" + 
				"28\r\n" + 
				"1.0000000000\r\n" + 
				"0.1000000000\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"false\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"2\r\n" + 
				"1\r\n" + 
				"8\r\n" + 
				"1\r\n" + 
				"7\r\n" + 
				"0\r\n" + 
				"0.0000000000\r\n" + 
				"0.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"-999.0000000000\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"0\r\n" + 
				"8\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"4\r\n" + 
				"5\r\n" + 
				"6\r\n" + 
				"7\r\n" + 
				"8\r\n" + 
				"7\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"4\r\n" + 
				"5\r\n" + 
				"6\r\n" + 
				"7\r\n" + 
				"28\r\n" + 
				"1\r\n" + 
				"2\r\n" + 
				"3\r\n" + 
				"4\r\n" + 
				"5\r\n" + 
				"6\r\n" + 
				"7\r\n" + 
				"8\r\n" + 
				"9\r\n" + 
				"10\r\n" + 
				"11\r\n" + 
				"12\r\n" + 
				"13\r\n" + 
				"14\r\n" + 
				"15\r\n" + 
				"16\r\n" + 
				"17\r\n" + 
				"18\r\n" + 
				"19\r\n" + 
				"20\r\n" + 
				"21\r\n" + 
				"22\r\n" + 
				"23\r\n" + 
				"24\r\n" + 
				"25\r\n" + 
				"26\r\n" + 
				"27\r\n" + 
				"28\r\n" + 
				"0\r\n" + 
				"";
		return fileData;
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
			if(file.getName().contains(".tmd5")) {
				readFileData(file.getPath());
			}else {
				try {
					ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(file));
					myPaper=new paper((paper)fileIn.readObject());
					fileIn.close();  
				} catch (Exception e) {
					System.out.println(file.toString());
					System.err.println("Could not open the file.");
					e.printStackTrace();
				}
			}
		}
		makeEdit();
		planner.p=myPaper;
		planner.hasChanged=true;
		drawPlanner();
	}
	private String readFileData(String filePath) {


		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
		{
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		String fileString =contentBuilder.toString();
		String[] lines= fileString.split("\n");
		int Psize= (int)(1/Float.parseFloat(lines[4]));
		paper newPaper=new paper(Psize,Psize);
		String Nodes=fileString.substring(fileString.indexOf("node"), fileString.indexOf("edge"));
		String[] nodeStrings=Nodes.split("node");
		String[] edgeStrings=fileString.substring(fileString.indexOf("edge"), fileString.indexOf("path")).split("edge");
		ArrayList<node>newNodes=new ArrayList<node>();
		for( String nodeString:nodeStrings) {
			if(nodeString.length()>1) {
				String[] nodeInfo=nodeString.split("\n");
				//System.out.println(nodeString.toString());
				int id=Integer.parseInt(nodeInfo[1]);
				int x=(int) (Psize*Float.parseFloat(nodeInfo[3]));
				int y=(int) Psize-(int)(Psize*Float.parseFloat(nodeInfo[4]));
				Boolean isLeaf= Boolean.parseBoolean(nodeInfo[7]);
				//System.out.println("id:"+id+" x: "+x+" y; "+y);
				node newNode=new node(x,y,0,id);
				newNode.isLeaf=isLeaf;
				newNodes.add(newNode);
			}

		}
		newPaper.addAll(newNodes);
		int index=newNodes.size();
		for( String edgeString:edgeStrings) {
			if(edgeString.length()>1) {
				String[] nodeInfo=edgeString.split("\n");
				//System.out.println(nodeString.toString());
				double Esize=Double.parseDouble(nodeInfo[3]);
				int start=(int) (Integer.parseInt(nodeInfo[9]))-1;
				int end=(int) (Integer.parseInt(nodeInfo[10]))-1;
				boolean containsLeaf=false;
				if(newNodes.get(start).isLeaf) {
					containsLeaf=true;
					newNodes.get(start).size=(int)Esize;
					newPaper.addConection(newNodes.get(start), newNodes.get(end));
				}
				if(newNodes.get(end).isLeaf) {
					containsLeaf=true;
					newNodes.get(end).size=(int)Esize;
					newPaper.addConection(newNodes.get(start), newNodes.get(end));
				}
				if(!containsLeaf) {
					index++;
					node startNode =newNodes.get(start);
					node endNode= newNodes.get(end);
					int x=(startNode.getX()+endNode.getX())/2;

					int y=(startNode.getY()+endNode.getY())/2;
					node NewNode = new node(x,y,(int)Esize,index);
					NewNode.isLeaf=false;
					newPaper.addNode(NewNode, false);
					newPaper.addConection(endNode, NewNode);
					newPaper.addConection(NewNode, startNode);
				}
				//System.out.println("size: "+Esize+" from: "+start+" to: "+end);
			}

		}
		//System.out.println(edgeStrings);
		this.myPaper=newPaper;
		myPaper.getTreeDistances();
		System.out.println("tree");
		System.out.println("5.0");
		float maxD=Math.max(myPaper.width, myPaper.height);
		System.out.println((float)myPaper.width/maxD);
		System.out.println((float)myPaper.height/maxD);
		System.out.println(1/maxD);
		System.out.println(myPaper.hasSymmetry);
		System.out.println("0.5");
		System.out.println("0.5");
		System.out.println("90");
		System.out.println(!myPaper.hasOverlap());
		System.out.println(!myPaper.hasOverlap());
		System.out.println(!myPaper.hasOverlap());
		System.out.println(!myPaper.hasOverlap());
		System.out.println(!myPaper.hasOverlap());
		System.out.println(!myPaper.hasOverlap());
		System.out.println(!myPaper.hasOverlap());
		int numLangNodes=0;
		int numLangEdges=0;
		int numLangPaths=0;
		int leafNodes=0;
		HashMap<Integer,Integer> langIndex= new HashMap<Integer,Integer>();
		HashMap<Integer,ArrayList<Integer>> searched= new HashMap<Integer,ArrayList<Integer>>();
		ArrayList<node> LangNodes= new ArrayList<node>();
		int LIndex=1;
		for(node n:myPaper.nodes) {
			searched.put(n.ID, new ArrayList<Integer>());
			if(n.isLeaf||n.size==0) {
			langIndex.put(n.ID,LIndex);
			LIndex++;
			}
		}
		String nodesString="";
		String edgesString="";
		String pathsString="";
		int edgeIndex=1;
		int pathIndex=1;
		for(node n:myPaper.nodes) {

			if(n.isLeaf||n.size==0) {
				numLangNodes++;
				LangNodes.add(n);
				nodesString+=("node \n");
				nodesString+=(langIndex.get(n.ID)+"\n");
				nodesString+="\n";
				nodesString+=((float)n.getX()/Psize+"\n");
				nodesString+=(1-(float)n.getY()/Psize+"\n");
				nodesString+=("-999.0000000000 \n");
				nodesString+=("0.0000000000 \n");
				nodesString+=(n.isLeaf+"\n");
				nodesString+="false \n";
				nodesString+="false \n";
				nodesString+="false \n";
				nodesString+="false \n";
				nodesString+="false \n";
				nodesString+="false \n";

				//numLangEdges+=myPaper.connections.get(n.ID).size();
				
				for( node m:myPaper.nodes) {
					if(!n.equals(m)&&!searched.get(n.ID).contains(m.ID)) {
						searched.get(m.ID).add(n.ID);
						if(m.isLeaf||m.size==0) {
							numLangPaths++;
							if(myPaper.connections.get(n.ID).contains(m)) {
								numLangEdges++;
								edgesString+="edge \n";
								edgesString+=(edgeIndex+"\n");
								edgesString+="\n";
								edgesString+=(myPaper.distances.get(n.ID).get(m.ID)+"\n");
								edgesString+=("0 \n");
								edgesString+=("1 \n");
								edgesString+="false \n";
								edgesString+="false \n";
								edgesString+="2 \n";
								edgesString+=(langIndex.get(n.ID)+"\n");
								edgesString+=(langIndex.get(m.ID)+"\n");
								edgeIndex++;
							}
						}
						if(n.isLeaf) {
							leafNodes++;
						}
					}
					if(m.isLeaf||m.size==0) {
					
					}
				}
			}else {
				numLangEdges++;
				edgesString+="edge \n";
				edgesString+=(edgeIndex+"\n");
				edgesString+="\n";
				edgesString+=(n.size+"\n");
				edgesString+=("0 \n");
				edgesString+=("1 \n");
				edgesString+="false \n";
				edgesString+="false \n";
				edgesString+="2 \n";
				edgesString+=(langIndex.get(myPaper.connections.get(n.ID).get(0).ID)+"\n");
				edgesString+=(langIndex.get(myPaper.connections.get(n.ID).get(1).ID)+"\n");
				edgeIndex++;
			}
		}

		//index=1;

		System.out.println(numLangNodes);
		System.out.println(numLangEdges);
		System.out.println(numLangPaths);
		System.out.println(0);
		System.out.println(0);
		System.out.println(0);
		System.out.println(0);
		System.out.println(0);
		System.out.println(nodesString);
		System.out.println(edgesString);
		System.out.println(pathsString);
		return contentBuilder.toString();
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
			myPaper.addNode(newNode1,true);
			myPaper.setSelectedNode(newNode1);
		}
		node newNode2= new node(x, y, 1,(startID));
		node newNode3= new node(x, y+1, 0,(startID+1));
		myPaper.addNode(newNode2,true);
		myPaper.setSelectedNode(newNode2);
		myPaper.addNode(newNode3,true);
		myPaper.setSelectedNode(newNode3);
		myEdit.nodeSize.setValue(0);
	}
	/***
	 * Adds a leaf node at the specified location to the paper
	 */
	private void addLeaf(int x, int y) {
		undoPapers.add(new paper(myPaper));
		node newNode= new node(x, y,1, this.myPaper.maxSize);
		myPaper.addNode(newNode,true);
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