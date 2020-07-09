/**
 * 
 */
package origamiProject;

import java.util.ArrayList;

/**
 * @author Benjamin Wettle
 *
 */
public class Optimizer {
paper p;
int step;
public final static int POP_SIZE=50;
public final static int NUM_GEN=5;
ArrayList<gene> newGen;
ArrayList<gene> oldGen;
	/**
	 * 
	 */
	public Optimizer(paper p) {
		this.p=p;
		
		
		// TODO Auto-generated constructor stub
	}
	public void optimize(paper p) {
		this.p=p;
		createFirstGen();
		
		
	}
	public paper 	getBestDesign(){
		return(newGen.get(0).getFinalDesign());
	}
	private void createFirstGen() {
		newGen= new ArrayList<gene>();
		oldGen= new ArrayList<gene>();
		
		for(int i=0;i<POP_SIZE;i++) {
			gene g= new gene(p);
			newGen.add(g);
		}
	}

}
