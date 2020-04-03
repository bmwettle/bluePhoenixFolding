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
	while(true) {
		System.out.println("next step");
		nextGen();
		//System.out.println(newGen.toString());
		for(paper one:newGen) {
			System.out.println(one.toText());
			System.out.println();
		}
		killOverlaping();
	
		//System.out.println(newGen.toString());
		if(newGen.size()==0) {
			System.out.println("no more options");
			break;
		}

		sortBest();
		System.out.println(newGen.toString());
		killWeak();
		System.out.println(newGen.toString());
		if(newGen.get(0).compareTo(oldGen.get(0))<=0) {
			System.out.println("ta da");
			break;
		}
		oldGen=newGen;
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
	System.out.println(newGen);
	Collections.sort(newGen);
	Collections.reverse(newGen);
	System.out.println(newGen);
}
private void killWeak() {
	// TODO Auto-generated method stub
	int survive= Math.min(200, newGen.size());
	System.out.println(survive);
	newGen= new ArrayList<paper>(newGen.subList(0, survive));
}

private void nextGen() {
	newGen= new ArrayList<paper>();
	for(paper test:oldGen) {
		ArrayList<paper> children = new ArrayList<paper>();
		for(int i=0;i<test.nodes.size();i++) {
			paper test1= new paper(test);
			node newNode= new node(test.nodes.get(i));
			test1.nodes.set(i, newNode);
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
		for(paper child:children) {
			System.out.println("ok now ");
			System.out.println(child.nodes.get(1));
			System.out.println(child.distances.get(child.nodes.get(1)).get(child.nodes.get(0)));
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
	System.out.println("dist"+test.distances+"one is"+one+"two is "+two);
	System.out.println(test.distances.get(one)==null);
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
