package origamiProject;

import java.io.Serializable;

/**
 * 
 * @author Benjamin Wettle
 * this class stores conditions that constrain the optimization.
 */
public class Condition implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
node node1;
node node2;
boolean matchX;
boolean matchY;
/**
 * this method makes a new Condition, between two nodes.
 * in the optimized design  they must share either x coordinates or y coordinates.
 * @param node1
 * @param node2
 * @param matchX
 * @param matchY
 */
	public Condition(node node1,node node2, boolean matchX, boolean matchY) {
		if(matchY!=matchX) {
			this.node1=node1;
			this.node2=node2;
			this.matchX=matchX;
			this.matchY=matchY;
		}
	}
}
