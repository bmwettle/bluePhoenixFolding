
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
	boolean end;
	//HashMap<skeleton,Boolean> globalChecked;
	//int hashIgnored;
	boolean isFixedRatio;
	boolean hasSymetry;
	double ratioX_Y;
	boolean improved;
	//Generation[] tree;
	//int min_size;
	int globalSize;
	int globalSmallSize;
	int globalScore;
	int upperBound;
	int lowerBound;
	//	int[] parents;
	//int[] children;
	//int[] finished;
	//int[] forced;
	//long[] times;
	int total;
	int depth;
	ArrayList<skeleton> newBestPartial;
	ArrayList<skeleton> oldBestPartial;
	//ArrayList<Double>[]totalDensity;
	skeleton globalBest;
	int max_checked;
	//int checked_live;
	int checked_dead;
	//String outputLog;
	String newline;
	//leafNodes is a list of the nodes in paper p that are leaf nodes
	//it is used to make sure that all the skeletons are working on the same paper,
	//without affecting the paper
	ArrayList<node> leafNodes;

	//the paper class is great for display and changes, but is slow to optimize with
	//the skeleton class is the bare bones of it- just the leaf nodes
	//this lets the optimizer run quickly, without worrying about redoing a lot of computation


	//these keep track of the data needed from the original paper
	HashMap<Integer,HashMap<Integer,Integer>> distances;
	//[][] distances;
	paper p;
	int size;
	int baseBuffer;
	//this sets up the layout, and make it ready to optimize
	public layout(paper p, int base_buffer,int max_checked) {
		newline=System.lineSeparator();
		//globalChecked= new HashMap<skeleton,Boolean>();
		//hashIgnored=0;
		this.max_checked=max_checked;
		this.baseBuffer=base_buffer;
		this.p=p;
		this.hasSymetry=p.hasSymmetry;
		end=false;
		//outputLog="starting"+System.lineSeparator()+"ok";
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
		globalSize=(int) Math.sqrt(globalSize);
		globalSmallSize=globalSize;
		//tree= new Generation[size];
		//for(int i=0;i<size;i++) {
		//tree[i]=new Generation();
		//}
		//	outputLog+=leafNodes+System.lineSeparator();		
	}

	private void addDiststances() {
		distances= new HashMap<Integer,HashMap<Integer,Integer>>();
		for (node n:p.nodes) {
			HashMap<Integer,Integer> ndist= new HashMap<Integer,Integer>();
			for( node m:p.nodes) {

				if(n.isLeaf&&m.isLeaf) {
					int dist= p.distances.get(n.ID).get(m.ID);
					ndist.put(m.ID, dist);
				}
			}
			distances.put(n.ID,ndist );
		}
	}

	private void addLeafNodes() {
		leafNodes= new ArrayList<node>();
		for(node n:p.nodes) {
			if(n.isLeaf) {
				leafNodes.add(n);
			}
		}
	}

	/**
	 *this function handles the core part of the program
	 * it takes given paper, and optimizes it to the best shape and size,
	 * subject to the constraints given by the paper.
	 */
	private int optimizeDF(int index2, skeleton parent) {
		//if(globalChecked.containsKey(parent)) {
		//	hashIgnored++;
		//	return 0;
		//}
		checked_dead++;
		if(!end) {
			if(checked_dead<max_checked) {
				total++;
				//times[0]+=System.nanoTime();
				//make a new generation of skeletons, by adding the next node to the parent
				Generation newGen= makeNewGen(parent,index2);
				// if that generation is possible
				if(newGen!=null&&newGen.size()>=1) {
					// get the best of the generation
					skeleton myBest=newGen.get(0);
					index2++;
					if(index2==size) {
						// if that is the last node to be added
						//check to see if that is an improvement over the current best skeleton
						int mySize=myBest.getSize();
						if(mySize<globalSize||(mySize==globalSize&&(myBest.smallSize<globalSmallSize))) {
							globalBest=myBest;
							globalSize=myBest.getSize();
							globalScore=myBest.score;
							globalSmallSize=myBest.smallSize;
							end=true;
							improved=true;
							globalBest.Printout();
						}

						return -10;
					}
					// extra code for finding partial solutions, not currently used
					else if(index2==depth) {

						int mySize=myBest.getSize();

						if(mySize<globalSize||(mySize==globalSize&&(myBest.smallSize<globalSmallSize))) {

							newBestPartial.add(myBest);

						}
						return -10;
					}else {
						int count=0;
						for(skeleton design:newGen) {
							count+=optimizeDF(index2,design);

							design=null;
						}
						if(!end) {
						//globalChecked.put(parent, true);
						}
						newGen=null;
						return (int) (count*.5);

					}
				}				else {

					return 1;
				}
			}else{

			}
		}
		return 0;
	}

