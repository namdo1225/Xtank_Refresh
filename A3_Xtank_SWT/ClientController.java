import java.io.IOException;

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
	
	public void closeInput() {
		try {
			if (clientModel.getInput() != null)
				clientModel.getInput().close();
		}
		catch (IOException e) {}
	}
	
	public void closeOutput() {
		try {
			if (clientModel.getOutput() != null)
				clientModel.getOutput().close();
		}
		catch (IOException e) {}
	}
	
	public void stopRunnable() {
		if (clientModel.getRun() != null)
			clientModel.getRun().stop();
	}
}