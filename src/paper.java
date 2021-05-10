
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;
public class paper implements Serializable {

	/**
	 * This class stores the bulk of the design. Nodes, connections, conditions, are stored here. 
	 * This class also initiates crease generation, and calculating minimum distances.
	 */
	private static final long serialVersionUID = -6420912694063224236L;
	int width;
	int height;
	int square_size;
	node selected;
	boolean isFixedRatio;
	int maxSize;
	double ratioX_Y;
	boolean hasSymmetry;
	public HashMap<Integer, ArrayList<node>> connections;
	public ArrayList<node> nodes;
	public HashMap<Integer,HashMap<Integer, Integer>> distances;
	ArrayList<node> settled;
	ArrayList<node> unSettled;
	ArrayList<node>edgeNodes;
	Creases Unused;
	HashMap<node,Area> largeAreas;
	HashMap<node,Area[]> trueAreas;

	public void refreshNodes() {
		this.settled= new ArrayList<node>();
		ArrayList<node> next= new ArrayList<node>();
		for(node n:nodes) {
			if(n.isLeaf) {
				settled.add(n);
				node m=this.connections.get(n.ID).get(0);
				if(!next.contains(m)) {
					next.add(m);
				}

			}
		}
		while(settled.size()<nodes.size()) {
			ArrayList<node>next2= new ArrayList<node>();
			for( node n:next) {
				refreshNode(n);
				for( node m:connections.get(n.ID)) {
					if(!next.contains(m)) {

						next2.add(m);
					}
				}
			}
			next=next2;
		}
	}

	private void refreshNode(node n) {
		settled.add(n);
		if(n.isLeaf) {
		}else {
			int xpos=0;
			int ypos=0;
			int count=0;
			for(node m :this.connections.get(n.ID)) {
				if(settled.contains(m)) {
					xpos+=m.getX();
					ypos+=m.getY();
					count++;
					if(m.isLeaf) {
						xpos+=m.getX();
						ypos+=m.getY();
						count+=1;
					}
				}
			}
			if(count!=0) {
				n.setX((int)(xpos/count));
				n.setY((int)(ypos/count));

			}

		}
		for(node m :this.connections.get(n.ID)) {
			if(!settled.contains(m)) {
				refreshNode(m);
			}
		}

	}