private Boolean[][] makePlaced(skeleton design, int index2) {
	int gap=globalSize-design.size;
	Boolean [][]placed= new Boolean[design.size+2*gap][design.size+2*gap];

	//times[1]+=System.nanoTime();

	for(node m:design.nodes) {
		if(m!=null) {
			// each pair of nodes must be a certain distance apart, called the radius
			int radius= this.distances.get(m.ID).get(leafNodes.get(index2).ID);
			int topx=Math.min(radius+baseBuffer,design.size-m.getX()+gap-1);
			int lowx=Math.max(-radius,-gap-m.getX());
			for(int i=topx;i>=lowx;i--) {
				int x= m.getX()+i+gap;

				int topy=Math.min(radius+baseBuffer,design.size-m.getY()+gap-1);
				int lowy=Math.max(-radius,-gap-m.getY());
				for(int j=topy;j>=lowy;j--) {
					int y= m.getY()+j+gap;
					// if the nodes are far enough apart, then you could place a node there
					if(Math.abs(i)>=radius||Math.abs(j)>=radius) {
						if(placed[x][y]==null) {
							placed[x][y]=true;
						}
					}else {
						//if they are too close, then no nodes can go there, 
						//even if it's ok with the other nodes
						placed[x][y]=false;
					}

				}

			}
		}
		
	}
	return placed;
}
	private Generation makeNewGen(skeleton design, int index2) {
		int gap=globalSize-design.size;
		// we start by setting up a place to store the new generated skeletons
		Generation newGen= new Generation();
		
		Boolean[][] placed=makePlaced(design,index2);
		//times[2]+=System.nanoTime();
		//times[3]+=System.nanoTime();

		int x=0;
		for(Boolean[] row:placed) {
			int y= 0;
			for(Boolean ok:row) {
				// if we can place a node in a spot, make an new skeleton that adds it there
				if(ok==null||ok) {

					skeleton newDesign=new skeleton(design);
					node n= new node(leafNodes.get(index2));
					n.setX(x-gap);
					n.setY(y-gap);
					if(!n.isFixedToSymmetryLine||n.getX()==0) {
						if(!this.hasSymetry||n.getX()>=0) {
							newDesign.add(n,index2);
							//newDesign.resize();
							if(newDesign.size<globalSize&&newDesign.checkConditions()) {

								newGen.add(newDesign);

							}
							//}
						}
					}
				}
				y++;
			}
			x++;
		}
		;
		//times[4]+=System.nanoTime();
		// return the list of possible skeletons, with the new node added
		return newGen;
	}

	public void optimize() {
		upperBound=globalSize;
		lowerBound=0;
		globalSize=(int)(lowerBound+(double)(upperBound-lowerBound)/2);
		//this.globalSize=1;
		//globalSmallSize=1;
		//first we add the first node
		//it doesn't matter where it goes, since all the locations are relative
		max_checked=100000000/100;
//max_checked=Integer.MAX_VALUE;
		this.checked_dead=0;
		long start=System.currentTimeMillis();
		long prev=start;
		int loop=0;

		//times= new long[5];
		depth=this.leafNodes.size()+1;
		total=0;

		int oldSize=globalSize;
		int oldSmallSize=globalSmallSize;
		//boolean finalSteps=false;
		while(true) {
			improved=false;
			//int oldScore=globalScore;
			System.out.println("up, low: "+upperBound+","+lowerBound);
			System.out.println("starting at"+globalSize);
			for(int l=0;l<leafNodes.size();l++) {
				improved=false;
				this.optimizeDF(1, generateFirst());
				//System.out.println("looping"+l);
				//upperBound=globalSize;
				// we now have to check other node orders, as they may contain better solutions
				
				node first=leafNodes.get(0);
				leafNodes.remove(first);
				leafNodes.add(first);
				if(improved) {
					break;
				}
			}
			//this.optimizeDF(1, generateFirst());

			if(!improved) {
				lowerBound=globalSize;
				
				System.out.println("not improved, global Size is: "+globalSize);
				//if(finalSteps) {
				//globalSize++;
				//globalSmallSize++;
				//}
			}else {
				upperBound=globalSize;
				System.out.println(globalBest.size+", "+globalBest.smallSize+": "+oldSize+", "+oldSmallSize+"ignored "+0);

			}
			if(globalBest!=null&&!improved) {
				System.out.println("looping"+loop);
				//upperBound=globalSize;
				// we now have to check other node orders, as they may contain better solutions
				loop++;
				node first=leafNodes.get(0);
				leafNodes.remove(first);
				leafNodes.add(first);
			
				//Collections.shuffle(leafNodes);
			}else {
				loop=0;
			}
			this.checked_dead=0;
			//checked_live=1;
			if(loop>p.nodes.size()) {
				break;
			}
			end=false;
			int gap=upperBound-lowerBound;
			System.out.println("up, low; gap "+upperBound+","+lowerBound+gap);
			
			if(gap>1) {
			globalSize=(int)(lowerBound+(double)(upperBound-lowerBound)/2);
			}else {
				break;
			}
		}
		/*	for( int i=lowerBound-1;i<upperBound+1;i++) {
				globalSize=i;
				System.out.println(i);
				//this.optimizeDF(1, generateFirst());
				for(int l=0;l<p.nodes.size();l++) {
					improved=false;
					this.optimizeDF(1, generateFirst());
					//System.out.println("looping"+l);
					//upperBound=globalSize;
					// we now have to check other node orders, as they may contain better solutions
					
					node first=leafNodes.get(0);
					leafNodes.remove(first);
					leafNodes.add(first);
					if(improved) {
						break;
					}
				}
					if(improved) {
						break;
					}
				
			}*/
			System.out.println("time is: "+Long.toString(System.currentTimeMillis()-prev));
			prev=System.currentTimeMillis();
		
		//System.out.println(this.globalChecked.size());
		long time=(System.currentTimeMillis()-start)/1000;
		int mins=(int)time/(60);
		int secs=(int)(time %60);
		System.out.println("finished in: "+mins+" min"+ secs+" seconds");

		// step1=(times[1]-times[0])/total;
		// step2=(times[2]-times[1])/total;
		// step3=(times[3]-times[2])/total;
		// step4=(times[4]-times[3])/total;
		//System.out.println("1 "+step1+" 2 "+step2+" 3 "+step3+" 4 "+step4);
		System.out.println(p.nodes.size());


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

			if(n.isLeaf) {
				for(node M:top.nodes) {
					if(M!=null) {
					if(n.ID==M.ID) {
						int newX=M.getX();
						int newY=M.getY();
						n.setX(newX);
						n.setY(newY);
					}
					}
				}
			}

		}

		oldp.refreshNodes();
		oldp.shrink();
		return oldp;
	}

	public skeleton generateFirst() {
		skeleton design= new skeleton(isFixedRatio,p.hasSymmetry, ratioX_Y,this.p.maxSize);
		node n= new node(leafNodes.get(0));
		n.setY(0);
		n.setX(0);
		design.add(n,0);
		return design;
	}

	//this lets the optimization run in the background.
	//for big designs, it can take a few minutes.
	@Override
	protected paper doInBackground() throws Exception {
		//nearestNode();

		this.optimize();
		System.out.println("was overlaping:"+p.hasOverlap());
		System.out.println(p.nodes.size());
		System.out.println(leafNodes.size());
		return this.getPaper(p);
	}
}