/**
 * A class that represents a controller and sets the game data 
 * for the host's model.
 * 
 * Pattern: Controller for the server from the MVC.
 * 
 * @author	Nam Do
 * @version	1.0
 * @since	2022-11-12
 */

public class HostController {
	private HostModel			hostModel;
	
	/**
	 * Set the model for the server's controller so it can be
	 * referred to.
	 * 
	 * @param model		a HostModel for the model of the server.
	 */
	public void setMVC(HostModel model) {
		hostModel = model;
	}
	
	/**
	 * A method to change the model's current screen mode (UI)
	 * 
	 * @param mode		a Mode representing the mode to set for the model.
	 */
	public void updateScreen(Mode mode) {
		hostModel.setMode(mode);
	}
	
	/**
	 * A method to create the server model's server socket.
	 * 
	 * @param port		an int for the server's port number.
	 * @param mapNum	an int for the map maze's id.
	 */
	public void createServer(int port, int mapNum) {
		try {
			hostModel.setServer(port, mapNum);
		} catch (Exception e) {}
	}

	/**
	 * A method to close and delete the server model's server socket.
	 */
	public void closeServer() {
		if (hostModel.getServer() != null)
			hostModel.getServer().closeServer();
		hostModel.deleteServer();
	}
}