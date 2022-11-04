
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
	
	public void createServerSocket(int port) {
		try {
			hostModel.setServerSocket(port);
		} catch (Exception e) {}
	}
}