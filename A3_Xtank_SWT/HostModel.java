import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HostModel {	
	private XTankUI			hostView;
	private Mode				mode;
	
	private Server				server;
	
	public HostModel() {
		mode = Mode.MAIN;
	}
	
	public void setMVC(XTankUI view) {
		hostView = view;
	}
	
	public void setMode(Mode m) {
		mode = m;
		hostView.updateScreen(mode);
	}
	
	public void setServerSocket(int port) {
        server = new Server(port);
	}
	
	public int getPlayer() {
		if (server == null)
			return 0;
		return server.getPlCount();
	}
}
