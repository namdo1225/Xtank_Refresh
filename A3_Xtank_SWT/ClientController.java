import java.io.IOException;

public class ClientController {
	private ClientModel				clientModel;
	
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
	
	public void endGame() {
		try {
			if (clientModel.getInput() != null)
				clientModel.getInput().close();
		}
		catch (IOException e) {}
		
		try {
			if (clientModel.getOutput() != null)
				clientModel.getOutput().close();
		}
		catch (IOException e) {}
		
		if (clientModel.getRun() != null)
			clientModel.getRun().stop();
		
		try {
			clientModel.getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public void writeOut(InputPacket packet) {
		try { clientModel.getOutput().writeObject(packet); }
		catch (IOException e) {
			System.out.println("The server did not respond (write KL).");
		}
	}
}