package origamiProject;

public class child implements Comparable<child> {
	paper p;
	int[][] Xsteps;
	int[][] Ysteps;
	int size;
	int num_steps=1;
	int step;
	int stopped;
	double breeding_number;
public child(paper myPaper) {
	p=new paper(myPaper);
	size= p.nodes.size();
	Xsteps= new int[size][num_steps];
	Ysteps= new int[size][num_steps];
	for(int i=0;i<size;i++){
		for( int j=0;j<num_steps ;j++) {
			double randX=Math.random();
			if(randX<1/2) {
		Xsteps[i][j]=-1; 
			}else {
				
					Xsteps[i][j]=1;
			}
			double randY=Math.random();
			if(randY<1/2) {
				Ysteps[i][j]=-1; 
			}else {
				Ysteps[i][j]=1;
			}
		}
	}
	stopped=0;
	step=0;
	for(int i=0;i<num_steps;i++) {
		step(i);		
	}
	System.out.println("stopped:"+stopped);
}
public child(child ch,paper p) {
	this.p=new paper(p);
	this.Xsteps=ch.Xsteps;
	this.Ysteps=ch.Ysteps;
	this.size=ch.size;
	this.step=0;
	for(int i=0;i<num_steps;i++) {
		step(i);
	}
}
public int compareTo(child b) {
	
	return this.p.compareTo(b.p);
}
public void step(int index) {
	for(int i=0;i<size;i++) {
		node A=p.nodes.get(i);
		//if(p.isLeaf(A)) {
			A.moveX(Xsteps[i][index]);
			for(node n:p.nodes) {
				if(p.overlaps(A, n)) {
					A.moveX(-1*Xsteps[i][index]);
					stopped++;
					Xsteps[i][index]=0;
					break;
				}
			}
			A.moveY(Ysteps[i][index]);
			for(node n:p.nodes) {
				if(p.overlaps(A, n)) {
					A.moveY(-1*Ysteps[i][index]);
					stopped++;
					Ysteps[i][step]=0;
					break;
				}
			}
		//}
	}
	p.shrink();
}
}
