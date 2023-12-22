package model;
/**
 * A class to that represents packaged information that can be sent
 * back and forth from the client and server.
 * 
 * This is supposed to be a C++ struct. Hence, the member variables
 * are public.
 * 
 * @author	Patrick Comden
 * @version	1.0
 * @since	2022-11-12
 */

public class InputPacket implements java.io.Serializable {
	private static final long	serialVersionUID = 1L;
	public int					id;
	public int					x;
	public int					y;
	public int					angle;
	public int					armor;
	public boolean				shoot;
	public boolean				isBullet;
	public boolean 				delete;
	
	/**
	 * First constructor for InputPacket.
	 * 
	 * @param id	an int for the id of the tank.
	 */
	public InputPacket(int id) {
		this.id = id;
	}
	
	/**
	 * Second constructor for InputPacket.
	 * 
	 * @param id	an int for the id of the tank.
	 * @param x		an int for the x position of the tank.
	 * @param y		an int for the y position of the tank.
	 * @param angle	an int for the angle/rotation of the tank.
	 * @param shoot	a boolean to see check if the packet contains bullet's
	 * 				data.
	 */
	public InputPacket(int id, int x, int y, int angle, boolean shoot) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.shoot = shoot;
		this.isBullet = false;
		this.delete = false;
		this.armor = 0;
	}
}