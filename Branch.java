package origamiProject;

import java.util.ArrayList;

public class Branch {
ArrayList<Branch> subBranches;
public Branch () {
	subBranches= new ArrayList<Branch>();
}
public Branch (ArrayList<Branch> branches) {
	subBranches= new ArrayList<Branch>(branches);
}
public void addSubBranch(Branch sub) {
	subBranches.add(sub);
}

}
