
public class Tank {

	private float x;
	private float y;
	private int rotate;
	private int id;
	public static final int width = 25;
	public static final int height = 50;
	
	public Tank(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.rotate = 0;
		this.id = id;
	}
	
	public void move(int x, int y) {
		this.x += x;
		this.y += y;
	}
	
	public void moveForward(int speed) {
		System.out.println(getDirectionX() + " " + getDirectionY());
		this.x += (float)getDirectionX() * (float)speed;
		this.y -= (float)getDirectionY() * (float)speed;
	}
	
	public void set(int x, int y, int rotate) {
		this.x = x;
		this.y = y;
		this.rotate = rotate;
	}
	
	public void rotate(int rotate) {
		this.rotate += rotate;
		if (this.rotate >= 360) {
			this.rotate %= 360;
		}
		if (this.rotate < 0) {
			this.rotate += 360;
		}
	}
	
	public double getDirectionX() {
		return Math.cos(Math.toRadians(rotate));
	}
	
	public double getDirectionY() {
		return Math.sin(Math.toRadians(rotate));
	}
	
	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	public int getRotate() {
		return rotate;
	}
	
	public int getID() {
		return id;
	}
}
