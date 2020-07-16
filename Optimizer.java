package origamiProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Optimizer{
	paper p;
	paper best;
	ArrayList<paper> newGen;
	ArrayList<paper>oldGen;
	Random r;
	public static final int MAX_POP_SIZE=5000;
	public static final int NUM_Gen=10;
public Optimizer(paper p) {
	this.p=new paper(p);
	this.p.getTreeDistances();
	r= new Random();
	//display();
}
public paper optimize() {
	createStarter();
	int i=0;
	while(i<NUM_Gen) {
		System.out.println("generation:"+i+" , population"+oldGen.size()+" ,size: "+oldGen.get(0).getSize());
		nextGen();
		killOverlaping();
		sortBest();
		oldGen=newGen;
		best=newGen.get(0);
		i++;
	}
	sortBest();
	p=oldGen.get(0);
	System.out.println(p.hasOverlap()+"ok,,,");
	//display();
	return p;
}
private void killOverlaping() {
	ArrayList<paper> toRemove= new ArrayList<paper>();
	for(paper ch:newGen) {
		paper gen= ch;
		if(gen.hasOverlap()) {
			toRemove.add(ch);
		}
	}
	newGen.removeAll(toRemove);
}
private void sortBest() {
	// TODO Auto-generated method stub
	for(paper g:newGen) {
		g.shrink();
		System.out.print("size"+g.getSize());
	}

	Collections.sort(newGen);
	if(newGen.size()>MAX_POP_SIZE) {
	newGen= new ArrayList<paper>(newGen.subList(0, MAX_POP_SIZE));
	}
	System.out.println("next sort");
	//newGen= new ArrayList<paper>(newGen.subList(0, POP_SIZE));
	for(paper g:newGen) {
		g.shrink();
		System.out.print("size"+g.getSize());
	}
	System.out.println("next sort");
	
	//Collections.reverse(newGen);
}

private void nextGen() {
		newGen= new ArrayList<paper>();
		newGen.add(best);
		for( int i=0;i<p.nodes.size();i++) {
			//if(best.isLeaf(best.nodes.get(i))) {
			ArrayList<paper> toAdd= new ArrayList<paper>();
			for(paper search:newGen) {
				for(int j=-1;j<=1;j++) {
					for(int k=-1;k<=1;k++) {
						paper e= new paper(search);
						node m= e.nodes.get(i);
						m.moveX(j);
						m.moveY(k);
						if(!e.hasOverlap(m)) {
							toAdd.add(e);						
						}
					}
				}
			}
			System.out.println(" adding new"+toAdd.size());
			newGen=toAdd;
			sortBest();
		//}
		}
		System.out.println("new gen size is: "+newGen.size());
		}
private void createStarter() {
	// TODO Auto-generated method stub
	oldGen= new ArrayList<paper>();
	newGen= new ArrayList<paper>();
	p.shrink();
	/*while(hasOverlap(p)) {
		for(node n:p.nodes) {
			n.forceX(n.getX()*2);
			n.forceY(n.getY()*2);
		}
	}*/
	System.out.println(p.distances);
	System.out.print(p.hasOverlap());
	p.shrink();
	oldGen.add(p);
	best=p;
	
}


}