
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
/**
 * 
 * @author Benjamin Wettle
 * this class handles the display of the paper.
 * depending on the mode, it can display the plan, or the creases.
 */
public class Oplanner extends JPanel implements ColorSettings    {
	/**
	 * 
	 */
	boolean drawCreases;
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
public void paintComponent(Graphics g) {
	super.paintComponent(g);
	
	this.drawPlan(p,(Graphics2D) g);
}
	//draws a grid, shifted to the left by shift
	public void drawGrid(Graphics2D g) {
		
			g.setStroke(LINE_STROKE);
		
		for (int i=0;i<=height;i++){
			g.drawLine(0, i*squareSize, width*squareSize, i*squareSize);

		}
		for (int i=0;i<=width;i++){
			g.drawLine( i*squareSize,0,i*squareSize, height*squareSize);
		}
	}
	/**draws the plan view of the paper.
	 * this includes locations, connections and conditions of the nodes.
	 * @param myP
	 */
	public void drawPlan(paper myP,Graphics2D g) {
		this.p=myP;
		setUp(myP);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		drawGrid(g);
		drawSymmetry(g);
		for(node n:myP.nodes) {
			drawNode(n,g);
			drawConnections(n,g);
			g.setColor(NODE_COLOR);
			g.setStroke(AREA_STROKE);
			
		}
		
	}
	private void drawSymmetry(Graphics2D g) {
		if(p.hasSymmetry) {
			g.setColor(SYMMERTY_LINE_COLOR);
			g.setStroke(SYMMERTY_LINE_STROKE);
		 g.drawLine(0, 0, 0,p.height*squareSize);
		}
	}

	private void drawConnections(node n,Graphics2D g) {
		g.setStroke(CONNECTION_STROKE);
		g.setColor(NODE_COLOR);
		for(node m:p.connections.get(n.ID)) {
			g.setStroke(new BasicStroke(3));
			g.drawLine(m.getX()*squareSize,m.getY()*squareSize, n.getX()*squareSize, n.getY()*squareSize);
			g.setStroke(new BasicStroke(1));
		}
	}

	private void drawNode(node n,Graphics2D g) {
		g.setColor(NODE_COLOR);
		g.setStroke(AREA_STROKE);
		double size= n.size;
		if(n.isFixedToSymmetryLine) {
			g.setColor(SYMMERTY_LINE_COLOR);
		}
		if(p.isSelcted(n)) {
			g.setColor(SELECTED_NODE_COLOR);
		}
		if(size==0) {
			g.fill(new Ellipse2D.Double(n.getX()*squareSize-smallSquareSize,n.getY()*squareSize-smallSquareSize,2*smallSquareSize,2*smallSquareSize));
			
		}else {
			if(n.isLeaf) {
			g.draw(new Rectangle2D.Double((n.getX()-size)*squareSize,(n.getY()-size)*squareSize,size*2*squareSize,size*2*squareSize));
			//g.fill(new Rectangle2D.Double(n.getX()*squareSize-size*smallSquareSize,n.getY()*squareSize-size*smallSquareSize,size*2*smallSquareSize,size*2*smallSquareSize));
			g.setColor(Color.black);
			String index=""+p.nodes.indexOf(n);
			g.drawChars(index.toCharArray(), 0, index.length(), n.getX()*squareSize, n.getY()*squareSize);
			
			}else {
				g.draw(new Ellipse2D.Double((n.getX()-size)*squareSize,(n.getY()-size)*squareSize,size*2*squareSize,size*2*squareSize));
				g.fill(new Ellipse2D.Double(n.getX()*squareSize-size*smallSquareSize,n.getY()*squareSize-size*smallSquareSize,size*2*smallSquareSize,size*2*smallSquareSize));		
			}
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
		squareSize= Math.min(getWidth()/width,getHeight()/height);
		smallSquareSize=squareSize/4;
	}
}
