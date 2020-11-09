
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
/**
 * 
 * @author Benjamin Wettle
 * this class handles the display of the paper.
 * depending on the mode, it can display the plan, or the creases.
 */
public class CreasePlanner extends JPanel implements Printable,ColorSettings    {
	/**
	 * 
	 */
	int squareSize;
	int smallSquareSize;
	paper p;
	int width;
	int height;
	boolean hasChanged;
	private static final long serialVersionUID = -1071442708667655401L;

	public CreasePlanner(paper p) {
		super();
		this.p=p;
		this.setDoubleBuffered(true);
	}
public void paintComponent(Graphics g) {
	super.paintComponent(g);
	hasChanged=true;
	Graphics2D g1=(Graphics2D)g;
	AffineTransform old=g1.getTransform();
	g.clearRect(0, 0, this.getWidth(), this.getHeight());
	
	setUp(p);
if(p.hasSymmetry) {
	AffineTransform scale=new AffineTransform();
	scale.translate(squareSize* p.width,0);
	g1.transform(scale);
	this.drawCreases(p, g1);
	AffineTransform mirror=new AffineTransform();
	mirror.scale(-1, 1);
	g1.transform(mirror);;
	this.hasChanged=false;
	this.drawCreases(p, g1);
	g1.setTransform(old);
}else {
	this.drawCreases(p, g1);
}
	//g.clearRect(p.width*2*squareSize, 0, this.getWidth()-p.width*squareSize, this.getHeight());
	//g.clearRect(0,p.height*squareSize, this.getWidth(), this.getHeight()-p.height*squareSize);

}
	//draws a grid, shifted to the left by shift
	public void drawGrid(int shift,Graphics2D g) {
		if(shift==0) {
			System.out.println("grid1");
			g.setStroke(LINE_STROKE);
		}else {
			g.setStroke(CONDITION_STROKE);
		}
		for (int i=0;i<=height;i++){
			g.drawLine(0, i*squareSize+shift, width*squareSize, i*squareSize+shift);

		}
		for (int i=0;i<=width;i++){
			g.drawLine( i*squareSize+shift,0,i*squareSize+shift, height*squareSize);
		}
	}
	/**
	 * set up the basic background and grid.
	 * @param myP
	 */
	private void setUp(paper myP) {
		p.getTreeDistances();
		width= p.width;
		height=p.height;
		squareSize= Math.min(getWidth()/(2*width),getHeight()/height);
		smallSquareSize=squareSize/4;
	}

	public void drawCreases(paper myP, Graphics2D g){
		drawGrid(0,g);
		drawGrid(squareSize/2,g);
		g.setStroke(AREA_STROKE);
		g.setColor(CREASE_COLOR);
		if(myP.nodes.size()>0) {

			if(hasChanged||myP.nodes.get(0).c==null) {
				myP.getAreas(squareSize);
				System.out.println(hasChanged);
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
	private void drawNodeCreases(node n,Graphics2D g) {
		if(n.c!=null) {
			for( Creases c :n.c) {
				//g.setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255),100));
				
			if(c!=null) {
			//g.setStroke(AREA_STROKE);
			//g.fill(c);
			//g.setColor(CREASE_COLOR);
			g.setStroke(LINE_STROKE);
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
			g.draw(c);
			}
			}
		}	
	}

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
		
		this.paint(g1);
		/* tell the caller that this page is part of the printed document */
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
