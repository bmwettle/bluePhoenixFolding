package origamiProject;

public class gene {
double [][]velocity;
paper p;
int max_mutate=5;
	public gene(paper p) {
		velocity= new double[p.nodes.size()][2];
		this.p=p;
		//this.mutate();
		System.out.println(velocity);
		// TODO Auto-generated constructor stub
	}
	public void slow() {
		for(int i=0;i<p.nodes.size();i++) {
			velocity[i][0]*=.5;
			velocity[i][1]*=.5;
			
		}
	}
public void move() {
	int i=0;
	for(node n:p.nodes){
		if(n.size==0) {
			int x=0;
			int y=0;
			for(node m:p.connections.get(n)) {
				x+=m.getX();
				y+=n.getY();
			}
			n.forceX((int)(x/p.connections.get(n).size()));
			n.forceY((int)(y/p.connections.get(n).size()));
		}else {
		double[] vel= velocity[i];
		boolean go=true;
		for(int j=1;j>=0&&go;j-=1) {
			for(int k=1;k>=0&&go;k-=1) {
				n.moveX(j*(int)vel[0]);
				n.moveY(k*(int)vel[1]);
				if(!p.hasOverlap(n)) {
					go=false;
					
				}else {
					n.moveX(-j*(int)vel[0]);
					n.moveY(-k*(int)vel[1]);
				}
				System.out.println(j+","+k);
				
			}
		}
		}
		i++;
		System.out.println("next");
	}
	p.shrink();
}
public void mutate() {
	for(int i=0;i<p.nodes.size();i++) {
		velocity[i][0]=(int) (Math.random()*max_mutate);
		velocity[i][1]=(int) (Math.random()*max_mutate);
	}
}
public void getForces() {
	
for(node n:p.nodes) {
	for(node m:p.connections.get(n)) {
		int[] overlap=p.getOverlap(n, m);
		int index= p.nodes.indexOf(m);
		velocity[index][0]-=(overlap[0])*Math.signum(n.getX()-m.getX());
		velocity[index][1]-=(overlap[1])*Math.signum(n.getY()-m.getY());
	}
	
}
for(int i=0;i<p.nodes.size();i++) {
	velocity[i][0]=(int) Math.signum(velocity[i][0]);
	velocity[i][1]=(int) Math.signum(velocity[i][1]);
}
}

}
