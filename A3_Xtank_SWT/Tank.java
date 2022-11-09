/**
 * A class to represent the tank of the game.
 * 
 * @author	Patrick Comden
 * @version	1.0
 * @since	2022-11-12
 */

public class Tank {
	private float			x;
	private float			y;
	private int				rotate;
	private int				id;
	public static final int width = 25;
	public static final int height = 50;
	
	/**
	 * A constructor for Tank.
	 * 
	 * @param x		an int for the tank's x position.
	 * @param y		an int for the tank's y position.
	 * @param id	an int for the tank's id.
	 */
	public Tank(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.rotate = 0;
		this.id = id;
	}
	
	/**
	 * A method to move the tank.
	 * 
	 * @param x		an int for the tank's x new position.
	 * @param y		an int for the tank's y new position.
	 */
	public void move(int x, int y) {
		this.x += x;
		this.y += y;
	}
	
	/**
	 * A method to move the tank forward.
	 * 
	 * @param speed	an int for the tank's speed.
	 */
	public void moveForward(int speed) {
		//System.out.println(getDirectionX() + " " + getDirectionY());
		this.x += (float)getDirectionX() * (float)speed;
		this.y -= (float)getDirectionY() * (float)speed;
	}
	
	/**
	 * A setter to set the tank's position and rotation.
	 * 
	 * @param x			an int for the tank's x position.
	 * @param y			an int for the tank's y position.
	 * @param rotate	an int for the tank's rotation.
	 */
	public void set(int x, int y, int rotate) {
		this.x = x;
		this.y = y;
		this.rotate = rotate;
	}
	
	/**
	 * A method to rotate the tank.
	 * 
	 * @param rotate	an int for the tank's rotation.
	 */
	public void rotate(int rotate) {
		this.rotate += rotate;
		if (this.rotate >= 360) {
			this.rotate %= 360;
		}
		if (this.rotate < 0) {
			this.rotate += 360;
		}
	}
	
	/**
	 * A method to get the tank's rotated x position.
	 * 
	 * @return a double for the tank's rotated x position.
	 */
	public double getDirectionX() {
		return Math.cos(Math.toRadians(rotate));
	}
	
	/**
	 * A method to get the tank's rotated y position.
	 * 
	 * @return a double for the tank's rotated y position.
	 */
	public double getDirectionY() {
		return Math.sin(Math.toRadians(rotate));
	}
	
	/**
	 * A getter to retun the tank's x position.
	 * 
	 * @return	an int for the tank's x position.
	 */
	public int getX() {
		return (int) x;
	}
	
	/**
	 * A getter to retun the tank's y position.
	 * 
	 * @return	an int for the tank's y position.
	 */
	public int getY() {
		return (int) y;
	}
	
	/**
	 * A getter to retun the tank's rotation.
	 * 
	 * @return	an int for the tank's rotation.
	 */
	public int getRotate() {
		return rotate;
	}
	
	/**
	 * A getter to retun the tank's id.
	 * 
	 * @return	an int for the tank's id.
	 */
	public int getID() {
		return id;
	}
}
