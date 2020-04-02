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
	System.out.print(p.distances);
	createStarter();
	while(true) {
		System.out.println("next step");
		nextGen();
		killOverlaping();
		if(newGen.size()==0) {
			System.out.println("ta da");
			break;
		}

		sortBest();
		System.out.println(newGen.toString());
		killWeak();
		System.out.println(newGen.toString());
		oldGen=newGen;
	}
	sortBest();
	p=oldGen.get(0);
	display();
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
	Collections.sort(newGen);
	Collections.reverse(newGen);
}
private void killWeak() {
	// TODO Auto-generated method stub
	int survive= Math.min(20, newGen.size());
	newGen= new ArrayList<paper>(newGen.subList(0, survive));
}
private boolean isFitter(paper a, paper b) {
	return getFittnes(a)>=getFittnes(b);
}
private int getFittnes(paper a) {
	// TODO Auto-generated method stub
	if(hasOverlap(a)) {
		return 0;
	}
	return a.getSize();
}
private void nextGen() {
	for(paper test:oldGen) {
		ArrayList<paper> children = new ArrayList<paper>();
		for(node a:p.nodes) {
			for(node b:p.nodes) {
				System.out.println("ok"+a.toString()+","+b.toString());
				if(!a.equals(b)) {
					if(p.isLoose(a, b)) {
						paper xshift= new paper(test);
						paper yshift= new paper(test);
						int deltaX=(int) Math.copySign(1,b.x-a.x );
						int deltaY=(int) Math.copySign(1,b.y-a.y );
						System.out.println("here we are"+deltaX+","+deltaY);
						xshift.nodes.get(test.nodes.indexOf(a)).x+=deltaX;
						yshift.nodes.get(test.nodes.indexOf(a)).y+=deltaY;
						children.add(xshift);
						children.add(yshift);
						System.out.println(newGen);
					}
				}
			}
		}
		newGen.addAll(children);
	}
}
private void createStarter() {
	// TODO Auto-generated method stub
	oldGen= new ArrayList<paper>();
	newGen= new ArrayList<paper>();
	paper starter= new paper(p);
	while(hasOverlap(starter)) {
		for(node n:starter.nodes){
			n.x*=2;
			n.y*=2;
		}
		starter.shrink();
	}
	oldGen.add(starter);
	System.out.println("starter is"+starter.toString());
}
private void display() {
	System.out.println(p.toText());
	System.out.print(hasOverlap(p));
}
private boolean overlaps(node one, node two) {
	return p.getLongestLegWithoutCuts(one, two)<p.distances.get(one).get(two);
}
private boolean hasOverlap(paper test) {
	for(node N1:test.nodes) {
		for(node N2:test.nodes) {
			if(overlaps(N1,N2)) {
				return true;
			}
		}
	}
	return false;
}
}
