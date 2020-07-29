package origamiProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class layout {
ArrayList<node> leafNodes;
int max_gen;
int MAX_POP_SIZE=5000 ;
ArrayList<skeleton>generated;
int[][] distances;
paper p;
	public layout(paper p) {
		this.p=p;
		System.out.println("hi there");
		generated= new ArrayList<skeleton>();
		leafNodes= new ArrayList<node>();
		
		for(node n:p.nodes) {
			if(p.isLeaf(n)) {
				leafNodes.add(n);
			}
		}
		int size= this.leafNodes.size();
		distances= new int[size][size];
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				this.distances[i][j]=p.distances.get(leafNodes.get(i)).get(leafNodes.get(j));
			}
		}
		generateFirst();
		for(int i=1;i<leafNodes.size();i++) {
			generate(i,0);
		}
		System.out.println("ok now");
		// TODO Auto-generated constructor stub
	}
	public paper getPaper(paper p) {
		ArrayList<paper>papers= new ArrayList<paper>();
		for(ArrayList<node>top:generated) {
			paper oldp= new paper(p);
		//for(node m:top) {
			//System.out.println(m.size+", "+m.getX()+":"+m.getY());
		//}
		int j=0;
		for(node n:oldp.nodes) {
			
			if(oldp.isLeaf(n)) {
				node newN= top.get(j);
				n.forceX(newN.getX());
				n.forceY(newN.getY());
				System.out.println(j+": "+n.size+", "+n.getX()+":"+n.getY());
				j++;
			}else {
				int x=0;
				int y=0;
				for(node m:oldp.connections.get(n)) {
					x+=m.getX();
					y+=n.getY();
				}
				n.forceX((int)(x/oldp.connections.get(n).size()));
				n.forceY((int)(y/oldp.connections.get(n).size()));
				
			}
		}
		oldp.shrink();
		papers.add(oldp);
		}
		Collections.sort(papers);
		System.out.println(papers.get(0).equals(papers.get(1)));
		for(paper a:papers) {
			//System.out.println(a.height+","+a.width);
		}
		return papers.get(0);
	}
	public void generateFirst() {
			skeleton design= new skeleton();
			node n= new node(leafNodes.get(0));
			design.add(n);
			this.generated.add(design);
		
		System.out.println("hi");
	}
public void generate(int index,int buffer) {
	//System.out.println("here");
	ArrayList<skeleton>newGen= new ArrayList<skeleton>();
	for(ArrayList<node> design:generated) {
		
		node m=design.get(index-1);
		int radius= this.distances[index-1][index]+buffer;
		//System.out.println(radius+index);
		for(int i=radius;i>=-radius;i--) {
			for(int j=radius;j>=-radius;j--) {
				if(Math.abs(i)>=radius||Math.abs(j)>=radius) {
				node n= new node(leafNodes.get(index));
				n.forceX(i+m.getX());
				n.forceY(j+m.getY());
				if(!overlaps(design,n,index)) {
					//System.out.println("tada");
					skeleton newDesign= new skeleton();
					for(node old:design) {
						newDesign.add(new node(old));
					}
					newDesign.add(n);
					newGen.add(newDesign);
				}
				}
			}
		}
	}
	if(newGen.size()!=0) {
	generated=newGen;
	Collections.sort(this.generated);
	if(newGen.size()>MAX_POP_SIZE) {
		newGen= new ArrayList<skeleton>(newGen.subList(0, MAX_POP_SIZE));
		}
	System.out.println(newGen.size()+"here we ");
	System.out.println(newGen.get(0).size()+"here we go");
	}else {
		generate(index,buffer+1);
	}
}
public int getSize(ArrayList<node>nodes) {
	int xmax=0;
	int xmin=Integer.MAX_VALUE;
	int ymax=0;
	int ymin=Integer.MAX_VALUE;
	for(node n:nodes) {
		//if(isLeaf(n)) {
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
		//}
	}
	return Math.max(xmax-xmin,ymax-ymin);
	
}
public boolean overlaps(ArrayList<node> nodes, node n, int index) {
	for(node m:nodes) {
	if(!m.equals(n)) {
		
			int deltax= Math.abs(n.getX()-m.getX());
			int deltay= Math.abs(n.getY()-m.getY());
			int dist=distances[index][nodes.indexOf(m)];
			if(deltax<dist) {
				if(deltay<dist) {
					return true;
				}
				
			
			
		}
	}
	}
	return false;
}
}
