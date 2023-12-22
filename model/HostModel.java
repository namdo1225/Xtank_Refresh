package model;
import view.XTankUI;

/**
 * A class that represents the server's model, holding all the
 * server and game data so that such data could be rendered on the UI
 * of the clients.
 * 
 * Pattern: Model for the server from the MVC.
 * 			Singleton pattern.
 * 
 * @author	Nam Do
 * @version	1.0
 * @since	2022-11-12
 */

public class HostModel {	
	private static HostModel	model;
	
	private static XTankUI		hostView;
	private static Mode			mode;
	
	private static Server		server;
	
	/**
	 * Private constructor for HostModel.
	 */
	private HostModel() {
		mode = Mode.MAIN;
	}
	
	/**
	 * A getter to get the singular HostModel object.
	 * 
	 * @return	a HostModel object.
	 */
	public synchronized static HostModel get() {
		if (model == null) {
			model = new HostModel();
		}
		return model;
	}
	
	/**
	 * Method to set the view of the server's model.
	 * 
	 * @param view		a XTankUI for the view of the MVC.
	 */
	public void setMVC(XTankUI view) {
		hostView = view;
	}
	
	/**
	 * A setter to set the screen mode, allowing the model to change
	 * the UI for the view.
	 * 
	 * @param m		a Mode enum representing the correct screen mode.
	 */
	public void setMode(Mode m) {
		mode = m;
		hostView.updateScreen(mode);
	}
	
	/**
	 * A method to create a server for the model.
	 * 
	 * @param port		an int of the port number
	 * @param mapNum	an int of the map maze's id
	 * @param maxLives	an int of the set max lives for each player.
	 */
	public void setServer(int port, int mapNum, int maxLives) {
        server = new Server(port, mapNum, maxLives);
	}
	
	/**
	 * A getter to get the count of players on the server.
	 * 
	 * @return	an int representing the # of players on the server.
	 */
	public int getPlayer() {
		if (server == null)
			return 0;
		return server.getPlCount();
	}
	
	/**
	 * A getter to get the Server object representing the server.
	 * 
	 * @return	a Server object representing the server.
	 */
	public Server getServer() {
		return server;
	}
	
	/**
	 * A method to delete the Server object.
	 */
	public void deleteServer() {
		server = null;
	}
}
