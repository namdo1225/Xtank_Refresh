
public class ClientController {
	private ClientModel				clientModel;
	
	public ClientController() {
		
	}
	
	public void setMVC(ClientModel model) {
		clientModel = model;
	}
	
	public void updateScreen(Mode mode) {
		clientModel.setMode(mode);
	}
	
	public void createSocket(String ip, int port) {
		try {
			clientModel.setSocket(ip, port);
		} catch (Exception e) {}
	}
}