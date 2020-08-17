package origamiProject;

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.SwingWorker;

/**
 * 
 * @author Benjamin Wettle
 * this class handles the core part of the program
 * the optimize method takes a paper design, and places the leaf nodes
 * in the optimal arrangement. The class assumes that the design is finalized.
 * 
 * 
 */

public class layout  extends SwingWorker<paper,Void>{

	boolean isFixedRatio;
	boolean hasSymetry;
	boolean isXSymmetric;
	double ratioX_Y;
	Generation[] tree;
	int min_size;
	int globalSize;
	int globalScore;
	skeleton globalBest;
	int rejections;
	int sorts;
	//leafNodes is a list of the nodes in paper p that are leaf nodes
	//it is used to make sure that all the skeletons are working on the same paper,
	//without affecting the paper
	ArrayList<node> leafNodes;

	//the paper class is great for display and changes, but is slow to optimize with
	//the skeleton class is the bare bones of it- just the leaf nodes
	//this lets the optimizer run quickly, without worrying about redoing a lot of computation
	ArrayList<skeleton>generated;
	ArrayList<skeleton> minor;

	//these keep track of the data needed from the original paper
	int[][] distances;
	paper p;
	int size;
	int baseBuffer;


	//this sets up the layout, and make it ready to optimize
	public layout(paper p) {

		this.p=p;

		generated= new ArrayList<skeleton>();
		addLeafNodes();
		size= this.leafNodes.size();
		isFixedRatio=p.isFixedRatio;
		ratioX_Y=p.ratioX_Y;
		addDiststances();
		globalSize=Integer.MAX_VALUE;
		globalScore=Integer.MAX_VALUE;
		tree= new Generation[size];
		for(int i=0;i<size;i++) {
			tree[i]=new Generation();
		}
		minor= new ArrayList<skeleton>();
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

	/**
	 *this function handles the core part of the program
	 * it takes given paper, and optimizes it to the best shape and size,
	 * subject to the constraints given by the paper.
	 */
	private void optimizeDF(int index2, skeleton parent) {	
		Generation newGen= makeNewGen(parent,index2);
		sortNewGen(newGen);
		if(newGen.size()>=1) {
		skeleton myBest= newGen.get(0);
		index2++;
		if(index2==size) {
			if(p.hasSymmetry) {
			newGen=enforceConditions(newGen);
			newGen=newGen.checkFixed(p.isXsymmetry);
			sortNewGen(newGen);
			
			}
			if(newGen.size()>=1) {
			myBest= newGen.get(0);
			int mySize=myBest.getSize();
			//System.out.println("checking best"+index2+","+newGen.size());
			if(mySize<globalSize) {
				System.out.println("updating best"+index2+": "+myBest.getSize()+",>" +myBest.score);
				globalBest=myBest;
				globalSize=myBest.getSize();
				minor = new ArrayList<skeleton>();
				//globalScore=myBest.score;
			}else {
				if(mySize==globalSize&&myBest.score<globalScore) {
					minor.add(myBest);
				}
			}
			}
		}else {
			for(skeleton design:newGen) {
				int designSize=design.getSize();
				if(designSize<globalSize) {
					optimizeDF(index2,design);
				}else {
					if(designSize==globalSize&&design.score<globalScore) {
						minor.add(design);
					}
				}
			}
		}
		}
	}
	private void sortNewGen(Generation newGen) {
		Collections.sort(newGen);
	}

	private Generation enforceConditions(Generation newGen) {
		Generation checkGen= new Generation();
		for(skeleton sk:newGen) {
			if(this.meetsConditions(sk)) {
				checkGen.add(sk);
			}
		}
		return checkGen;
	}

	private Generation makeNewGen(skeleton design, int index2) {
		// we start by setting up a place to store the new generated skeletons
		Generation newGen= new Generation();
		for(node m:design) {
			int radius= this.distances[index2-1][index2];
			for(int i=radius+baseBuffer;i>=-radius+baseBuffer;i--) {
				for(int j=radius+baseBuffer;j>=-radius+baseBuffer;j--) {
					if(Math.abs(i)>=radius||Math.abs(j)>=radius) {
						node n= new node(leafNodes.get(index2));
						n.setX(i+m.getX());
						n.setY(j+m.getY());
						if(!design.overlaps(distances, n, index2)) {
							skeleton newDesign=new skeleton(isFixedRatio, ratioX_Y);
							for(node old:design) {
								newDesign.add(new node(old));
							}
							newDesign.score=design.score+n.getX()+n.getY();
							newDesign.add(n);
							newGen.add(newDesign);
						}
					}
				}
			}
		}
		return newGen;
	}

	public void optimize(int buffer, boolean checkminor) {
		System.out.println(checkminor);
		//first we add the first node
		//it doesn't matter where it goes, since all the locations are relative
		this.baseBuffer=buffer;
		generateFirst();
		
		this.optimizeDF(1, generated.get(0));
		if(checkminor) {
			checkMinorImprovements();
		}
		
	}
	private void checkMinorImprovements() {
		System.out.println("checking for minor improvements");
		ArrayList<skeleton> toCheck= new ArrayList<skeleton>(this.minor);
		for(skeleton sk:toCheck) {
			this.optimizeDF(sk.size(), sk);
		}
	}

	//now that we have the best designs, we need to get the best paper.
	// since skeletons only store the locations of leaf nodes, 
	//we basically have to do the opposite of the constructor function
	//this also lets us compare two papers together
	//this could give us more insight as to which is better
	public paper getPaper(paper p) {
		
		// we don't really care about designs bigger than the smallest, only equal.
		skeleton top=globalBest;
				paper oldp= new paper(p);
				int j=0;
				//we will set the nodes to the locations found in optimization
				for(node n:oldp.nodes) {
					if(oldp.isLeaf(n)) {
						node newN= top.get(j);
						n.setX(newN.getX());
						n.setY(newN.getY());
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
						n.setX((int)(x/oldp.connections.get(n).size()));
						n.setY((int)(y/oldp.connections.get(n).size()));
					}
				}
				oldp.shrink();
			
			
		//now we sort the papers, and pick the best one
		
		return oldp;
	}
	public void generateFirst() {
		skeleton design= new skeleton(isFixedRatio, ratioX_Y);
		node n= new node(leafNodes.get(0));
		design.add(n);
		this.generated.add(design);

	}
private boolean meetsConditions(skeleton design) {
		if(!p.hasSymmetry) {
			return true;
		}
		//don't check the nodes we didn't add yet, only up the the latest
		for(Condition con:p.conditions) {
			node one=con.node1;
			node two=con.node2;
			if(p.isLeaf(one)&&p.isLeaf(two)) {
				int index1 =leafNodes.indexOf(one);
				int index2 =leafNodes.indexOf(two);
				int index0=design.getSize()-1;
				if(index1<=index0&&index2<=index0) {
					int xscale=1;
					int yscale=-1;
					if(p.isXsymmetry) {
						xscale=-1;
						yscale=1;
					}
					if(design.get(index2).getX()!=xscale*design.get(index1).getX()) {
						return false;
					}
					if(design.get(index2).getY()!=yscale*design.get(index1).getY()) {
						return false;
					}
				}
			}
		}
		//if all the conditions are met, or the were none, it's a good design
		return true;
	}

	//this lets the optimization run in the background.
	//for big designs, it can take a few minutes.
	@Override
	protected paper doInBackground() throws Exception {
		this.optimize(baseBuffer, hasSymetry);
		
		return this.getPaper(p);
	}
}
