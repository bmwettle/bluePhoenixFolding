package origamiProject;

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
public HashMap<node, ArrayList<node>> connections;
public ArrayList<node> nodes;
public HashMap<node,HashMap<node, Integer>> distances;
ArrayList<node> settled;
ArrayList<node> unSettled;

@Override
public int compareTo(paper b) {
	return this.getSize()-b.getSize();
}
public int getSize() {
	// TODO Auto-generated method stub
	return Math.max(width, height);
	//return(width*height);
}
public void deleteNode(node delete) {
	if(delete.equals(selected)) {
		selected= nodes.get(0);
	}
	nodes.remove(delete);
	if(delete.equals(selected)) {
		selected= nodes.get(0);
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
	this.settled=new ArrayList<node>();
	this.unSettled= new ArrayList<node>();
	for(node n: p.nodes) {
		node m= new node(n);
		this.nodes.add(m);
		this.unSettled.add(m);
	}
	this.selected= this.nodes.get(p.nodes.indexOf(p.selected));
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
		ArrayList<node> cut= new ArrayList<node>();
	
		
		
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
	n.forceX(n.getX()-xmin);	
	n.forceY(n.getY()-ymin);;
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
public int[] getXcords() {
	int[] Xcords= new int[nodes.size()];
	for(int i=0;i<nodes.size();i++) {
		Xcords[i]=nodes.get(i).getX();
	}
	return Xcords;
}
public int[] getYcords() {
	int[] Ycords= new int[nodes.size()];
	for(int i=0;i<nodes.size();i++) {
		Ycords[i]=nodes.get(i).getY();
	}
	return Ycords;
}

public paper(int w,int h) {
	width=w;
	height=h;
	connections= new HashMap<node,ArrayList<node>>();
	nodes= new ArrayList<node>();

}


public void moveSelect(int x, int y) {
	selected.forceX(x);
	selected.forceY(y);
}
public void getTreeDistances() {
	distances= new HashMap<node,HashMap<node,Integer>>();
	
	for(node one:nodes) {
		
			
			HashMap<node,Integer> selfMap= new HashMap<node,Integer>();
			
			selfMap.put(one,0);
			distances.put(one,selfMap);
			//System.out.println(one.toString()+","+connections.get(one).toString());
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
	//System.out.println("now at:"+nodes.indexOf(start)+","+nodes.indexOf(current)+","+nodes.indexOf(end)+","+ length);
	ArrayList<node> searched= new ArrayList<node>();
	searched.addAll(mySearched);
	searched.add(current);
	
	if (current.equals(end)){
		length+=current.size;
		distances.get(end).put(start, length);
		distances.get(start).put(end, length);
		
		
		//System.out.println("ended:"+start.toString()+","+end.toString()+":"+length);
		
		
		
	}else {
		ArrayList<node> nearby= new ArrayList<node>();
		nearby.addAll(connections.get(current));
		//System.out.println(current.toString());
		//System.out.println(nearby.toString());
		nearby.removeAll(searched);
		//System.out.println("now at:"+start.toString()+","+current.toString()+","+end.toString()+",nearby"+nearby.toString()+"searched:"+searched.toString()+","+ length);
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
public String toText() {
	this.shrink();
	System.out.println("width"+width+" , hieght"+height);
	String text="";
	char[][]display=new char[this.width+1][this.height+1];
	
	for(int i=0;i<width+1;i++) {
		for(int j=0;j<height+1;j++) {
		display[i][j]='.';
		}
	}
	for(node n:nodes) {
		display[n.getX()][n.getY()]=n.toString().charAt(1);
	}
	for(int i=0;i<height+1;i++) {
		for(int j=0;j<width+1;j++) {
		text+=display[j][i];
		}
		text+=System.lineSeparator();
	}
	return text;
}
public boolean isLeaf(node n) {
	//System.out.println(connections.get(n));
	
	return connections.get(n).size()<=1;
}
public boolean overlaps(node one, node two) {
	if(!one.equals(two)) {
	if(isLeaf(one)&&isLeaf(two)) {
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
	}
	return false;
}
public node getNodeAt(int x, int y) {
	//System.out.println("getting node at:"+x+","+y);
	for(node Node:nodes) {
		if (Node.getX()==x&&Node.getY()==y){
			System.out.println(Node.toString());
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
}
