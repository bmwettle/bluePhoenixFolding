package origamiProject;

import java.util.ArrayList;

public class gene {
paper p;
ArrayList<int[][]> path;


	public gene(paper p) {
		this.p=p;
		
		path= new ArrayList<int[][]>();
		int[][]step=		new int[p.nodes.size()][2];
		for(int i=0;i<p.nodes.size();i++) {
			step[i][0]=(int) Math.round(3*Math.random()-1);
			step[i][1]=(int) Math.round(3*Math.random()-1);
			System.out.println(step[i][0]+", "+step[i][1]);
		}
		addStep(step);
		// TODO Auto-generated constructor stub
	}
	public void addStep(int[][] step) {
		path.add(step);
	}
	public gene(paper p, ArrayList<int[][]>path) {
		this.p=p;

		this.path= path;
		// TODO Auto-generated constructor stub
	}
public paper getFinalDesign() {
	paper finalP= new paper(p);
	for(int i=0;i<path.size();i++) {
		for(int j=0;j<p.nodes.size();j++) {
		node n=finalP.nodes.get(j);
		n.moveX(path.get(i)[j][0]);
		n.moveY(path.get(i)[j][1]);
		for(node m:finalP.nodes) {
			if(!m.equals(n)&&finalP.overlaps(n, m)) {
				n.moveX(-1*path.get(i)[j][0]);
				n.moveY(-1*path.get(i)[j][1]);
				path.get(i)[j][0]=0;
				path.get(i)[j][1]=0;
				
			}
		}
		}
	}
	return finalP;
}
}
