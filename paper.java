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
HashMap<node, ArrayList<node>> connections;
public ArrayList<node> nodes;
HashMap<node,ArrayList<node>> cuts;
HashMap<node,HashMap<node, Integer>> distances;
ArrayList<node> settled;
ArrayList<node> unSettled;

@Override
public int compareTo(paper b) {
	return this.getSize()-b.getSize();
}
public int getSize() {
	// TODO Auto-generated method stub
	return Math.max(width, height);
}
@SuppressWarnings("unchecked")
public paper(paper p) {
	this.width=p.width;
	this.height=p.height;
	this.square_size=p.square_size;
	this.selected=new node(p.selected);
	this.connections= (HashMap<node, ArrayList<node>>) p.connections.clone();
	this.nodes=(ArrayList<node>) p.nodes.clone();
	this.cuts = (HashMap<node, ArrayList<node>>) p.cuts.clone();
	this.distances=(HashMap<node, HashMap<node, Integer>>) p.distances.clone();

}

public void shrink() {
	int xmax=0;
	int xmin=Integer.MAX_VALUE;
	int ymax=0;
	int ymin=Integer.MAX_VALUE;
	
	for(node n:nodes) {
		if(n.x>xmax) {
			xmax=n.x;
		}
		if(n.x<xmin) {
			xmin=n.x;
		}
		if(n.y>ymax) {
			ymax=n.y;
		}
		if(n.y<ymin) {
			ymin=n.y;
		}
		
	}
	for( node n:nodes) {
	n.x-=xmin;	
	n.y-=ymin;
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
		Xcords[i]=nodes.get(i).x;
	}
	return Xcords;
}
public int[] getYcords() {
	int[] Ycords= new int[nodes.size()];
	for(int i=0;i<nodes.size();i++) {
		Ycords[i]=nodes.get(i).y;
	}
	return Ycords;
}

public paper(int w,int h) {
	width=w;
	height=h;
	connections= new HashMap<node,ArrayList<node>>();
	nodes= new ArrayList<node>();
	cuts= new HashMap<node,ArrayList<node>>();
}
public void addCut(node second) {
	if(selected!=null) {
	cuts.get(selected).add(second);
	cuts.get(second).add(selected);
	}
}
public ArrayList<node> getCuts(node Node) {
	return cuts.get(Node);
}
public void moveSelect(int x, int y) {
	selected.x=x;
	selected.y=y;
}
public void getTreeDistances() {
	distances= new HashMap<node,HashMap<node,Integer>>();
	for(node one:nodes) {
		
			
			HashMap<node,Integer> selfMap= new HashMap<node,Integer>();
			
			selfMap.put(one,0);
			distances.put(one,selfMap);
			//System.out.println(one.toString()+","+connections.get(one).toString());
		}
	for (node start:nodes){
		
		
		for(node end:nodes) {
			
			searchNodes(start,start,end,0,new ArrayList<node>());
			
		}
	}
		
}
public void setPaperSize(int w, int h) {
	this.width=w;
	this.height=h;
}
public int getLongestLegWithoutCuts(node one,node two) {
	int deltaX= Math.abs(one.x-two.x);
	int deltaY= Math.abs(one.y-two.y);

	return Math.max(deltaX, deltaY);
}


private void searchNodes(node start, node current,node end,int length, ArrayList<node> mySearched) {
	//System.out.println("now at:"+nodes.indexOf(start)+","+nodes.indexOf(current)+","+nodes.indexOf(end)+","+ length);
	ArrayList<node> searched= new ArrayList<node>();
	searched.addAll(mySearched);
	searched.add(current);
	
	if (current.equals(end)||cuts.get(current).contains(end)){
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
			searchNodes(start,connect,end,length+connect.size+current.size,searched);
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
	ArrayList<node> newCuts= new ArrayList<node>();
	newList.add(startNode);
	connections.put(newNode,newList);
	cuts.put(newNode, newCuts);
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
		ArrayList<node> newCuts= new ArrayList<node>();
		cuts.put(newNode, newCuts);
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
		display[n.x][n.y]=n.toString().charAt(1);
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
	return connections.get(n).size()>1;
}
public boolean overlaps(node one, node two) {
	int deltax= Math.abs(one.x-two.x);
	int deltay= Math.abs(one.y-two.y);
	if(deltax>=distances.get(one).get(two)) {
		return false;
	}
	if(deltay>=distances.get(one).get(two)) {
		return false;
	}

	return true;
}
public node getNodeAt(int x, int y) {
	System.out.println("getting node at:"+x+","+y);
	for(node Node:nodes) {
		if (Node.x==x&&Node.y==y){
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
