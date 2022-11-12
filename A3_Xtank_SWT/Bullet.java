/**
 * A class that represent bullets shot by the tanks. Includes
 * information about position, angle, etc. that makes sure it
 * is displayed in the right screen and position.
 * 
 * @author	Patrick Comden
 * @version	1.0
 * @since	2022-11-12
 */

public class Bullet {
	private float				x, y;
	private int					angle;
	private int					speed;
	private static GameMap		map = null;
	public static final int		size = 5;
	
	/**
	 * Constructor for the Bullet class.
	 * 
	 * @param x			a float for the bullet's x position.
	 * @param y			a float for the bullet's y position.
	 * @param angle		an int for the angle the bullet is traveling to.
	 * @param speed		an int for the bullet's speed.
	 */
	public Bullet(float x, float y, int angle, int speed) {
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.speed = speed;
	}
	
	/**
	 * A getter to return the bullet's x position.
	 * 
	 * @return x		a float for the bullet's x position.
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * A getter to return the bullet's y position.
	 * 
	 * @return y		a float for the bullet's y position.
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * A getter to return the angle the bullet is travelling on.
	 * 
	 * @return	an int for the bullet's angle.
	 */
	public int getAngle() {
		return angle;
	}
	
	/**
	 * A method to calculate and return the rotated bullet's
	 * x position.
	 * 
	 * @return	a double representing the bullet's rotated x position.
	 */
	public double getDirectionX() {
		return Math.cos(Math.toRadians(angle));
	}
	
	/**
	 * A method to calculate and return the rotated bullet's
	 * y position.
	 * 
	 * @return	a double representing the bullet's rotated y position.
	 */
	public double getDirectionY() {
		return Math.sin(Math.toRadians(angle));
	}
	
	/**
	 * A method to check if the bullet has hit an object.
	 * 
	 * @return	a boolean. false if it collides with an object.
	 *			true if not.
	 */
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
	
	/**
	 * A method to set the bullet's position.
	 * 
	 * @param x			a float for the bullet's new x.
	 * @param y			a float for the bullet's new y.
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * A static method to delete the map the bullet is displayed
	 * on.
	 */
	public static void resetMap() {
		Bullet.map = null;
	}
	
	/**
	 * A static method to set the game map's for the bullet class.
	 * 
	 * @param map		a GameMap representing the canvas of the game arena.
	 */
	public static void setMap(GameMap map) {
		Bullet.map = map;
	}
}
