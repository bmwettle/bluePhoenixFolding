package origamiProject;

import java.util.*;
public class paper {
int num_squares;
int square_size;
Map<node, List<node>> connections;
ArrayList<node> nodes;
public paper() {
	connections= new HashMap<node,List<node>>();
	nodes= new ArrayList<node>();
	node a= new node(4,4,1,"leaf");
	addNode(a);
	addNode(2,1,2,"leaf",a);
	
}
public void addNode(int x, int y,int size, String type, node startNode) {
	node newNode= new node(x,y,size,type);
	connections.get(startNode).add(newNode);
	List<node>newList= new ArrayList<node>();
	newList.add(startNode);
	connections.put(newNode,newList);
	nodes.add(newNode);
	
}
public void addNode(node newNode, node startNode) {
	
	connections.get(startNode).add(newNode);
	List<node>newList= new ArrayList<node>();
	newList.add(startNode);
	connections.put(newNode,newList);
	nodes.add(newNode);
	
}
public void addNode(node newNode) {
	nodes.add(newNode);
	List<node>newList= new ArrayList<node>();
	connections.put(newNode,newList);
}

public void addNode(int x, int y,int size, String type) {
	node newNode= new node(x,y,size,type);
	List<node>newList= new ArrayList<node>();
	connections.put(newNode,newList);
	nodes.add(newNode);
	
}

public Iterator<node> getNodes(){
	return nodes.iterator();
	
}
public List<node> getConections(node myNode) {
	return connections.get(myNode);
}
}
