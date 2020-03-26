package origamiProject;




public class optimizer{
	paper p;
public optimizer(paper p) {
	this.p=new paper(p);
	this.p.getTreeDistances();
	//display();
}
public paper optimizeWithoutCuts() {
	System.out.print(p.distances);
	display();
	return p;
}
private void display() {
	System.out.println(p.toText());
	System.out.print(hasOverlap());
}
private boolean overlaps(node one, node two) {
	return p.getLongestLegWithoutCuts(one, two)<p.distances.get(one).get(two);
}
private boolean hasOverlap() {
	for(node N1:p.nodes) {
		for(node N2:p.nodes) {
			if(overlaps(N1,N2)) {
				return true;
			}
		}
	}
	return false;
}
}
