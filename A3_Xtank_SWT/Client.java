
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Client 
{
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	public Client(String address, int port) {
		System.out.println("1");
		try {
			var socket = new Socket(address, port);
			System.out.println("cat");
			out = new ObjectOutputStream(socket.getOutputStream());
        	out.flush();
        	in = new ObjectInputStream(socket.getInputStream());
        	try {
        		System.out.println("CAT " + in.available());
				InputPacket test = (InputPacket)in.readObject();
				System.out.println("CAT " + in.available());
				System.out.println(test.id);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	System.out.println("cat");
            var ui = new XTankUI(in, out);
            ui.start();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean getServerData() {
		try {
			if (in.available() > 0) {
				int test = in.readInt();
				System.out.println(test);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean sendServerData() {
		
		
		return true;
	}
}