/**
 * A class that represents the client's model, holding all the
 * game data so that such data could be rendered on the UI.
 * 
 * It also has a socket to connect to a server.
 * 
 * Pattern: Model for the client from the MVC.
 * 
 * @author	Nam Do
 * @version	1.0
 * @since	2022-11-12
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientModel {	
	private XTankUI						clientView;
	
	private Mode						mode;
	
	private	Socket						socket;
	private ObjectInputStream 			in;
	private ObjectOutputStream 			out;
	private InputPacket					new_tank;
	
	private Tank						tank;
	
	private Map<Integer, Tank>			tanks;
	private List<Bullet>				bullets;
	
	private Runner						runnable;
	private ExecutorService				pool;
	
	private	int							tankModel;
	private boolean						terminate;
	
	/**
	 * Constructor for ClientModel.
	 */
	public ClientModel() {
		mode = Mode.MAIN;
		tanks = new HashMap<>();
		bullets = new ArrayList<>();
		tank = new Tank(0, 0, 0);
	}
	
	/**
	 * A method to set the view for the model.
	 * 
	 * @param view		a XTankUI object for the view of the MVC.
	 */
	public void setMVC(XTankUI view) {
		clientView = view;
	}
	
	/**
	 * A setter to set the screen mode, allowing the model to change
	 * the UI for the view.
	 * 
	 * @param m		a Mode enum representing the correct screen mode.
	 */
	public void setMode(Mode m) {
		mode = m;
		clientView.updateScreen(mode);
	}
	
	/**
	 * A method set the socket and connect to a server.
	 * 
	 * @param ip		a String for the IP of the server
	 * @param port		an int for the port number of the server
	 */
	public void setSocket(String ip, int port) {
		terminate = false;
		
		try {
        	socket = new Socket();
        	socket.connect(new InetSocketAddress(ip, port), 100);
        	
        	in = new ObjectInputStream(socket.getInputStream());
        	out = new ObjectOutputStream(socket.getOutputStream());
        	out.flush();
        	try {
        		int map_id = (Integer)in.readObject();
        		// Somehow change GameMap here
        		        		
        		new_tank = (InputPacket)in.readObject();
        		clientView.recreateGameScreen(map_id, tankModel, new_tank.id);

        		
        		int num_tanks = (Integer)in.readObject();
        		for (int i = 0; i < num_tanks; i++) {
        			InputPacket enemy_tank = (InputPacket)in.readObject();
        			System.out.println("Adding enemy: " + enemy_tank.id);
        			tanks.put(enemy_tank.id, new Tank(0, 0, enemy_tank.id));
        			tanks.get(enemy_tank.id).set(enemy_tank.x, enemy_tank.y, enemy_tank.angle);
        		}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	tank = new Tank(new_tank.x, new_tank.y, new_tank.id);
        	tanks.put(tank.getID(), tank);
        }
        catch (Exception e) {
        	//System.out.println("error");
        }
        
        if (socket.isConnected()) {        	
        	runnable = new Runner();
        	pool = Executors.newFixedThreadPool(1);
			pool.execute(runnable);
        } else
    		terminate = false;
	}
	
	/**
	 * A getter to get the player's tank object.
	 * 
	 * @return	a Tank object with data of the player's tank.
	 */
	public Tank getTank() {
		return tank;
	}
	
	/**
	 * A getter to get the model's map of player and enemies tanks.
	 * 
	 * @return	a Map<Integer, Tank> object representing the player and
	 * 			enemies' tank data.
	 */
	public Map<Integer, Tank> getTanks() {
		return tanks;
	}
	
	/**
	 * A getter to get the model's input stream for the server.
	 * 
	 * @return	an ObjectInputStream object for the model's input stream
	 * 			to the server.
	 */
	public ObjectInputStream getInput() {
		return in;
	}
	
	/**
	 * A getter to get the model's output stream for the server.
	 * 
	 * @return	an ObjectOutputStream object for the model's output stream
	 * 			to the server.
	 */
	public ObjectOutputStream getOutput() {
		return out;
	}
	
	/**
	 * A getter for the Runner object, a Runnable derived class.
	 * 
	 * @return	a Runner object.
	 */
	public Runner getRun() {
		return runnable;
	}
	
	/**
	 * A getter for the Socket object for client to connect to the server.
	 * 
	 * @return	a Socket object.
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * A method to delete the client's socket to the server.
	 */
	public void deleteSocket() {
		socket = null;
	}
	
	/**
	 * A method to delete the client's input stream to the server.
	 */
	public void deleteInput() {
		in = null;
	}
	
	/**
	 * A method to delete the client's output stream to the server.
	 */
	public void deleteOutput() {
		out = null;
	}
	
	/**
	 * A method to reset the client's tank data.
	 */
	public void resetTankData() {
		tanks = new HashMap<>();
		bullets = new ArrayList<>();
		tank = new Tank(0, 0, 0);
	}
	
	/**
	 * A method to delete the client's thread pool.
	 */
	public void deleteThreadPool() {
		if (pool != null) {
			pool.shutdownNow();
			pool = null;
		}
	}
	
	/**
	 * A method to see if the client's thread pool is set
	 * to be terminated.
	 * 
	 * @return	a boolean. true if the client's thread pool is
	 * 			set for termination. false if not.
	 */
	public boolean getTerminate() {
		return terminate;
	}
	
	/**
	 * A method to set the client's tank's model.
	 * 
	 * @param model		an int for the tank's model.
	 */
	public void setTankModel(int model) {
		tankModel = model;
	}
	
	/**
	 * A getter to return a list of bullets to be displayed on the
	 * client.
	 * 
	 * @return	a List<Bullet> of a list of Bullet objects.
	 */
	public List<Bullet> getBullets() {
		return bullets;
	};
	
	/**
	 * A Runnable derived class that represents the task of
	 * getting input and output from the server.
	 * 
	 * @author	Nam Do
	 * @version	1.0
	 * @since	2022-11-12
	 */
	public class Runner implements Runnable {
		
		/**
		 * A method to run the task, which is to get input and output
		 * from the server.
		 */
		@Override
		public void run() {
			while (!terminate) {
				try {
					if (in.available() > 0 || true) {
						// read in only other tanks and shots
						InputPacket packet = null;
						try {
							packet = (InputPacket)in.readObject();
							System.out.println(in + " " + packet.id + " " + tank.getID());
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (packet.is_bullet) {
							// new bullet
							if (packet.id >= bullets.size()) {
								bullets.add(new Bullet(packet.x, packet.y, 0, 0));
							}
							// update bullet
							else {
								bullets.get(packet.id).set(packet.x, packet.y);
							}
							// delete bullet
							if (packet.delete) {
								bullets.remove(packet.id);
							}
						}
						else {
							if (packet.x == -69 && packet.y == -69 && packet.angle == -69) {
								// TODO: send packet.id as winner to ui somehow
								
							}
							if (packet.delete) {
								tanks.remove(packet.id);
							}
							else {
								// used if new tank added after this local tank was added
								if (packet.id == tank.getID()) {
									tank.set(packet.x, packet.y, packet.angle);
									tanks.get(packet.id).set(packet.x, packet.y, packet.angle);
								}
								else if (!tanks.containsKey(packet.id)) {
									tanks.put(packet.id, new Tank(packet.x, packet.y, packet.id));
									tanks.get(packet.id).set(packet.x, packet.y, packet.angle);
								}
								else {
									tanks.get(packet.id).set(packet.x, packet.y, packet.angle);
								}
							}
						}
					}
				}
				catch(IOException ex) {
					//System.out.println("The server did not respond (async).");
					stop();
				}
				catch(NullPointerException ex) {
					System.out.println("Nullptr error in ClientModel line ~141. This meant"
							+ "server has closed socket communication due to player restriction"
							+ "or other issue.");
					stop();
				}
		        //display.timerExec(150, this);
			}
		}
		
		/**
		 * A method to skip the task, which stops getting input and output
		 * from the server.
		 */
		public void stop() {
			terminate = true;			
		}
	}
}