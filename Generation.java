package bluePheonixFolding;

import java.util.ArrayList;

public class Generation extends ArrayList<skeleton>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1953315825717890693L;

	public Generation() {
		super();
		// TODO Auto-generated constructor stub
	}
public Generation checkFixed(boolean xSymmetry) {
	Generation check= new Generation();
	for(skeleton sk:this) {
		if(checkFixedSkeleton(sk,xSymmetry)) {
			check.add(sk);
		}
		
	}
	return check;
	
}
private boolean checkFixedSkeleton(skeleton sk, boolean xSym) {
	for(node n:sk) {
		if(n.isFixedToEdge) {
			
			if(Math.abs(n.getX())==sk.size||Math.abs(n.getY())==sk.size) {
				
			}else {
				return false;
			}
		}
		if(n.isFixedToSymmetryLine) {
			if(xSym) {
				if(n.getX()!=0) {
					return false;
				}
			}else {
				if(n.getY()!=0) {
					return false;
				}
			}
		}
	}
	return true;
}
}
