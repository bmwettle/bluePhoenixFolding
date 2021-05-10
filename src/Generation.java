
import java.util.ArrayList;
import java.util.Collections;

public class Generation extends ArrayList<skeleton>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1953315825717890693L;

	public Generation() {
		super();
	}
	@Override
	
	public boolean add(skeleton x) {
		 int pos = Collections.binarySearch(this, x);
		    if (pos < 0) {
		       add(-pos-1, x);
		       return true;
		    }
		    return false;
	}
}

