package mx.com.joortiz.agent.single;

public class GridPoint {
	int x, y;
	GridPoint(int x, int y){
		this.x = x;
		this.y = y;
	}
	public String toString(){
		return "(" + this.x + "," + this.y + ")";
	}
	
	public boolean isPointEqual(GridPoint point2){
		if( x == point2.x && y == point2.y )
			return true;
		else
			return false;
	}
	
	public void updatePoint(int x,int y){
		this.x = x;
		this.y = y;
	}
}
