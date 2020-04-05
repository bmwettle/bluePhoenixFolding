package origamiProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class optimizer{
	paper p;
	ArrayList<paper> newGen;
	ArrayList<paper>oldGen;
	Random r;
public optimizer(paper p) {
	this.p=new paper(p);
	this.p.getTreeDistances();
	r= new Random();
	//display();
}
public paper optimizeWithoutCuts() {
	
	
	createStarter();
	int i=0;
	while(i<2000) {
		System.out.println("next step");
		nextGen();
	
		
		killOverlaping();
	
		if(newGen.size()==0) {
			System.out.println("no more options");
			break;
		}

		sortBest();
	//	System.out.println(newGen.toString());
		killWeak();
		//System.out.println(newGen.toString());
		
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
	System.out.println("before"+newGen);

}
private void killWeak() {
	for(paper p:newGen) {
		System.out.println(p.width+" : "+p.height);
	}

	int survive=Math.min(20, newGen.size()-1);
	newGen= new ArrayList<paper>(newGen.subList(0,survive));
	for(paper p:newGen) {
		System.out.println(p.width+" , "+p.height);
	}
}

private void nextGen() {
	newGen= new ArrayList<paper>();
	for(paper test:oldGen) {
		ArrayList<paper> children = new ArrayList<paper>();
		for(int i=0;i<test.nodes.size();i++) {
			paper test1= new paper(test);
			
			test1.nodes.get(i).moveX(1);
			test1.shrink();
			children.add(test1);
			
			paper test2= new paper(test);
			test2.nodes.get(i).moveX(-1);
			test2.shrink();
			children.add(test2);
			
			paper test3= new paper(test);
			test3.nodes.get(i).moveY(1);
			test3.shrink();
			children.add(test1);
			paper test4= new paper(test);
			test4.nodes.get(i).moveY(-1);
			test4.shrink();
			children.add(test4);
		
		}
	
		newGen.addAll(children);
	}
	System.out.println(newGen);
}
private void createStarter() {
	// TODO Auto-generated method stub
	oldGen= new ArrayList<paper>();
	newGen= new ArrayList<paper>();
	p.shrink();
	paper starter= new paper(p);
	System.out.println(starter.nodes.get(0)==p.nodes.get(0));
	while(hasOverlap(starter)) {
		System.out.println("expanding");
		for(node n:starter.nodes){
			n.x*=2;
			n.y*=2;
		}
		starter.shrink();
	}
	oldGen.add(starter);
	System.out.println("starter is"+starter.toString());
	System.out.println(starter.toText());
	System.out.print(hasOverlap(starter));
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
