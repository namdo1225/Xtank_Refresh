import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HostModel {	
	private XTankUI2			hostView;
	private Mode				mode;
	private	ServerSocket		socket;
	private DataInputStream 	in;
	private DataOutputStream 	out;
	
	public HostModel() {
		mode = Mode.MAIN;
	}
	
	public void setMVC(XTankUI2 view) {
		hostView = view;
	}
	
	public void setMode(Mode m) {
		mode = m;
		hostView.updateScreen(mode);
	}
	
	public void setServerSocket(int port) {
        try
        {
        	socket = new ServerSocket(port);
        } catch (Exception e) {}
	}
}
