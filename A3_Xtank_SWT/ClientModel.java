import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientModel {	
	private XTankUI2			clientView;
	private Mode				mode;
	private	Socket				socket;
	private DataInputStream 	in;
	private DataOutputStream 	out;
	
	public ClientModel() {
		mode = Mode.MAIN;
	}
	
	public void setMVC(XTankUI2 view) {
		clientView = view;
	}
	
	public void setMode(Mode m) {
		mode = m;
		clientView.updateScreen(mode);
	}
	
	public void setSocket(String ip, int port) {
        try
        {
        	socket = new Socket(ip, port);
        } catch (Exception e) {}
	}
}