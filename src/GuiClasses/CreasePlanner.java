package GuiClasses;
//if debugging the crease generation, remove this line
//import java.awt.Color;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JPanel;

import origamiClasses.node;
import origamiClasses.paper;
import origamiClasses.Creases;
/**
 * 
 * @author Benjamin Wettle
 * this class handles the display of the paper.
 * It shows the diagonal creases of the paper, on the minimun grid used to fold them
 */
public class CreasePlanner extends JPanel implements Printable,ColorSettings    {
	int squareSize;
	int smallSquareSize;
	paper p;
	int width;
	int height;
	boolean hasChanged;
	boolean doSetup;
	private static final long serialVersionUID = -1071442708667655401L;

	public CreasePlanner(paper p) {
		super();
		this.p=p;
		this.setDoubleBuffered(true);
		doSetup=true;
	}
	/**
	 * paints the crease pattern on this window
	 */
public void paintComponent(Graphics g) {
	super.paintComponent(g);
	hasChanged=true;
	Graphics2D g1=(Graphics2D)g;
	AffineTransform old=g1.getTransform();
	g.clearRect(0, 0, this.getWidth(), this.getHeight());
	g.setColor(Color.white);
	g.drawRect(0, 0,getWidth(), getHeight());
	g.setColor(Color.BLACK);
	setUp(p);
	if(p.hasSymmetry) {
		AffineTransform scale=new AffineTransform();
		scale.translate(squareSize*p.width,0);
		g1.transform(scale);
		//draw the creases shifted over
		this.drawCreases(p, g1);
		AffineTransform mirror=new AffineTransform();
		mirror.scale(-1, 1);
		g1.transform(mirror);;
		this.hasChanged=false;
		// and then draw the again mirrored on the other side
		this.drawCreases(p, g1);
		g1.setTransform(old);
	}else {
		this.drawCreases(p, g1);
	}
}
/**
 * draws a grid, shifted to the right by shift
 * @param shift
 * @param g
 */
	public void drawGrid(int shift,Graphics2D g) {
		if(shift==0) {
			g.setStroke(LINE_STROKE);
		}else {
			g.setStroke(CONDITION_STROKE);
		}
		for (int i=0;i<=height-1;i++){
			g.drawLine(0, i*squareSize+shift, width*squareSize, i*squareSize+shift);
		}
		for (int i=0;i<=width-1;i++){
			g.drawLine( i*squareSize+shift,0,i*squareSize+shift, height*squareSize);
		}
	}
	/**
	 * set up the basic background and grid.
	 * @param myP
	 */
	private void setUp(paper myP) {
		if(doSetup) {
		p.getTreeDistances();
		width= p.width;
		height=p.height;
		squareSize= Math.min(getWidth()/(2*width),getHeight()/height);
		smallSquareSize=squareSize/4;
		}
	}
	/**
	 * draws the creases lines found by each nodes crease object
	 * @param myP
	 * @param g
	 */
	public void drawCreases(paper myP, Graphics2D g){
		drawGrid(0,g);
		drawGrid(squareSize/2,g);
		g.setStroke(AREA_STROKE);
		g.setColor(CREASE_COLOR);
		if(myP.nodes.size()>0) {

			if(hasChanged||myP.nodes.get(0).c==null) {
				myP.getAreas(squareSize);
			}
			//total stores all the area on the paper to start
			//as nodes are drawn, their areas are removed. since all
			//since all areas must be used, we mark unused areas in orange(142)
			for(node n:myP.nodes) {
				if(n.size>0) {
				drawNodeCreases(n,g);	
			}
			}
			//g.setColor(UN_USED_AREA_COLOR);
			//g.draw(p.Unused);
			for(Line2D.Double l:p.Unused.creases) {
				g.draw(l);
			}
		}
		}
	/**
	 * draws the crease lines for a node
	 * @param n
	 * @param g
	 */
	private void drawNodeCreases(node n,Graphics2D g) {
		if(n.c!=null) {
			int i=0;
			for( Creases c :n.c) {
				//these comments are useful for debugging the crease generation
				//g.setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255),100));
				
			if(c!=null) {
			//g.setStroke(AREA_STROKE);
			//g.fill(c);
			//g.setColor(CREASE_COLOR);
				if(i%2==0) {
			g.setStroke(LINE_STROKE);
				}else {
					g.setStroke(CONDITION_STROKE);
				}
			//g.draw(c);
			// int i=0;
			//for(Point p:c.corners) {
				//g.fill(new Ellipse2D.Double(p.getX(),p.getY(),smallSquareSize,smallSquareSize));
				//int[] dir=c.directions.get(i);
				//g.draw(new Line2D.Double(p.getX(),p.getY(),p.getX()+dir[0]*smallSquareSize,p.getY()+dir[1]*smallSquareSize));
				//i++;
			//}
			for(Line2D.Double l:c.creases) {
				g.draw(l);
			}
			i++;
			//g.draw(c);
			}
			}
		}	
	}
/**
 * prints the crease pattern out to the printer
 */
public int print(Graphics g, PageFormat pf, int page) throws
	PrinterException {
		if (page > 0) { /* We have only one page, and 'page' is zero-based */
			return NO_SUCH_PAGE;
		}

		/* User (0,0) is typically outside the image able area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
		 */
		Graphics2D g1 = (Graphics2D)g;
		g1.translate(pf.getImageableX(), pf.getImageableY());
		
	
		g1.setBackground(Color.WHITE);
		width= p.width;
		height=p.height;
		if(p.hasSymmetry) {
		squareSize= (int) Math.min((pf.getImageableWidth())/(2*p.width),(pf.getImageableHeight())/p.height);
}	else {
	squareSize= (int) Math.min(pf.getImageableWidth()/(p.width),pf.getImageableHeight()/p.height);
	}	
		
		smallSquareSize=squareSize/4;
		hasChanged=true;
		doSetup=false;
		this.paint(g1);
		/* tell the caller that this page is part of the printed document */
		doSetup=true;
		return PAGE_EXISTS;
	}

	public void printCreases(paper p) {
		this.p=p;
		PrinterJob job = PrinterJob.getPrinterJob();
		  job.setPrintable(this);
		
		boolean ok = job.printDialog();
		if (ok) {
			try {
				job.print();
			} catch (PrinterException ex) {
				/* The job did not successfully complete */
			}
		}
	}
}
