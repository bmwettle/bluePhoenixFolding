package origamiProject;

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.SwingWorker;


//this class handles the core part of the program
// it takes in a paper, and optimizes it to the best shape and size
// it starts with one node,
//considers posibles placements for the next node
//elimnates any that will not work
//sort out the best and keeps them
// and the repeats until all the nodes are added

public class layout  extends SwingWorker<paper,Void>{

	private static final int BASE_BUFFER_SIZE=0;
	private static final int MAX_POP_SIZE=50000;

	//leafNodes is a list of the nodes in paper p that are leaf nodes
	//it is used to make sure that all the skeletons are working on the same paper,
	//without affecting the paper
	ArrayList<node> leafNodes;
	
	//the paper class is great for display and changes, but is slow to optimize with
	//the skeleton class is the bare bones of it- just the leaf nodes
	//this lets the optimizer run quickly, ithout worrying about redoing a lot of computation
	ArrayList<skeleton>generated;
	//index shows what leaf node is being added in each step of the optimazation
	int index;
	
	//these keep track of the data needed from the original paper
	int[][] distances;
	paper p;
	int size;
	int baseBuffer;
	boolean[][]Xcon;
	boolean[][]Ycon;
	boolean[] isEdge;

//this sets up the layout, and make it ready to optimize
	public layout(paper p) {
		
		this.p=p;
		baseBuffer=BASE_BUFFER_SIZE;
		generated= new ArrayList<skeleton>();
		addLeafNodes();
		size= this.leafNodes.size();
		addDiststances();
		addXandYConditions();
		addEdgeConditions();
	}
	