	public int getSize() {
		if(this.isFixedRatio) {
			return Math.max(width, (int)( height*this.ratioX_Y));
		}
		return Math.max(width, height);
	}
	public void getAreas(double scale) {
		ArrayList<Point> corners=new ArrayList<Point>();
		corners.add(new Point(0,0));
		corners.add(new Point(0,(int) (height*scale)));
		corners.add(new Point((int) (width*scale),0));
		corners.add(new Point((int)(width*scale),(int)(height*scale)));


		Area empty=new Area(new Rectangle2D.Double(-scale,-scale,(width+2)*scale,(height+2)*scale));
		Area border=new Area(new Rectangle2D.Double(0,0,width*scale,height*scale));
		settled= new ArrayList<node>();

		node start= getFirstLeaf();
		trueAreas= new HashMap<node,Area[]>();
		largeAreas= new HashMap<node,Area>();

		getArea(start,scale);
		int[][] edges= new int[][] {new int[] {0,(int) ((width)*scale)},new int[] {0,(int) ((height)*scale)}};

		for(node n:nodes) {

			if(n.size!=0) {
				n.makeCreases(scale, trueAreas.get(n),edges);
				for(Creases c: n.c) {
					empty.subtract(c);
					c.intersect(border);
				}

			}

		} 
		Unused= new Creases(scale,empty,edges);

		Unused.makeCreases();
		Unused.intersect(border);
		settled= new ArrayList<node>();
	}
	public node getFirstLeaf() {
		for(node n:nodes) {
			if(n.isLeaf) {
				return n;
			}
		}
		return nodes.get(0);
	}
	public void getArea(node n, double scale) {
		if(n.size>0) {
			if(n.isLeaf) {
				makeLeaf(n,scale);
			}else {
				Area[] poly = new Area[n.size*2];	
				largeAreas.put(n, poly[0]);
				trueAreas.put(n, poly);
				settled.add(n);
				for(node m:this.connections.get(n.ID)) {
					if(!settled.contains(m)) {
						getArea(m,scale);
					}	
				}
				Area base=new Area();
				Area large= new Area();
				for(node m:this.connections.get(n.ID)) {
					if(settled.contains(m)) {
						Area sub=largeAreas.get(m);
						if(sub!=null) {
							base.add(sub);
						}
					}
				}
				large.add(base);
				for( int m=0;m<2*n.size;m++) {
					poly[m]= new Area();
					for(int i=-1;i<=1;i++) {
						for (int j=-1;j<=1;j++) {
							AffineTransform t= new AffineTransform();
							t.translate((scale*(i)/2), (scale*(j)/2));
							if(m==0) {
								Area tr=base.createTransformedArea(t);
								poly[m].add(tr);
							}else {
								Area tr=poly[m-1].createTransformedArea(t);
								poly[m].add(tr);
							}

						}
					}
					large.add(poly[m]);
				}
				for( int m=2*n.size-1;m>0;m--) {
					poly[m].subtract(poly[m-1]);
				}
				poly[0].subtract(base);
				largeAreas.replace(n, large);
				trueAreas.replace(n, poly);
			}
			for(node m:this.connections.get(n.ID)) {
				if(!settled.contains(m)) {
					getArea(m,scale);
				}	
			}
		}else {
			Area[] poly = new Area[1];	
			largeAreas.put(n, poly[0]);
			trueAreas.put(n, poly);
			settled.add(n);
			for(node m:this.connections.get(n.ID)) {
				if(!settled.contains(m)) {
					getArea(m,scale);
				}	
			}
			poly[0]=new Area();
			for(node m:this.connections.get(n.ID)) {
				if(settled.contains(m)) {
					Area sub=largeAreas.get(m);
					////
					if(sub!=null) {
						poly[0].add(sub);}
				}

			}
			largeAreas.replace(n, poly[0]);
			trueAreas.replace(n, poly);
		}



	}
	private void makeLeaf(node n, double scale) {
		Area[]poly= new Area[n.size*2];
		for(int i=0;i<n.size*2;i++) {
			Area po= new Area(new Rectangle2D.Double(Math.floor(scale*n.getX()-(i+1)*scale/2),Math.floor(scale*n.getY()-(i+1)*scale/2),scale*(i+1),(i+1)*scale));
			poly[i]=po;

		}
		for( int m=2*n.size-1;m>0;m--) {
			poly[m].subtract(poly[m-1]);
		}
		settled.add(n);
		trueAreas.put(n, poly);
		largeAreas.put(n, poly[n.size*2-1]);
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
		connections.remove(delete.ID);
		for(node n:nodes) {
			connections.get(n.ID).remove(delete);
			if(connections.get(n.ID).size()<=1) {
				n.isLeaf=true;
			}
		}

	}
	public paper(paper p) {
		this.width=p.width;
		this.height=p.height;
		this.square_size=p.square_size;
		this.nodes= new ArrayList<node>();
		this.isFixedRatio=p.isFixedRatio;
		this.ratioX_Y=p.ratioX_Y;
		this.maxSize=p.maxSize;
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
		this.connections = new HashMap<Integer, ArrayList<node>>();
		this.distances= new HashMap<Integer,HashMap<Integer, Integer>>();
		Iterator<Integer> it= p.distances.keySet().iterator();
		while(it.hasNext()) {
			int id =it.next();
			node oldStart=p.nodes.stream().filter(a -> (int)a.ID == id).collect(Collectors.toList()).get(0);
			node newStart= this.nodes.get(p.nodes.indexOf(oldStart));
			Iterator<Integer> end= p.distances.get(oldStart.ID).keySet().iterator();
			HashMap<Integer, Integer> dist= new HashMap<Integer,Integer>();
			while(end.hasNext()) {
				int nextID=end.next();
				node oldEnd=p.nodes.stream().filter(b -> (int)b.ID == nextID).collect(Collectors.toList()).get(0);
				int length= p.distances.get(oldStart.ID).get(oldEnd.ID);
				node newEnd= this.nodes.get(p.nodes.indexOf(oldEnd));
				dist.put((Integer)newEnd.ID, length);
			}
			this.distances.put((Integer)newStart.ID, dist);
		}
		for(int i=0;i<p.nodes.size();i++) {
			node n= this.nodes.get(i);
			node pn = p.nodes.get(i);
			ArrayList<node> con = new ArrayList<node>();
			ArrayList<node> oldCon= p.connections.get(pn.ID);
			if(oldCon!=null) {
				for( node c:oldCon) {
					int oldindex= p.nodes.indexOf(c);
					con.add(this.nodes.get(oldindex));
				}
			}
			connections.put(n.ID, con);
		}
	}
	public void shrink() {
		int xmax=0;
		int xmin=Integer.MAX_VALUE;
		int ymax=0;
		int ymin=Integer.MAX_VALUE;
		for(node n:nodes) {
			if(n.isLeaf) {
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
		maxSize=0;
		connections= new HashMap<Integer,ArrayList<node>>();
		nodes= new ArrayList<node>();
		edgeNodes= new ArrayList<node>();
	}
	public void moveSelect(int x, int y) {
		if(selected!=null) {
			selected.setX(x);
			selected.setY(y);
		}
	}
	public void getTreeDistances() {
		distances= new HashMap<Integer,HashMap<Integer,Integer>>();
		for(node one:nodes) {
			HashMap<Integer,Integer> selfMap= new HashMap<Integer,Integer>();
			selfMap.put(one.ID,0);
			distances.put(one.ID,selfMap);
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
			distances.get(end.ID).put(start.ID, length);
			distances.get(start.ID).put(end.ID, length);
		}else {
			ArrayList<node> nearby= new ArrayList<node>();
			nearby.addAll(connections.get(current.ID));
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
		connections.get(startNode.ID).add(newNode);
		ArrayList<node>newList= new ArrayList<node>();
		newList.add(startNode);
		connections.put(newNode.ID,newList);
		nodes.add(newNode);
		if(connections.get(newNode.ID).size()!=1){
			newNode.isLeaf=false;
		}
		if(connections.get(startNode.ID).size()!=1){
			startNode.isLeaf=false;
		}
		maxSize++;

	}
	public void addNode(node newNode) {
		if(!isFirstNode()) {
			this.addNode(newNode,selected);
		}else {
			ArrayList<node>newList= new ArrayList<node>();
			connections.put(newNode.ID,newList);
			nodes.add(newNode);

		}
		maxSize++;
	}

	public void addMirroedNode(node toMirror) {
		toMirror.isMirrored=true;
	}

	public int[] getOverlap(node one, node two) {
		if(!one.equals(two)) {
			int deltax= Math.abs(one.getX()-two.getX());
			int deltay= Math.abs(one.getY()-two.getY());
			int overlapX=distances.get(one.ID).get(two.ID)-deltax;
			int overlapY=distances.get(one.ID).get(two.ID)-deltay;
			return new int[] {overlapX,overlapY};
		}
		return new int[] {0,0};

	}
	public boolean overlaps(node one, node two) {
		if(!one.equals(two)) {
			if(one.isLeaf&&two.isLeaf) {
				int deltax= Math.abs(one.getX()-two.getX());
				int deltay= Math.abs(one.getY()-two.getY());
				if(deltax>=distances.get(one.ID).get(two.ID)) {
					return false;
				}
				if(deltay>=distances.get(one.ID).get(two.ID)) {
					return false;
				}
				return true;
			}
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
		return connections.get(myNode.ID);
	}
	public void addConection(node node1, node node2) {
		connections.get(node1.ID).add(node2);
		connections.get(node2.ID).add(node1);
		if(connections.get(node2.ID).size()!=1){
			node2.isLeaf=false;
		}
		if(connections.get(node1.ID).size()!=1){
			node1.isLeaf=false;
		}
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
}
