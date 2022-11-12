/**
 * A class that represents the client's model, holding all the
 * game data so that such data could be rendered on the UI.
 * 
 * It also has a socket to connect to a server.
 * 
 * Pattern: Model for the client from the MVC.
 * 			Singleton pattern.
 * 
 * @author	Patrick Comden
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientModel {
	private static ClientModel			model;

	private static XTankUI				clientView;
	
	private static Mode					mode;
	
	private	static Socket				socket;
	private static ObjectInputStream 	in;
	private static ObjectOutputStream 	out;
	private static InputPacket			newTank;
	
	private static Tank					tank;
	
	private static Map<Integer, Tank>	tanks;
	private static List<Bullet>			bullets;
	
	private static Runner				runnable;
	private static ExecutorService		pool;
	private static Lock 				clientLock;
	
	private	int							tankModel;
	private boolean						terminate;
	private int							winner;
	
	/**
	 * Private constructor for ClientModel.
	 */
	private ClientModel() {
		clientLock = new ReentrantLock();
		winner = -1;
		mode = Mode.MAIN;
		tanks = new HashMap<>();
		bullets = new ArrayList<>();
		tank = new Tank(0, 0, 0, 0);
	}
	
	/**
	 * A getter to get the singular ClientModel object.
	 * 
	 * @return	a ClientModel object.
	 */
	public synchronized static ClientModel get() {
		if (model == null)
			model = new ClientModel();
		return model;
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
	 * @param m			a Mode emum representing the correct screen mode.
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

			// Setup input/output stream from and to server.
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			
			// read map data and enemy players' tanks.
			int map_id = (Integer)in.readObject();
			out.writeObject(tankModel);   		
			newTank = (InputPacket)in.readObject();
			clientView.recreateGameScreen(map_id, tankModel, newTank.id);

			int num_tanks = (Integer)in.readObject();
			for (int i = 0; i < num_tanks; i++) {
				InputPacket enemy_tank = (InputPacket)in.readObject();
				tanks.put(enemy_tank.id, new Tank(enemy_tank.x, enemy_tank.y, enemy_tank.id, enemy_tank.armor));
				tanks.get(enemy_tank.id).set(enemy_tank.x, enemy_tank.y, enemy_tank.angle, enemy_tank.armor);
			}

			tank = new Tank(newTank.x, newTank.y, newTank.id, newTank.armor);
			tanks.put(tank.getID(), tank);
        } catch (ClassNotFoundException e) {}
		catch (IOException e) {}
        
		// if socket is connected to the server, create a thread to handle server/client input/output.
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
		tank = new Tank(0, 0, 0, 0);
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
	
	public int getWinner() {
		return winner;
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
	 * @author	Patrick Comden
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
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						
						if (packet.isBullet)
							handleBullet(packet);
						else
							handleTank(packet);
					}
				}
				catch(Exception ex) {
					stop();
				}
			}
		}
		
		/**
		 * A method to handle bullet packet received from the server.
		 * 
		 * @param packet		an InputPacket object packed with data received
		 * 						from the server.
		 */
		private void handleBullet(InputPacket packet) {
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
		
		/**
		 * A method to handle tank packet received from the server.
		 * 
		 * @param packet		an InputPacket object packed with data received
		 * 						from the server.
		 */
		private void handleTank(InputPacket packet) {
			clientLock.lock();
			if (packet.x == -69 && packet.y == -69 && packet.angle == -69) {
				winner = packet.id;								
			}
			if (packet.delete) {
				tanks.remove(packet.id);
			}
			else {
				// used if new tank added after this local tank was added
				if (packet.id == tank.getID()) {
					tank.set(packet.x, packet.y, packet.angle, packet.armor);
					tanks.get(packet.id).set(packet.x, packet.y, packet.angle, packet.armor);
				}
				else if (!tanks.containsKey(packet.id)) {
					tanks.put(packet.id, new Tank(packet.x, packet.y, packet.id, packet.armor));
					tanks.get(packet.id).set(packet.x, packet.y, packet.angle, packet.armor);
				}
				else {
					tanks.get(packet.id).set(packet.x, packet.y, packet.angle, packet.armor);
				}
			}
			clientLock.unlock();
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