	private void addDiststances() {
		distances= new int[size][size];
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				this.distances[i][j]=p.distances.get(leafNodes.get(i)).get(leafNodes.get(j));
			}
		}
	}
	
	private void addLeafNodes() {
		leafNodes= new ArrayList<node>();
		for(node n:p.nodes) {
			if(p.isLeaf(n)) {
				leafNodes.add(n);
			}
		}
	}
	
	private void addEdgeConditions() {
		isEdge= new boolean[leafNodes.size()];
		for(node e:p.edgeNodes) {
			if(p.isLeaf(e)) {
				isEdge[leafNodes.indexOf(e)]=true;
			}
		}
	}
	
	private void addXandYConditions() {
		Xcon= new boolean[leafNodes.size()][leafNodes.size()];
		Ycon= new boolean[leafNodes.size()][leafNodes.size()];
		for(Condition condition:p.conditions) {
			node one= condition.node1;
			node two= condition.node2;
			if(p.isLeaf(one)&&p.isLeaf(two)) {
				int index1= this.leafNodes.indexOf(one);
				int index2= this.leafNodes.indexOf(two);
				Xcon[index1][index2]=condition.matchX;
				Ycon[index1][index2]=condition.matchY;
				Xcon[index2][index1]=condition.matchX;
				Ycon[index2][index1]=condition.matchY;

			}
		}
	}
	
	//this method makes the best* design, by placing the leaf nodes in diferent locations
	//* it is nearly impossible to prove that this is the best for all casses.
	public void optimize() {
		index=0;
		//first we add the first node
		//it doesn't matter where it goes, since all the locations are relitive
		generateFirst();
		//assuming we have more the one node, we need to find the best spots for the rest
		if(leafNodes.size()>1) {
			for(int i=1;i<leafNodes.size();i++) {
				//we will do this for each node in sequence
				//the order does not make a big difference.
				index=i;
				generate(i,baseBuffer);
			}
			//as in the generation function, we don't need to keep all the deisgns
			Collections.sort(this.generated);
			if(generated.size()>MAX_POP_SIZE) {
				generated= new ArrayList<skeleton>(generated.subList(0, MAX_POP_SIZE));

			}
		}
	}
	
	//now that we have the best designs, we need to get the best paper.
	// since skeletons only store the locations of leaf nodes, 
	//we basiclly have to do the oposite of the constructor function
	//this also lets us compare two papers together
	//this could give us more insight as to which is better
	public paper getPaper(paper p) {
		ArrayList<paper>papers= new ArrayList<paper>();
		// we dont realy care about designs bigger than the smallest, only equals
		int MaxSize=generated.get(0).getSize();
		for(skeleton top:generated) {
			if(top.getSize()<=MaxSize) {
				paper oldp= new paper(p);
				int j=0;
				//we will set the nodes to the locations found in optimazation
				for(node n:oldp.nodes) {
					if(oldp.isLeaf(n)) {
						node newN= top.get(j);
						n.forceX(newN.getX());
						n.forceY(newN.getY());
						//System.out.println(j+": "+n.size+", "+n.getX()+":"+n.getY());
						j++;
					}
				}
				//now we want to place the rivers and hubs.
				//the locations don't really matter, 
				//but its nice to see how things connect in the plan display
				//TODO finish this method
				for(node n:oldp.nodes) {
					if(oldp.isLeaf(n)) {
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
		}
		//now we sort the papers, and pick the best one
		Collections.sort(papers);
		return papers.get(0);
	}
	public void generateFirst() {
		skeleton design= new skeleton();
		node n= new node(leafNodes.get(0));
		design.add(n);
		this.generated.add(design);
	}
	//here we generate the next node
	public void generate(int index,int buffer) {
		// we start by setting up a place to store the new generated skeletons
		ArrayList<skeleton>newGen= new ArrayList<skeleton>();
		//for all the designs we have
		for(skeleton design:generated) {
			//get the last node we made
			//we know there is one, since the first one is made seperatly
			node m=design.get(index-1);
			
			// this is the "radius" we will check around the last node for this new one
			//since we are useing squares, this is a radius*2 square around the last node
			// we could search more area, but that is slow and not that usefull
			int radius= this.distances[index-1][index]+buffer;
			for(int i=radius;i>=-radius;i--) {
				for(int j=radius;j>=-radius;j--) {
					//we only want to search the parts of the square
					//that are farr enough away to not overlap the last node
					if(Math.abs(i)>=radius||Math.abs(j)>=radius) {
						//now we put a node at that spot
						node n= new node(leafNodes.get(index));
						n.forceX(i+m.getX());
						n.forceY(j+m.getY());
						//if it doesnt overlap with anything else,
						//Great!, it"s a canidate
						if(!design.overlaps(distances, n, index)) {
							//we store it in a new skeleton
							skeleton newDesign= new skeleton();
							for(node old:design) {
								newDesign.add(new node(old));
							}
							newDesign.score=design.score;
							newDesign.add(n);
							
							//now we check if it meets the conditions
							//these can force two nodes to match X or Y cordinates,
							//or to be on the Y or X axis
							if(meetsConditions(newDesign,index+1)) {
								//add it to the list of designs under consideration
								newGen.add(newDesign);
							}
						}
					}
				}
			}
		}
		
		if(newGen.size()!=0) {
			generated=newGen;
			//once we have all the possible placements of this node, we have a problem
			//if we looked at all of them in the next step, the problem would become huge
			// so, we will only look at the best
			// the skeleton class can compare itself to other skeletons.
			Collections.sort(this.generated);
			//now that we know what skeletons are best, we only need to kep some
			//MAX_POP_SIZE is set by default to 50,000. for simple designs, this is over kill
			//for huge designs, this might need to be increased
			if(newGen.size()>MAX_POP_SIZE) {
				generated= new ArrayList<skeleton>(newGen.subList(0, MAX_POP_SIZE));
			}
		
		}else {
			
			//if we have no designs, we have to search a bigger area
			System.out.println("adding buffer: "+buffer+1);
			generate(index,buffer+1);
		}
	}


	private boolean meetsConditions(skeleton design,int index) {
		//dont check the nodes we didn't add yet, ony up the the latest
		for(int i=0;i<index;i++) {
			for(int j=0;j<index;j++) {
//Xcon stores if nodes i and j need to have the same Xcords,
				if(Xcon[i][j]) {
					if(design.get(i).getX()!=design.get(j).getX()) {
					//if one pair is wround, the skeleton will not work
						return false;
					}
				}
				//same thing, for y cords
				if(Ycon[i][j]) {
					if(design.get(i).getY()!=design.get(j).getY()) {
						return false;
					}
				}
			}
			//if the node needs to be on the edge, make sure it is
			if(isEdge[i]) {
				node n= design.get(i);
				if(n.getX()==0||n.getY()==0) {
					System.out.println("ok");
				}else {
					//if its not, the design will not work
					return false;
				}
			}
		}
		//if all the conditions are met, or the were none, it's a good design
		return true;
	}
	
	//this lets the optimazation run in the background.
	//for big designs, it can take a few muineutes.
	@Override
	protected paper doInBackground() throws Exception {
		this.optimize();
		return this.getPaper(p);
	}
}
