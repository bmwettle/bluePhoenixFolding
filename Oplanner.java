package origamiProject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
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
public class Oplanner extends JPanel implements Printable    {
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
	private static final long serialVersionUID = -1071442708667655401L;
	
	public Oplanner(paper p) {
		super();
		this.p=p;
		this.setDoubleBuffered(true);
	}

	//draws a grid, shifted to the left by shift
	public void drawGrid(int shift) {
		if(shift==0) {
			g.clearRect(0, 0, this.getWidth(), this.getHeight());
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
		if(myP.conditions!=null&&myP.conditions.size()!=0) {
			drawConditions();
		}
		for(node n:myP.nodes) {
			drawNode(n);
			drawConnections(n);
		}
	}
private void drawConnections(node n) {
	for(node m:p.connections.get(n)) {
		g.setStroke(new BasicStroke(3));
		g.drawLine(m.getX()*squareSize,m.getY()*squareSize, n.getX()*squareSize, n.getY()*squareSize);
		g.setStroke(new BasicStroke(1));
	}
}

private void drawNode(node n) {
	int size= n.size;
	if(p.isSelcted(n)) {
		g.setColor(Color.green);
	}
	g.draw(new Rectangle2D.Double((n.getX()-size)*squareSize,(n.getY()-size)*squareSize,size*2*squareSize,size*2*squareSize));
	g.fill(new Rectangle2D.Double(n.getX()*squareSize-size*smallSquareSize,n.getY()*squareSize-size*smallSquareSize,size*2*smallSquareSize,size*2*smallSquareSize));
	g.setColor(Color.blue);
}

private void drawConditions() {
	g.setStroke(new BasicStroke(2));
	for(Condition con:p.conditions) {
		g.setColor(Color.PINK);
		if(con.matchX) {
			g.setColor(Color.cyan);
		}
		g.drawLine(con.node1.getX()*squareSize, con.node1.getY()*squareSize, con.node2.getX()*squareSize, con.node2.getY()*squareSize);
		g.setColor(Color.BLUE);
		g.setStroke(new BasicStroke(1));
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
		smallSquareSize=squareSize/6;
		drawGrid(0);
		g.setColor(Color.BLUE);
}

	public void drawCreases(paper myP, Graphics g1){
		setUp(myP);
		drawGrid(squareSize/2);
		if(myP.nodes.size()>0) {
			myP.getAreas(squareSize);
			//total stores all the area on the paper to start
			//as nodes are drawn, their areas are removed. since all
			//since all areas must be used, we mark unused areas in orange(142)
			Area total = new Area(new Rectangle2D.Double(0,0,myP.width*squareSize,myP.height*squareSize));
			for(node n:myP.nodes) {
				drawNodeCreases(n);	
				//total.subtract(n.A);
			}
			g.setColor(Color.ORANGE);
			g.fill(total);
		}
		g.setColor(Color.BLACK);

	}
	private void drawNodeCreases(node n) {
		if(p.isLeaf(n)) {
			g.setColor(Color.blue);
		}else {
			g.setColor(Color.green);
		}
		g.draw(n.A);
		
		if(n.creases!=null) {
			for(Line2D.Double l:n.creases) {
				g.setColor(Color.BLACK);
				g.draw(l);
			}

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
