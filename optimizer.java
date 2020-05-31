package origamiProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class optimizer{
	paper p;
	ArrayList<child> newGen;
	ArrayList<child>oldGen;
	Random r;
	public static final int POP_SIZE=50;
	public static final int NUM_Gen=1;
public optimizer(paper p) {
	this.p=new paper(p);
	this.p.getTreeDistances();
	r= new Random();
	//display();
}
public paper optimizeWithoutCuts() {
	createStarter();
	int i=0;
	while(i<NUM_Gen) {
		System.out.println("generation:"+i+" , population"+oldGen.size()+" ,size: "+oldGen.get(0).p.getSize());
		nextGen();
		killOverlaping();
		if(newGen.size()<POP_SIZE) {
			System.out.println("a problem occurred");
			break;
		}
		sortBest();
		oldGen=newGen;
		i++;
	}
	sortBest();
	p=oldGen.get(0).p;
	System.out.println(hasOverlap(p)+"ok,,,");
	//display();
	return p;
}
private void killOverlaping() {
	ArrayList<child> toRemove= new ArrayList<child>();
	for(child ch:newGen) {
		paper gen= ch.p;
		if(hasOverlap(gen)) {
			toRemove.add(ch);
		}
	}
	newGen.removeAll(toRemove);
}
private void sortBest() {
	// TODO Auto-generated method stub
	//System.out.println(newGen);
	Collections.sort(newGen);
	
}

private void nextGen() {
		newGen= new ArrayList<child>();
		double total_breeding=0;
		for(int i=0;i<POP_SIZE;i++) {
			System.out.println(i+": "+oldGen.get(i).p.toString());
			//double chance=1;
			double chance=Math.pow(i+1, -1);
			oldGen.get(i).breeding_number=chance+total_breeding;
			//System.out.println("i"+i+",chance "+chance+" , number:"+oldGen.get(i).breeding_number);
			total_breeding+=chance;
		}
		for(int i=0;i<POP_SIZE;i++) {
			newGen.add(newChild(newParent(total_breeding),newParent(total_breeding)));
		}
		
		}
private child newChild(child newParent1, child newParent2) {
	child newCh= new child(newParent1,p);
	for(int i=0;i<newParent1.size;i++) {
		for(int j=0;j<newParent1.num_steps;j++) {
			if(Math.random()<.5) {
				newCh.Xsteps[i][j]=newParent2.Xsteps[i][j];
			}
			if(Math.random()<.5) {
				newCh.Ysteps[i][j]=newParent2.Ysteps[i][j];
			}
		}
	}
	return newCh;
}
private child newParent(double total) {
	double rand= Math.random()*total;
	//System.out.println(rand);
	for(child ch:oldGen) {
		if(ch.breeding_number>rand) {
			//System.out.println(true);
			return(ch);
		}
	}
	return null;
	//return new child(oldGen.get(0).p);
}
private void createStarter() {
	// TODO Auto-generated method stub
	oldGen= new ArrayList<child>();
	newGen= new ArrayList<child>();
	p.shrink();
	/*while(hasOverlap(p)) {
		for(node n:p.nodes) {
			n.forceX(n.getX()*2);
			n.forceY(n.getY()*2);
		}
	}*/
	System.out.println(p.distances);
	System.out.print(hasOverlap(p));
	p.shrink();
	for(int i=0;i<POP_SIZE;i++) {
		child newCh= new child(p);
	oldGen.add(newCh);
	}
}

private boolean overlaps(node one, node two, paper test) {
	if(one.equals(two)) {
		return false;
	}
	return test.getLongestLegWithoutCuts(one, two)<test.distances.get(one).get(two);
}
private boolean hasOverlap(paper test) {
	for(node N1:test.nodes) {
		for(node N2:test.nodes) {
			if(overlaps(N1,N2, test)) {
				return true;
			}
		}
	}
	return false;
}
}
