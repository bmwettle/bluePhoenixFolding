package origamiProject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JPanel;

public class Oplanner extends JPanel implements Printable    {
	/**
	 * 
	 */
	paper myPaper;
	int mouseX=0;
	int mouseY=0;

	Graphics2D g;
	int squareSize;
	int smallSquareSize;
	paper p;
	int width;
	int height;
	private static final long serialVersionUID = -1071442708667655401L;
	public Oplanner(paper p) {
		super();
		myPaper=p;
		this.setDoubleBuffered(true);
	}
	void paintCommponent(Graphics g){
		super.paintComponent(g);
		//drawNodes(myPaper);
		//g.drawOval(mouseX-3, mouseY-3, 6, 6);

	}

	public void drawCursor(int x,int y) {
		//repaint();
		//this.getGraphics().drawOval(x-3,y-3,6,6);
		mouseX=x;
		mouseY=y;

	}
	public void drawPlan(paper myP) {
		this.p=myP;
		p.getTreeDistances();
		g= (Graphics2D)this.getGraphics();
		g.setColor(Color.BLACK);

		width= p.width;
		height=p.height;
		squareSize= Math.min(getWidth()/width,getHeight()/height);
		smallSquareSize=squareSize/6;
		//drawBranches();

		for (int i=0;i<=height;i++){
			g.drawLine(0, i*squareSize, width*squareSize, i*squareSize);

		}
		for (int i=0;i<=width;i++){
			g.drawLine( i*squareSize,0,i*squareSize, height*squareSize);

		}
		g.setColor(Color.BLUE);

		for(node n:myP.nodes) {
			int size= n.size;
			g.draw(new Rectangle2D.Double((n.getX()-size)*squareSize,(n.getY()-size)*squareSize,size*2*squareSize,size*2*squareSize));
			g.fill(new Rectangle2D.Double(n.getX()*squareSize-size*smallSquareSize,n.getY()*squareSize-size*smallSquareSize,size*2*smallSquareSize,size*2*smallSquareSize));
			for(node m:myP.connections.get(n)) {
				g.drawLine(m.getX()*squareSize,m.getY()*squareSize, n.getX()*squareSize, n.getY()*squareSize);
			}
		}
	}
	public void drawCreases(paper myP, Graphics g1){
		this.p=myP;
		p.getTreeDistances();
		Graphics2D g= (Graphics2D)g1;
		g.setColor(Color.BLACK);
		width= p.width;
		height=p.height;
		squareSize= Math.min(getWidth()/width,getHeight()/height);
		smallSquareSize=squareSize/6;
		//drawBranches();

		for (int i=0;i<=height;i++){
			g.drawLine(0, i*squareSize, width*squareSize, i*squareSize);

		}
		for (int i=0;i<=width;i++){
			g.drawLine( i*squareSize,0,i*squareSize, height*squareSize);

		}
		g.setColor(Color.BLUE);
		g.setStroke(new BasicStroke(1));
		if(myP.nodes.size()>0) {
			myP.getAreas(squareSize);

			for(node n:myP.nodes) {
				if(myP.isLeaf(n)) {
					g.setColor(Color.blue);
					g.draw(n.A);
				}else {
					g.setColor(Color.red);
					g.draw(n.A);
				}
				if(n.creases!=null) {
				for(Line2D.Double l:n.creases) {
					g.draw(l);
				}
				
				}				
			}
		}
		g.setColor(Color.BLACK);

	}
	public int print(Graphics g1, PageFormat pf, int page) throws
	PrinterException {

		if (page > 0) { /* We have only one page, and 'page' is zero-based */
			return NO_SUCH_PAGE;
		}

		/* User (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
		 */
		Graphics2D g2d = (Graphics2D)g1;
		g2d.translate(pf.getImageableX(), pf.getImageableY());

		/* Now we perform our rendering */
		drawCreases(this.p,g1);
		//this.printAll(g1);

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
