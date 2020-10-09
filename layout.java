
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	int max_checked;
	int checked;
	String outputLog;
	//leafNodes is a list of the nodes in paper p that are leaf nodes
	//it is used to make sure that all the skeletons are working on the same paper,
	//without affecting the paper
	ArrayList<node> leafNodes;

	//the paper class is great for display and changes, but is slow to optimize with
	//the skeleton class is the bare bones of it- just the leaf nodes
	//this lets the optimizer run quickly, without worrying about redoing a lot of computation
	
	ArrayList<skeleton> minor;

	//these keep track of the data needed from the original paper
	HashMap<UID,HashMap<UID,Integer>> distances;
	//[][] distances;
	paper p;
	int size;
	int baseBuffer;
	node [] paired;
	boolean checkminor;
	//this sets up the layout, and make it ready to optimize
	public layout(paper p, int base_buffer,int max_checked,boolean check_minor) {
		this.max_checked=max_checked;
		this.baseBuffer=base_buffer;
		this.p=p;
		this.checkminor=check_minor;
		outputLog="starting"+System.lineSeparator()+"ok";
		addLeafNodes();
		size= this.leafNodes.size();
		isFixedRatio=p.isFixedRatio;
		ratioX_Y=p.ratioX_Y;
		addDiststances();
		globalSize=1;
		globalScore=Integer.MAX_VALUE;
		for(node n:leafNodes) {
			for(node m:leafNodes) {
			globalSize+=distances.get(n.ID).get(m.ID);
		}
		}
		tree= new Generation[size];
		for(int i=0;i<size;i++) {
			tree[i]=new Generation();
		}
		minor= new ArrayList<skeleton>();
		outputLog+=leafNodes+System.lineSeparator();
		for(int i=0;i<leafNodes.size();i++) {
			if(paired[i]!=null) {
				outputLog+=paired[i].toString()+": "+leafNodes.get(i).toString()+System.lineSeparator();	
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
		if(parent.size>globalSize) {
			System.out.println("good idea");
		}
		Generation newGen= makeNewGen(parent,index2);
		sortNewGen(newGen);
	
		
		if(newGen.size()>=1) {
			checked+=newGen.size();
			skeleton myBest= newGen.get(0);
			index2++;
			if(index2==size) {
				
				int mySize=myBest.getSize();
				
				//System.out.println("checking best"+index2+","+newGen.size());
				if(mySize<globalSize) {
					System.out.println("better");
					outputLog+="updating best"+index2+": "+myBest.getSize()+",>" +myBest.score+", "+myBest.size()+". "+leafNodes.size()+System.lineSeparator();
					outputLog+="checked "+checked+"nodes"+System.lineSeparator();
					outputLog+=newGen.get(0)+System.lineSeparator();
					globalBest=myBest;
					globalSize=myBest.getSize();
					globalScore=myBest.score;
					checked=0;
					minor = new ArrayList<skeleton>();
					//globalScore=myBest.score;
				}
			}else {
				if(checked<max_checked) {
					
				for(skeleton design:newGen) {
					int designSize=design.getSize();
					int designScore=design.score;
					if(designSize<globalSize&&designScore<globalScore) {
						for(int i=0;i<index2;i++) {
							outputLog+="	";
						}
							outputLog+="<"+index2+">"+design+"ch"+checked+System.lineSeparator();
						optimizeDF(index2,design);
						
						//}
					}else {
						break;
						}
						
					design.removeAll(design);
				}
				newGen.removeAll(newGen);
			}else{
				outputLog+="checked too many"+System.lineSeparator();
			}}
		}else {
			outputLog+="dead end"+System.lineSeparator();
		}
		
	}
	private void sortNewGen(Generation newGen) {
		Collections.sort(newGen);
	}



	private Generation makeNewGen(skeleton design, int index2) {
		int gap=globalSize-design.size;
		// we start by setting up a place to store the new generated skeletons
		Generation newGen= new Generation();
		boolean [][] placed= new boolean[globalSize+2*gap][globalSize+2*gap];
		for(node m:design) {
			int radius= this.distances.get(m.ID).get(leafNodes.get(index2).ID);
			for(int i=radius+baseBuffer;i>=-radius-baseBuffer;i--) {
				int x= m.getX()+i;
				if(x>=-gap&&x<globalSize+gap) {
				for(int j=radius+baseBuffer;j>=-radius-baseBuffer;j--) {
					int y= m.getY()+j;
					if(y>=-gap&&y<globalSize+gap) {
						placed[x+gap][y+gap]=true;
					}
				}
				}
			}
		}
		for(node m:design) {
			int radius= this.distances.get(m.ID).get(leafNodes.get(index2).ID)-1;
			for(int i=radius;i>=-radius;i--) {
				int x= m.getX()+i;
				if(x>=-gap&&x<globalSize+gap) {
				for(int j=radius;j>=-radius;j--) {
					int y= m.getY()-0+j;
					if(y>=-gap&&y<globalSize+gap) {
						placed[x+gap][y+gap]=false;
					}
				}
				}
			}
		}
		int x=0;
		for(boolean[] row:placed) {
			int y= 0;
			
			for(boolean ok:row) {
				
				if(ok) {
					
					skeleton newDesign=new skeleton(isFixedRatio, ratioX_Y);
					for(node old:design) {
						newDesign.add(new node(old));
					}
					for( node old:design.paired) {
						newDesign.addPaired(new node(old));
					}
					node n= new node(leafNodes.get(index2));
					n.setX(x-gap);
					n.setY(y-gap);
					newDesign.score=design.score+Math.abs(n.getX())+Math.abs(n.getY());
					if((newDesign.score)<globalScore) {
					newDesign.add(n);
//					if(paired[index2]!=null) {
//						node pair= new node( paired[index2]);
//						pair.setX(-1*n.getX());
//						pair.setY(n.getY());
//						if(!newDesign.overlaps(distances.get(pair.ID), pair)) {
//							newDesign.score+=Math.abs(pair.getX())+Math.abs(pair.getY());
//							newDesign.addPaired(pair);
//							
//								newGen.add(newDesign);
//								
//						}
//					}else {
						
					newGen.add(newDesign);
					//}
				//	System.out.println("x"+x+", "+y+newDesign.score);
				}
					}else {
						//System.out.print(".");
					}
				y++;
				
			}
		
			x++;
		}

		return newGen;
	}

	public void optimize() {
		System.out.println(checkminor);
		//first we add the first node
		//it doesn't matter where it goes, since all the locations are relative
		
		this.checked=0;
		
		

		this.optimizeDF(1, generateFirst());
		if(checkminor) {
			checkMinorImprovements();
		}
		System.out.println(globalBest);
		for(node n:globalBest) {
			if(globalBest.overlaps(distances.get(n.ID), n)) {
				outputLog+="panic"+System.lineSeparator();
			}
		}
		try {
			outputLog+="ok, finished optimazation";
		      FileWriter myWriter = new FileWriter("outputLog.txt");
		      myWriter.write(outputLog);
		      myWriter.close();
		      System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
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
	public paper getPaper(paper oldp) {

		// we don't really care about designs bigger than the smallest.
		skeleton top=globalBest;
		//we will set the nodes to the locations found in optimization
		for(node n:oldp.nodes) {
			if(oldp.isLeaf(n)) {
				for(node M:top) {
					if(n.ID==M.ID) {

						n.setX(M.getX());
						n.setY(M.getY());
						}
				}
				for(node M:top.paired) {
					if(n.ID==M.ID) {
						n.setX(M.getX());
						n.setY(M.getY());}
				}}
		}
		//oldp.setSelectedNode(oldp.nodes.get(0));
		//oldp.shrink();
		oldp.refreshNodes();
		oldp.shrink();
		//now we sort the papers, and pick the best one

		return oldp;
	}
	public skeleton generateFirst() {
		skeleton design= new skeleton(isFixedRatio, ratioX_Y);
		node n= new node(leafNodes.get(0));
		n.setY(0);
		n.setX(0);
		design.add(n);
		return design;
	}

	//this lets the optimization run in the background.
	//for big designs, it can take a few minutes.
	@Override
	protected paper doInBackground() throws Exception {
		this.optimize();
		System.out.println("was overlaping:"+p.hasOverlap());
		return this.getPaper(p);
	}
}