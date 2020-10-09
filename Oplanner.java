
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
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
public class Oplanner extends JPanel implements Printable,ColorSettings    {
	/**
	 * 
	 */
	boolean drawCreases;
	Graphics2D g;
	int squareSize;
	int smallSquareSize;
	paper p;
	int width;
	int height;
	boolean hasChanged;
	private static final long serialVersionUID = -1071442708667655401L;

	public Oplanner(paper p) {
		super();
		this.p=p;
		this.setDoubleBuffered(true);
	}

	//draws a grid, shifted to the left by shift
	public void drawGrid(int shift) {
		if(shift==0) {
			
			g.setStroke(LINE_STROKE);
		}else {
			g.setStroke(CONDITION_STROKE);
		}
		for (int i=0;i<=height;i++){
			g.drawLine(0, i*squareSize-shift, width*squareSize, i*squareSize-shift);

		}
		for (int i=0;i<=width;i++){
			g.drawLine( i*squareSize-shift,0,i*squareSize-shift, height*squareSize);
		}
	}
	/**draws the plan view of the paper.
	 * this includes locations, connections and conditions of the nodes.
	 * @param myP
	 */
	public void drawPlan(paper myP) {
		this.p=myP;
		setUp(myP);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		drawGrid(0);
		drawSymmetry();
		if(myP.conditions!=null&&myP.conditions.size()!=0) {

			drawConditions();
		}
		for(node n:myP.nodes) {
			drawNode(n);
			drawConnections(n);
			g.setColor(NODE_COLOR);
			g.setStroke(AREA_STROKE);
		}
	}
	private void drawSymmetry() {
		if(p.hasSymmetry) {
			System.out.print("ok");
			g.setColor(SYMMERTY_LINE_COLOR);
			g.setStroke(SYMMERTY_LINE_STROKE);
			if(p.isXsymmetry) {
				g.drawLine((int)(p.width*.5*squareSize), 0, (int)(p.width*.5*squareSize),p.height*squareSize);
			}else {
				g.drawLine(0,(int)(p.height*.5*squareSize),p.width*squareSize, (int)(p.height*.5*squareSize));

			}
		}


	}

	private void drawConnections(node n) {
		g.setStroke(CONNECTION_STROKE);
		g.setColor(NODE_COLOR);
		for(node m:p.connections.get(n)) {
			g.setStroke(new BasicStroke(3));
			g.drawLine(m.getX()*squareSize,m.getY()*squareSize, n.getX()*squareSize, n.getY()*squareSize);
			g.setStroke(new BasicStroke(1));
		}
	}

	private void drawNode(node n) {
		g.setColor(NODE_COLOR);
		g.setStroke(AREA_STROKE);
		double size= n.size;
		if(p.isSelcted(n)) {
			g.setColor(SELECTED_NODE_COLOR);
		}
		if(size==0) {
			g.fill(new Ellipse2D.Double(n.getX()*squareSize-smallSquareSize,n.getY()*squareSize-smallSquareSize,2*smallSquareSize,2*smallSquareSize));
			
		}else {
			if(p.isLeaf(n)) {
			//g.drawChars(n.ID.toString().toCharArray(), 0, n.ID.toString().length(), n.getX()*squareSize, n.getY()*squareSize);
			g.draw(new Rectangle2D.Double((n.getX()-size)*squareSize,(n.getY()-size)*squareSize,size*2*squareSize,size*2*squareSize));
			g.fill(new Rectangle2D.Double(n.getX()*squareSize-size*smallSquareSize,n.getY()*squareSize-size*smallSquareSize,size*2*smallSquareSize,size*2*smallSquareSize));
			}else {
				g.draw(new Ellipse2D.Double((n.getX()-size)*squareSize,(n.getY()-size)*squareSize,size*2*squareSize,size*2*squareSize));
				g.fill(new Ellipse2D.Double(n.getX()*squareSize-size*smallSquareSize,n.getY()*squareSize-size*smallSquareSize,size*2*smallSquareSize,size*2*smallSquareSize));
				
			}
		}
		if(n.isMirrored) {}
		
	}

	private void drawConditions() {
		g.setStroke(CONDITION_STROKE);
		g.setColor(CONDITION_LINE_COLOR);
		for(Condition con:p.conditions) {
			g.drawLine(con.node1.getX()*squareSize, con.node1.getY()*squareSize, con.node2.getX()*squareSize, con.node2.getY()*squareSize);
			g.setColor(Color.BLUE);
		}
	}

	/**
	 * set up the basic background and grid.
	 * @param myP
	 */
	private void setUp(paper myP) {
		p.getTreeDistances();
		g= (Graphics2D)this.getGraphics();
		g.setColor(Color.BLACK);
		width= p.width;
		height=p.height;
		squareSize= Math.min(getWidth()/width,getHeight()/height);
		smallSquareSize=squareSize/4;
		

	}

	public void drawCreases(paper myP, Graphics g1){
		setUp(myP);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		//g.translate(squareSize*p.width/2, squareSize*p.height/2);
		drawGrid(0);
		drawGrid(squareSize/2);
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
				drawNodeCreases(n);	

			}
			g.setColor(UN_USED_AREA_COLOR);
			g.draw(p.Unused);
			for(Line2D.Double l:p.Unused.creases) {
				g.draw(l);
			}
		}
		
		//AffineTransform mirror=new AffineTransform();
		//mirror.scale(-1, 1);
		//g.transform(mirror);;
		
	}
	private void drawNodeCreases(node n) {
		g.setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
		if(n.c!=null) {
			
			g.setStroke(LINE_STROKE);
			g.setStroke(AREA_STROKE);
			g.draw(n.c.large);
			int i=0;
			for(Point p:n.c.activeCorners) {
				g.fill(new Ellipse2D.Double(p.getX(),p.getY(),smallSquareSize,smallSquareSize));
				int[] dir=n.c.directions.get(i);
				g.draw(new Line2D.Double(p.getX(),p.getY(),p.getX()+dir[0]*smallSquareSize,p.getY()+dir[1]*smallSquareSize));
				i++;
			}
			for(Line2D.Double l:n.c.creases) {
				g.draw(l);
			}
			g.draw(n.c);
		}	
	}

	public int print(Graphics g1, PageFormat pf, int page) throws
	PrinterException {
		if (page > 0) { /* We have only one page, and 'page' is zero-based */
			return NO_SUCH_PAGE;
		}

		/* User (0,0) is typically outside the image able area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
		 */
		Graphics2D g2d = (Graphics2D)g1;
		g2d.translate(pf.getImageableX(), pf.getImageableY());

		/* Now we perform our rendering */
		drawCreases(this.p,g1);
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
	public void drawCreases(paper myPaper2) {
		this.drawCreases(myPaper2, this.getGraphics());
	}
}
