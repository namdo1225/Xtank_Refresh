import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
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
	private HashMap<Integer, Tank>		tanks;
	private ArrayList<Bullet>			bullets;
	private Runner						runnable;
	private ExecutorService				pool;
	
	private	int							tankModel;
	
	private boolean						terminate;
	
	
	public ClientModel() {
		mode = Mode.MAIN;
		tanks = new HashMap<>();
		bullets = new ArrayList<>();
		tank = new Tank(0, 0, 0);
	}
	
	public void setMVC(XTankUI view) {
		clientView = view;
	}
	
	public void setMode(Mode m) {
		mode = m;
		clientView.updateScreen(mode);
	}
	
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
        		
        		clientView.recreateGameScreen(map_id, tankModel);
        		
        		new_tank = (InputPacket)in.readObject();
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
		
	public Tank getTank() {
		return tank;
	}
	
	public HashMap<Integer, Tank> getTanks() {
		return tanks;
	}
	
	public ObjectInputStream getInput() {
		return in;
	}
	
	public ObjectOutputStream getOutput() {
		return out;
	}
	
	public Runner getRun() {
		return runnable;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void deleteSocket() {
		socket = null;
	}
	
	public void deleteInput() {
		in = null;
	}
	
	public void deleteOutput() {
		out = null;
	}
	
	public void resetTankData() {
		tanks = new HashMap<>();
		bullets = new ArrayList<>();
		tank = new Tank(0, 0, 0);
	}
	
	public void deleteThreadPool() {
		if (pool != null) {
			pool.shutdownNow();
			pool = null;
		}
	}
	
	public boolean getTerminate() {
		return terminate;
	}
	
	public void setTankModel(int model) {
		tankModel = model;
	}
	
	public class Runner implements Runnable {
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
							if (packet.id > bullets.size()) {
								bullets.add(new Bullet(packet.x, packet.y, 0, 0));
							}
							// update bullet
							else {
								bullets.get(packet.id).set(packet.x, packet.y);
							}
							// delete bullet
							if (packet.shoot) {
								bullets.remove(packet.id);
							}
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
		
		public void stop() {
			terminate = true;			
		}
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	};
}