package origamiProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class optimizer{
	paper p;
	ArrayList<paper> newGen;
	ArrayList<paper>oldGen;
	Random r;
	public static final int POP_SIZE=1000;
	public static final int NUM_CHILDREN=50;
	public static final int NUM_Gen=20;
	public static final double CHANCE_MUTATE=.8;
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
		nextGen();
		killOverlaping();
		if(newGen.size()<POP_SIZE) {
			System.out.println("a problem occurred");
			break;
		}
		sortBest();
		killWeak();
		oldGen=newGen;
		i++;
	}
	sortBest();
	p=oldGen.get(0);
	System.out.print(hasOverlap(p));
	//display();
	return p;
}
private void killOverlaping() {
	ArrayList<paper> toRemove= new ArrayList<paper>();
	for(paper gen:newGen) {
		if(hasOverlap(gen)) {
			toRemove.add(gen);
		}
	}
	newGen.removeAll(toRemove);
}
private void sortBest() {
	// TODO Auto-generated method stub
	//System.out.println(newGen);
	Collections.sort(newGen);
}
private void killWeak() {	
	System.out.println(newGen.size()+",");
	newGen= new ArrayList<paper>(newGen.subList(0,POP_SIZE));
}
private void nextGen() {
	newGen= new ArrayList<paper>();
		for( int i=0;i<POP_SIZE;i++) {
			paper Parent=oldGen.get(i);
			for(int j=0;j<NUM_CHILDREN;j++) {
				paper child= new paper(Parent);
				for(node n:child.nodes) {
					double rand= Math.random();
					if(rand>CHANCE_MUTATE) {
						if(Math.random()>1-(CHANCE_MUTATE/2)) {
							n.x+=1;
						}else {
							n.x-=1;
						}
					}
					rand= Math.random();
					if(rand>CHANCE_MUTATE) {
						if(Math.random()>1-(CHANCE_MUTATE/2)) {
							n.y+=1;
						}else {
							n.y-=1;
						}
					}
				}
				child.shrink();
				newGen.add(child);
				}
			}
		}
private void createStarter() {
	// TODO Auto-generated method stub
	oldGen= new ArrayList<paper>();
	newGen= new ArrayList<paper>();
	p.shrink();
	paper starter= new paper(p);
	starter.shrink();
	for(int i=0;i<POP_SIZE;i++) {
		
	oldGen.add(starter);
	}
	System.out.print(starter.distances.toString());
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
