
public class InputPacket implements java.io.Serializable {
	public InputPacket(int id) {
		this.id = id;
	}
	public InputPacket(int id, int x, int y, int angle, boolean shoot) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.shoot = shoot;
		this.is_bullet = false;
	}
	private static final long serialVersionUID = 1L;
	public int id;
	public int x;
	public int y;
	public int angle;
	public boolean shoot;
	public boolean is_bullet;
}

class IntPacket {
	public int number;
}