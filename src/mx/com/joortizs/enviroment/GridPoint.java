package mx.com.joortizs.enviroment;

public class GridPoint {
	private int x, y;
	
	public GridPoint(int x, int y){
		this.x = x;
		this.y = y;
	}
	public String toString(){
		return "(" + this.x + "," + this.y + ")";
	}
	
	public boolean isPointEqual(GridPoint point2){
		if( x == point2.getX() && y == point2.getY() )
			return true;
		else
			return false;
	}
	
	public void updatePoint(int x,int y){
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
}
