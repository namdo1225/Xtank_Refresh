
public class HostController {
	private HostModel				hostModel;
	
	public HostController() {
		
	}
	
	public void setMVC(HostModel model) {
		hostModel = model;
	}
	
	public void updateScreen(Mode mode) {
		hostModel.setMode(mode);
	}
	
	public void createServer(int port, int mapNum) {
		try {
			hostModel.setServer(port, mapNum);
		} catch (Exception e) {}
	}

	public void stopNewConnection() {
		hostModel.stopNewConnection();
	}
	
	public void closeServer() {
		if (hostModel.getServer() != null)
			hostModel.getServer().closeServer();
		hostModel.deleteServer();
	}
}