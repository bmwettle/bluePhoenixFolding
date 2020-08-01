package origamiProject;

public class Condition {
node node1;
node node2;
boolean matchX;
boolean matchY;
	public Condition(node node1,node node2, boolean matchX, boolean matchY) {
		if(matchY!=matchX) {
			this.node1=node1;
			this.node2=node2;
			this.matchX=matchX;
			this.matchY=matchY;
		}
	}
}
