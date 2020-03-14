package origamiProject;
import java.util.Iterator;
import java.util.Random;

public class optimizer {
	paper[] myPapers;
	paper bestPaper;
	Random r;
	final static double social_weight=1.5;
	final static double congnative_weight=1.5;
	final static double inertia_weight=.72;
	final static int num_papers=10;
	//stores the best placements found so far for all papers
	int[] globalBestx;
	int[]globalBesty;
	//stores the best placemnts found for each paper
	int[][]bestx;
	int[][]besty;
	int numNodes;
public optimizer(paper p) {
	myPapers= new paper[num_papers];
	for(int i=0;i<num_papers;i++) {
		myPapers[i]=new paper(p);
	}
	numNodes= p.nodes.size();
	//System.out.println(myPapers.toString());
	r= new Random();
	r.setSeed(0);
}

public paper optimizeWithoutCuts() {
	//paper end= new paper();
	
	initialize();
	for(int i=0;i<30;i++) {
		updateBest();
		updateVs();
		moveNodes();
	}
	paper bestPaper=finish();
	System.out.println("done"+bestPaper);
	return bestPaper;
}

private void updateBest() {
	// TODO Auto-generated method stub
	getGlobalBest();
	getLocalBest();
}
private void getLocalBest() {
	// TODO Auto-generated method stub
	for(int i=0;i<num_papers;i++) {
		paper p=myPapers[i];
		int[] bounds= getBounds(p);
		int[]bestBounds= new int[] {p.bestXBounds,p.bestYBounds};
		if(isBetterBounds(bounds,bestBounds)) {
			p.bestXBounds=bounds[0];
			p.bestYBounds=bounds[0];
			bestx[i]=p.getXcords();
			besty[i]=p.getYcords();
		}
	}
}
private void updateVs() {
	System.out.print("ok");
	// TODO Auto-generated method stub
	for(int i=0;i<num_papers;i++) {
		paper p=myPapers[i];
		for(int j=0;j<numNodes;j++) {
			node n=p.nodes.get(j);
			n.Vx=inertia_weight*n.Vx+congnative_weight*(bestx[i][j]-n.x)+social_weight*(globalBestx[j]-n.x);
			n.Vy=inertia_weight*n.Vy+congnative_weight*(bestx[i][j]-n.y)+social_weight*(globalBesty[j]-n.y);
		}
	}
	
}
private paper finish() {
	paper finalPaper= new paper(bestPaper);
	for(node n:finalPaper.nodes) {
		n.x-=getBounds(finalPaper)[0];
		n.y-=getBounds(finalPaper)[1];
	}
	return finalPaper;
	
}
private void moveNodes() {
	for(paper p:myPapers) {
		for(node n:p.nodes) {
			n.x+=n.Vx;
			n.y+=n.Vy;
		}
		
	}
}
private void initialize() {
	globalBestx = new int[numNodes];
	globalBesty=new int[numNodes];
	
	bestx=new int[num_papers][numNodes];
	besty=new int[num_papers][numNodes];
	for(int i=0;i<num_papers;i++) {
		paper p= myPapers[i];
		for(node n:p.nodes) {
			
			n.setX(r.nextInt(p.width));
			n.setY(r.nextInt(p.height));
			n.Vx=0;
			n.Vy=0;
			
		}
		
		bestx[i]=p.getXcords();
		besty[i]=p.getYcords();
		int[] bestBounds= getBounds(p);
		p.bestXBounds=bestBounds[2]-bestBounds[0];
		p.bestYBounds=bestBounds[3]-bestBounds[1];
	}
	getGlobalBest();
}
public int[] getBounds(paper p) {
	int xmax=0;
	int ymax=0;
	int ymin=Integer.MAX_VALUE;
	int xmin= Integer.MAX_VALUE;
	for(node n:p.nodes) {
		if( n.x<xmin){
			xmin=n.x;
		}
		if( n.x>xmax){
			xmax=n.x;
		}
		if( n.y<xmin){
			ymin=n.y;
		}
		if( n.y>xmin){
			ymin=n.y;
		}
	}
	return new int[] {xmin,ymin,xmax,ymax};
}
public void getGlobalBest(){
	int []bestBounds=new int[] {Integer.MAX_VALUE,Integer.MAX_VALUE};
	int bestIndex=0;
	for(int i=0;i<num_papers;i++) {
		int []bounds= getBounds(myPapers[i]);
		if(isBetterBounds(bounds,bestBounds)) {
			bestBounds=bounds;
			bestIndex=i;
		}
	}
	bestPaper=myPapers[bestIndex];
	globalBestx=bestPaper.getXcords();
	globalBesty=bestPaper.getYcords();
}
private boolean isBetterBounds(int[] test, int[] best) {
	if(test[0]<best[0]&&test[1]<best[1]) {
		return true;
	}
	// TODO Auto-generated method stub
	return false;
}
public boolean hasOverlap(paper myPaper) {
	
	for(node one:myPaper.nodes) {
		for (node two:myPaper.nodes){
			if(one.equals(two)) {
				
			}else {
				if( isOverlap(one,two,myPaper) ){
					System.out.println(one.toString()+","+two.toString()+":"+myPaper.getLongestLegWithoutCuts(one, two)+":"+myPaper.distances.get(one).get(two));
					return true;
				}else {
					System.out.println("ok");
				}
			}
		}
	}
	return false;
}
public boolean isOverlap(node one, node two, paper myPaper) {
	return myPaper.distances.get(one).get(two)>myPaper.getLongestLegWithoutCuts(one, two);
}
}
