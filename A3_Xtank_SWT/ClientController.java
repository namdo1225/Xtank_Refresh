/**
 * A class that represents a controller and sets the game data 
 * for the client's model.
 * 
 * Pattern: Controller for the client from the MVC.
 * 			Singleton pattern.
 * 
 * @author	Nam Do
 * @version	1.0
 * @since	2022-11-12
 */

import java.io.IOException;

public class ClientController {
	private static ClientController	controller;
	
	private static ClientModel		clientModel;
	
	/**
	 * Private constructor for ClientController.
	 */
	private ClientController() {}
	
	/**
	 * A getter to get the singular ClientController object.
	 * 
	 * @return	a ClientController object.
	 */
	public synchronized static ClientController get() {
		if (controller == null)
			controller = new ClientController();
		return controller;
	}
	
	/**
	 * A method to set the model for the controller so the controller
	 * could reference the model.
	 * 
	 * @param model		a ClientModel represent the client's model in MVC.
	 */
	public void setMVC(ClientModel model) {
		clientModel = model;
	}
	
	/**
	 * A method to change the model's current screen mode (UI)
	 * 
	 * @param mode		a Mode representing the mode to set for the model.
	 */
	public void updateScreen(Mode mode) {
		clientModel.setMode(mode);
	}
	
	/**
	 * A method to have the model create a socket to the server.
	 * 
	 * @param ip		a String for the IP of the server.
	 * @param port		an int for the port number.
	 */
	public void createSocket(String ip, int port) {
		try {
			clientModel.setSocket(ip, port);
		} catch (Exception e) {}
	}
	
	/**
	 * A method to have the model end the game.
	 */
	public void endGame() {
		try {
			if (clientModel.getInput() != null) {
				clientModel.getInput().close();
				clientModel.deleteInput();
			}
			
			if (clientModel.getOutput() != null) {
				clientModel.getOutput().close();
				clientModel.deleteOutput();
			}
			
			if (clientModel.getSocket() != null)
				clientModel.getSocket().close();

		}
		catch (IOException e) {}
		
		if (clientModel.getRun() != null) {
			clientModel.getRun().stop();
			clientModel.deleteThreadPool();
		}
		
		clientModel.deleteSocket();
		
		clientModel.resetTankData();
	}
	
	/**
	 * A method to close the model's input stream.
	 */
	public void closeInput() {
		try {
			if (clientModel.getInput() != null)
				clientModel.getInput().close();
		}
		catch (IOException e) {}
	}
	
	/**
	 * A method to close the model's output stream.
	 */
	public void closeOutput() {
		try {
			if (clientModel.getOutput() != null)
				clientModel.getOutput().close();
		}
		catch (IOException e) {}
	}
	
	/**
	 * A method to block the model's listening thread from
	 * checking for new input/output.
	 */
	public void stopRunnable() {
		if (clientModel.getRun() != null)
			clientModel.getRun().stop();
	}
	
	/**
	 * A method to write a data packet out to the server.
	 * 
	 * @param packet		an InputPacket that packs many different member
	 * 						variable to be sent to the server together.
	 */
	public void writeOut(InputPacket packet) {
		try { clientModel.getOutput().writeObject(packet); }
		catch (IOException e) {
			System.out.println("The server did not respond (write KL).");
		}
	}
	
	/**
	 * A method to see if the model is still connected to the server.
	 * 
	 * @return	a boolean. true if client is connected to server. false if not.
	 */
	public boolean isConnected() {
		if (clientModel.getSocket() == null || clientModel.getTerminate())
			return false;
		return true;
	}

	/**
	 * A method to set the player's tank model for the client model.
	 * 
	 * @param model		an int representing the player's tank model.
	 */
	public void setTankModel(int model) {
		clientModel.setTankModel(model);
	}
}