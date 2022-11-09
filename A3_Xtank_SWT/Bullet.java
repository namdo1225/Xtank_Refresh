
public class Bullet {
	
	private float x, y;
	private int angle;
	private int speed;
	private static GameMap map = null;
	public static final int size = 5;
	
	public Bullet(float x, float y, int angle, int speed) {
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.speed = speed;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public int getAngle() {
		return angle;
	}
	
	public double getDirectionX() {
		return Math.cos(Math.toRadians(angle));
	}
	
	public double getDirectionY() {
		return Math.sin(Math.toRadians(angle));
	}
	
	public boolean step() {
		if (map != null) {
			x += getDirectionX() * speed;
			y -= getDirectionY() * speed;
			// hit a wall
			if (map.collision((int)x, (int)y, (int)x + size, (int)y + size)) {
				return false;
			}
			// hit a player (any player)
		}
		return true;
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static void resetMap() {
		Bullet.map = null;
	}
	
	public static void setMap(GameMap map) {
		Bullet.map = map;
	}
}
