package bluePheonixFolding;

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

/**
 * this method makes a new Condition, between two nodes.
 * in the optimized design  they must share either x or y symmetry.
 * @param node1
 * @param node2
 */
	public Condition(node node1,node node2) {
			this.node1=node1;
			this.node2=node2;
		
	}
}
