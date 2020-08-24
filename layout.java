package origamiProject;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
	HashMap<UID,HashMap<UID,Integer>> distances;
	//[][] distances;
	paper p;
	int size;
	int baseBuffer;
node [] paired;

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
		System.out.println(leafNodes);
		for(int i=0;i<leafNodes.size();i++) {
			if(paired[i]!=null) {
				System.out.println(paired[i].toString()+": "+leafNodes.get(i).toString());	
			}
			
		}
	}

	private void addDiststances() {
		distances= new HashMap<UID,HashMap<UID,Integer>>();
		for (node n:p.nodes) {
			HashMap<UID,Integer> ndist= new HashMap<UID,Integer>();
			for( node m:p.nodes) {
				
				if(p.isLeaf(n)&&p.isLeaf(m)) {
					int dist= p.distances.get(n).get(m);
					ndist.put(m.ID, dist);
				}
			}
			distances.put(n.ID,ndist );
		}
	}

	private void addLeafNodes() {
		leafNodes= new ArrayList<node>();
		for(node n:p.nodes) {
			if(p.isLeaf(n)) {
				leafNodes.add(n);
			}
		}
		for (Condition c:p.conditions){
			node one= c.node1;
			node two= c.node2;
			if(p.isLeaf(one)&&p.isLeaf(two)) {
				leafNodes.remove(two);
			}
		}
		paired= new node[leafNodes.size()];
		for (Condition c:p.conditions){
			node one= c.node1;
			node two= c.node2;
			if(p.isLeaf(one)&&p.isLeaf(two)) {
				paired[leafNodes.indexOf(one)]=two;
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
		}else {
			for(skeleton design:newGen) {
				int designSize=design.getSize();
				int designScore=design.score;
				if(designSize<globalSize) {
					optimizeDF(index2,design);
				}else {
					if(designSize==globalSize&&designScore<globalScore) {
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

	

	private Generation makeNewGen(skeleton design, int index2) {
		// we start by setting up a place to store the new generated skeletons
		Generation newGen= new Generation();
		for(node m:design) {
			int radius= this.distances.get(m.ID).get(leafNodes.get(index2).ID);
			for(int i=radius+baseBuffer;i>=-radius-baseBuffer;i--) {
				for(int j=radius+baseBuffer;j>=-radius-baseBuffer;j--) {
					if(Math.abs(i)>=radius||Math.abs(j)>=radius) {
						node n= new node(leafNodes.get(index2));
						n.setX(i+m.getX());
						n.setY(j+m.getY());
						
						if(!design.overlaps(distances.get(n.ID), n, index2)) {
							skeleton newDesign=new skeleton(isFixedRatio, ratioX_Y);
							for(node old:design) {
								newDesign.add(new node(old));
							}
							for( node old:design.paired) {
								newDesign.addPaired(new node(old));
							}
							newDesign.score=design.score+Math.abs(n.getX())+Math.abs(n.getY());
							newDesign.add(n);
							if(paired[index2]!=null) {
								node pair= new node( paired[index2]);
								pair.setX(-1*n.getX());
								pair.setY(n.getY());
								if(!newDesign.overlaps(distances.get(pair.ID), pair, index2)) {
									newDesign.score+=Math.abs(pair.getX())+Math.abs(pair.getY());
									newDesign.addPaired(pair);
									newGen.add(newDesign);
									//System.out.println("checking"+pair.getX()+","+pair.getY()+";"+n.getX()+","+n.getY());
								}
							}else {
							newGen.add(newDesign);
						}
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
		for(node M:top.paired) {
			
				System.out.println(M.ID);
				
		
		}
				paper oldp= new paper(p);
				
				
				//we will set the nodes to the locations found in optimization
				for(node n:oldp.nodes) {
					if(oldp.isLeaf(n)) {
						for(node M:top) {
							if(n.ID==M.ID) {
								
								n.setX(M.getX());
								n.setY(M.getY());
								//System.out.println(j+": "+n.size+", "+n.getX()+":"+n.getY());
							
							}
						}
						for(node M:top.paired) {
							if(n.ID==M.ID) {
								
								n.setX(M.getX());
								n.setY(M.getY());
								System.out.println("ok");
								
							}
						}
						
					}
				}
				oldp.refreshNodes();
				oldp.shrink();
			
			
		//now we sort the papers, and pick the best one
		
		return oldp;
	}
	public void generateFirst() {
		skeleton design= new skeleton(isFixedRatio, ratioX_Y);
		node n= new node(leafNodes.get(0));
		n.setY(1);
		n.setX(1);
		design.add(n);
		this.generated.add(design);

	}

	

	//this lets the optimization run in the background.
	//for big designs, it can take a few minutes.
	@Override
	protected paper doInBackground() throws Exception {
		this.optimize(baseBuffer, hasSymetry);
		System.out.println("is overlaping:"+p.hasOverlap());
		return this.getPaper(p);
	}
}
