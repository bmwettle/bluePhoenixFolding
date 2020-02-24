package origamiProject;

import java.util.*;
public class paper {
int num_squares;
int square_size;
Map<node, ArrayList<node>> connections;
ArrayList<node> nodes;


public paper(int squares) {
	num_squares=squares;
	connections= new HashMap<node,ArrayList<node>>();
	nodes= new ArrayList<node>();
addNode(new node(1,1,4,"leaf"));
addNode(new node(1,3,4,"leaf"));
addNode(new node(1,6,2,"leaf"));
	
}

public void addNode(int x, int y,int size, String type, node startNode) {
	node newNode= new node(x,y,size,type);
	connections.get(startNode).add(newNode);
	ArrayList<node>newList= new ArrayList<node>();
	newList.add(startNode);
	connections.put(newNode,newList);
	nodes.add(newNode);	
}
public void addNode(node newNode, node startNode) {
	
	connections.get(startNode).add(newNode);
	ArrayList<node>newList= new ArrayList<node>();
	newList.add(startNode);
	connections.put(newNode,newList);
	nodes.add(newNode);
}
public void addNode(node newNode) {
	nodes.add(newNode);
	ArrayList<node>newList= new ArrayList<node>();
	connections.put(newNode,newList);
}

public void addNode(int x, int y,int size, String type) {
	node newNode= new node(x,y,size,type);
	ArrayList<node>newList= new ArrayList<node>();
	connections.put(newNode,newList);
	nodes.add(newNode);
	
}

public Iterator<node> getNodes(){
	return nodes.iterator();
	
}
public ArrayList<node> getConections(node myNode) {
	return connections.get(myNode);
}
}
