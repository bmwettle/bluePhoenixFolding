import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Arrays;
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
	boolean isFixedRatio;
	boolean hasSymetry;
	//double ratioX_Y=1;
	//Generation[] tree;
	//int min_size;
	int globalSize;
	int globalSmallSize;
	int globalScore;
	HashMap<UID,Integer> minDist;
//	int[] parents;
	//int[] children;
	//int[] finished;
	//int[] forced;
	long[] times;
	int total;
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
	HashMap<UID,HashMap<UID,Integer>> distances;
	//[][] distances;
	paper p;
	int size;
	int baseBuffer;
	//this sets up the layout, and make it ready to optimize
	public layout(paper p, int base_buffer,int max_checked) {
		newline=System.lineSeparator();
		this.max_checked=max_checked;
		this.baseBuffer=base_buffer;
		this.p=p;
		this.hasSymetry=p.hasSymmetry;
		end=false;
		//outputLog="starting"+System.lineSeparator()+"ok";
		addLeafNodes();
		size= this.leafNodes.size();
		isFixedRatio=p.isFixedRatio;
		//ratioX_Y=p.ratioX_Y;
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
		distances= new HashMap<UID,HashMap<UID,Integer>>();
		for (node n:p.nodes) {
			HashMap<UID,Integer> ndist= new HashMap<UID,Integer>();
			for( node m:p.nodes) {

				if(n.isLeaf&&m.isLeaf) {
					int dist= p.distances.get(n).get(m);
					ndist.put(m.ID, dist);
				}
			}
			distances.put(n.ID,ndist );
		}
	}

	private void addLeafNodes() {
		leafNodes= new ArrayList<node>();

		minDist=new HashMap<UID,Integer>();
		for(node n:p.nodes) {
			if(n.isLeaf) {
				leafNodes.add(n);
				if(n.isMirrored) {
					int dist=Integer.MAX_VALUE;
					for(node m:p.nodes) {
						if(m.isFixedToSymmetryLine) {
							int d=p.distances.get(m).get(n);
							if(d<dist){
								dist=d;
							}
							
						}
					}
					if(dist==Integer.MAX_VALUE) {
						dist=0;
					}
					minDist.put(n.ID,dist);
				}else {
					minDist.put(n.ID,0);
				}
			}
		}
		}

	/**
	 *this function handles the core part of the program
	 * it takes given paper, and optimizes it to the best shape and size,
	 * subject to the constraints given by the paper.
	 */
	private int optimizeDF(int index2, skeleton parent) {
		
		//parents[index2]++;
		checked_dead++;
		//totalDensity[index2].add((double)parent.size/(double)globalSize-(double)index2/(double)size);
		//outputLog+=checked_live+", "+checked_dead+newline;
		if(!end) {
		if(checked_dead<max_checked) {
			total++;
			times[0]+=System.nanoTime();
		Generation newGen= makeNewGen(parent,index2);
		
		if(newGen!=null&&newGen.size()>=1) {
			//children[index2]+=newGen.size();
			//sortNewGen(newGen);
			//checked_live+=newGen.size();
			skeleton myBest=newGen.get(0);
			//System.out.println(myBest+";; "+myBest.size+", "+myBest.smallSize+": "+myBest.score+"in; "+index2);
			index2++;
			if(index2==size) {
				//for(skeleton my:newGen) {
				
					//System.out.println(my.size+", "+my.smallSize+": "+my.score);
			//}
				
				int mySize=myBest.getSize();
				
				//System.out.println("checking best"+index2+","+newGen.size());
				if(mySize<globalSize||(mySize==globalSize&&(myBest.smallSize<globalSmallSize))) {
				//if(mySize<globalSize) {
					//System.out.println("better");
					//System.out.println("xmin"+myBest.xmin+ " xmax"+myBest.xmax);

					//System.out.println("ymin"+myBest.ymin+ " ymax"+myBest.ymax);
					//System.out.println(myBest);

					//System.out.println(System.currentTimeMillis()/1000);
					//outputLog+="updating best"+index2+": "+myBest.getSize()+",>" +myBest.score+", "+". "+leafNodes.size()+System.lineSeparator();
					//outputLog+="checked live"+checked_live+"nodes"+System.lineSeparator();
					//outputLog+=newGen.get(0)+System.lineSeparator();
					globalBest=myBest;
					globalSize=myBest.getSize();
					globalScore=myBest.score;
					globalSmallSize=myBest.smallSize;
					end=true;
					//System.out.println("better");
					//System.out.println(myBest.size+", "+myBest.smallSize+": "+myBest.score);
					//System.out.println();
					//globalScore=myBest.score;
				//}
				}

				return -10;
			}else {
				int count=0;
				for(skeleton design:newGen) {
					//int designSize=design.getSize();
					//int designScore=design.score;
					//if(designSize<globalSize||(designSize==globalSize&&(design.smallSize<globalSmallSize))) {
					//if(designSize<globalSize) {
						
							//outputLog+="<"+index2+">"+design+"size"+design.size+"ch"+checked+" time is"+System.currentTimeMillis()+System.lineSeparator();
						//if(count<200) {
						count+=optimizeDF(index2,design);
						//}else{
							//break;
							//forced[index2-1]++;
						//}
						//finished[index2]++;
					//}else {
					//	break;
						//}
					design=null;
				}
				newGen=null;
				return (int) (count*.5);
				
			}
		}else {
			//outputLog+="dead end"+System.lineSeparator();
			//checked_dead++;
			return 1;
		}
		}else{
			//outputLog+="checked too many"+System.lineSeparator();
			//forced[index2]++;
		}
		}
		return 0;
	}
	private void sortNewGen(Generation newGen) {
		Collections.sort(newGen);
	}



	private Generation makeNewGen(skeleton design, int index2) {
		int gap=globalSize-design.size;
		
		
		//outputLog+="start time is"+System.currentTimeMillis();
		// we start by setting up a place to store the new generated skeletons
		Generation newGen= new Generation();
		Boolean[][] placed= new Boolean[design.size+2*gap][design.size+2*gap];
		
		times[1]+=System.nanoTime();
		for(node m:design.nodes) {
			if(m!=null) {
			int radius= this.distances.get(m.ID).get(leafNodes.get(index2).ID);
			int topx=Math.min(radius+baseBuffer,design.size-m.getX()+gap-1);
			int lowx=Math.max(-radius,-gap-m.getX());
			for(int i=topx;i>=lowx;i--) {
				int x= m.getX()+i+gap;
				//if(x>=-gap) {
				//	if(x<design.size+gap) {
					int topy=Math.min(radius+baseBuffer,design.size-m.getY()+gap-1);
					int lowy=Math.max(-radius,-gap-m.getY());
				for(int j=topy;j>=lowy;j--) {
					int y= m.getY()+j+gap;
					//if(y>=-gap) {
						//if(y<design.size+2*gap) {
							if(Math.abs(i)>=radius||Math.abs(j)>=radius) {
							if(placed[x][y]==null) {
						placed[x][y]=true;
							}
							}else {
								placed[x][y]=false;
							}
						//}
					//}else {break;}
				}
				//}
				//}else {break;}
			}
			}
			else {break;}
		}
		times[2]+=System.nanoTime();
		//outputLog+="mid1 time is"+System.currentTimeMillis();
		/*for(node m:design.nodes) {
			if(m!=null) {
			int radius= this.distances.get(m.ID).get(leafNodes.get(index2).ID)-1;
			for(int i=radius;i>=-radius;i--) {
				int x= m.getX()+i;
				if(x>=-gap&&x<design.size+gap) {
				for(int j=radius;j>=-radius;j--) {
					int y= m.getY()-0+j;
					if(y>=-gap&&y<design.size+gap) {
					//	if(placed[x+gap][y+gap]) {
						placed[x+gap][y+gap]=false;
						
					//	}
						
					}
				}
				}
			}
			}else {break;}
		}*/
		
		times[3]+=System.nanoTime();
		//outputLog+="mid2 time is"+System.currentTimeMillis();
		int x=0;
		for(Boolean[] row:placed) {
			int y= 0;
			for(Boolean ok:row) {
				//System.out.print(blocked);
				if(ok==null||ok) {
					//outputLog+="copy1 time is"+System.currentTimeMillis();
					skeleton newDesign=new skeleton(design);
					
					//outputLog+="copy2 time is"+System.currentTimeMillis();
					node n= new node(leafNodes.get(index2));
					n.setX(x-gap);
					n.setY(y-gap);
					if(!n.isFixedToSymmetryLine||n.getX()==0) {
						if(!this.hasSymetry||n.getX()>=this.minDist.get(n.ID)) {
						//	newDesign.score=design.score+Math.abs(n.getX())+Math.abs(n.getY());
							//if((newDesign.score)<globalScore) {
							newDesign.add(n,index2);
							newDesign.resize();
							if(newDesign.size<globalSize&&newDesign.checkConditions()) {
								
							newGen.add(newDesign);
								
							}
							//}
							}
						}
					}
				y++;
			}
			//System.out.println();
			x++;
		}

		//outputLog+="end time is"+System.currentTimeMillis();
		times[4]+=System.nanoTime();
		return newGen;
	}

	public void optimize() {
		//first we add the first node
		//it doesn't matter where it goes, since all the locations are relative
		max_checked=100000;
		this.checked_dead=0;
		//checked_live=1;
		System.out.println(System.currentTimeMillis());
		int loop=0;
		//skeleton sk31=null;
		//skeleton sk30=null;
		//skeleton sk29=null;
		//skeleton sk28=null;
		//tree= new Generation[size];
//Collections.shuffle(leafNodes);
		times= new long[5];

		total=0;
		while(true) {
			int oldSize=globalSize;
			int oldSmallSize=globalSmallSize;
			int oldScore=globalScore;
			//parents= new int[size];
			//children=new int[size];
			//forced= new int[size];
			//finished=new int[size];
			
			//totalDensity=new ArrayList[size];
			//for(int i=0;i<size;i++) {
				//totalDensity[i]=new ArrayList<Double>();
			//}
			try {
				this.optimizeDF(1, generateFirst());
			}catch(Exception e){
				//e.printStackTrace();
				globalSize++;
			}
		

		
		//System.out.println("1 "+step1+" 2 "+step2+" 3 "+step3+" 4 "+step4);
		
		//System.out.println(globalBest);
		//System.out.println(checked_dead);
		//System.out.println(System.currentTimeMillis());
		
		//if(checked_dead>=max_checked) {
			//System.out.println("forced end");
			//break;
		//}
		if(globalBest==null) {
			globalSize++;
		}else {
			System.out.println(globalBest.size+", "+globalBest.smallSize+": "+oldSize+", "+oldSmallSize);
			
		
		if(globalBest!=null&&!end) {
			//System.out.println("best found");
			//break;

			loop++;
			node first=leafNodes.get(0);
			//node second=leafNodes.get(1);
			leafNodes.remove(first);
			//leafNodes.remove(second);

			//leafNodes.add(second);
			leafNodes.add(first);
			//Collections.reverse(leafNodes);
			Collections.shuffle(leafNodes);
		}else {
			loop=0;
		}
		this.checked_dead=0;
		//checked_live=1;
		if(loop>size) {
			break;
		}
		}
end=false;

		}
		System.out.println(System.currentTimeMillis());
		//System.out.println("index: parents, children; finished, forced, density: ");
		//for(int i=0;i<size;i++) {
			//System.out.print(i+": "+parents[i]);
			//System.out.print(", "+children[i]);

			//System.out.print("; "+finished[i]);

			//System.out.print(", "+forced[i]);
			//if(parents[i]!=0) {
//			System.out.println("> "+totalDensity[i]);
		//	}
			//System.out.println();
		//}
		long step1=(times[1]-times[0])/total;
		long step2=(times[2]-times[1])/total;
		long step3=(times[3]-times[2])/total;
		long step4=(times[4]-times[3])/total;
		System.out.println("1 "+step1+" 2 "+step2+" 3 "+step3+" 4 "+step4);
		//System.out.println(sk31);
		//System.out.println(sk30);
		//System.out.println(sk29);
		//System.out.println(sk28);
		
		//outputLog="";
	//	for(int i=0;i<size;i++) {
			
		//	double max=0;
			//double min=Double.MAX_VALUE;
			//double ave=0;
		//	for(double d:totalDensity[i]) {
			//	ave+=d;
				//if(d>max) {
					//max=d;
				//}
				//if(d<min) {
					//min=d;
				//}
			//}
			//ave=ave/totalDensity[i].size();
			//outputLog+=i+" max "+max+" min "+min+"ave"+ave+System.lineSeparator();
			
		//}
		/*try {
			//outputLog+="ok, finished optimazation";
		      FileWriter myWriter = new FileWriter("outputLog.txt");
		      myWriter.write(outputLog);
		      myWriter.close();
		      System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }*/

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
					if(n.ID==M.ID) {

						n.setX(M.getX());
						n.setY(M.getY());
						//System.out.println(n.ID+" , "+M.ID+" : "+M.getX()+" ' "+M.getY());
						}
				}
				}
		}
		oldp.refreshNodes();
		oldp.shrink();
		return oldp;
	}
	private void nearestNode() {
		int minDist=Integer.MAX_VALUE;
		ArrayList<node> bestNodes=new ArrayList<node>();
		for(int i=0;i<leafNodes.size();i++) {
		ArrayList<node> settled= new ArrayList<node>();
		node current=leafNodes.get(i);
		int dist=0;
		while(settled.size()<leafNodes.size()) {
			int min=Integer.MAX_VALUE;
			node best=null;
			for(node n:leafNodes) {
				if(!settled.contains(n)&&n.ID!=current.ID ){
					int gap=0;
					for(node m:settled) {
						gap+=this.distances.get(m.ID).get(n.ID);
					}
					if(gap<min) {
						best=n;
						min=gap;
						
					}
				}
			}
			dist+=min;
			settled.add(current);
			current=best;
		}
		if(dist<minDist) {
		bestNodes=settled;
		}
		}
		this.leafNodes=bestNodes;
	}
	public skeleton generateFirst() {
		//System.out.println("check first");
		skeleton design= new skeleton(isFixedRatio,p.hasSymmetry,this.leafNodes.size());
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
		return this.getPaper(p);
	}
}