
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class XTank 
{
	public static void main(String[] args) throws Exception 
    {
        try (var socket = new Socket("127.0.0.1", 8080)) 
        {
        	ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        	ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        	out.flush();
        	InputPacket new_tank = null;
        	try {
        		new_tank = (InputPacket)in.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	Tank tank = new Tank(new_tank.x, new_tank.y, new_tank.id);
        	var ui = new XTankUI(in, out, tank);
            ui.start();
        }
    }
}


