package utils;

/**
 * simple class for handling points
 * @author Michal
 *
 */


public class Point implements java.io.Serializable {
	
	static final long serialVersionUID = 0x17E300AFA13BE7L;
	
	private int x = 0;
	private int y = 0;
	
	public Point() {
	}
	
	public Point(int x, int y) {
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
