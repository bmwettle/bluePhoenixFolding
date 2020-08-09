package origamiProject;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;
public class paper implements Serializable , Comparable<paper>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6420912694063224236L;
	int width;
	int height;
	int square_size;
	node selected;
	boolean isFixedRatio;
	
	double ratioX_Y;
	boolean hasSymmetry;
	boolean isXsymmetry;
	public HashMap<node, ArrayList<node>> connections;
	public ArrayList<node> nodes;
	public HashMap<node,HashMap<node, Integer>> distances;
	ArrayList<node> settled;
	ArrayList<node> unSettled;
	ArrayList<Condition> conditions;
	ArrayList<node>edgeNodes;
public int getScore() {
	int score=0;
	for(node n:nodes) {
		for(node m:this.nodes) {
			int[] overlap= this.getOverlap(n, m);
			score-=Math.min(overlap[0],overlap[1]);
		}
	}
	return score;
}
	public int compareTo(paper b) {
		int size1=this.getSize();
		int size2=b.getSize();
		if(size1==size2) {
			return this.getScore()-b.getScore();
		}
		return size1-size2;
	}
	public int getSize() {
			if(this.isFixedRatio) {
				return Math.max(width, (int)( height*this.ratioX_Y));
			}
		return Math.max(width, height);
	}
	public void getAreas(int scale) {
		settled= new ArrayList<node>();
		for(node n:nodes) {
			n.corners=new ArrayList<Point>();
			n.creases=new ArrayList<Line2D.Double>();
			n.A= new Area();
		}
		node start= getFirstLeaf();
		getArea(start,scale);
		settled= new ArrayList<node>();
	}
	
	public node getFirstLeaf() {
		for(node n:nodes) {
			if(isLeaf(n)) {
				return n;
			}
		}
		return nodes.get(0);
	}
	public void getArea(node n, int scale) {
		int Size=n.size;

		if(isLeaf(n)) {
			makeLeaf(n,scale,Size);
		}else {
			Area poly = new Area();	
			n.A=poly;
			settled.add(n);
			for(node m:this.connections.get(n)) {
				if(!settled.contains(m)) {
					getArea(m,scale);
				}	
			}
			for(node m:this.connections.get(n)) {
				if(settled.contains(m)) {
					Area sub=m.A;
					
					for(int i=-Size;i<=Size;i++) {
						for (int j=-Size;j<=Size;j++) {
							AffineTransform t= new AffineTransform();
							t.translate(scale*i, scale*j);
							poly.add(sub.createTransformedArea(t));
							n.A=poly;
							

						}
					}
					for(Point p:m.corners) {
						for(int a=-1;a<=1;a+=2) {
							for(int b=-1;b<=1;b+=2) {
								Point k= new Point(p.x-2*a,p.y-2*b);
								if(m.A.contains(k)) {
									Point d= new Point(p.x+2*a,p.y-2*b);
									Point e= new Point(p.x-2*a,p.y+2*b);
									if(m.A.contains(d)==m.A.contains(e)) {
									Point l= new Point(p.x+a*n.size*scale,p.y+b*n.size*scale);
									n.corners.add(l);
									n.creases.add(new Line2D.Double(p.x,p.y,l.x,l.y));
									}
								}
							}
						}

					}
				}

			}
			n.A=poly;
		}
		for(node m:this.connections.get(n)) {
			if(!settled.contains(m)) {
				getArea(m,scale);
			}	
		}


	}

	private Area expandNodeArea(node n, int scale) {
		Area Poly= new Area();
			for(int a=-1;a<=1;a+=2) {
				for(int b=-1;b<=1;b+=2) {
					AffineTransform t= new AffineTransform();
					t.translate(scale*a, scale*b);
					Poly.add(n.A.createTransformedArea(t));
					n.A=Poly;
					
					for(Point p:n.corners) {
					Point k= new Point(p.x-2*a,p.y-2*b);
					if(n.A.contains(k)) {
						Point d= new Point(p.x+2*a,p.y-2*b);
						Point e= new Point(p.x-2*a,p.y+2*b);
						if(n.A.contains(d)==n.A.contains(e)) {
						Point l= new Point(p.x+a*n.size*scale,p.y+b*n.size*scale);
						n.corners.add(l);
						n.creases.add(new Line2D.Double(p.x,p.y,l.x,l.y));
						}
					}
				}
			}
	}
		return Poly;
	}
	private void makeLeaf(node n, int scale, int Size) {
		Area poly= new Area(new Rectangle2D.Double(scale*(n.getX()-Size),scale*(n.getY()-Size),2*scale*Size,2*scale*Size));
		settled.add(n);
		n.A=poly;
		n.corners=new ArrayList<Point>();
		n.creases= new ArrayList<Line2D.Double>();
		for(int i=-1;i<=1;i+=2) {
			for(int j=-1;j<=1;j+=2) {
				Point s=new Point(scale*(n.getX()),scale*(n.getY()));
				Point p=new Point(scale*(n.getX()-i*n.size),scale*(n.getY()-j*n.size));
				n.corners.add(p);
				n.creases.add(new Line2D.Double(s.getX(),s.getY(),p.getX(),p.getY()));
			}
		}
	}
	public void deleteNode(node delete) {
		
		nodes.remove(delete);
		if(delete.equals(selected)) {
			if(nodes.size()>0) {
			selected= nodes.get(nodes.size()-1);
			}else {
				selected=null;
			}
		}
		connections.remove(delete);
		for(node n:nodes) {
			connections.get(n).remove(delete);
		}
	}
	public paper(paper p) {
		this.width=p.width;
		this.height=p.height;
		this.square_size=p.square_size;
		this.nodes= new ArrayList<node>();
		this.isFixedRatio=p.isFixedRatio;
		this.ratioX_Y=p.ratioX_Y;
		this.settled=new ArrayList<node>();
		this.unSettled= new ArrayList<node>();
		for(node n: p.nodes) {
			node m= new node(n);
			this.nodes.add(m);
			this.unSettled.add(m);
		}
		if(p.selected!=null) {
		this.selected= this.nodes.get(p.nodes.indexOf(p.selected));
		}
		edgeNodes= new ArrayList<node>();
		if(p.edgeNodes!=null) {
			for(node e:p.edgeNodes) {
				edgeNodes.add(this.nodes.get(p.nodes.indexOf(e)));
			}
		}
		conditions = new ArrayList<Condition>();
		if(p.conditions!=null) {
			for(Condition con:p.conditions) {
				node one= this.nodes.get(p.nodes.indexOf(con.node1));
				node two= this.nodes.get(p.nodes.indexOf(con.node2));
				this.conditions.add(new Condition(one,two));
			}
		}
		this.connections = new HashMap<node, ArrayList<node>>();
		this.distances= new HashMap<node,HashMap<node, Integer>>();
		Iterator<node> it= p.distances.keySet().iterator();
		while(it.hasNext()) {
			node oldStart= it.next();
			node newStart= this.nodes.get(p.nodes.indexOf(oldStart));
			Iterator<node> end= p.distances.get(oldStart).keySet().iterator();
			HashMap<node, Integer> dist= new HashMap<node,Integer>();
			while(end.hasNext()) {
				node oldEnd= end.next();
				int length= p.distances.get(oldStart).get(oldEnd);
				node newEnd= this.nodes.get(p.nodes.indexOf(oldEnd));
				dist.put(newEnd, length);
			}
			this.distances.put(newStart, dist);
		}
		for(int i=0;i<p.nodes.size();i++) {
			node n= this.nodes.get(i);
			node pn = p.nodes.get(i);
			ArrayList<node> con = new ArrayList<node>();
			ArrayList<node> oldCon= p.connections.get(pn);
			if(oldCon!=null) {
				for( node c:oldCon) {
					int oldindex= p.nodes.indexOf(c);
					con.add(this.nodes.get(oldindex));
				}
			}
			connections.put(n, con);
		}
	}
	public void shrink() {
		int xmax=0;
		int xmin=Integer.MAX_VALUE;
		int ymax=0;
		int ymin=Integer.MAX_VALUE;
		for(node n:nodes) {
			if(n.getX()>xmax) {
					xmax=n.getX();
				}
				if(n.getX()<xmin) {
					xmin=n.getX();
				}
				if(n.getY()>ymax) {
					ymax=n.getY();
				}
				if(n.getY()<ymin) {
					ymin=n.getY();
				}
		}
		for( node n:nodes) {
			n.setX(n.getX()-xmin);	
			n.setY(n.getY()-ymin);;
		}
		width=xmax-xmin;
		height=ymax-ymin;
		if(width==0) {
			width=1;
		}
		if(height==0) {
			height=1;
		}
	}
	public paper(int w,int h) {
		width=w;
		height=h;
		connections= new HashMap<node,ArrayList<node>>();
		nodes= new ArrayList<node>();
		conditions = new ArrayList<Condition>();
		edgeNodes= new ArrayList<node>();
	}
	public void moveSelect(int x, int y) {
		selected.setX(x);
		selected.setY(y);
	}
	public void getTreeDistances() {
		distances= new HashMap<node,HashMap<node,Integer>>();
		for(node one:nodes) {
			HashMap<node,Integer> selfMap= new HashMap<node,Integer>();
			selfMap.put(one,0);
			distances.put(one,selfMap);
			}
		for (node start:this.nodes){
			for(node end:this.nodes) {
				searchNodes(start,start,end,0,new ArrayList<node>());
			}
		}

	}
	public void setPaperSize(int w, int h) {
		this.width=w;
		this.height=h;
	}
	public int getLongestLegWithoutCuts(node one,node two) {
		int deltaX= Math.abs(one.getX()-two.getX());
		int deltaY= Math.abs(one.getY()-two.getY());
		return Math.max(deltaX, deltaY);
	}
	private void searchNodes(node start, node current,node end,int length, ArrayList<node> mySearched) {
		ArrayList<node> searched= new ArrayList<node>();
		searched.addAll(mySearched);
		searched.add(current);
		if (current.equals(end)){
			length+=current.size;
			distances.get(end).put(start, length);
			distances.get(start).put(end, length);
		}else {
			ArrayList<node> nearby= new ArrayList<node>();
			nearby.addAll(connections.get(current));
			nearby.removeAll(searched);
			for(node connect:nearby) {
				searchNodes(start,connect,end,length+current.size,searched);
			}
		}
	}
	public void setSelectedNode(node select) {
		selected= select;
	}
	public boolean isSelcted(node test) {
		return test.equals(selected);
	}
	public boolean isFirstNode() {
		return nodes.size()==0;
	}
	public void addNode(node newNode, node startNode) {
		connections.get(startNode).add(newNode);
		ArrayList<node>newList= new ArrayList<node>();
		newList.add(startNode);
		connections.put(newNode,newList);
		nodes.add(newNode);
		newNode.setID(nodes.indexOf(newNode));
	}
	public void addNode(node newNode) {
		if(!isFirstNode()) {
			this.addNode(newNode,selected);
		}else {
			ArrayList<node>newList= new ArrayList<node>();
			connections.put(newNode,newList);
			nodes.add(newNode);
			newNode.setID(nodes.indexOf(newNode));
		}
	}
	public boolean isLeaf(node n) {
		return connections.get(n).size()<=1;
	}
	public int[] getOverlap(node one, node two) {
		if(!one.equals(two)) {
			int deltax= Math.abs(one.getX()-two.getX());
				int deltay= Math.abs(one.getY()-two.getY());
				int overlapX=distances.get(one).get(two)-deltax;
				int overlapY=distances.get(one).get(two)-deltay;
				return new int[] {overlapX,overlapY};
		}
		return new int[] {0,0};

	}
	public boolean overlaps(node one, node two) {
		if(!one.equals(two)) {
			int deltax= Math.abs(one.getX()-two.getX());
				int deltay= Math.abs(one.getY()-two.getY());
				if(deltax>=distances.get(one).get(two)) {
					return false;
				}
				if(deltay>=distances.get(one).get(two)) {
					return false;
				}
				return true;

		}
		return false;
	}
	public node getNodeAt(int x, int y) {
		for(node Node:nodes) {
			if (Node.getX()==x&&Node.getY()==y){
				return Node;
			}
		}
		return null;
	}
	public Iterator<node> getNodes(){
		return nodes.iterator();

	}
	public ArrayList<node> getConections(node myNode) {
		return connections.get(myNode);
	}
	public void addConection(node node1, node node2) {
		connections.get(node1).add(node2);
		connections.get(node2).add(node1);
	}
	public boolean hasOverlap( node N1) {
		for(node N2:nodes) {
			if(overlaps(N1,N2)) {
				return true;
			}
		}
		return false;
	}
	public boolean hasOverlap() {
		for(node N1:nodes) {
			for(node N2:nodes) {
				if(overlaps(N1,N2)) {
					return true;
				}
			}
		}
		return false;
	}
	public void addConditions(node selected2, node selected3) {
		if(this.conditions==null) {
			conditions = new ArrayList<Condition>();
		}
		conditions.add(new Condition(selected2,selected3));
		
	}
	public void removeConditions(node selected2, node selected3) {
		if(this.conditions==null) {
			conditions = new ArrayList<Condition>();
		}
		for(Condition con:this.conditions) {
			if(con.node1.equals(selected3)||con.node1.equals(selected2)) {
				if(con.node2.equals(selected3)||con.node2.equals(selected2)) {
					conditions.remove(con);
					break;
				}
			}
		}
	}
}
